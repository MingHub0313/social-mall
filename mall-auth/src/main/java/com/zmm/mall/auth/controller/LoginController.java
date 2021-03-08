package com.zmm.mall.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zmm.common.base.model.ReqResult;
import com.zmm.common.base.model.ResultCode;
import com.zmm.common.constant.AuthConstant;
import com.zmm.common.constant.NumberConstant;
import com.zmm.common.utils.StringUtil;
import com.zmm.common.utils.redis.key.RedisTimeOut;
import com.zmm.common.constant.StringConstant;
import com.zmm.mall.auth.feign.MemberFeignService;
import com.zmm.mall.auth.feign.ThirdPartyFeignService;
import com.zmm.mall.auth.vo.LoginVo;
import com.zmm.mall.auth.vo.RegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 900045
 * @description:
 * @name LoginController
 * @date By 2021-02-24 11:14:38
 */
@Slf4j
@RestController
public class LoginController {
	
	@Autowired
	private ThirdPartyFeignService thirdPartyFeignService;

	@Resource
	private MemberFeignService memberFeignService;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@GetMapping("/login.html")
	public ReqResult loginPage(HttpSession session){
		Object attribute = session.getAttribute(StringConstant.LOGIN_USER);
		if (ObjectUtils.isEmpty(attribute)){
			//没登录
			log.error("用户还没有登录,[SpringSession中没有数据]");
			return new ReqResult(ResultCode.USER_NOT_LOGIN);
		}
		//登录了 跳 登录页面

		ObjectMapper objectMapper = new ObjectMapper();
		ReqResult reqResult = objectMapper.convertValue(attribute, ReqResult.class);
		log.error("用户已经登录,[SpringSession中存在数据]");
		return reqResult;
	}


	@PostMapping("/login")
	public ReqResult login(@Valid LoginVo loginVo, BindingResult bindingResult, HttpSession session,
						   RedirectAttributes redirectAttributes){
		if (bindingResult.hasErrors()){
			return paramVerify(bindingResult);
		}
		ReqResult result = memberFeignService.login(loginVo);
		if (result.getResultCode() == NumberConstant.THOUSAND){
			// 成功 将用户信息放入session
			//MemberEntity memberEntity = result.getData("data",new TypeReference<MemberEntity>(){})
			//session.setAttribute(StringConstant.LOGIN_USER,memberEntity)
			// "redirect:http://mall.com";
		} else {
			// 失败
			Map<String,String> errors = new HashMap<>();
			//errors.put("msg",result.getData("data",new TypeReference<MemberEntity>(){}));
			redirectAttributes.addFlashAttribute("errors",errors);
			// "redirect:http://auth.mall.com/login.html";
		}
		// 远程登录 重定向商场的首页地址
		// return "redirect:http://mall.com"
		return result;
	}
	
	@GetMapping("/sms/send/code")
	public ReqResult sendCode(@RequestParam("phone") String phone){
		String smsCodeCachePrefix = AuthConstant.SMS_CODE_CACHE_PREFIX + phone;

		String redisCode = redisTemplate.opsForValue().get(smsCodeCachePrefix);
		// 判断不为空 才需要 判断
		if (StringUtils.isNotEmpty(redisCode)){
			long l = Long.parseLong(redisCode.split(StringConstant.REGULAR_)[1]);
			if (System.currentTimeMillis() - l< RedisTimeOut.S_60 * RedisTimeOut.MILL_1000){
				// 60 秒内不能再发
				return new ReqResult(ResultCode.SMS_CODE_EXCEPTION);
			}
		}
		// 不为空 说明之前没有发过验证码 
		
		/**
		 * 存在的问题:
		 * 	1.接口防刷:
		 * 	2.验证码的再次校验: 将验证码存入 redis中 key : phone value : smsCode ==>sms:code:phone ----> smsCode
		 */
		String code = StringUtil.getRandom(6);
		redisCode =  code +"_"+System.currentTimeMillis();
		log.error("手机号:{} 生成的验证码:{}",phone,redisCode);
		// 存入 redis 中 [同一个手机号 不可以在1分钟内 再次发送]
		redisTemplate.opsForValue().set(smsCodeCachePrefix,redisCode,10, TimeUnit.MINUTES);
		thirdPartyFeignService.sendCode(phone,code);
		return new ReqResult(ResultCode.SUCCESS);
	}
	
	@PostMapping("/register")
	public ReqResult register(@Valid RegisterVo vo, BindingResult result){
		// 1.表达校验
		if (result.hasErrors()){
			return paramVerify(result);
		}
		// 2.验证码校验 不用判空 @NotEmpty
		String code = vo.getCode();

		String smsCodeCachePrefix = AuthConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone();

		String redisCode = redisTemplate.opsForValue().get(smsCodeCachePrefix);
		if (StringUtils.isNotEmpty(redisCode)){
			// 如果不为空 取出判断
			if (code.equals(redisCode.split(StringConstant.REGULAR_)[0])){
				// 删除 redis中的 验证码 令牌机制
				redisTemplate.delete(smsCodeCachePrefix);
				// 3.开始注册 调用远程服务进行注册
				ReqResult reqResult = memberFeignService.register(vo);
				if (reqResult.getResultCode() == NumberConstant.THOUSAND){
					// 成功
					// "redirect:http://auth.mall.com/login.html";
				} else {
					// return "redirect:http://auth.mall.com/reg.html"
				}
				return reqResult;
			} else {
				Map<String, String> errors = getErrorsMap("code", "验证码失效");
				// return "redirect:http://auth.mall.com/reg.html"
				return new ReqResult(ResultCode.METHOD_CALL_PARAMETER_ERROR,errors);
			}
			
		} else {
			// 如果为空 需要重发送验证码
			Map<String, String> errors = getErrorsMap("code", "验证码失效");
			// return "redirect:http://auth.mall.com/reg.html"
			return new ReqResult(ResultCode.METHOD_CALL_PARAMETER_ERROR,errors);
		}

		// 重定向至 登录页
		// return "redirect:/login.html"
	}

	/**
	 * 包装 错误信息
	 * @author: 900045
	 * @date: 2021-02-25 09:48:46
	 * @throws 
	 * @param code2: 
	 * @param 验证码失效: 
	 * @return: java.util.Map<java.lang.String,java.lang.String>
	 **/
	private Map<String, String> getErrorsMap(String code2, String 验证码失效) {
		Map<String, String> errors = new HashMap<>(1);
		errors.put(code2, 验证码失效);
		return errors;
	}
	
	private ReqResult paramVerify(BindingResult result){
		/**
		 * result.getFieldErrors().stream().map( fieldError->{
		 * 				String field = fieldError.getField()
		 * 				String message = fieldError.getDefaultMessage()
		 * 				errors.put("field",field)
		 * 				errors.put("message",message)
		 * 				return errors
		 *                        })
		 */
		Map<String ,String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));
		// model.addAttribute("errors",errors)
		// 校验出错 转发至 注册页面
		// return "reg"
		return new ReqResult(ResultCode.METHOD_CALL_PARAMETER_ERROR,errors);
	}

}
