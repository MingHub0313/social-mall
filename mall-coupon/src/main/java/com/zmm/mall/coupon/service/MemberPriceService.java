package com.zmm.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.coupon.entity.MemberPriceEntity;

import java.util.Map;

/**
 * 商品会员价格
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:15
 */
public interface MemberPriceService extends IService<MemberPriceEntity> {

    /**
     * 查询 商品会员价格
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

