package com.zmm.mall.order.dao;

import com.zmm.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:50:05
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

	/**
	 * 修改订单状态为 成功
	 * @author: 900045
	 * @date: 2021-04-15 17:18:28
	 * @throws 
	 * @param orderSn: 
	 * @param code: 
	 * @return: void
	 **/
	void updateOrderPayFinishedStatus(@Param("orderSn") String orderSn, @Param("code") Integer code);
	
}
