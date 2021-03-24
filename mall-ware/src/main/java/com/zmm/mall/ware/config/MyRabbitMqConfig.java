package com.zmm.mall.ware.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 900045
 * @description:
 * @name MyRabbitMqConfig
 * @date By 2021-03-24 10:51:32
 */
@Configuration
public class MyRabbitMqConfig {

	/**
	 * 使用 JSON 序列化机制 进行消息转换
	 * @return
	 */
	@Bean
	public MessageConverter messageConverter(){
		return new Jackson2JsonMessageConverter();
	}
	
	
	@Bean
	public Exchange stockEventExchange(){
		//String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
		return new TopicExchange("stock-event-exchange",true,false);
	}

	@Bean
	public Queue stockReleaseStockQueue(){
		//String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
		return new Queue("stock.release.stock.queue",true,false,false);
	}

	@Bean
	public Queue stockDelayQueue(){
		Map<String ,Object> arguments = new HashMap<>(3);
		// 死信路由
		arguments.put("x-dead-letter-exchange","stock-event-exchange");
		// 用的路由键
		arguments.put("x-dead-letter-routing-key","stock.release");
		// 过期时间 单位 毫秒 120000 = 2分钟
		arguments.put("x-message-ttl",120000);
		return new Queue("stock.delay.queue",true,false,false,arguments);
	}

	@Bean
	public Binding stockReleaseBinding(){
		//String destination, Binding.DestinationType destinationType, String exchange, String routingKey, Map<String, Object> arguments
		return new Binding("stock.release.stock.queue",
				Binding.DestinationType.QUEUE,
				"stock-event-exchange",
				"stock.release.#",
				null);
	}

	@Bean
	public Binding stockLockedBinding(){
		//String destination, Binding.DestinationType destinationType, String exchange, String routingKey, Map<String, Object> arguments
		return new Binding("stock.delay.queue",
				Binding.DestinationType.QUEUE,
				"stock-event-exchange",
				"stock.locked",
				null);
	}
	
}
