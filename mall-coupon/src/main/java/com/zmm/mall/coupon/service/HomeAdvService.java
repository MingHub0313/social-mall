package com.zmm.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.coupon.entity.HomeAdvEntity;

import java.util.Map;

/**
 * 首页轮播广告
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:14
 */
public interface HomeAdvService extends IService<HomeAdvEntity> {

    /**
     * 查询 首页轮播广告
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

