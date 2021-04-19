package com.zmm.mall.product.vo;

import com.zmm.mall.product.entity.SkuImagesEntity;
import com.zmm.mall.product.entity.SkuInfoEntity;
import com.zmm.mall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author 900045
 * @description:
 * @name SkuItemVo
 * @date By 2021-02-23 15:47:22
 */
@Data
public class SkuItemVo {

	/**
	 * 1.获取 sku 基本信息            -->pms_sku_info
	 */
	SkuInfoEntity skuInfoEntity;

	/**
	 * 2.sku图片信息                  -->pms_sku_images
	 */
	List<SkuImagesEntity> images;

	/**
	 * 3.获取 spu 的销售属性组合    
	 */
	List<SkuItemSaleAttrVo> saleAttr;


	/**
	 * 4.获取 spu 的介绍              -->pms_spu_desc
	 */
	SpuInfoDescEntity spuInfoDescEntity;

	/**
	 * 5.获取 spu的规格参数信息
	 */
	
	List<SpuItemAttrGroupVo> groupAttrs;

	/**
	 * 当前商品秒杀的数据
	 */
	SeckillInfoVo seckillInfoVo;
	
}
