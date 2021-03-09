package com.bigdata.zmm.mall.cart.controller;

import com.bigdata.zmm.mall.cart.service.CartService;
import com.bigdata.zmm.mall.cart.vo.Cart;
import com.bigdata.zmm.mall.cart.vo.CartItem;
import com.zmm.common.base.model.ReqResult;
import com.zmm.common.base.model.ResultCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
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
     * 获取当前用户选中的购物项
     * 如果返回的是 json 格式 必须添加 @ResponseBody 或者 使用 @RestController
     * @description:
     * @author: Administrator
     * @date: 2021-03-09 20:58:49
     * @return: java.util.List<com.bigdata.zmm.mall.cart.vo.CartItem>
     **/
    @GetMapping("/find/cartItems/user'")
    public List<CartItem> getCartItemByUser(){
        List<CartItem> cartItems = cartService.getCartItemByUser();
        return cartItems;
    }


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
    public ReqResult cartListPage() throws ExecutionException, InterruptedException {

        // 1. 快速得到用户用户信息 id:user-key
        // 目标方法


        Cart cart = cartService.getCart();

        return new ReqResult(cart);
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

    /**
     * 选中商品
     * @author: Administrator
     * @date: 2021-03-05 19:57:12
     * @param skuId: 商品skuId
     * @param check: 是否被选中 1: 选中 0: 未选中
     * @return: com.zmm.common.base.model.ReqResult
     **/
    @GetMapping("/checkItem")
    public ReqResult checkItem(@RequestParam("skuId")Long skuId,@RequestParam("check") Integer check){
        cartService.checkItem(skuId,check);
        // 重定向购物车列表
        return new ReqResult(ResultCode.SUCCESS);
    }


    @GetMapping("/countItem")
    public ReqResult countItem(@RequestParam("skuId")Long skuId,@RequestParam("number") Integer number){
        cartService.changeItemCount(skuId,number);
        // 重定向购物车列表
        return new ReqResult(ResultCode.SUCCESS);
    }

    @GetMapping("/deleteItem")
    public ReqResult deleteCartItem(@RequestParam("skuId")Long skuId){
        cartService.deleteCartItem(skuId);
        // 重定向购物车列表
        return new ReqResult(ResultCode.SUCCESS);
    }
}
