package com.bigdata.zmm.mall.cart.config;

import com.bigdata.zmm.mall.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description:
 * @Name MallWebConfig
 * @Author Administrator
 * @Date By 2021-02-28 23:39:18
 */
@Configuration
public class MallWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截 什么请求 ** : 所有请求
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
    }
}
