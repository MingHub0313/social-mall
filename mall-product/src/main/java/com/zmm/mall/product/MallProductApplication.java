package com.zmm.mall.product;

import com.zmm.mall.product.util.ProductScheduleUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 1.整合  Mybatis-plus
 * 			1).导入依赖
 * 		           <dependency>
 *             			<groupId>com.baomidou</groupId>
 *             			<artifactId>mybatis-plus-boot-starter</artifactId>
 *             			<version>3.3.2</version>
 *         			</dependency>
 *         	2).配置
 *         		 1.配置数据源:
 *         		 	1).导入数据库驱动
 *         		  	2).在 application.yml 中配置数据源相关信息
 *         		 2.配置 Mybatis-plus
 *         		 	1).使用 MapperScan
 *         		 	2).告诉 Mybatis-plus slq映射位置
 * 2.逻辑删除
 * 		1).配置全局的逻辑删除规则 (可省略)
 * 		2).配置逻辑删除的组件 Bean (可省略)
 * 		3).实体类字段上加上@TableLogic注解
 *
 * 	3.JSR303
 * 		1).给需要校验的字段 添加注解  [javax.validation.constraints] 并定义自己的 message 提示
 * 	 	2).开启校验功能 @Valid
 * 	 		效果: 校验错误以后会有默认的响应
 * 	 	3).给校验的bean 后 紧跟一个BindingResult 就可以获取校验的结果
 * 	 	4).分组校验
 * 	 			1.@NotBlank(message = "品牌名称不能为空",groups = {AddGroup.class,UpdateGroup.class})
 * 	 				给校验注解标注什么情况需要进行校验
 * 	 		  	2. @Validated({AddGroup.class})
 * 	 		  			注意 : 如果分组存在 没有分组的字段是不校验的
 *		5.自定义方式校验
 *			1.编写一个自定义的校验注解
 *		 	2.编写一个自定义的校验器 ConstraintValidator
 *		 	3.关联自定义的校验器和自定义的校验注解
 *
 * 	4.统一的异常处理 @ControllerAdvice
 * 		1).编写异常处理类,使用 @ControllerAdvice
 * 		2).使用 @ExceptionHandler 标注方法可以处理的异常
 *
 *  5.模板引擎
 *    1). 引入  thymeleaf-starter : 关闭缓存
 *    2). 静态资源都放在 static 文件夹下 就可以按照路径直接访问
 *    3). 页面都放在 templates 下 ,直接访问
 *    	Spring Boot 访问项目的时候,默认会找index
 *   4). 页面修改实时更新 不需要重启服务器
 *   	1). 引入 dev-tools
 *   	2). 修改完页面 使用 ctrl + shift + F9 重新自动编译页面  如果是代码配置 建议还是重启
 *
 *  6.整合redis
 *  	1). 引入 data-redis-starter
 *  	2). 简单配置redis的host 等信息
 *  	3). 使用 springboot 自动配置好的 RedisTemplate  stringRedisTemplate 来操作redis
 *
 *  7.整合 redisSon 作为分布式锁等功能框架
 *  	1). 引入 依赖
 *  		<dependency>
 *             <groupId>org.redisson</groupId>
 *             <artifactId>redisson</artifactId>
 *             <version>3.12.0</version>
 *         </dependency>
 *      2). 配置参数
 *
 *  8.整合 SpringCache 简化缓存开发
 *  	1).引入依赖  spring-boot-starter-cache、spring-boot-starter-data-redis
 *  		<dependency>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-starter-cache</artifactId>
 *         </dependency>
 *
 *      2).写配置
 *      	(1).自动配置哪些数据
 *      			CacheAutoConfiguration 会导入 RedisCacheConfiguration.
 *      			自动配置好缓存管理器 RedisCacheManager
 *      	(2).配置使用 redis作为缓存
 *      			spring.cache.type=redis
 *      	(3).测试使用缓存
 *                	@Cacheable: Triggers cache population.   -- 触发将数据保存到缓存的操作
 *
 * 					@CacheEvict: Triggers cache eviction.	-- 将数据从缓存中删除的操作
 *
 * 					@CachePut: Updates the cache without interfering with the method execution.		-- 不影响方法执行更新缓存
 *
 * 					@Caching: Regroups multiple cache operations to be applied on a method.			-- 组合以上多个操作
 *
 * 					@CacheConfig: Shares some common cache-related settings at class-level.			-- 在类级别共享缓存的相同配置
 * 				1).开启缓存功能 @EnableCaching
 * 				2).只需要使用注解就能完成缓存操作
 * 			(4).原理:
 * 				CacheAutoConfiguration 中导入了 RedisCacheConfiguration 自动配置了 RedisCacheManager
 * 				初始化所有的缓存 -> 每个缓存决定使用什么配置
 * 				-> 如果 RedisCacheConfiguration有就用已有的 没有就用默认配置 ->想改缓存的配置 只需要给容器中放一个 RedisCacheConfiguration即可
 * 				-> 就会应用到当前 RedisCacheManager 管理的所有缓存分区中
 *
 *				CacheManager(RedisCacheManager) --> Cache(RedisCache) --> Cache负责缓存的读写
 *
 * 		3).Spring-Cache的不足:
 * 			(1).读模式:
 * 				缓存穿透 -- 查询一个永不存在的数据,将其放行 查询数据库.						解决方案:缓存空数据 cache-null-values = true
 * 				缓存击穿 -- 大量并发进行同时查询一个正好过期的数据. 将其放行 查询数据库		解决方案:加锁 :? 默认是不加锁 sync =true(加锁)
 * 				缓存雪崩 -- 大量的 key 同时过期.											解决方案:加随机时间 spring.cache.redis.time-to-live=300000
 * 			(2).写模式: [缓存与数据库一致]
 * 				1.读写加锁. 适用于读多写少
 * 				2.引入中间件 Canal 感知到Mysql的更新去操作缓存
 * 				3.读多写多 直接去数据库查询
 *
 * 		4).总结:
 * 			常规数据(读多写少,即时性,一致性要求不高的数据) : 完全可以使用 Spring-Cache 写模式 只要缓存的数据有过期时间就足够了
 * 			特殊数据 : 特殊处理
 */
/**
 * @Name MallProductApplication
 * @Author 900045
 * @Created by 2020/8/26
 */
@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.zmm.mall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.zmm.mall.product.dao")
@SpringBootApplication
@EnableScheduling
public class MallProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallProductApplication.class, args);
	}
	
	@Bean
	public ProductScheduleUtils productScheduleUtils(){
		return new ProductScheduleUtils();
	}

}
