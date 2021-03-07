package com.zmm.mall.order;

import com.zmm.mall.order.entity.OrderEntity;
import com.zmm.mall.order.entity.OrderReturnReasonEntity;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MallOrderApplicationTests {

	@Autowired
	private AmqpAdmin amqpAdmin;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	public void testStream(){
		System.out.println("测试 MallOrderApplicationTests");
	}

	/**
	 * 1、如何创建 Exchange Queue Binding
	 * 		1). 使用 AmqpAdmin 进行创建
	 * 2、如何收发信息
	 */

	@Test
	public void sendMessageTest(){
		// 1、
		String exchangeName = "hello-java-exchange";
		String routingKey = "hello.java";
		String message = "你好!";
		// 如果发送的消息是个对象,必须使用序列化机制,将该对象写出去.
		// 改变发送对象类型未 json
		for (int i = 0; i < 10; i++) {
			if (i%2 == 0){
				OrderReturnReasonEntity orderMessage = new OrderReturnReasonEntity();
				orderMessage.setId(1L);
				orderMessage.setCreateTime(new Date());
				orderMessage.setName("哈哈-"+i);
				rabbitTemplate.convertAndSend(exchangeName,routingKey,orderMessage,new CorrelationData(UUID.randomUUID().toString()));
			} else {
				OrderEntity orderEntity = new OrderEntity();
				orderEntity.setOrderSn(UUID.randomUUID().toString());
				rabbitTemplate.convertAndSend(exchangeName,routingKey,orderEntity,new CorrelationData(UUID.randomUUID().toString()));
			}
		}

		log.info("消息发送完成");
	}

	@Test
	public void creatExchange(){

		//amqpAdmin -->创建 [hello.java.exchange]
		String exchangeName = "hello-java-exchange";
		// DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
		// 第一个参数 : 交换器的名称 第二个参数: 是否持久化 第三个参数: 是否自动删除 第四个参数: 可配置的参数
		DirectExchange directExchange = new DirectExchange(exchangeName,true,false);
		amqpAdmin.declareExchange(directExchange);
		log.info("Exchange[{}]创建成功",exchangeName);
	}


	@Test
	public void creatQueue(){
		String queueName = "hello-java-queue";
		// Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
		// 第一个参数: 队列名称 第二个参数: 是否持久化 第三个参数: 是否排它 (只能有一连接) 第四个参数: 是否自动删除 第五个参数: 可配置的参数
		Queue queue = new Queue(queueName,true,false,false);
		amqpAdmin.declareQueue(queue);
		log.info("Queue[{}]创建成功",queueName);
	}


	@Test
	public void creatBinding(){
		//Binding(String destination, Binding.DestinationType destinationType, String exchange, String routingKey, Map<String, Object> arguments)
		// 第一个参数: 目的地() 第二个参数: 目的地的类型 第三个参数: 交换机 第四个参数: 路由键 第五个参数: 可配置的参数
		// 将 exchange 指定的交换机和 destination 目的地进行绑定,使用 routingKey 作为指定的路由键
		Binding binding = new Binding(
				"hello-java-queue",
				Binding.DestinationType.QUEUE,
				"hello-java-exchange",
				"hello.java",null);
		amqpAdmin.declareBinding(binding);
		log.info("Binding[{}]创建成功","hello-java-binding");
	}

	@ToString
	@Data
	class OrderMessage implements Serializable {

		private Long id;

		private Long createTime;

		private String text;


	}

}
