package com.zmm.mall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 900045
 * @description:
 * @name MyThreadConfig
 * @date By 2021-02-23 17:20:38
 * 
 * @EnableConfigurationProperties(ThreadPoolConfigProperties.class)
 */
@Configuration
public class MyThreadConfig {
	
	@Bean
	public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties properties){
		return new ThreadPoolExecutor(
				properties.getCoreSize(),
				properties.getMaxSize(),
				properties.getKeepAliveTime(),
				TimeUnit.SECONDS,
				new LinkedBlockingDeque<>(100000),
				Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.AbortPolicy());
	}
}
