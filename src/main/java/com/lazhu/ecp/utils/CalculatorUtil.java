package com.lazhu.ecp.utils;

import java.math.BigDecimal;

/**
 * 数字处理类
 * 【计算器】
 * @author hz
 * @date 2017年9月7日  
 * @version 1.0.0
 */

public class CalculatorUtil {
	
	/**
	 * 除法运算（保留N位小数）
	 * @param numerator		除数
	 * @param denominator	被除数
	 * @param decimal		保留小数位
	 * @return
	 */
	public static Double divisionKeepDecimal(Double numerator, Double denominator,Integer decimal) {
		if (decimal == null) {
			decimal = 2;
		}
		denominator = (denominator == null || numerator == null) ? 0D : denominator;
		BigDecimal b = new BigDecimal(denominator.doubleValue() == 0 ? 0D : numerator/denominator);  
		return b.setScale(decimal, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 计算百分比
	 * @param numerator		除数
	 * @param denominator	被除数
	 * @return
	 */
	public static Double percent (Double numerator, Double denominator) {
		denominator = (denominator == null || numerator == null) ? 0D : denominator;
		BigDecimal b = new BigDecimal(denominator.intValue() == 0 ? 0D : (numerator/denominator)*100);  
		return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static void main(String[] args) {
		Integer a = 6,b = 9;
		System.out.println(divisionKeepDecimal(a * 1.0,b * 1.0,20));
	}
	
}
