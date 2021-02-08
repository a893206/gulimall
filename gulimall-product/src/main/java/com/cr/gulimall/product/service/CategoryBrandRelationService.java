package com.cr.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cr.common.utils.PageUtils;
import com.cr.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author cr
 * @email 931009686@qq.com
 * @date 2021-01-25 23:32:51
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrand(Long brandId, String name);

    void updateCategory(Long catId, String name);

}

