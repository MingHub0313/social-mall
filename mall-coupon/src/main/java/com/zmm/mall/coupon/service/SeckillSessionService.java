package com.zmm.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.coupon.entity.SecKillSessionEntity;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:14
 */
public interface SeckillSessionService extends IService<SecKillSessionEntity> {

    /**
     * 查询 秒杀活动场次
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 最近三天参与秒杀的商品
     * @author: 900045
     * @date: 2021-04-16 16:01:40
     * @throws 
    
     * @return: java.util.List<com.zmm.mall.coupon.entity.SecKillSessionEntity>
     **/
	List<SecKillSessionEntity> getLatest3DaySession();
	
}

