package com.cr.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cr.gulimall.product.entity.SpuInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 *
 * @author cr
 * @email 931009686@qq.com
 * @date 2021-01-25 23:32:51
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {
    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
