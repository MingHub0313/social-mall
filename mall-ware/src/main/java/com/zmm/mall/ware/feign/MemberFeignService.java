package com.zmm.mall.ware.feign;

import com.zmm.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Name MemberFeignService
 * @Author Administrator
 * @Date By 2021-03-09 21:34:08
 */
@FeignClient(name = "mall-member",url = "127.0.0.1:88/api")
public interface MemberFeignService {


    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    R addrInfo(@PathVariable("id") Long id);
}
