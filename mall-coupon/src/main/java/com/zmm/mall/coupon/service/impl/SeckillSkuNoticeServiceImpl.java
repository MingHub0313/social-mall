package com.zmm.mall.coupon.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;

import com.zmm.mall.coupon.dao.SecKillSkuNoticeDao;
import com.zmm.mall.coupon.entity.SecKillSkuNoticeEntity;
import com.zmm.mall.coupon.service.SeckillSkuNoticeService;

/**
 * 秒杀商品通知订阅
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:14
 */
@Service("seckillSkuNoticeService")
public class SeckillSkuNoticeServiceImpl extends ServiceImpl<SecKillSkuNoticeDao, SecKillSkuNoticeEntity> implements SeckillSkuNoticeService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SecKillSkuNoticeEntity> page = this.page(
                new Query<SecKillSkuNoticeEntity>().getPage(params),
                new QueryWrapper<SecKillSkuNoticeEntity>()
        );

        return new PageUtils(page);
    }

}
