package com.bigdata.zmm.mall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bigdata.zmm.mall.cart.feign.ProductFeignService;
import com.bigdata.zmm.mall.cart.interceptor.CartInterceptor;
import com.bigdata.zmm.mall.cart.service.CartService;
import com.bigdata.zmm.mall.cart.to.UserInfoTo;
import com.bigdata.zmm.mall.cart.vo.CartItem;
import com.bigdata.zmm.mall.cart.vo.SkuInfoVo;
import com.zmm.common.utils.R;
import com.zmm.common.utils.redis.RedisUtil;
import com.zmm.common.utils.redis.key.CartKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description:
 * @Name CartServiceImpl
 * @Author Administrator
 * @Date By 2021-02-28 23:01:49
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource
    private ProductFeignService productFeignService;

    @Resource
    private ThreadPoolExecutor executor;



    @Override
    public CartItem addToCart(Long skuId, Integer number) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = new CartItem();
        // 1.根据 skuId 远程查询当前商品的信息
        CompletableFuture<Void> futureSkuInfo = CompletableFuture.runAsync(() -> {
            R skuInfo = productFeignService.getSkuInfo(skuId);
            SkuInfoVo skuInfoData = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
            });
            // 2.商品添加到购物车 (CartItem)
            cartItem.setCheck(true);
            cartItem.setCount(number);
            cartItem.setImage(skuInfoData.getSkuDefaultImg());
            cartItem.setTitle(skuInfoData.getSkuTitle());
            cartItem.setSkuId(skuId);
            cartItem.setPrice(skuInfoData.getPrice());
        },executor);

        // 3.远程查询 sku的 组合信息
        CompletableFuture<Void> futureAttrValues = CompletableFuture.runAsync(() -> {
            R attrValues = productFeignService.getSkuSaleAttrValues(skuId);
            List<String> list = attrValues.getData("list", new TypeReference<List<String>>() {
            });
            cartItem.setSkuAttr(list);
        }, executor);
        // 4.阻塞线程
        CompletableFuture.allOf(futureSkuInfo,futureAttrValues).get();
        String jsonString = JSON.toJSONString(cartItem);
        // 可能会出现问题 一定要等到 上面两个异步执行完成后
        // 操作 Redis 的方案 一.
        cartOps.put(skuId.toString(),jsonString);
        // 操作 Redis 的方案 二.
        redisUtil.hash(getCartKey(),skuId);
        return cartItem;
    }

    /**
     * 统一操作 redis
     * @description:
     * @author: Administrator
     * @date: 2021-03-03 22:01:10
     * @return: org.springframework.data.redis.core.BoundHashOperations<java.lang.String,java.lang.Object,java.lang.Object>
     **/
    private BoundHashOperations<String, Object, Object> getCartOps() {
        CartKey cartKey = getCartKey();
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey.getKey());
        return operations;
    }

    /**
     * 获取 购物车的key
     * @author: Administrator
     * @date: 2021-03-03 22:09:36
     * @return: com.zmm.common.utils.redis.key.CartKey
     **/
    private CartKey getCartKey() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        // 1.判断是否登陆
        CartKey cartKey = CartKey.MALL_CART;
        if (userInfoTo.getUserId() != null){
            // 已登陆 真正的用户
            cartKey.setSuffix(userInfoTo.getUserId());
        } else {
            // 未登陆 临时用户
            cartKey.setSuffix(userInfoTo.getUserKey());
        }
        return cartKey;
    }
}
