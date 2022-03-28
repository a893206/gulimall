package com.cr.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cr.common.utils.PageUtils;
import com.cr.common.utils.Query;
import com.cr.gulimall.product.dao.CategoryDao;
import com.cr.gulimall.product.entity.CategoryEntity;
import com.cr.gulimall.product.service.CategoryBrandRelationService;
import com.cr.gulimall.product.service.CategoryService;
import com.cr.gulimall.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author cr
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    private final Map<String, Object> cache = new HashMap<>(16);

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //2、组装成父子的树形结构

        //2.1、找到所有的一级分类
        return entities.stream()
                .filter((categoryEntity) -> categoryEntity.getParentCid() == 0)
                .peek((menu) -> menu.setChildren(getChildren(menu, entities)))
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());
    }

    @Override
    public void removeMenuByIds(List<Long> list) {
        baseMapper.deleteBatchIds(list);
    }

    /**
     * 找到catalogId的完整路径
     * [父, 子, 孙]
     *
     * @param catalogId
     * @return
     */
    @Override
    public Long[] findCatalogPath(Long catalogId) {
        List<Long> parentPath = findParentPath(catalogId, new ArrayList<>());
        Collections.reverse(parentPath);

        return parentPath.toArray(new Long[0]);
    }

    /**
     * 级联更新所有关联的数据
     *
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        updateById(category);

        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    private List<Long> findParentPath(Long catalogId, List<Long> paths) {
        paths.add(catalogId);
        CategoryEntity byId = getById(catalogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 递归查找所有菜单的子菜单
     *
     * @param root 根结点
     * @param all  所有菜单
     * @return 子菜单
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        return all.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid().equals(root.getCatId()))
                //1、找到子菜单
                .peek(categoryEntity -> categoryEntity.setChildren(getChildren(categoryEntity, all)))
                //2、菜单的排序
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryEntity> getLevel1Categories() {
        long l = System.currentTimeMillis();
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        System.out.println("消耗时间：" + (System.currentTimeMillis() - l));
        return categoryEntities;
    }

    /**
     * TODO 产生堆外内存溢出：OutOfDirectMemoryError
     * 1、SpringBoot2.0以后默认使用lettuce作为操作redis的客户端。
     * 2、lettuce的bug导致netty堆外内存溢出 -Xmx300m；netty如果没有指定堆外内存，默认使用-Xmx300m
     *   可以通过-Dio.netty.maxDirectMemory进行设置
     * 解决方案：不能使用-Dio.netty.maxDirectMemory只去调大堆外内存。
     * 1、升级lettuce客户端。
     * 2、切换使用jedis
     * Spring再次封装redisTemplate：
     * lettuce、jedis操作redis的底层客户端。Spring再次封装redisTemplate；
     *
     * @return 分类数据
     */
    @Override
    public Map<String, Object> getCatalogJson() {
        // 给缓存中放json字符串，拿出的json字符串，还要逆转为能用的对象类型；【序列化与反序列化】

        // 1、空结果缓存：解决缓存穿透
        // 2、设置过期时间（加随机值）：解决缓存雪崩
        // 3、加锁：解决缓存击穿

        // 1、加入缓存逻辑，缓存中存的数据是json字符串。
        // JSON跨语言，跨平台兼容。
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            // 2、缓存中没有
            Map<String, Object> catalogJsonFromDb = getCatalogJsonFromDb();
            // 3、查到的数据再放入缓存，将对象转为json放在缓存中
            String s = JSON.toJSONString(catalogJsonFromDb);
            stringRedisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);
            return catalogJsonFromDb;
        }

        // 转为我们指定的对象
        return JSON.parseObject(catalogJson, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * 从数据库查询并封装分类数据
     *
     * @return 分类数据
     */
    public Map<String, Object> getCatalogJsonFromDb() {
        // 1、如果缓存中有就用缓存的
//         Map<String, Object> catalogJson = (Map<String, Object>) cache.get("catalogJson");
//        if (cache.get("catalogJson") == null) {
//            // 调用业务
//            // 返回数据又放入缓存
//            cache.put("catalogJson", catalogJson);
//        }
//        return catalogJson;

        // 1、将数据库的多次查询变为一次
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        // 1、查出所有1级分类
        List<CategoryEntity> level1Categories = getLevel1Categories();

        // 2、封装数据
        return level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 1、每一个的一级分类，查到这个一级分类的所有二级分类
            List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
            // 2、封装上面的结果
            List<Catalog2Vo> catalog2Vos = new ArrayList<>();
            if (categoryEntities != null) {
                catalog2Vos = categoryEntities.stream().map(l2 -> {
                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    // 1、找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catalogs = getParentCid(selectList, l2.getCatId());
                    if (level3Catalogs != null) {
                        List<Catalog2Vo.Catalog3Vo> collect = level3Catalogs.stream().map(l3 -> {
                            // 2、封装成指定格式
                            return new Catalog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                        }).collect(Collectors.toList());
                        catalog2Vo.setCatalog3List(collect);
                    }
                    return catalog2Vo;
                }).collect(Collectors.toList());
            }
            return catalog2Vos;
        }));
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList, Long parentCid) {
        // return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        return selectList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
    }
}