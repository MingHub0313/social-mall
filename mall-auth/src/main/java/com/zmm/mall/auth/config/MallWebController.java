package com.zmm.mall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 900045
 * @description:
 * @name MallWebController
 * @date By 2021-02-24 11:25:31
 */
@Configuration
public class MallWebController implements WebMvcConfigurer {


	/**
	 * 视图映射
	 * @param registry
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {

		/**
		 * @GetMapping("/login.html")  --> urlPath
		 * 	public String loginPage(){
		 * 		return "login";		--->viewName
		 * 	}
		 */
		registry.addViewController("/login.html").setViewName("login");
		registry.addViewController("/reg.html").setViewName("reg");
	}
}
