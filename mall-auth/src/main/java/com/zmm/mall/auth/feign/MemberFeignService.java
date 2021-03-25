package com.zmm.mall.auth.feign;

import com.zmm.common.base.model.ReqResult;
import com.zmm.mall.auth.vo.LoginVo;
import com.zmm.mall.auth.vo.RegisterVo;
import com.zmm.mall.auth.vo.SocialUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Name MemberFeignService
 * @Author Administrator
 * @Date By 2021-02-24 21:41:01
 */
@FeignClient(value = "mall-member",url = "127.0.0.1:88/api")
public interface MemberFeignService {

    
    /**
     * 远程调用 mall-member 模块 进行 注册
     * @author: 900045
     * @date: 2021-02-25 09:15:37
     * @throws 
     * @param vo: 
     * @return: com.zmm.common.base.model.ReqResult
     **/
    @RequestMapping("member/member/register")
    ReqResult register(@RequestBody RegisterVo vo);


    /**
     * 用户登录
     * @author: 900045
     * @date: 2021-02-25 10:27:55
     * @throws 
     * @param vo: 
     * @return: com.zmm.common.base.model.ReqResult
     **/
    @RequestMapping("member/member/login")
    ReqResult login(@RequestBody LoginVo vo);

    /**
     * 社交登录
     * @author: 900045
     * @date: 2021-02-25 15:07:23
     * @throws 
     * @param socialUser: 
     * @return: com.zmm.common.base.model.ReqResult
     **/
    @RequestMapping("member/member/oauth2/login")
    ReqResult login(@RequestBody SocialUser socialUser);
    
}
