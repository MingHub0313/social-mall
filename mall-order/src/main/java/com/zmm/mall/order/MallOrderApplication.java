package com.zmm.mall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Name MallOrderApplication
 * @Author 900045
 * @Created by 2020/8/26
 */
@EnableRabbit
@SpringBootApplication
public class MallOrderApplication {
	/**
	 * 使用 RabbitMQ
	 * 1、引入 amqp 场景 RabbitAutoConfiguration就会自动生效
	 * 2、给容器中自动配置了
	 * 		RabbitTemplate:
	 * 		AmqpAdmin:
	 * 		CachingConnectionFactory:
	 * 		RabbitMessagingTemplate:
	 *
	 * 		所有的属性都是在:RabbitProperties
	 * 3、给配置文件中配置 spring.rabbitmq 信息
	 * 4、配置 @EnableRabbit 开启 amqp
	 * 5、监听消息:
	 * 		@RabbitListener 必须要开启 amqp  标注在类或方法上 (监听很多的队列)
	 * 		@RabbitHandler: 标注在方法上 (重载区分不同的消息)
	 *
	 */

	public static void main(String[] args) {
		SpringApplication.run(MallOrderApplication.class, args);
	}

}
