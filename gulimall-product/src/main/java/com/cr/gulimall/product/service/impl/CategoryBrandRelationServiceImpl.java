package com.cr.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cr.common.utils.PageUtils;
import com.cr.common.utils.Query;
import com.cr.gulimall.product.dao.BrandDao;
import com.cr.gulimall.product.dao.CategoryBrandRelationDao;
import com.cr.gulimall.product.dao.CategoryDao;
import com.cr.gulimall.product.entity.BrandEntity;
import com.cr.gulimall.product.entity.CategoryBrandRelationEntity;
import com.cr.gulimall.product.entity.CategoryEntity;
import com.cr.gulimall.product.service.BrandService;
import com.cr.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author cr
 */
@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryBrandRelationDao relationDao;

    @Autowired
    private BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catalogId = categoryBrandRelation.getCatalogId();

        // 1、查询详细名字
        BrandEntity brandEntity = brandDao.selectById(brandId);
        CategoryEntity categoryEntity = categoryDao.selectById(catalogId);

        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatalogName(categoryEntity.getName());

        save(categoryBrandRelation);
    }

    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity relationEntity = new CategoryBrandRelationEntity();
        relationEntity.setBrandId(brandId);
        relationEntity.setBrandName(name);

        update(relationEntity, new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
    }

    @Override
    public void updateCategory(Long catId, String name) {
        baseMapper.updateCategory(catId, name);
    }

    @Override
    public List<BrandEntity> getBrandsByCatId(Long catId) {
        List<CategoryBrandRelationEntity> catalogId = relationDao.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catalog_id", catId));

        return catalogId.stream().map(item -> {
            Long brandId = item.getBrandId();
            return brandService.getById(brandId);
        }).collect(Collectors.toList());
    }

}