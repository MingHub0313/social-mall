package com.zmm.mall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

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
                log.error("Fail Message!!!===>message:[{}]===>replyCode:[{}]===>replyText:[{}]===>exchange:[{}]===>routingKey:[{}]",message,replyCode,replyText,exchange,routingKey);
            }
        });
    }
}
