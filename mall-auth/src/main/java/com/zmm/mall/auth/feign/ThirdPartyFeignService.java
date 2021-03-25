package com.zmm.mall.auth.feign;

import com.zmm.common.base.model.ReqResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 900045
 * @description:
 * @name ThirdPartyFeignService
 * @date By 2021-02-24 14:56:28
 * @FeignClient(value = "mall-third-party")
 */
@FeignClient(value = "mall-third-party",url = "127.0.0.1:88/api")
public interface ThirdPartyFeignService {

	/**
	 * 调用 远程的 mall-third-party 
	 * @author: 900045
	 * @date: 2021-02-24 14:58:18
	 * @throws 
	 * @param phone: 手机号
	 * @param code: 验证码
	 * @return: com.zmm.common.base.model.ReqResult
	 **/
	@GetMapping("/sms/send/code")
	ReqResult sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
