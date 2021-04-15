package com.zmm.mall.order.web;

import com.alipay.api.AlipayApiException;
import com.zmm.mall.order.config.AliPayTemplate;
import com.zmm.mall.order.service.OrderService;
import com.zmm.mall.order.vo.PayVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 900045
 * @description:
 * @name PayWebController
 * @date By 2021-04-15 15:30:15
 */
@Slf4j
@RestController
public class PayWebController {
	
	@Resource
	private AliPayTemplate aliPayTemplate;
	
	@Resource
	private OrderService orderService;
	
	/**
	 * 1.支付宝的支付页面让浏览器展示
	 * 2.支付成功以后,我们要跳转到用户的订单列表页
	 * 
	 * @author: 900045
	 * @date: 2021-04-15 15:50:52
	 * @throws 
	 * @param orderSn: 
	 * @return: java.lang.String
	 **/
	@GetMapping(value = "/pay/order",produces = "text/html")
	public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
		PayVo payVo = orderService.getOrderPay(orderSn);
		// 支付宝返回的是一个页面.将此页面直接交给浏览器就OK
		String pay = aliPayTemplate.pay(payVo);
		log.error("打印数据{}",pay);
		return pay;
	}
	
}
