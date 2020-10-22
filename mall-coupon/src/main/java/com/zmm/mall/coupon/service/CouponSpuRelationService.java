package com.zmm.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.coupon.entity.CouponSpuRelationEntity;

import java.util.Map;

/**
 * 优惠券与产品关联
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:15
 */
public interface CouponSpuRelationService extends IService<CouponSpuRelationEntity> {

    /**
     * 查询 优惠券与产品
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

