package com.bigdata.zmm.mall.cart.controller;

import com.bigdata.zmm.mall.cart.interceptor.CartInterceptor;
import com.bigdata.zmm.mall.cart.service.CartService;
import com.bigdata.zmm.mall.cart.to.UserInfoTo;
import com.bigdata.zmm.mall.cart.vo.CartItem;
import com.zmm.common.base.model.ReqResult;
import com.zmm.common.base.model.ResultCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Name CartController
 * @Author Administrator
 * @Date By 2021-02-28 23:07:21
 */
@RestController
public class CartController {

    @Resource
    private CartService cartService;


    /**
     *
     * 浏览器有一个 cookie user-key:标识用户身份 一个月后过期 如果第一次访问(京东)购物车功能 都会给一个临时的用户身份;
     * 浏览器以后保存 每次都会带上这个cookie
     * 登陆: session 有
     * 没有登陆 :按照cookie里面带来的 user-key
     * 第一次: 如果没有临时用户,需要创建一个临时用户
     * @description:
     * @author: Administrator
     * @date: 2021-02-28 23:11:54
     * @return: com.zmm.common.base.model.ReqResult
     **/
    @GetMapping("/cart")
    public ReqResult cartListPage(){

        // 1. 快速得到用户用户信息 id:user-key
        // 目标方法
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();

        return new ReqResult(ResultCode.SUCCESS);
    }
    
    /**
     * 添加商品到 购物车
     * @author: Administrator
     * @date: 2021-03-03 20:43:45
     * @param skuId: 
     * @param number: 
     * @return: com.zmm.common.base.model.ReqResult
     **/
    public ReqResult addToCart(@RequestParam("skuId") Long skuId,
                               @RequestParam("number") Integer number) throws ExecutionException, InterruptedException {

        CartItem cartItem = cartService.addToCart(skuId,number);

        return new ReqResult(ResultCode.SUCCESS);
    }
}
