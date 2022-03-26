package com.cr.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cr.gulimall.product.entity.AttrEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 *
 * @author cr
 * @email 931009686@qq.com
 * @date 2021-01-25 23:32:51
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
    List<Long> selectSearchAttrIds(@Param("attrIds") List<Long> attrIds);
}
