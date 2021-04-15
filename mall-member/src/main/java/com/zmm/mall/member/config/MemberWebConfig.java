package com.zmm.mall.member.config;

import com.zmm.mall.member.interceptor.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author 900045
 * @description:
 * @name MemberWebConfig
 * @date By 2021-04-15 15:57:05
 */
@Configuration
public class MemberWebConfig implements WebMvcConfigurer {

	@Resource
	private LoginUserInterceptor loginUserInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
	}
}
