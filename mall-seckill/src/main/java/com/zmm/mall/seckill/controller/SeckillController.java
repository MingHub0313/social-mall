package com.zmm.mall.seckill.controller;

import com.zmm.common.utils.R;
import com.zmm.mall.seckill.service.SeckillService;
import com.zmm.mall.seckill.to.SeckillSkuRedisTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 900045
 * @description:
 * @name SeckillController
 * @date By 2021-04-19 15:28:29
 */
@Slf4j
@RestController
@RequestMapping("seckill")
public class SeckillController {
	
	@Resource
	private SeckillService seckillService;
	
	/**
	 * 返回当前时间可以参与秒杀商品信息
	 * @author: 900045
	 * @date: 2021-04-19 15:30:15
	 * @throws 
	
	 * @return: com.zmm.common.utils.R
	 **/
	@GetMapping("/current/seckill/sku")
	public R getCurrentSeckillSkuList(){
		List<SeckillSkuRedisTo> vos = seckillService.getCurrentSeckillSkuList();
		return R.ok().setData(vos);
	}
	
	/**
	 * 根据 skuId 获取秒杀商品的详情
	 * @author: 900045
	 * @date: 2021-04-19 16:03:11
	 * @throws 
	 * @param skuId: 
	 * @return: com.zmm.common.utils.R
	 **/
	@GetMapping("/sku/seckill/{skuId}")
	public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId){
		SeckillSkuRedisTo redisTo = seckillService.getSkuSeckillInfo(skuId);
		return R.ok().setData(redisTo);
	}
	
	@GetMapping("/kill")
	public R seckill(@RequestParam("killId") String killId,
					 @RequestParam("key") String  key,
					 @RequestParam("num") Integer num){
		// 1.业务开始
		String orderSn = seckillService.kill(killId,key,num);
		return R.ok().setData(orderSn);
	}

	@GetMapping(value = "/feign/{skuId}")
	public String setOpenFeign(@PathVariable("skuId") Long skuId){
		log.info("准备查询:{}详情",skuId);
		Map<String,Object> map = new HashMap<>(3);
		map.put("name","wu");
		map.put("age",26);
		map.put("addr","shanghai");
		return "OK";
	}
}
