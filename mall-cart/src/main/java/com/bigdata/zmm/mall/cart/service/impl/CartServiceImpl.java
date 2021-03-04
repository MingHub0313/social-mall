package com.bigdata.zmm.mall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bigdata.zmm.mall.cart.feign.ProductFeignService;
import com.bigdata.zmm.mall.cart.interceptor.CartInterceptor;
import com.bigdata.zmm.mall.cart.service.CartService;
import com.bigdata.zmm.mall.cart.to.UserInfoTo;
import com.bigdata.zmm.mall.cart.vo.Cart;
import com.bigdata.zmm.mall.cart.vo.CartItem;
import com.bigdata.zmm.mall.cart.vo.SkuInfoVo;
import com.zmm.common.utils.R;
import com.zmm.common.utils.StringUtil;
import com.zmm.common.utils.redis.RedisUtil;
import com.zmm.common.utils.redis.key.CartKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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

    /*@Resource
    private StringRedisTemplate redisTemplate;*/

    @Resource
    private ProductFeignService productFeignService;

    @Resource
    private ThreadPoolExecutor executor;



    @Override
    public CartItem addToCart(Long skuId, Integer number) throws ExecutionException, InterruptedException {

        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();

        // advance.1 判断购物车中是否存在
        String string = redisUtil.hashGet(getCartKey(userInfoTo), skuId);
        /**
         * 1.判断购物车中是否存在
         *      存在: 则修改数量
         *      不存在:则添加购物项
         */
        if (StringUtil.isNotBlank(string)){
            // 购物车 有此商品 修改商品数量
            CartItem cartItem = JSON.parseObject(string, CartItem.class);
            cartItem.setCount(cartItem.getCount() + number);
            // redisKey  field value
            redisUtil.hash(getCartKey(userInfoTo), skuId,cartItem);
            return cartItem;
        } else {
            // 购物车无此商品 开始往 redis 中 添加购物项(cartItem)

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
            }, executor);

            // 3.远程查询 sku的 组合信息
            CompletableFuture<Void> futureAttrValues = CompletableFuture.runAsync(() -> {
                R attrValues = productFeignService.getSkuSaleAttrValues(skuId);
                List<String> list = attrValues.getData("list", new TypeReference<List<String>>() {
                });
                cartItem.setSkuAttr(list);
            }, executor);
            // 4.阻塞线程
            CompletableFuture.allOf(futureSkuInfo, futureAttrValues).get();
            String jsonString = JSON.toJSONString(cartItem);
            // 可能会出现问题 一定要等到 上面两个异步执行完成后
            // 操作 Redis 的方案 一.
            cartOps.put(skuId.toString(), jsonString);
            // 操作 Redis 的方案 二.
            redisUtil.hash(getCartKey(userInfoTo), skuId, cartItem);
            return cartItem;
        }
    }

    /**
     * 统一操作 redis
     * @description:
     * @author: Administrator
     * @date: 2021-03-03 22:01:10
     * @return: org.springframework.data.redis.core.BoundHashOperations<java.lang.String,java.lang.Object,java.lang.Object>
     **/
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        CartKey cartKey = getCartKey(userInfoTo);
        BoundHashOperations<String, Object, Object> operations = null;
        return operations;
    }

    /**
     * 获取 购物车的key
     * @author: Administrator
     * @date: 2021-03-03 22:09:36
     * @return: com.zmm.common.utils.redis.key.CartKey
     **/
    private CartKey getCartKey(UserInfoTo userInfoTo) {
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

    @Override
    public Cart getCart() {
        Cart cart = new Cart();
        // 需要区分登陆还是未登陆
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        CartKey cartKey = getCartKey(userInfoTo);
        if (ObjectUtils.isEmpty(userInfoTo)){
            // 未登陆 临时用户
        } else {
            // 已登陆
            List<CartItem> cartItemList = redisUtil.hashValues(cartKey);
            cart.setCartItems(cartItemList);
        }
        return null;
    }
}
