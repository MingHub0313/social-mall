package com.zmm.mall.product.web;

import com.zmm.mall.product.entity.CategoryEntity;
import com.zmm.mall.product.service.CategoryService;
import com.zmm.mall.product.vo.Catalog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Name IndexController
 * @Author 900045
 * @Created by 2020/9/27 0027
 */
@Slf4j
@Controller
public class IndexController {

	@Autowired
	CategoryService categoryService;

	@Autowired
	private RedissonClient redisSon;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;


	@GetMapping(value = {"/","/index.html"})
	public String indexPage(Model model){
		//1. 查询出所有的一级分类
		List<CategoryEntity> categoryEntityList=categoryService.getLevel1Categories();
		model.addAttribute("categories",categoryEntityList);
		// 视图解析器 进行拼接
		return  "index";
	}

	@GetMapping("index/catalog.json")
	@ResponseBody
	public Map<String, List<Catalog2Vo>> getCateLog() {
		Map<String, List<Catalog2Vo>> json = categoryService.getCatalogJson();
		return json;
	}

	@GetMapping("/hello")
	@ResponseBody
	public String hello(){
		// 1.获取一把锁 ,只要锁的名字一样 ,就是同一把锁
		RLock lock = redisSon.getLock("my-lock");

		// 2.加锁 -- 阻塞式等待 默认加的锁都是30s
		//lock.lock()
		// 1).锁的自动续期, 如果业务超长 运行期间自动给锁续上新的30s 不用担心业务时间长 锁自动过期删掉
		// 2).加锁的业务只要运行完成,就不会给当期锁续期.即使不手动解锁,锁默认在30s以后自动删除.

		// 10 秒自动解锁 注意自动解锁时间一定要大于业务的执行时间
		// 问题: lock.lock(10, TimeUnit.SECONDS) 在锁时间到了以后 不会自动续期
		// 1.如果我们传递了锁的时间,就发送给redis 执行脚本 进行占锁 默认超时就是我们设定的时间
		// 2.如果未指定时间 就使用 lockWatchdogTimeout = 30000L 看门狗 默认的时间
		// 只要占锁成功 就会启动一个定时任务 【重新给锁设置过期时间,新的过期时间就是看门狗的默认时间】 每隔10s都会自动再次续期 续到满时间 30s
		// this.internalLockLeaseTime / 3L  1/3 的时间就会给看门狗重新设置过期时间
		lock.lock(10, TimeUnit.SECONDS);
		/**
		 * 最佳实战
		 * 	1).lock.lock(30, TimeUnit.SECONDS) 省掉了整个续期操作 . 手动解锁
		 */
		try{
			log.info("加锁成功!执行业务...{}",Thread.currentThread().getId());
			Thread.sleep(30000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 3.解锁
			//问题 : 假设解锁代码没有运行 redisson会不会出现死锁  --答案不会发生死锁
			log.info("释放锁...{}",Thread.currentThread().getId());
			lock.unlock();
		}

		return "hello";
	}


	/**
	 * 模拟 读写锁 保证一定能读到最新数据 ,修改期间 ,写锁是一个排它锁(互斥锁、独享锁)  只能存在一个写锁  如果是并发 那就排队等待.读锁是一个共享锁
	 * 写锁没释放 读就必须等待
	 * 读 + 读 : 相当于无锁,并发读 ,只会在Redis中记录好,所有当前的读锁.他们都会同时加锁成功
	 * 写 + 读 : 等待写锁释放
	 * 写 + 写 : 阻塞...
	 * 读 + 写 : 有读锁 , 写也需要等待
	 * 只要有写的存在 都必须等待
	 * @return
	 */
	@GetMapping("/write")
	@ResponseBody
	public String writeValue(){

		RReadWriteLock rReadWriteLock =  redisSon.getReadWriteLock("rw-lock");
		RLock lock = rReadWriteLock.writeLock();
		String string = "";

		try {
			// 1.改数据加写锁 , 读数据加读锁
			lock.lock();
			log.info("写锁加锁成功...{}",Thread.currentThread().getId());
			string = UUID.randomUUID().toString();
			Thread.sleep(30000);
			stringRedisTemplate.opsForValue().set("writeValue",string);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
			log.info("写锁释放成功...{}",Thread.currentThread().getId());
		}

		return string;

	}

	/**
	 * 模拟 读写锁
	 * 写读
	 * @return
	 */
	@GetMapping("/read")
	@ResponseBody
	public String readValue(){
		RReadWriteLock rReadWriteLock =  redisSon.getReadWriteLock("rw-lock");
		RLock lock = rReadWriteLock.readLock();
		String string = "" ;
		log.info("读锁加锁成功...{}",Thread.currentThread().getId());
		lock.lock();
		try {
			Thread.sleep(30000);
			string = stringRedisTemplate.opsForValue().get("writeValue");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			log.info("读锁加释放成功...{}",Thread.currentThread().getId());
			lock.unlock();
		}

		return string;
	}

	/**
	 * 车库停车 3车
	 * tryAcquire 信号量 也可以作为分布式限流
	 */

	@GetMapping("/park")
	@ResponseBody
	public String park() throws InterruptedException {
		RSemaphore semaphore = redisSon.getSemaphore("park");
		// 获取一个信号 获取一个值 占一个车位 阻塞式
		// semaphore.acquire()
		boolean b = semaphore.tryAcquire();
		if (b){
			//执行业务
		} else {
			return "稍后再试!";
		}
		// tryAcquire 大量流量 尝试获取
		return "OK==>";
	}


	@GetMapping("/go")
	@ResponseBody
	public String go(){
		RSemaphore semaphore = redisSon.getSemaphore("park");
		// 释放一个车位
		semaphore.release();
		return "OK";
	}


	/**
	 * 放假 , 锁门
	 * 1 班没有 2 ...5个班全部走完 才可以锁大门
	 */

	@GetMapping("/lockDoor")
	@ResponseBody
	public String lockDoor() throws InterruptedException {
		RCountDownLatch countDownLatch = redisSon.getCountDownLatch("door");
		countDownLatch.trySetCount(5L);
		//等待闭锁完成
		countDownLatch.await();
		return "放假了..全部锁门";
	}

	@GetMapping("/goHome/{id}")
	@ResponseBody
	public String goHome(@PathVariable("id") Long id){
		RCountDownLatch countDownLatch = redisSon.getCountDownLatch("door");
		//计数减一
		countDownLatch.countDown();
		return "放假了.."+id+"班的人都走了...";
	}

}
