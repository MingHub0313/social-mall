package com.zmm.mall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @Name MallCorsConfiguration
 * @Author 900045
 * @Created by 2020/8/24 0024
 */
@Configuration
public class MallCorsConfiguration {

	@Bean
	public CorsWebFilter corsWebFilter(){

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		//1.配置跨域
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.setAllowCredentials(true);
		source.registerCorsConfiguration("/**",corsConfiguration);
		return new CorsWebFilter(source);
	}
}
