package com.lazhu.core.util2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用来对一些数据进行模糊化处理
 * 
 * @author wHui
 * 
 *         2016年5月9日
 */
public class FuzzyUtil {

	private static FuzzyUtil fuzzy = null;

	private FuzzyUtil() {

	}

	private static void init() {
		fuzzy = new FuzzyUtil();
	}

	public static FuzzyUtil getInstance() {
		if (fuzzy == null) {
			init();
		}
		return fuzzy;
	}

	/**
	 * 验证手机号
	 */
	private boolean checkMobile(String mobile) {
		Pattern p = Pattern
				.compile("^1\\d{10}$");
		Matcher m = p.matcher(mobile);
		return m.matches();
	}

	/**
	 * 模糊手机号
	 */
	public String fuzzyMobile(String mobile) {
		if(checkMobile(mobile)){
			return mobile.substring(0, 3).concat("****").concat(mobile.substring(7));
		}
		return null;
	}

}
