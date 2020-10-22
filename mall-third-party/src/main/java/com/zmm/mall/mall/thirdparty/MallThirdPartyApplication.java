package com.zmm.mall.mall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
/**
 * @Name MallThirdPartyApplication
 * @Author 900045
 * @Created by 2020/8/28 0028
 */
@EnableDiscoveryClient
@SpringBootApplication
public class MallThirdPartyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallThirdPartyApplication.class, args);
	}

}
