package com.zmm.mall.order.feign;

import com.zmm.common.utils.R;
import com.zmm.mall.order.vo.CategoryEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Description:
 * @Name ProductFeignService
 * @Author Administrator
 * @Date By 2021-03-11 22:04:36
 */
@FeignClient(name = "mall-product",url = "127.0.0.1:88/api")
public interface ProductFeignService {

    @GetMapping("/product/spuinfo/skuId/{id}")
    R getSpuInfoBySkuId(@PathVariable("id") Long skuId);

    @RequestMapping("/product/category/list/tree")
    List<CategoryEntity> list();
}
