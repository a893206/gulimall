package com.cr.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cr.common.utils.PageUtils;
import com.cr.common.utils.Query;
import com.cr.gulimall.product.dao.CategoryDao;
import com.cr.gulimall.product.entity.CategoryEntity;
import com.cr.gulimall.product.service.CategoryBrandRelationService;
import com.cr.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author cr
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

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

}