package com.zmm.mall.gateway.config;

import com.zmm.common.utils.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Name HelloController
 * @Author 900045
 * @Created by 2020/11/6 0006
 */
@RefreshScope
@RestController
@RequestMapping("hello")
public class HelloController {

	@Value("${gateway.name}")
	private String name;

	@Value("${gateway.age}")
	private Integer age;


	@RequestMapping("/test")
	public R test(){
		return R.ok().put("name",name).put("age",age);
	}
}
