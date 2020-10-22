package com.zmm.mall.coupon.dao;

import com.zmm.mall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:15
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
