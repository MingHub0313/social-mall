package com.zmm.mall.seckill;

import com.zmm.common.config.BaseConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Component;

/**
 * @EnableRabbit 监听时需要加 如果单单是发送可以不用添加
 * @Name MallSeckillApplication
 * @Author 900045
 * @Created by 2020/8/26
 */
@EnableRedisHttpSession
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class MallSeckillApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallSeckillApplication.class, args);
	}

	@Component
	public class ServiceConfigure extends BaseConfigure {
	}
}
