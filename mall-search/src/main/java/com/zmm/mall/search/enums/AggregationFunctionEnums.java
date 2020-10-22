package com.zmm.mall.search.enums;

/**
 * @Name AggregationFunctionEnums
 * @Author 900045
 * @Created by 2020/9/17 0017
 */
public enum AggregationFunctionEnums {

	AGE_AGG(1,"ageAgg"),
	BALANCE_AVG(2,"balanceAvg");

	;


	private int code;
	private String name;
	AggregationFunctionEnums(int code,String name){
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}}
