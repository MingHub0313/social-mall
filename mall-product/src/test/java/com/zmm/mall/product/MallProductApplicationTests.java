package com.zmm.mall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zmm.mall.product.entity.BrandEntity;
import com.zmm.mall.product.service.BrandService;
import com.zmm.mall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MallProductApplicationTests {

	@Autowired
	private BrandService brandService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private RedissonClient redisSon;


	@Test
	public void testRedisSon(){
		System.out.println(redisSon);

	}

	@Test
	public void testStringRedisTemplate(){
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();

		//保存
		opsForValue.set("hello","world_"+ UUID.randomUUID().toString());

		//查询
		String hello = opsForValue.get("hello");
		System.out.println(hello);
	}

	@Test
	public void testFindPath(){
		Long[] cateLogPath = categoryService.findCateLogPath(225L);
		log.info("完整路径:{}", Arrays.asList(cateLogPath));
	}

	@Test
	public void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();
		//brandEntity.setName("华为");
		//brandService.save(brandEntity);

		//brandEntity.setBrandId(1L);
		//brandEntity.setDescript("国产");
		//brandService.updateById(brandEntity);

		List<BrandEntity> entityList = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
		entityList.forEach( item ->{
			System.out.println(item);
		});

		System.out.println("保存成功....");
	}

	@Test
	public void testStream(){
		BrandEntity brandEntity1 = new BrandEntity();
		brandEntity1.setBrandId(1L);
		brandEntity1.setName("华为1");

		BrandEntity brandEntity2 = new BrandEntity();
		brandEntity2.setBrandId(2L);
		brandEntity2.setName("华为2");

		BrandEntity brandEntity3 = new BrandEntity();
		brandEntity3.setBrandId(null);
		brandEntity3.setName("华为3");

		BrandEntity brandEntity4 = new BrandEntity();
		brandEntity4.setBrandId(null);
		brandEntity4.setName("华为4");

		List<BrandEntity> brandEntities = new ArrayList<>();
		brandEntities.add(brandEntity1);
		brandEntities.add(brandEntity2);
		brandEntities.add(brandEntity3);
		brandEntities.add(brandEntity4);

		brandEntities.stream().map(brandEntity -> {
			if (brandEntity.getBrandId() == null){
				brandEntity.setBrandId(-1L);
			}
			return brandEntity;
		}).collect(Collectors.toList());

		System.out.println(brandEntities);
	}

}
