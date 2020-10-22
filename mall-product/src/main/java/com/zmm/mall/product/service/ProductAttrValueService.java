package com.zmm.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45pp[
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

	/**
	 * 查询
	 * @param params
	 * @return
	 */
    PageUtils queryPage(Map<String, Object> params);

	/**
	 * 保存
	 * @param collect
	 */
	void saveProductAttr(List<ProductAttrValueEntity> collect);

	/**
	 * 根据 spuId 返回规格
	 * @param spuId
	 * @return
	 */
	List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);

	/**
	 * 更新规格
	 * @param spuId
	 * @param entities
	 */
	void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities);
}

