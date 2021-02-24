package com.zmm.mall.member;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MallMemberApplicationTests {

	@Test
	public void testStream(){
		System.out.println("测试 MallMemberApplicationTests");

		/**
		 * 1.压缩性: 任意长度的数据,计算出来的 MD5 值的长度都是固定的
		 * 2.容易计算:从原数据计算出MD5值很容易
		 * 3.抗修改性:对原数据即使修改一个字节,所得到的新的MD5与原数据计算的MD5 有很大区别
		 * 4.强抗碰撞性: 想找到两个不同数据,使它们具有相同的MD5值,是非常困难的
		 * 5.不可逆性: 根据 MD5 值 不可能得到原数据. --> 彩虹表 : 暴力破解
		 * 即 MD5 不能直接进行密码的加密存储 ===> 盐值加密
		 */
		//e10adc3949ba59abbe56e057f20f883e
		String s = DigestUtils.md5Hex("123456");
		log.info(s);
		
		String md5Crypt = Md5Crypt.md5Crypt("123456".getBytes(),"$1$aaaaaaaa");
		log.info(md5Crypt);

		/**
		 * $2a$10$hv2U.aFVc72TaYD2O5rkkO4ZhHNd1W.rVY6GroGfMqT7bZtlNNS3O
		 * $2a$10$ZRHw0GWH6qtzGb9gmH6TIuNGJRmDaxi3NMrEUErtbVMadZRVsjY9i
		 * 发现两次不一致 ???
		 */
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String encode = bCryptPasswordEncoder.encode("123456");
		// 匹配是否相同
		boolean matches = bCryptPasswordEncoder.matches("123456", "$2a$10$ZRHw0GWH6qtzGb9gmH6TIuNGJRmDaxi3NMrEUErtbVMadZRVsjY9i");
		log.info("MD5值:{},是否匹配{}",encode,matches);
	}

}
