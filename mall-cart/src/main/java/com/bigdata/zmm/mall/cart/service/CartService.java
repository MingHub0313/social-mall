package com.bigdata.zmm.mall.cart.service;

import com.bigdata.zmm.mall.cart.vo.Cart;
import com.bigdata.zmm.mall.cart.vo.CartItem;
import com.zmm.common.utils.redis.key.RedisKey;

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

    /**
     * 获取当前用户的购物车信息
     * @author: Administrator
     * @date: 2021-03-04 20:22:42
     * @return: com.bigdata.zmm.mall.cart.vo.Cart
     **/
    Cart getCart() throws ExecutionException, InterruptedException;

    /**
     * 根据 用户的购物车 key 清空购物车
     * @author: Administrator
     * @date: 2021-03-05 19:48:54
     * @param redisKey: 
     * @return: void
     **/
    void clearCart(RedisKey redisKey);

    /**
     * 勾选购物项
     * @author: Administrator
     * @date: 2021-03-05 19:59:32
     * @param skuId: 
     * @param check: 
     * @return: void
     **/
    void checkItem(Long skuId, Integer check);

    /**
     * 改变购物项的数量
     * @author: Administrator
     * @date: 2021-03-05 20:31:10
     * @param skuId: 
     * @param number: 
     * @return: void
     **/
    void changeItemCount(Long skuId, Integer number);

    /**
     * 删除购物项
     * @author: Administrator
     * @date: 2021-03-05 20:43:43
     * @param skuId: 
     * @return: void
     **/
    void deleteCartItem(Long skuId);
}
