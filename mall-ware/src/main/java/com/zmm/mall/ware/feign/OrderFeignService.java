package com.zmm.mall.ware.feign;

import com.zmm.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 900045
 * @description:
 * @name OrderFeignService
 * @date By 2021-03-24 13:47:46
 */
@FeignClient(name = "mall-member",url = "127.0.0.1:88/api")
public interface OrderFeignService {


	/**
	 * 远程调用 订单服务查询 订单信息
	 * @author: 900045
	 * @date: 2021-03-24 13:48:19
	 * @throws 
	 * @param orderSn: 
	 * @return: com.zmm.common.utils.R
	 **/
	@GetMapping("/order/order/status/{orderSn}")
	R getOrderStatus(@PathVariable String orderSn);
}
