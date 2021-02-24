package com.zmm.mall.auth.feign;

import com.zmm.common.base.model.ReqResult;
import com.zmm.mall.auth.vo.RegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Name MemberFeignService
 * @Author Administrator
 * @Date By 2021-02-24 21:41:01
 */
@FeignClient(value = "mall-member",url = "localhost:8000")
public interface MemberFeignService {

    @RequestMapping("member/member/register")
    ReqResult register(@RequestBody RegisterVo vo);
}
