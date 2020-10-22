package com.zmm.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @Name ListValueConstraintValidator
 * @Author 900045
 * @Created by 2020/8/31 0031
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {

	private Set<Integer> set = new HashSet<>();

	/**
	 * 初始化
	 * @param value
	 * @param context
	 * @return
	 */
	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		return set.contains(value);
	}

	@Override
	public void initialize(ListValue constraintAnnotation) {

		int[] values = constraintAnnotation.values();
		for (int value:values) {
			set.add(value);
		}
	}
}
