package com.zmm.mall.order.listener;

import com.rabbitmq.client.Channel;
import com.zmm.mall.order.entity.OrderEntity;
import com.zmm.mall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author 900045
 * @description:
 * @name OrderCloseListener
 * @date By 2021-03-24 14:41:43
 */
@RabbitListener(queues = "order.release.order.queue")
@Service
public class OrderCloseListener {
	
	@Resource
	private OrderService orderService;


	@RabbitHandler
	public void listenerOrder(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
		System.out.println("收到过期的订单信息:准备关闭订单"+orderEntity.getOrderSn()+"===>"+orderEntity.getId());
		try {
			orderService.closeOrder(orderEntity);
			//手动调用支付宝收单
			
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		} catch (Exception e){
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
		}

	}
}
