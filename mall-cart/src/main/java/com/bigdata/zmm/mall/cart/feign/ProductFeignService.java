package com.bigdata.zmm.mall.cart.feign;

import com.zmm.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Name ProductFeignService
 * @Author Administrator
 * @Date By 2021-03-03 21:02:36
 */
@FeignClient(value = "mall-product",url = "127.0.0.1:88/api")
public interface ProductFeignService {

    /**
     * 远程查询商品的详细信息
     * @author: Administrator
     * @date: 2021-03-03 21:52:38
     * @param skuId: 
     * @return: com.zmm.common.utils.R
     **/
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 远程查询 sku的 组合信息
     * @author: Administrator
     * @date: 2021-03-03 21:52:55
     * @param skuId: 
     * @return: com.zmm.common.utils.R
     **/
    @RequestMapping("/product/skusaleattrvalue/find/sku/attr/values/{skuId}")
    R getSkuSaleAttrValues( @PathVariable("skuId") Long skuId);



    /**
     * 远程查询商品的真实价格
     * @description:
     * @author: Administrator
     * @date: 2021-03-08 22:40:55
     * @param skuId: 
     * @return: java.math.BigDecimal
     **/
    @GetMapping("/product/skuinfo/{skuId}/price")
    R getPrice(@PathVariable("skuId") Long skuId);
}
