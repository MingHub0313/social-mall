package com.zmm.mall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 900045
 * @description:
 * @name HelloSchedule
 * @date By 2021-04-16 15:30:15
 * @EnableAsync
 * @EnableScheduling
 * 移至 --> config
 */

@Component
@Slf4j
public class HelloSchedule {

	/**
	 * =======定时任务======
	 * 1.开启定时任务 @EnableScheduling
	 * 2.放入容器中	@Component
	 * 3.在 Scheduled中添加表达式
	 * 语法 : 秒  分   时   日   月   周 
	 * 注意:
	 * 	1.在spring 中 cron 是由 6位组成的 不允许第7位的年
	 * 	2.在spring 中 在 周的位置 1-7 分别代表 周一 到 周日 MON-SUN
	 * 	3.定时任务不应该阻塞.即有一个定时任务阻塞也不应该阻塞其它的定时任务 (默认是阻塞的)
	 * 		修改阻塞方法
	 * 			方案1:可以让业务运行以异步的方式,自己提交到线程池
	 * 				CompletableFuture.runAsync(()->{
	 *						xxxService.hello();
	 *                },executor);	
	 *          方案2:支持定时任务线程池:设置 TaskSchedulingProperties
	 *          	spring.task.scheduling.pool.size=5 (不好使用)
	 *          方案3:让定时任务异步执行
	 *          	异步任务:
	 *          
	 * =======异步任务=======
	 * 1.@EnableAsync 开启异步任务功能
	 * 2.方法上添加 @Async 
	 * 3.自动配置类: TaskExecutionAutoConfiguration 属性绑定在 spring.task.execution
	 * 
	 * 	总结:使用异步+定时任务来完成定时任务不阻塞的功能
	 */
	@Async
	@Scheduled(cron = "* * * ? * 5")
	public void hello() throws InterruptedException {
		log.info("hello....");
		
		Thread.sleep(3000);
	}
	
}
