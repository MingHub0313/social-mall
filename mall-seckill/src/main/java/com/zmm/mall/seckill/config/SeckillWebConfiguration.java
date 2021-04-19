package com.zmm.mall.seckill.config;

import com.zmm.mall.seckill.interceptor.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author 900045
 * @description:
 * @name SeckillWebConfiguration
 * @date By 2021-04-19 17:04:56
 */
@Configuration
public class SeckillWebConfiguration implements WebMvcConfigurer {

	@Resource
	private LoginUserInterceptor loginUserInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
	}
}
