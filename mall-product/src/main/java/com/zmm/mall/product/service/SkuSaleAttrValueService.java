package com.zmm.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.product.entity.SkuSaleAttrValueEntity;
import com.zmm.mall.product.vo.SkuItemSaleAttrVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    /**
     * 查询
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据 spuId 获取 销售信息
     * @author: 900045
     * @date: 2021-02-23 16:57:36
     * @throws 
     * @param spuId: 
     * @return: java.util.List<com.zmm.mall.product.vo.SkuItemSaleAttrVo>
     **/
	List<SkuItemSaleAttrVo> getSaleAttrsBySpId(Long spuId);
}

