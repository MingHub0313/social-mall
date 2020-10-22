package com.zmm.mall.coupon.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;

import com.zmm.mall.coupon.dao.SecKillSkuRelationDao;
import com.zmm.mall.coupon.entity.SecKillSkuRelationEntity;
import com.zmm.mall.coupon.service.SeckillSkuRelationService;

/**
 * 秒杀活动商品关联
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:14
 */
@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SecKillSkuRelationDao, SecKillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SecKillSkuRelationEntity> page = this.page(
                new Query<SecKillSkuRelationEntity>().getPage(params),
                new QueryWrapper<SecKillSkuRelationEntity>()
        );

        return new PageUtils(page);
    }

}
