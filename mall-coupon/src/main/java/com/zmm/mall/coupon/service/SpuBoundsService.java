package com.zmm.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:14
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    /**
     * 查询 商品 积分设置
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

