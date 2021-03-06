package com.cr.gulimall.order.dao;

import com.cr.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author cr
 * @email 931009686@qq.com
 * @date 2021-01-26 11:43:55
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
