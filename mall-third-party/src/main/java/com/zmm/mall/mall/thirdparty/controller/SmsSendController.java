package com.zmm.mall.mall.thirdparty.controller;

import com.zmm.common.base.model.ReqResult;
import com.zmm.common.base.model.ResultCode;
import com.zmm.mall.mall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 900045
 * @description:
 * @name SmsSendController
 * @date By 2021-02-24 14:30:50
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {

	@Autowired
	private SmsComponent smsComponent;
	
	@GetMapping("/send/code")
	public ReqResult sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code){
		smsComponent.sendSmsCode(phone,code);
		return new ReqResult(ResultCode.SUCCESS);
	}
}
