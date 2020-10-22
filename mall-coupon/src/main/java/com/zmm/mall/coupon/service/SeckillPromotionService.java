package com.zmm.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.coupon.entity.SecKillPromotionEntity;

import java.util.Map;

/**
 * 秒杀活动
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:15
 */
public interface SeckillPromotionService extends IService<SecKillPromotionEntity> {

    /**
     * 查询秒杀活动
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

