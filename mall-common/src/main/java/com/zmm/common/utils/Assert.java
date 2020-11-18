package com.zmm.common.utils;

import com.zmm.common.base.model.RespCode;
import com.zmm.common.base.model.ResultCode;
import com.zmm.common.enums.AssertEnum;
import com.zmm.common.exception.CustomRunTimeException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @Name Assert 断言类
 * @Author 900045
 * @Created by 2020/11/18 0018
 */
public final class Assert {


	// ==============================抛出异常区域 START ==============================================================

	/**
	 *	断言为空 抛出异常 CustomRunTimeException
	 * @param object
	 * @param message
	 */
	public static void isNull(Object object, String message) {
		if (object != null) {
			throw new CustomRunTimeException(message);
		}
	}

	/**
	 * 断言为 正整数 抛出异常 CustomRunTimeException
	 * @param number
	 * @param respCode
	 * @param message
	 */
	public static void isPositiveInteger(Number number, RespCode respCode, String message) {
		if (number == null || number.longValue() <= 0) {
			throw new CustomRunTimeException(respCode, message);
		}
	}

	/**
	 *  参数不能为空， 为空时抛出运行时异常
	 *
	 * @param object
	 * @param message
	 */
	public static void notNull(Object object, RespCode respCode, String message) {
		if (object == null) {
			throw new CustomRunTimeException(respCode, message);
		}
	}


	/**
	 * 字符串不能为空
	 *
	 * @param text
	 * @param respCode
	 * @param message
	 */
	public static void notEmpty(String text, RespCode respCode, String message) {
		if (!StringUtils.hasText(text)) {
			throw new CustomRunTimeException(respCode, message);
		}
	}

	/**
	 * 断言集合为空
	 *
	 * @param collection
	 * @param respCode
	 * @param message
	 */
	public static void notEmpty(Collection<?> collection, RespCode respCode, String message) {
		if (CollectionUtils.isEmpty(collection)) {
			throw new CustomRunTimeException(respCode, message);
		}
	}


	public static void notEmpty(Map<?, ?> map, String message) {
		if (CollectionUtils.isEmpty(map)) {
			throw new CustomRunTimeException(message);
		}
	}





	public static void state(boolean expression, String message) {
		if (!expression) {
			throw new CustomRunTimeException(message);
		}
	}

	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new CustomRunTimeException(ResultCode.METHOD_CALL_PARAMETER_ERROR);
		}
	}

	public static void hasLength(String text, String message) {
		if (!StringUtils.hasLength(text)) {
			throw new CustomRunTimeException(message);
		}
	}

	public static void hasText(String text, String message) {
		if (!StringUtils.hasText(text)) {
			throw new CustomRunTimeException(message);
		}
	}

	public static void doesNotContain(String textToSearch, String substring, String message) {
		if (StringUtils.hasLength(textToSearch) && StringUtils.hasLength(substring)
				&& textToSearch.contains(substring)) {
			throw new CustomRunTimeException(message);
		}
	}


	public static void notEmpty(Object[] array, String message) {
		if (ObjectUtils.isEmpty(array)) {
			throw new CustomRunTimeException(message);
		}
	}

	public static void noNullElements(Object[] array, String message) {
		if (array != null) {
			for (Object element : array) {
				if (element == null) {
					throw new CustomRunTimeException(message);
				}
			}
		}
	}


	// ==============================抛出异常区域 END ==============================================================


	// ==============================判断为正整数  START ===========================================================


	/**
	 * 断言为 正整数
	 *
	 * @param number
	 */
	public static void isPositiveInteger(Number number) {
    	isPositiveInteger(number, ResultCode.METHOD_CALL_PARAMETER_ERROR, AssertEnum.NOT_POSITIVE_INTEGER.getMessage());
	}

	// ==============================判断为正整数  END ===========================================================



	// ==============================判断参数为空  START ==========================================================

	/**
	 *  参数不能为空， 为空时抛出运行时异常
	 *
	 * @param object
	 */
	public static void notNull(Object object) {
		notNull(object, ResultCode.METHOD_CALL_PARAMETER_ERROR, AssertEnum.PARAMETER_CANNOT_NULL.getMessage());
	}

	/**
	 *  参数不能为空， 为空时抛出运行时异常
	 *
	 * @param object
	 * @param message
	 */
	public static void notNull(Object object, String message) {
		notNull(object, ResultCode.METHOD_CALL_PARAMETER_ERROR, message);
	}


	/**
	 *  参数不能为空， 为空时抛出运行时异常
	 *
	 * @param object
	 * @param respCode
	 */
	public static void notNull(Object object, RespCode respCode) {
		notNull(object, respCode, null);
	}


	// ==============================判断参数为空  END ==========================================================


	// ==============================判断字符串不能为空  START ==================================================

	/**
	 *  字符串不能为空
	 *
	 * @param text
	 */
	public static void notEmpty(String text) {
		notEmpty(text, ResultCode.METHOD_CALL_PARAMETER_ERROR, AssertEnum.STRING_CANNOT_NULL.getMessage());
	}


	/**
	 *  字符串不能为空
	 *
	 * @param text  字符串
	 * @param message  提示信息
	 */
	public static void notEmpty(String text, String message) {
		notEmpty(text, ResultCode.METHOD_CALL_PARAMETER_ERROR, message);
	}


	/**
	 * 字符串不能为空
	 *
	 * @param text
	 * @param respCode
	 */
	public static void notEmpty(String text, RespCode respCode) {
		notEmpty(text, ResultCode.METHOD_CALL_PARAMETER_ERROR, null);
	}

	// ==============================判断字符串不能为空  END ==================================================


	// ==============================判断集合为空  START =======================================================

	/**
	 * 断言集合为空
	 *
	 * @param collection
	 */
	public static void notEmpty(Collection<?> collection) {
		notEmpty(collection, ResultCode.METHOD_CALL_PARAMETER_ERROR, null);
	}

	/**
	 * 断言集合为空
	 *
	 * @param collection
	 * @param message
	 */
	public static void notEmpty(Collection<?> collection, String message) {
		notEmpty(collection, ResultCode.METHOD_CALL_PARAMETER_ERROR, message);
	}

	// ==============================判断集合为空  END =======================================================
}
