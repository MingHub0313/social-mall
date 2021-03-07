package com.zmm.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbitmq.client.Channel;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;
import com.zmm.mall.order.dao.OrderItemDao;
import com.zmm.mall.order.entity.OrderItemEntity;
import com.zmm.mall.order.entity.OrderReturnReasonEntity;
import com.zmm.mall.order.service.OrderItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@RabbitListener(queues = {"hello-java-queue"})
@Slf4j
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * queues:声明需要监听的所有队列
     * Queue: 可以有很多监听者(但只能有一个能收到信息,(收到信息后)队列就会删除信息)\
     * 场景描述:
     *      1).订单服务启动多个:同一个消息只能有一个收到(竞争关系)
     *      2).模拟业务执行时间很长(在业务处理期间是否还能收到消息)[一个消息处理完成才会接收另一个消息]
     * @author: Administrator
     * @date: 2021-03-07 21:16:55
     * @param message: 原生的消息详信息 头+体
     * @param content: T<当时发送的类型> 不需要手动转换为 实体对象
     * @param channel: 当前传输数据的通道
     * @return: void
     **/
    @RabbitHandler
    public void receiverMessage(Message message, OrderReturnReasonEntity content, Channel channel){
        // {"id":1,"name":"哈哈","sort":null,"status":null,"createTime":1615122726551}
        byte[] body = message.getBody();

        //[headers={__TypeId__=com.zmm.mall.order.entity.OrderReturnReasonEntity},
        // contentType=application/json, contentEncoding=UTF-8, contentLength=0,
        // receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=hello-java-exchange,
        // receivedRoutingKey=hello.java, deliveryTag=2, consumerTag=amq.tag-OSU6m0X1RztSZqA,
        // consumerQueue=hello-java-queue]
        MessageProperties messageProperties = message.getMessageProperties();
        // channel 通道内按顺序自增的
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //basicAck(long deliveryTag, boolean multiple)
        // deliveryTag: 当前消息派发的标签 multiple:是否批量确认
        // 签收货物 非批量模式
        try {
            if (deliveryTag % 2 == 0){
                // 收货
                channel.basicAck(deliveryTag,false);
                log.info("签收了获取...[{}]",deliveryTag);
            } else {
                // 退货 requeue 重新入队 true:发回 服务器,服务器重新入队 false:直接丢弃
                // void basicNack(long deliveryTag, boolean multiple, boolean requeue) throws IOException;
                channel.basicNack(deliveryTag,false,true);
                // void basicReject(long deliveryTag, boolean requeue)
                // channel.basicReject();
                log.info("没有签收获取...[{}]",deliveryTag);
            }

        } catch (IOException e) {
            // 网络中断
            e.printStackTrace();
        }
        log.info("接收到消息...内容:[{}] ===>内容[{}]",message.getBody(),content);
    }

    @RabbitHandler
    public void receiverMessage2(OrderReturnReasonEntity content){
        log.info("接收到消息2...内容:[{}]",content);
    }

}