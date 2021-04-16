package com.zmm.mall.coupon;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MallCouponApplicationTests {

	@Test
	public void testStream(){
    System.out.println("测试 MallCouponApplicationTests");
	}
	
	@Test
	public void testDate(){
		//2021-04-17 00:00:00 ~ 2021-04-19 23;59:59
		LocalDate now = LocalDate.now();
		LocalDate plus1 = now.plusDays(1);
		LocalDate plus2 = now.plusDays(2);
		LocalDate plus3 = now.plusDays(3);

		// 00:00
		LocalTime min = LocalTime.MIN;
		// 23:59:59
		LocalTime max = LocalTime.MAX;
		LocalDateTime start = LocalDateTime.of(now, min);
		LocalDateTime end = LocalDateTime.of(plus3, max);
		log.info("plus1:[{}]",plus1);
		log.info("plus2:[{}]",plus2);
		log.info("plus3:[{}]",plus3);
		
		// 2021-04-16T00:00
		log.info("start:[{}]",start);
		// 2021-04-19T23:59:59.999999999
		log.info("end:[{}]",end);
	}

}
