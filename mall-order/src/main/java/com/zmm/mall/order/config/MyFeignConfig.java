package com.zmm.mall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @Name MyFeignConfig
 * @Author Administrator
 * @Date By 2021-03-09 19:51:23
 */
@Configuration
public class MyFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){
        // 是从构造器(SynchronousMethodHandler)中获得的 List<RequestInterceptor>
        // 由 MethodHandler 创建 SynchronousMethodHandler

        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                System.out.println("feign 远程之前先进行RequestInterceptor.apply 方法");
                // 1.RequestContextHolder (是从 ThreadLocal 中获取的 异步请求 会造成  ThreadLocal 不是同一个 ) 获取刚进来的请求
                ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
                if (ObjectUtils.isEmpty(servletRequestAttributes)){
                    return;
                }
                // 2.获取当前请求(存在请求头信息) 此处可能为 null --> java.lang.NullPointException
                HttpServletRequest request = servletRequestAttributes.getRequest();
                if (ObjectUtils.isEmpty(request)){
                    return;
                }
                // 3.同步1请求数据 cookie
                String cookie = request.getHeader("Cookie");
                // 4.给新的请求(java方式的请求)同步之前的请求数据
                requestTemplate.header("Cookie",cookie);
            }
        };
    }
}
