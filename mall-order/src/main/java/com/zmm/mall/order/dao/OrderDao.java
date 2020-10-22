package com.zmm.mall.order.dao;

import com.zmm.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:50:05
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
