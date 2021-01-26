package com.cr.gulimall.product.dao;

import com.cr.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author cr
 * @email 931009686@qq.com
 * @date 2021-01-25 23:32:51
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
