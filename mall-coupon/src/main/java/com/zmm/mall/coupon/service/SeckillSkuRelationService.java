package com.zmm.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.coupon.entity.SecKillSkuRelationEntity;

import java.util.Map;

/**
 * 秒杀活动商品关联
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:14
 */
public interface SeckillSkuRelationService extends IService<SecKillSkuRelationEntity> {

    /**
     * 查询 秒杀活动商品关联
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

