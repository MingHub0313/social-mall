package com.zmm.mall.order.feign;

import com.zmm.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description:
 * @Name WareFeignService
 * @Author Administrator
 * @Date By 2021-03-09 21:13:52
 */
@FeignClient(name = "mall-ware",url = "127.0.0.1:0000")
public interface WareFeignService {

    /**
     * 根据 skuId 集合 获取 是否有库存
     * @description:
     * @author: Administrator
     * @date: 2021-03-09 21:16:02
     * @param skuIds: 
     * @return: com.zmm.common.utils.R
     **/
    @PostMapping("/ware/waresku/has/stock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);


    @GetMapping("/ware/wareinfo/fare")
    R getFare(@RequestParam("addrId") Long addrId);
}
