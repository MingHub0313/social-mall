package com.zmm.mall.order.feign;

import com.zmm.mall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Description:
 * @Name MemberFeignService
 * @Author Administrator
 * @Date By 2021-03-08 22:13:46
 */
@FeignClient(name = "mall-member",url = "127.0.0.1:0000")
public interface MemberFeignService {


    /**
     * 远程调用获取该用户下的全部地址列表
     * @description:
     * @author: Administrator
     * @date: 2021-03-08 22:20:09
     * @param memberId: 
     * @return: java.util.List<com.zmm.mall.order.vo.MemberAddressVo>
     **/
    @GetMapping("/member/{memberId}/address")
    List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);
}
