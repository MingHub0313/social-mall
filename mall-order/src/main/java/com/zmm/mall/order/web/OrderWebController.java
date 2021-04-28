package com.zmm.mall.order.web;

import com.zmm.common.base.model.ReqResult;
import com.zmm.common.base.model.ResultCode;
import com.zmm.common.exception.NoStockException;
import com.zmm.mall.order.service.OrderService;
import com.zmm.mall.order.vo.CategoryEntity;
import com.zmm.mall.order.vo.OrderConfirmVo;
import com.zmm.mall.order.vo.OrderSubmitVo;
import com.zmm.mall.order.vo.SubmitOrderResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Name OrderWebController
 * @Author Administrator
 * @Date By 2021-03-08 21:33:31
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderWebController {

    @Resource
    private OrderService orderService;



    /**
     * 确认订单
     * @description:
     * @author: Administrator
     * @date: 2021-03-11 20:46:02
     * @param # model: 
     * @param # httpServletRequest: 
     * @return: com.zmm.common.base.model.ReqResult
     **/
    @GetMapping("/toTrade")
    public ReqResult toTrade() throws ExecutionException, InterruptedException {
        //展示订单的的确认页
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        //model.addAttribute("orderConfirmData",orderConfirmVo)
        return new ReqResult(orderConfirmVo);
    }
    
    /**
     * 测试远程接口 返回 list
     * @author: 900045
     * @date: 2021-04-28 11:45:57
     * @throws ExecutionException
     * @throws InterruptedException
    
     * @return: com.zmm.common.base.model.ReqResult
     **/
    @GetMapping("/tree")
    public ReqResult toListTrade() throws ExecutionException, InterruptedException {
        List<CategoryEntity> list = orderService.testList();
        return new ReqResult(list);
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
    public ReqResult submitOrder(OrderSubmitVo orderSubmitVo) {
        // 创建订单 验令牌 验价格 锁库存 ...
        try {
            SubmitOrderResponseVo submitOrderResponseVo = orderService.submitOrder(orderSubmitVo);


            if (submitOrderResponseVo.getCode() != 0) {
                // 重定向携带数据 ==> 使用 RedirectAttributes redirectAttributes
                String msg = "下单失败";
                switch (submitOrderResponseVo.getCode()) {
                    case 1:
                        msg += "订单信息过期,青刷新再次提交";
                        break;
                    case 2:
                        msg += "订单价格发生变化,请确认后再次提交";
                        break;
                    case 3:
                        msg += "库存锁定失败.商品库存不足";
                        break;
                }
                // redirectAttributes.addFlashAttribute("msg",msg)
                log.error("下单失败提示信息:[{}]", msg);
                // 下单失败 回到订单确认页 重新确认订单信息
                // return "redirect:http://order.mall.com/toTrade"
                // 下单失败的后续操作 ...
                return new ReqResult(ResultCode.EXCEPTION,msg);
            }
            // Model model
            //model.addAttribute("submitOrderResp",submitOrderResponseVo)
            // return "pay"
            // 下单成功 来到支付选择页
            return new ReqResult(ResultCode.SUCCESS);
        } catch (Exception e) {
            if ( e instanceof NoStockException) {
                String message = ((NoStockException) e).getMessage();
                log.error("下单失败提示信息:[{}]", message);
            }
            return new ReqResult(ResultCode.EXCEPTION,e.getMessage());
        }
    }
}
