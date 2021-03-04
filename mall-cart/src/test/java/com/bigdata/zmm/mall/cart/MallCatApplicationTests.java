package com.bigdata.zmm.mall.cart;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bigdata.zmm.mall.cart.feign.ProductFeignService;
import com.bigdata.zmm.mall.cart.vo.Cart;
import com.bigdata.zmm.mall.cart.vo.CartItem;
import com.bigdata.zmm.mall.cart.vo.SkuInfoVo;
import com.google.gson.Gson;
import com.zmm.common.utils.R;
import com.zmm.common.utils.StringUtil;
import com.zmm.common.utils.redis.RedisUtil;
import com.zmm.common.utils.redis.key.CartKey;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MallCatApplicationTests {

    @Resource
    private ProductFeignService productFeignService;

    @Resource
    private ThreadPoolExecutor executor;

    @Resource
    private RedisUtil redisUtil;


    @Resource
    private Gson gson;

    @Test
    public void hashValues(){
        Cart cart = new Cart();
        CartKey cartKey = CartKey.MALL_CART;
        List cartItemList = redisUtil.hashValues(cartKey.setSuffix(3L));
        List<CartItem> cartItems = JSON.parseArray(JSON.toJSONString(cartItemList), CartItem.class);
        for (CartItem cartItem :cartItems){
            log.info("-->{}",cartItem
            );
        }

    }
    @Test
    public void addCartRedis() throws ExecutionException, InterruptedException {
        CartKey cartKey = CartKey.MALL_CART;

        Long skuId = 14L;

        Integer number = 1;

        Long userId = 3L;

        String string = redisUtil.hashGet(cartKey.setSuffix(userId), skuId.toString());

        if (StringUtil.isNotBlank(string)){
            CartItem cartItem = JSON.parseObject(string, CartItem.class);
            cartItem.setCount(cartItem.getCount() + number);
            // redisKey  field value
            redisUtil.hash(cartKey.setSuffix(userId), skuId,cartItem);
            return;
        }

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
        //cartOps.put(skuId.toString(), jsonString);
        // 操作 Redis 的方案 二.

        redisUtil.hash(cartKey.setSuffix(userId), skuId, cartItem);
    }

}
