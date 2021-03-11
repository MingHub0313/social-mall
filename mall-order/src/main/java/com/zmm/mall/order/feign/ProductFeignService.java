package com.zmm.mall.order.feign;

import com.zmm.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Description:
 * @Name ProductFeignService
 * @Author Administrator
 * @Date By 2021-03-11 22:04:36
 */
@FeignClient(name = "mall-product",url = "127.0.0.1:0000")
public interface ProductFeignService {

    @GetMapping("/product/spuinfo/skuId/{id}")
    R getSpuInfoBySkuId(@PathVariable("id") Long skuId);
}
