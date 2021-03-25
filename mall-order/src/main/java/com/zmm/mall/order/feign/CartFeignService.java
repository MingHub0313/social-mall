package com.zmm.mall.order.feign;

import com.zmm.mall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @Description:
 * @Name CartFeignService
 * @Author Administrator
 * @Date By 2021-03-08 22:42:46
 */
@FeignClient(name = "mall-cart",url = "127.0.0.1:88/api")
public interface CartFeignService {


    /**
     * 远程调用购物车系统进行查询
     * @description:
     * @author: Administrator
     * @date: 2021-03-08 22:43:57
     * @return: java.util.List<com.zmm.mall.order.vo.OrderItemVo>
     **/
    @GetMapping("/find/cartItems/user'")
    List<OrderItemVo> getCartItemByUser();
}
