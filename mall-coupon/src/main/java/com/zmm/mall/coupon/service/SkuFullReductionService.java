package com.zmm.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.to.SkuReductionTo;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:14
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

	/**
	 * 查询 商品满减信息
	 * @param params
	 * @return
	 */
    PageUtils queryPage(Map<String, Object> params);

	/**
	 * 保存 满减活动信息
	 * @param reductionTo
	 */
	void saveSkuReduction(SkuReductionTo reductionTo);
}

