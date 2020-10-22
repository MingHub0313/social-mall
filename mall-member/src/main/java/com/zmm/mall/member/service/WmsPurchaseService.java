package com.zmm.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.member.entity.WmsPurchaseEntity;

import java.util.Map;

/**
 * 采购信息
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
public interface WmsPurchaseService extends IService<WmsPurchaseEntity> {

    /**
     * 查询 采购信息
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

