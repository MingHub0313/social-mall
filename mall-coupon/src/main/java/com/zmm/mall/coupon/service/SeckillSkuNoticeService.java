package com.zmm.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.coupon.entity.SecKillSkuNoticeEntity;

import java.util.Map;

/**
 * 秒杀商品通知订阅
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:14
 */
public interface SeckillSkuNoticeService extends IService<SecKillSkuNoticeEntity> {

    /**
     * 查询 秒杀商品通知订阅
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

