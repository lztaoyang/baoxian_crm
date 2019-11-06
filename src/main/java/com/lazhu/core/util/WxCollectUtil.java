package com.lazhu.core.util;

import com.alibaba.fastjson.JSONObject;
import com.lazhu.ecp.utils.ConstantUtils;
import com.lazhu.policy.token.TokenUtil;

/**
 * @author leidemeng
 * 蘑菇云提供给insurance_crm的工具类，包括上传码验证和生成token两个方法
 */
public class WxCollectUtil {
	//public static String HTTP_LINK = "http://gcqd.cf69.com/insure/message/list.do?WTS_TOKEN=";
	//public static String HTTP_NEW_LINK = "http://172.16.48.89/crm/message/list.do?code=";//&WTS_TOKEN=
	private static String HTTP_LINK = ConstantUtils.getLoadConstant("wx.collect.util.url");;
	private static String HTTP_NEW_LINK = ConstantUtils.getLoadConstant("wx.collect.util.newUrl");;//&WTS_TOKEN=
	
	//url链接超时时间（分钟）
	private static Integer expireMinute = 60 * 24 * 1;
	
	/**
	 * 校验上传码，保存上传码前使用
	 * @param code
	 * @return
	 */
	public static boolean verify(String code) {
		return ShortUUID.verify(code.trim());
	}
	
	/**
	 * 生成token，通过上传码查看消息内容时使用
	 * 使用方式为访问的url后加上?WTS_TOKEN=...,，其中...即为这里生成的string字符串
	 * @param code 上传码
	 * @return
	 */
	public static String buildEZToken(String code) {
		JSONObject json = new JSONObject();
		json.put("code", code.trim());
		String jwtss = TokenUtil.buildJWT("wt", "ws", Long.valueOf(1L), Long.valueOf(2L), "3", json.toJSONString(), expireMinute);
	    return jwtss;
	}
	
	public static void main(String[] args) {
		String jwtss = getUrl("EANG-SYMG-JO");
		System.out.println(jwtss);
		String jwtss1 = getUrl("LYAT-JKGA-GP");
		System.out.println(jwtss1);
		String jwtss2 = getOldUrl("DEQB-QQOM-IG");
		System.out.println(jwtss2);
	}
	
	public static String getUrl(String code) {
		return HTTP_NEW_LINK+code+"&WTS_TOKEN="+buildEZToken(code);
	}
	
	public static String getOldUrl(String code) {
		return HTTP_LINK+buildEZToken(code);
	}
	
}
