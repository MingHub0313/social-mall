package com.zmm.mall.product.exception;

import com.zmm.common.exception.BizCodeEnums;
import com.zmm.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 几种处理所有的异常
 * @Name MallExceptionControllerAdvice
 * @Author 900045
 * @Created by 2020/8/31 0031
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.zmm.mall.product.controller")
public class MallExceptionControllerAdvice {


	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public R handleValidException(MethodArgumentNotValidException e){
		log.error("数据校验出现问题{},异常类型:{}",e.getMessage(),e.getClass());
		BindingResult result = e.getBindingResult();
		Map<String,String> map = new HashMap<>();
		// 1.获取校验的错误结果
		result.getFieldErrors().forEach( item->{
			// FiledError 获取到错误提示
			String message = item.getDefaultMessage();
			map.put("message",message);
			// 获取错误的属性名称
			String field = item.getField();
			map.put("field",field);
		});
		return R.error(BizCodeEnums.VALID_EXCEPTION.getCode(),BizCodeEnums.VALID_EXCEPTION.getMsg()).put("data",map);

	}

	@ExceptionHandler(value = Throwable.class)
	public R handleException(Throwable throwable){
		log.error("错误:",throwable);
		return R.error(BizCodeEnums.UNKNOWN_EXCEPTION.getCode(),BizCodeEnums.UNKNOWN_EXCEPTION.getMsg());
	}

}
