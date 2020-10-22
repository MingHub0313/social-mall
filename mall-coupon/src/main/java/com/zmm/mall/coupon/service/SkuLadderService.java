package com.zmm.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.coupon.entity.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:14
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    /**
     * 查询 商品 阶梯价格
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

