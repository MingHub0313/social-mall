package com.bigdata.zmm.mall.cart.service;

import com.bigdata.zmm.mall.cart.vo.CartItem;

import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Name CartService
 * @Author Administrator
 * @Date By 2021-02-28 23:01:24
 */
public interface CartService {
    /**
     * 添加商品到 购物车
     * @author: Administrator
     * @date: 2021-03-03 22:05:28
     * @param skuId:
     * @param number:
     * @return: com.bigdata.zmm.mall.cart.vo.CartItem
     **/
    CartItem addToCart(Long skuId, Integer number) throws ExecutionException, InterruptedException;
}
