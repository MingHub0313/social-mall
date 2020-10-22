package com.zmm.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.coupon.entity.HomeSubjectSpuEntity;

import java.util.Map;

/**
 * 专题商品
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:14
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

    /**
     * 查询 专题商品
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

