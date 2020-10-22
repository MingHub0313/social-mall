package com.zmm.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.coupon.entity.CouponHistoryEntity;

import java.util.Map;

/**
 * 优惠券领取历史记录
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:15
 */
public interface CouponHistoryService extends IService<CouponHistoryEntity> {

    /**
     * 查询优惠券领取记录
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

