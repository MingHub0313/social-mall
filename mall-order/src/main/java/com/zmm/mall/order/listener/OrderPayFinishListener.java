package com.zmm.mall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.zmm.common.constant.Constants;
import com.zmm.mall.order.config.AliPayTemplate;
import com.zmm.mall.order.service.OrderService;
import com.zmm.mall.order.vo.PayAsyncVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author 900045
 * @description:
 * @name OrderPayFinishListener
 * @date By 2021-04-15 16:46:17
 */
@Slf4j
@RestController
public class OrderPayFinishListener {
	
	@Resource
	private AliPayTemplate aliPayTemplate;
	
	@Resource
	private OrderService orderService;
	
	/**
	 * 支付成功后 支付宝回调该接口 --->修改订单状态
	 * @author: 900045
	 * @date: 2021-04-15 17:31:29
	 * @throws 
	 * @param vo: 
	 * @param request: 
	 * @return: java.lang.String
	 **/
	@PostMapping("/pay/notify")
	public String handleAliPayFinishNotify(PayAsyncVo vo,HttpServletRequest request){
		// 只要我们收到支付宝给我们异步通知,告诉我们订单支付成功.返回 success ,支付宝就再也不通知
		Map<String, String[]> parameterMap = request.getParameterMap();
		for (String  key:parameterMap.keySet()){
			String value = request.getParameter(key);
			log.error("键:[{}]----------值:[{}]",key,value);
		}
		// 验签 --验证是不是支付宝返回的消息

		//获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> requestParams = request.getParameterMap();
		
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = iter.next();
			String[] values = requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8")
			params.put(name, valueStr);
		}
		if (params.isEmpty()) {
			log.error("{}-PARAM  resp is blank: {}", Constants.LOG_PREFIX, params);
			return "fail";
		}
		log.error("支付宝通知了....数据:[{}]",parameterMap);
		try {
			boolean flag = AlipaySignature.rsaCheckV1(params, aliPayTemplate.getAlipay_public_key(), aliPayTemplate.getCharset(),
					aliPayTemplate.getSign_type());
			if (flag) {
				log.error("签名验证成功.......");
				String result = orderService.handlePayResult(vo);
				return result;
			}
		} catch (AlipayApiException e) {
			log.error("VERIFY E", e);
		}
		log.error("签名验证失败......");
		return "error";
	}
}
