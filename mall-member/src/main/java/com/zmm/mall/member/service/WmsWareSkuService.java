package com.zmm.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.member.entity.WmsWareSkuEntity;

import java.util.Map;

/**
 * 商品库存
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
public interface WmsWareSkuService extends IService<WmsWareSkuEntity> {

    /**
     * 查询 商品库存
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

