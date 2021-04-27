package com.zmm.mall.product.web;

import com.zmm.common.utils.R;
import com.zmm.mall.product.service.SkuInfoService;
import com.zmm.mall.product.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 900045
 * @description:
 * @name ItemController
 * @date By 2021-02-23 15:40:24
 */
@Slf4j
@RestController
public class ItemController {
	
	@Autowired
	private SkuInfoService skuInfoService;

	/**
	 * 展示 当前 sku的详情
	 * @author: 900045
	 * @date: 2021-02-23 15:42:28
	 * @throws 
	 * @param skuId: 
	 * @return: java.lang.String
	 **/
	@GetMapping(value = "/product/{skuId}")
	public SkuItemVo skuItem(@PathVariable("skuId") Long skuId){
		log.info("准备查询:{}详情",skuId);
		SkuItemVo skuItemVo = skuInfoService.item(skuId);
		return skuItemVo;
	}

	@GetMapping(value = "/product/test/{skuId}")
	public R testOpenFeign(@PathVariable("skuId") Long skuId){
		log.info("准备查询:{}详情",skuId);
		String test = skuInfoService.test(skuId);
		return R.ok().setData(test);
	}
	
}
