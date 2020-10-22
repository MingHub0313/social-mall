package com.zmm.mall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
/**
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
@EnableFeignClients(basePackages = "com.zmm.mall.member.feign")
@SpringBootApplication
@EnableDiscoveryClient
public class MallMemberApplication {

	/**
	 * 1.想要远程调用别的服务
	 * 	1).引入 open-feign
	 * 	2).编写一个接口 , 告诉SpringCloud 这个接口需要调用远程服务
	 * 		1).声明接口的每一个方法都是调用哪个远程服务的哪个请求
	 * 	3).开启远程调用功能
	 */

	public static void main(String[] args) {
		SpringApplication.run(MallMemberApplication.class, args);
	}

}
