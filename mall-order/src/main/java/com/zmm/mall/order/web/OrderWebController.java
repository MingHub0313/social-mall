package com.zmm.mall.order.web;

import com.zmm.common.base.model.ReqResult;
import com.zmm.common.base.model.ResultCode;
import com.zmm.mall.order.service.OrderService;
import com.zmm.mall.order.vo.OrderConfirmVo;
import com.zmm.mall.order.vo.OrderSubmitVo;
import com.zmm.mall.order.vo.SubmitOrderResponseVo;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

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



    /**
     * 确认订单
     * @description:
     * @author: Administrator
     * @date: 2021-03-11 20:46:02
     * @param model: 
     * @param httpServletRequest: 
     * @return: com.zmm.common.base.model.ReqResult
     **/
    @GetMapping("/toTrade")
    public ReqResult toTrade(Model model, HttpServletRequest httpServletRequest) throws ExecutionException, InterruptedException {
        //展示订单的的确认页
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData",orderConfirmVo);
        return new ReqResult(orderConfirmVo);
    }

    /**
     * 下单功能
     * @description:
     * @author: Administrator
     * @date: 2021-03-11 20:45:50
     * @param orderSubmitVo: 
     * @return: com.zmm.common.base.model.ReqResult
     **/
    @PostMapping("/submit/order")
    public ReqResult submitOrder(OrderSubmitVo orderSubmitVo){
        // 创建订单 验令牌 验价格 锁库存 ...
        SubmitOrderResponseVo submitOrderResponseVo = orderService.submitOrder(orderSubmitVo);


        if (submitOrderResponseVo.getCode() == 0){
            // 下单失败 回到订单确认页 重新确认订单信息
            // return "redirect:http://order.mall.com/toTrade"
            // 下单失败的后续操作 ...
        }
        // 下单成功 来到支付选择页
        return new ReqResult(ResultCode.SUCCESS);
    }
}
