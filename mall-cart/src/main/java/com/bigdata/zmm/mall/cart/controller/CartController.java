package com.bigdata.zmm.mall.cart.controller;

import com.bigdata.zmm.mall.cart.interceptor.CartInterceptor;
import com.bigdata.zmm.mall.cart.to.UserInfoTo;
import com.zmm.common.base.model.ReqResult;
import com.zmm.common.base.model.ResultCode;
import com.zmm.common.constant.StringConstant;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @Description:
 * @Name CartController
 * @Author Administrator
 * @Date By 2021-02-28 23:07:21
 */
@RestController
public class CartController {

    /**
     *
     * 浏览器有一个 cookie user-key:标识用户身份 一个月后过期 如果第一次访问(京东)购物车功能 都会给一个临时的用户身份;
     * 浏览器以后保存 每次都会带上这个cookie
     * 登陆: session 有
     * 没有登陆 :按照cookie里面带来的 user-key
     * 第一次: 如果没有临时用户,需要创建一个临时用户
     * @description:
     * @author: Administrator
     * @date: 2021-02-28 23:11:54
     * @return: com.zmm.common.base.model.ReqResult
     **/
    @GetMapping("/cart")
    public ReqResult cartListPage(){

        // 1. 快速得到用户用户信息 id:user-key
        // 目标方法
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();

        return new ReqResult(ResultCode.SUCCESS);
    }
}
