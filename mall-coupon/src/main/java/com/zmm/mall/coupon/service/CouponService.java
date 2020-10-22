package com.zmm.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.coupon.entity.CouponEntity;

import java.util.Map;

/**
 * 查询优惠券信息
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:15
 */
public interface CouponService extends IService<CouponEntity> {

    /**
     * 查询优惠券信息
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

