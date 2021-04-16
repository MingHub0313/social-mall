package com.zmm.mall.seckill.feign;

import com.zmm.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author 900045
 * @description:
 * @name CouponFeignService
 * @date By 2021-04-16 15:58:04
 */
@FeignClient(name = "mall-coupon",url = "127.0.0.1:88/api")
public interface CouponFeignService {

	/**
	 * 远程调用获取最近三天的商品数据
	 * @author: 900045
	 * @date: 2021-04-16 16:24:51
	 * @throws 
	 * @return: com.zmm.common.utils.R
	 **/
	@GetMapping("coupon/seckillsession/latest3DaySession")
	R getLatest3DaySession();
}
