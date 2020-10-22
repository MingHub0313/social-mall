package com.zmm.mall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @Name MyRedisSonConfig
 * @Author 900045
 * @Created by 2020/10/19 0019
 */
@Configuration
public class MyRedisSonConfig {

	/**
	 * 所有对  RedisSon 的使用 都是通过 RedissonClient 对象
	 * @return
	 * @throws IOException
	 */
	@Bean(destroyMethod = "shutdown")
	public RedissonClient redisSon() throws IOException {
		Config config = new Config();
		config.useSingleServer().setAddress("redis://47.111.112.220:6379").setPassword("19961001zmm");
		return Redisson.create(config);
	}

}
