package com.zmm.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.product.entity.SkuInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

	/**
	 * 查询
	 * @param params
	 * @return
	 */
    PageUtils queryPage(Map<String, Object> params);

	/**
	 * 保存sku
	 * @param skuInfoEntity
	 */
	void saveSkuInfo(SkuInfoEntity skuInfoEntity);

	/**
	 * 查询 sku 详情
	 * @param params
	 * @return
	 */
	PageUtils queryPageByCondition(Map<String, Object> params);

	/**
	 * 根据 spuId 查询 sku信息
	 * @param spuId
	 * @return
	 */
	List<SkuInfoEntity> getSkuBySpuId(Long spuId);
}

