package com.zmm.mall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:14
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MallCouponApplication {

	/**
	 * 1.如何使用 Nacos 作为配置中心统一管理配置
	 * 	1).引入依赖
	 *			<dependency>
	 *             <groupId>com.alibaba.cloud</groupId>
	 *             <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
	 *         </dependency>
	 *   2).创建一个 bootstrap.properties
	 *   	配置两项
	 *   		spring.application.name=mall-coupon
	 *
	 * 			spring.cloud.nacos.config.server-addr=111.229.118.62:8848
	 * 	3).需要给配置中心默认添加一个叫 数据集 (Data Id)项目名.properties
	 *
	 * 	4).给配置文件添加任何配置信息
	 *
	 * 	5).动态获取配置
	 * 		结合一下两个注解
	 * 		@RefreshScope
	 *      @Value("${配置项的key}")
	 *  6).配置中心有的 优先使用配置中心的 value
	 *
	 *
	 * 2.细节 :
	 * 	1).命名空间:用于进行租户粒度的配置隔离
	 * 		默认 public (保留空间) :默认新增的所有配置都在 public 空间
	 * 			1. 开发 , 测试 ,生产 有很多环境配置 : 利用命名空间来做环境隔离
	 * 				注意: 需要在 bootstrap.properties 配置 spring.cloud.nacos.config.namespace=58758cbc-012c-4f1a-a662-213de51a0c5f
	 * 			2.每个微服务之间互相隔离配置,每一个微服务都创建自己的命名空间,只加载自己命名空间下的所有配置
	 * 	2).配置集 : 所有配置的集合
	 * 	3).配置集ID : 类似于配置文件名 ---->Data ID
	 * 	4).配置分组 :
	 * 		默认所有的配置集都属于 : DEFAULT_GROUP
	 *	每个微服务创建自己的命名空间,使用配置分组区分环境 dev test prop
	 * 3.同时加载多个配置集
	 * 	1).微服务任何配置信息,任何配置文件都可以放在配置中心中
	 * 	2).只需要在 bootstrap.properties 说明加载配置中心中哪些配置文件即可
	 * 	3).@Value @ConfigurationProperties ...以前springboot任何方法从配置文件中取值,都能使用
	 * 	配置中心有的优先使用配置中心的
	 */

	public static void main(String[] args) {
		SpringApplication.run(MallCouponApplication.class, args);
	}

}
