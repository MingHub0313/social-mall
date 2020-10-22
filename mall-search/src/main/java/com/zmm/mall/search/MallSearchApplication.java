package com.zmm.mall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Name MallSearchApplication
 * @Author 900045
 * @Created by 2020/8/26
 */

@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MallSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallSearchApplication.class, args);
	}

}
