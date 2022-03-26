package com.cr.gulimall.search.service;

import com.cr.common.to.es.SkuEsModel;

import java.util.List;

/**
 * @author cr
 * @date 2022/3/27 01:08
 */
public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels);
}
