package com.zmm.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author 900045
 * @description:
 * @name MallSessionConfig
 * @date By 2021-02-25 17:30:10
 */
@EnableRedisHttpSession
@Configuration
public class MallSessionConfig {

	/**
	 * 1.spring-session-依赖
	 * 2.spring-session 配置
	 * 3.引入 LoginInterceptor WebMvcConfigure
	 * 4.开始@EnableRedisHttpSession
	 * @return
	 */

	@Bean
	public CookieSerializer cookieSerializer() {
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
		serializer.setCookieName("MALL_SESSION");
		serializer.setCookiePath("/");
		//serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$")
		serializer.setDomainNamePattern("mall.com");
		return serializer;
	}

	@Bean
	public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}
}
