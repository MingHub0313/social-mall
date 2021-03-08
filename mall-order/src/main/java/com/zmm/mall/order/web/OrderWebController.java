package com.zmm.mall.order.web;

import com.zmm.common.base.model.ReqResult;
import com.zmm.mall.order.service.OrderService;
import com.zmm.mall.order.vo.OrderConfirmVo;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description:
 * @Name OrderWebController
 * @Author Administrator
 * @Date By 2021-03-08 21:33:31
 */
@RestController
public class OrderWebController {

    @Resource
    private OrderService orderService;



    @GetMapping("/toTrade")
    public ReqResult toTrade(Model model){
        //展示订单的的确认页
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData",orderConfirmVo);
        return new ReqResult(orderConfirmVo);
    }
}
