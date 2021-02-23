package com.zmm.mall.product.dao;

import com.zmm.mall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmm.mall.product.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

	/**
	 * 根据 spuId 获取 销售信息
	 * @author: 900045
	 * @date: 2021-02-23 16:58:36
	 * @throws 
	 * @param spuId: 
	 * @return: java.util.List<com.zmm.mall.product.vo.SkuItemSaleAttrVo>
	 **/
	List<SkuItemSaleAttrVo> getSaleAttrsBySpId(@Param("spuId") Long spuId);
}
