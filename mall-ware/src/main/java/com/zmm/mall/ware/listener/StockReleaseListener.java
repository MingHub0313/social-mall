package com.zmm.mall.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.zmm.common.enums.OrderStatusEnum;
import com.zmm.common.to.mq.OrderTo;
import com.zmm.common.to.mq.StockDetailTo;
import com.zmm.common.to.mq.StockLockedTo;
import com.zmm.common.utils.R;
import com.zmm.mall.ware.entity.WareOrderTaskDetailEntity;
import com.zmm.mall.ware.entity.WareOrderTaskEntity;
import com.zmm.mall.ware.service.WareSkuService;
import com.zmm.mall.ware.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author 900045
 * @description:
 * @name StockReleaseListener
 * @date By 2021-03-24 14:16:35
 */
@Slf4j
@RabbitListener(queues = "stock.release.stock.queue")
@Service
public class StockReleaseListener {

	@Resource
	private WareSkuService wareSkuService;

	/**
	 * 1.库存自动解锁
	 *  下订单成功,库存锁定成功,其它的业务调用失败,导致订单回滚.之前锁定的库存也要解锁
	 * 2.订单失败
	 *  锁库存失败
	 *
	 *  只要解锁库存的消息失败  一定要告诉服务 此次解锁失败 要重试
	 *  spring.rabbitmq.listener.simple.acknowledge-mode=manual 手动 ack -->
	 * @author: 900045
	 * @date: 2021-03-24 11:48:24
	 * @throws
	 * @param to: 
	 * @param message: 
	 * @return: void
	 **/
	@RabbitHandler
	public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {

		log.error("收到解锁库存的消息...");
		// 只要这个方法不抛异常 即 --> 库存解锁成功
		// 如果有异常抛出 --> 库存解锁失败 拒收消息 将消息重新丢回队列 让别人重新消费
		try {
			// 该消息是否是重新投递的 太过暴力了
			// Boolean redelivered = message.getMessageProperties().getRedelivered()
			wareSkuService.unLockStock(to);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		} catch (Exception e){
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
		}
	}

	@RabbitHandler
	public void handleOrderCloseRelease(OrderTo orderTo, Message message, Channel channel) throws IOException {
		log.error("订单自动关闭,准备解锁库存");
		try {
			wareSkuService.unLockStock(orderTo);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		}catch (Exception e){
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
		}
	}
	
}
