package com.zmm.mall.ware;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
/**
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 15:20:08
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class MallWareApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallWareApplication.class, args);
	}

}
