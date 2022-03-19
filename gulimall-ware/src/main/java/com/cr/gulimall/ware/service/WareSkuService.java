package com.cr.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cr.common.utils.PageUtils;
import com.cr.gulimall.ware.entity.WareSkuEntity;

import java.util.Map;

/**
 * 商品库存
 *
 * @author cr
 * @email 931009686@qq.com
 * @date 2021-01-26 11:52:56
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 入库
     *
     * @param skuId  skuId
     * @param wareId 仓库id
     * @param skuNum sku数量
     */
    void addStock(Long skuId, Long wareId, Integer skuNum);
}
