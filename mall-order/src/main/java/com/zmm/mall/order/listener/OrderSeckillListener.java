package com.zmm.mall.order.listener;

import com.rabbitmq.client.Channel;
import com.zmm.common.to.mq.SeckillOrderTo;
import com.zmm.mall.order.entity.OrderEntity;
import com.zmm.mall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author 900045
 * @description:
 * @name OrderSeckillListener
 * @date By 2021-04-19 17:49:07
 */
@Slf4j
@RabbitListener(queues = "order.seckill.order.queue")
@Component
public class OrderSeckillListener {
	@Resource
	private OrderService orderService;


	@RabbitHandler
	public void listenerOrder(SeckillOrderTo seckillOrderTo, Channel channel, Message message) throws IOException {
		System.out.println("收到秒杀订单"+seckillOrderTo.getOrderSn()+"===>"+seckillOrderTo.getMemberId());
		try {
			log.info("准备创建秒杀单的详细信息....");
			orderService.createSeckillOrder(seckillOrderTo);
			//手动调用支付宝收单

			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		} catch (Exception e){
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
		}

	}
	
}
