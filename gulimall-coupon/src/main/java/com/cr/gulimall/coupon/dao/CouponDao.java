package com.cr.gulimall.coupon.dao;

import com.cr.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author cr
 * @email 931009686@qq.com
 * @date 2021-01-26 11:31:40
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
