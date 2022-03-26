package com.cr.gulimall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cr.gulimall.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 *
 * @author cr
 * @email 931009686@qq.com
 * @date 2021-01-26 11:52:56
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
    /**
     * 入库
     *
     * @param skuId  skuId
     * @param wareId 仓库id
     * @param skuNum sku数量
     */
    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    long getSkuStock(Long skuId);
}
