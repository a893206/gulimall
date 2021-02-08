package com.cr.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cr.common.utils.PageUtils;
import com.cr.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author cr
 * @email 931009686@qq.com
 * @date 2021-01-25 23:32:51
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> list);

    /**
     * 找到catalogId的完整路径
     * [父, 子, 孙]
     * @param catalogId
     * @return
     */
    Long[] findCatalogPath(Long catalogId);

    /**
     * 级联更新所有关联的数据
     * @param category
     */
    void updateCascade(CategoryEntity category);

}

