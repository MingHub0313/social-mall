package com.zmm.mall.member.web;

import com.zmm.common.utils.R;
import com.zmm.mall.member.feign.OrderFeignService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 900045
 * @description:
 * @name MemberWebController
 * @date By 2021-04-15 15:54:19
 */
@RestController
public class MemberWebController {
	
	@Resource
	private OrderFeignService orderFeignService;
	
	@GetMapping("/member/order/list")
	public String memberOrderPage(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, Model model, HttpServletRequest request){
		//查询出当前登录的用户下所有的订单列表数据
		//
		Map<String,Object> map = new HashMap<>();
		map.put("page",pageNum.toString());
		// 
		R r = orderFeignService.listWithItem(map);
		model.addAttribute("orders",r);
		return "orderList";
	}
}
