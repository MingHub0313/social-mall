package com.zmm.mall.product.feign;

import com.zmm.common.to.SkuReductionTo;
import com.zmm.common.to.SpuBoundTo;
import com.zmm.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Name CouponFeignService
 * @Author 900045
 * @Created by 2020/9/3 0003
 */
@FeignClient(name = "mall-coupon",url = "127.0.0.1:88/api")
public interface CouponFeignService {

	/**
	 * 1、CouponFeignService.saveSpuBounds(spuBoundTo);
	 *      1）、@RequestBody将这个对象转为json。
	 *      2）、找到gulimall-coupon服务，给/coupon/spubounds/save发送请求。
	 *          将上一步转的json放在请求体位置，发送请求；
	 *      3）、对方服务收到请求。请求体里有json数据。
	 *          (@RequestBody SpuBoundsEntity spuBounds)；将请求体的json转为SpuBoundsEntity；
	 * 只要json数据模型是兼容的。双方服务无需使用同一个to
	 * @param spuBoundTo
	 * @return
	 */


	@PostMapping("/coupon/spubounds/save")
	R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);


	/**
	 * 保存优惠
	 * @param skuReductionTo
	 * @return
	 */
	@PostMapping("/coupon/skufullreduction/saveinfo")
	R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);

}
