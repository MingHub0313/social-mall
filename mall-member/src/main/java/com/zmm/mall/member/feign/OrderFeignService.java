package com.zmm.mall.member.feign;

import com.zmm.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author 900045
 * @description:
 * @name OrderFeignService
 * @date By 2021-04-15 16:21:01
 */
@FeignClient(name = "mall-order",url = "127.0.0.1:88/api")
public interface OrderFeignService {

	/**
	 * 用户查询其下订单详情
	 * @author: 900045
	 * @date: 2021-04-15 16:22:37
	 * @throws 
	 * @param params: 
	 * @return: com.zmm.common.utils.R
	 **/
	@PostMapping("order/order/list/item")
	R listWithItem(@RequestBody Map<String, Object> params);
}
