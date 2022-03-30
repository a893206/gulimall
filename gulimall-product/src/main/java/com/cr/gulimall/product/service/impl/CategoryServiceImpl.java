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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
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

    @Autowired
    private RedissonClient redissonClient;

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
     * CacheEvict：失效模式
     * 1、同时进行多种缓存操作 @Caching
     * 2、指定删除某个分区下的所有数据 @CacheEvict(value = "category", allEntries = true)
     * 3、存储同一类型的数据，都可以指定成同一个分区。
     *
     * @param category 分类
     */
//    @Caching(evict = {
//            @CacheEvict(value = "category", key = "'getLevel1Categories'"),
//            @CacheEvict(value = "category", key = "'getCatalogJson'")
//    })
    // 失效模式
    @CacheEvict(value = "category", allEntries = true)
    // 双写模式
    // @CachePut
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCascade(CategoryEntity category) {
        updateById(category);

        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());

        // 同时修改缓存中的数据
        // redis.del("catalogJson");等待下次主动查询进行更新
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

    /**
     * 1、每一个需要缓存的数据我们都来指定要放到哪个名字的缓存。【缓存的分区（按照业务类型分）】
     * 2、@Cacheable({"category"})
     * 代表当前方法的结果需要缓存，如果缓存中有，方法不用调用。
     * 如果缓存中没有，会调用方法，最后将方法的结果放入缓存
     * 3、默认行为
     * 3.1、如果缓存中有，方法不用调用。
     * 3.2、key默认自动生成：缓存的名字::SimpleKey []（自主生成的key值）
     * 3.3、缓存的value的值。默认使用jdk序列化机制，将序列化后的数据存到redis
     * 3.4、默认ttl时间 -1；
     * <p>
     * 自定义：
     * 1、指定生成的缓存使用的key：key属性指定，接收一个SpEL
     * SpEL的详细https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache-spel-context
     * 2、指定缓存的数据的存活时间：配置文件中修改ttl
     * 3、将数据保存为json格式：
     * <p>
     * 4、原理：
     * CacheAutoConfiguration ->
     * RedisCacheConfiguration ->
     * 自动配置了RedisCacheManager ->
     * 初始化所有的缓存 ->
     * 每个缓存决定使用什么配置 ->
     * 如果redisCacheConfiguration有就用已有的，没有就用默认配置 ->
     * 想改缓存的配置，只需要给容器中放一个RedisCacheConfiguration即可 ->
     * 就会应用到当前RedisCacheManager管理的所有缓存分区中 ->
     * CacheManager(自动配置了RedisCacheManager) -> Cache(RedisCache) -> Cache负责缓存的读写
     * <p>
     * 5、Spring-Cache的不足：
     * 5.1、读模式：
     * 缓存穿透：查询一个null数据。解决：缓存空数据；cache-null-value: true
     * 缓存击穿：大量并发进来同时查询一个正好过期的数据。解决：加锁；？默认是无加锁的；sync = true（加锁，解决击穿）
     * 缓存雪崩：大量的key同时过期。解决：加随机时间。加上过期时间。time-to-live: 3600000
     * 5.2、写模式：（缓存与数据库一致）
     * 5.2.1、读写加锁。
     * 5.2.2、引入Canal，感知到MySQL的更新去更新数据库
     * 5.2.3、读多写多，直接去数据库查询就行
     * 总结：
     * 常规数据（读多写少，即时性，一致性要求不高的数据）；完全可以使用Spring-Cache
     * 特殊数据：特殊设计
     *
     * @return 1级分类列表
     */
    @Cacheable(value = {"category"}, key = "#root.methodName", sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        System.out.println("getLevel1Categories……");
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
    @Cacheable(value = "category", key = "#root.methodName")
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        System.out.println("查询了数据库……");
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        List<CategoryEntity> level1Categories = getParentCid(selectList, 0L);
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
    public Map<String, List<Catalog2Vo>> getCatalogJson2() {
        // 给缓存中放json字符串，拿出的json字符串，还要逆转为能用的对象类型；【序列化与反序列化】

        // 1、空结果缓存：解决缓存穿透
        // 2、设置过期时间（加随机值）：解决缓存雪崩
        // 3、加锁：解决缓存击穿

        // 1、加入缓存逻辑，缓存中存的数据是json字符串。
        // JSON跨语言，跨平台兼容。
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            // 2、缓存中没有，查询数据库
            System.out.println("缓存不命中……将要查询数据库……");
            return getCatalogJsonFromDbWithRedisLock();
        }

        System.out.println("缓存命中……直接返回……");
        // 转为我们指定的对象
        return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
        });
    }

    /**
     * 从数据库查询并封装分类数据
     *
     * @return 分类数据
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        // 1、如果缓存中有就用缓存的
//         Map<String, Object> catalogJson = (Map<String, Object>) cache.get("catalogJson");
//        if (cache.get("catalogJson") == null) {
//            // 调用业务
//            // 返回数据又放入缓存
//            cache.put("catalogJson", catalogJson);
//        }
//        return catalogJson;

        // 只要是同一把锁，就能锁住需要这个锁的线程
        // 1、synchronized (this): SpringBoot所有的组件在容器中都是单例的。
        // TODO 本地锁：synchronized，JUC（Lock），在分布式情况下，想锁住所有，必须使用分布式锁

        synchronized (this) {
            return getDataFromDb();
        }
    }

    /**
     * 从数据库查询并封装分类数据
     *
     * @return 分类数据
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        // 1、占分布式锁。去redis占坑
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功……");
            // 加锁成功……
            // 2、设置过期时间，必须和加锁是同步的，原子的
            // stringRedisTemplate.expire("lock", 30, TimeUnit.SECONDS);
            Map<String, List<Catalog2Vo>> dataFromDb;
            try {
                dataFromDb = getDataFromDb();
            } finally {
                // 获取值对比+对比成功删除=原子操作 lua脚本解锁
//            String lockValue = stringRedisTemplate.opsForValue().get("lock");
//            if (uuid.equals(lockValue)) {
//                // 删除我自己的锁
//                stringRedisTemplate.delete("lock");
//            }
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                // 删除锁
                Long lock1 = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList("lock"), uuid);
            }
            return dataFromDb;
        } else {
            // 加锁失败……重试。synchronized ()
            // 自旋的方式
            // 休眠100ms重试
            System.out.println("获取分布式锁失败……等待重试");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDbWithRedisLock();
        }
    }

    /**
     * 从数据库查询并封装分类数据
     * 缓存里面的数据如何和数据库保持一致
     * 缓存数据一致性
     * 1、双写模式
     * 2、失效模式
     *
     * @return 分类数据
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        // 1、锁的名字。锁的粒度，越细越快。
        // 锁的粒度：具体缓存的是某个数据。11-号商品；product-11-lock product-12-lock product-lock
        RLock lock = redissonClient.getLock("catalogJson-lock");
        lock.lock();

        Map<String, List<Catalog2Vo>> dataFromDb;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }

        return dataFromDb;
    }

    private Map<String, List<Catalog2Vo>> getDataFromDb() {
        // 得到锁以后，我们应该再去缓存中确定一次，如果没有才需要继续查询
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {
            // 缓存不为null直接返回
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
            });
        }
        System.out.println("查询了数据库……");

        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> level1Categories = getLevel1Categories();

        // 2、封装数据
        Map<String, List<Catalog2Vo>> catalogJsonFromDb = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
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

        // 3、查到的数据再放入缓存，将对象转为json放在缓存中
        String s = JSON.toJSONString(catalogJsonFromDb);
        stringRedisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);
        return catalogJsonFromDb;
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList, Long parentCid) {
        // return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        return selectList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
    }
}