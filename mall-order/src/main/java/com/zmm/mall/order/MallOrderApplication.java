package com.zmm.mall.order;

import com.zmm.common.config.BaseConfigure;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Component;

/**
 * @Name MallOrderApplication
 * @Author 900045
 * @Created by 2020/8/26
 */
@EnableFeignClients
@EnableRedisHttpSession
@EnableRabbit
@EnableDiscoveryClient
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
	 *
	 * 	接口幂等性就是用户对于同一操作发起的一次请求或者多次请求的结果是一致的,不会因为多次点击而产生了副作用;比如:支付场景,
	 * 	用户购买商品支付扣款成功,但是返回结果的时候网络异常,此时钱已经扣了,用户再次点击按钮,此时进行第二次扣款,返回结果成功,
	 * 	流水记录产生两条,这就没有保证接口的幂等性.
	 *
	 * 	一、令牌机制
	 * 		需要保证 1.获取服务端的 token  2.对比 token	3.删除服务端token 三步操作是原子性 ==>使用[lua]脚本
	 *
	 * 	二、各种锁机制
	 * 		1.数据库悲观锁
	 * 			select * from where id = 1 for update;
	 * 			悲观锁使用时一般伴随着事务一起使用,数据锁定时间可能会很长,需要根据实际情况选用.
	 * 			另外注意的是 id 字段一定是主键或者唯一索引,不然可能造成锁表的结果,处理起来会非常麻烦
	 * 		2.数据库乐观锁 (居多)
	 * 			update t_goods set count = count -1,version = version + 1 where good_id = 2 and version = 1.
	 * 			根据字段 version(版本), 操作成功就给version + 1.重试携带还是老数据.
	 * 		3.分布式锁
	 * 			如果多个机器可能在同一时间同时处理相同的数据,比如多台机器定时任务到拿到了相同数据处理.
	 * 			我们就可以加分布式,锁定数据,处理完成后释放锁.获得到锁的必须先判断这个数据是否被处理过.
	 * 	三、	各种唯一约束
	 * 		1.数据库唯一约束
	 * 		2.redis 防重
	 * 				很多数据需要处理,只能被处理一次,比如我们可以计算数据的MD5 将其放入redis 中,每次处理数据,先看这个MD5 是否已经存在,存在就不处理
	 *	四、防重表
	 *		使用订单号 orderNo 做为 去重表的唯一索引,把唯一索引插入去重表,再进行业务操作,且他们在同一个事务中.这个保证了重复请求时,因为去重表
	 *		有唯一约束,导致请求失败,避免了幂等性问题.
	 *		注意: 去重表和业务表应该在同一个库中,这样就保证了在同一个事务,即使业务操作失败了,也会把去重表的数据回滚.保证了数据的一致性.
	 *	五、全局请求唯一id
	 *		调用接口时.生成一个唯一的id.redis将数据保存到集合中(去重),存在即处理过.
	 *		可以使用 nginx 设置每一个请求的唯一id proxy_set_header X-Request-Id $request_id;
	 *
	 */

	public static void main(String[] args) {
		SpringApplication.run(MallOrderApplication.class, args);
	}


	@Component
	public class serviceConfigure extends BaseConfigure {
	}
}
