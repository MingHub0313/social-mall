package com.zmm.mall.seckill.feign;

import com.zmm.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 900045
 * @description:
 * @name ProductFeignService
 * @date By 2021-04-16 16:54:47
 */
@FeignClient(name = "mall-product",url = "127.0.0.1:88/api")
public interface ProductFeignService {

	/**
	 * 远程调用查询sku详情
	 * @author: 900045
	 * @date: 2021-04-16 16:55:27
	 * @throws 
	 * @param skuId: 
	 * @return: com.zmm.common.utils.R
	 **/
	@RequestMapping("product/skuinfo/info/{skuId}")
	R getSkuInfo(@PathVariable("skuId") Long skuId);
}
