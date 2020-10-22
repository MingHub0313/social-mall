package com.zmm.mall.coupon.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;

import com.zmm.mall.coupon.dao.SecKillPromotionDao;
import com.zmm.mall.coupon.entity.SecKillPromotionEntity;
import com.zmm.mall.coupon.service.SeckillPromotionService;

/**
 * 秒杀活动
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:15
 */
@Service("seckillPromotionService")
public class SeckillPromotionServiceImpl extends ServiceImpl<SecKillPromotionDao, SecKillPromotionEntity> implements SeckillPromotionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SecKillPromotionEntity> page = this.page(
                new Query<SecKillPromotionEntity>().getPage(params),
                new QueryWrapper<SecKillPromotionEntity>()
        );

        return new PageUtils(page);
    }

}
