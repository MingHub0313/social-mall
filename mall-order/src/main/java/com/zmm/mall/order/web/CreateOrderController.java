package com.zmm.mall.order.web;

import com.zmm.mall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
 * @Description:
 * @Name CreateOrderController
 * @Author Administrator
 * @Date By 2021-03-23 20:38:42
 */
@RestController
public class CreateOrderController {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/test/create/order")
    public String createOrderTest(){
        // 1.假设订单下单成功
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(UUID.randomUUID().toString());
        orderEntity.setCreateTime(new Date());

        // 2.给MQ 发送消息 下单成功 配置文件中的手动确认 spring.rabbitmq.publisher-confirms=true
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",orderEntity);
        return "ok";
    }
}
