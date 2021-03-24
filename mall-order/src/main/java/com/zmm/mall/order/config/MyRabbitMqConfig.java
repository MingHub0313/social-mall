package com.zmm.mall.order.config;

import com.rabbitmq.client.Channel;
import com.zmm.mall.order.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Name MyRabbitMqConfig
 * @Author Administrator
 * @Date By 2021-03-07 20:28:26
 */
@Slf4j
@Configuration
public class MyRabbitMqConfig {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制 : RabbitTemplate
     *  1.服务器收到消息就会回调
     *      (1).spring.rabbitmq.publisher-confirms=true 开启发送端确认
     *      (2).设置确认回调 ConfirmCallback
     *  2.消息正确抵达队列
     *      (1).spring.rabbitmq.publisher-returns=true 开启发送端消息抵达队列确认
     *      (2).spring.rabbitmq.template.mandatory=true 只要抵达队列,以异步方式优先回调我们这个 returnConfirm
     *      (3).设置确认回调 ReturnCallback
     *  3.消费端确认 (保证每个消息被正确消息,此时broker才可以删除这个消息)
     *      (1).默认自动确认的,只要消息接收到,客户端自动确认,服务端就会移除这个消息 [问题很大,服务器如果宕机 消息都会自动回复给服务器ack,就会发生消息丢失] ==> 需要手动确认
     *          spring.rabbitmq.listener.simple.acknowledge-mode=manual 手动 ack
     *          只是添加配置还是不行,只要我们没有明确告诉mq,货物被签收就相当于没有ack,
     *          消息就是一直是unacked状态.即使服务器宕机,消息也不会丢失,会重新变为Ready,下次有新的 Consumer连接进来就会给他发送消息.
     *      (2).如何签收
     *          channel.basicAck(deliveryTag,false);    签收;业务成功完成就应该签收
     *          channel.basicNack(deliveryTag,false,true);  拒签;业务失败,拒签
     * @PostConstruct MyRabbitMqConfig对象创建完成以后 执行这个方法
     * @author: Administrator
     * @date: 2021-03-07 21:56:33
     * @return: void
     **/
    @PostConstruct
    public void initRabbitTemplate(){
        //设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 1.只要消息抵达 Broker ==> ack =true
             * @author: Administrator
             * @date: 2021-03-07 21:57:28
             * @param correlationData: 当前消息的唯一关联数据 (这个是消息的唯一id)
             * @param ack: 消息是否成功收到
             * @param cause: 失败的原因
             * @return: void
             **/
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                //convertAndSend 第四个参数: 指定的唯一ID
                // 服务器收到了
                // 防止消息丢失
                /**
                 * 1.做好消息的确认机制 (publisher、consumer 【手动 ack】)
                 * 2.每一个发送的消息都在数据库做好记录.定期将失败的消息(无论是消息失败还是服务器未抵达的失败)再次发送.
                 */
                
                // 防止消息重复
                log.info("confirms...correlationData:[{}]===>ack:[{}]===>cause:[{}]",correlationData, ack,cause);
            }
        });
        //设置消息抵达队列的确认回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 触发时机:只要消息没有投递给指定的队列 就会触发这个失败回调
             * @author: Administrator
             * @date: 2021-03-07 22:09:15
             * @param message:      投递失败的消息详细信息
             * @param replyCode:    回复的状态码
             * @param replyText:    回复的文本内容
             * @param exchange:     当时这个消息发送给哪个交换机
             * @param routingKey:   当时这个消息用哪个路由键
             * @return: void
             * test:让其路由键错误
             **/
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                // 发生错误 --> 修改数据库当前的消息状态
                log.error("Fail Message!!!===>message:[{}]===>replyCode:[{}]===>replyText:[{}]===>exchange:[{}]===>routingKey:[{}]",message,replyCode,replyText,exchange,routingKey);
            }
        });
    }

    /**
     * @Bean exchange Binding Queue
     * 容器中的组件 使用 @Bean 都会自动创建 (前提是 RabbitMq中没有的情况)
     * RabbitMq 如果有了 某些属性 路由、queue，服务再次重启不会进行覆盖 --->需要手动删除
     * @return
     */

    @Bean
    public Queue orderDelayQueue(){
        // 死信队列
        // name:队列的名称 durable:是不是持久化 exclusive:是不是排它的 autoDelete:是不是自动删除的
        //Queue(String name, boolean durable, boolean exclusive, boolean autoDelete)

        /**
         * 设置死信队列 -- 自定义参数
         * x-dead-letter-exchange : order-event-exchange
         * x-dead-letter-routing-key: order.release.order
         * x-message-ttl: 60000 /毫秒
         */
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","order-event-exchange");
        arguments.put("x-dead-letter-routing-key","order.release.order");
        arguments.put("x-message-ttl",60000);

        Queue queue = new Queue("order.delay.queue", true, false, false,arguments);
        return queue;
    }

    @Bean
    public Queue orderReleaseOrderQueue(){
        // 普通的队列
        Queue queue = new Queue("order.release.order.queue", true, false, false);
        return queue;
    }

    @Bean
    public Exchange orderEventExchange(){

        // name: 交换机的名称 durable:是否持久化 autoDelete: 是否自动删除 arguments: 自定义参数
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange("order-event-exchange",true,false);
    }


    @Bean
    public Binding orderCreateOrderBinding(){
        // destination: 目的地 destinationType: 目的地的类型 exchange: 交换机 routingKey: 路由键 arguments: 自定义参数
        //String destination, Binding.DestinationType destinationType, String exchange, String routingKey, Map<String, Object> arguments
        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);
    }

    @Bean
    public Binding orderReleaseOrderBinding(){
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }

    /**
     * 订单释放直接 和 库存 释放进行绑定
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBinding(){
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",
                null);
    }
}
