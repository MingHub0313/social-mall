package com.zmm.mall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * EnableRedisHttpSession 整合 redis 作为 Session 存储
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2021-02-24 11:33:14
 */
@EnableRedisHttpSession
@EnableFeignClients(basePackages="com.zmm.mall.auth.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class MallAuthApplication {

	/**
	 * SpringSession 核心原理
	 * 	1).@EnableRedisHttpSession
	 * 		引入 RedisHttpSessionConfiguration 配置
	 * 		给 容器中添加的组件 
	 * 			(1).SessionRepository ---》》》【RedisOperationsSessionRepository】 -----》》》redis 操作 session (CURD封装类)
	 * 			(2).SessionRepositoryFilter: session存储过滤器 (相当于 Filter) 每个请求过来都必须进过 filter
	 * 				1.创建的时候通过构造器 自动从容器中获取到 SessionRepository	
	 * 				2.原生的 request , response	都被包装成 SessionRepositoryRequestWrapper 和 SessionRepositoryResponseWrapper
	 * 				3.以后获取 session , 即 不是通过 这种方式 HTTPSession session = request.getSession();
	 * 				4.而是 通过 wrappedRequest.getSession(); ==> 其实是从 SessionRepository 中获取到的.
	 * 		
	 * 		装饰者模式:
	 * 		自动续期:
	 * @param args
	 */

	public static void main(String[] args) {
		SpringApplication.run(MallAuthApplication.class, args);
	}
	
	

}
