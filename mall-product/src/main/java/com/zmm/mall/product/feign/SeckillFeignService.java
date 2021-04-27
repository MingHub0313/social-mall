package com.zmm.mall.product.feign;

import com.zmm.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 900045
 * @description:
 * @name SeckillFeignService
 * @date By 2021-04-19 16:12:53
 */
@FeignClient(name = "mall-seckill",url = "127.0.0.1:88/api")
public interface SeckillFeignService {

	/**
	 * 远程调用 秒杀服务 查询商品详情
	 * @author: 900045
	 * @date: 2021-04-19 16:13:17
	 * @throws 
	 * @param skuId: 
	 * @return: com.zmm.common.utils.R
	 **/
	@GetMapping("/seckill/sku/seckill/{skuId}")
	R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);

	/**
	 * 远程调式 接口
	 * @author: 900045
	 * @date: 2021-04-27 14:54:59
	 * @throws 
	 * @param skuId: 
	 * @return: java.util.Map
	 **/
	@GetMapping(value = "/seckill/feign/{skuId}")
	String setOpenFeign(@PathVariable("skuId") Long skuId);
}
