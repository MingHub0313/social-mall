package com.zmm.mall.coupon.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;

import com.zmm.mall.coupon.dao.SecKillSessionDao;
import com.zmm.mall.coupon.entity.SecKillSessionEntity;
import com.zmm.mall.coupon.service.SeckillSessionService;

/**
 * 秒杀活动场次
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:14
 */
@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SecKillSessionDao, SecKillSessionEntity> implements SeckillSessionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SecKillSessionEntity> page = this.page(
                new Query<SecKillSessionEntity>().getPage(params),
                new QueryWrapper<SecKillSessionEntity>()
        );

        return new PageUtils(page);
    }

}
