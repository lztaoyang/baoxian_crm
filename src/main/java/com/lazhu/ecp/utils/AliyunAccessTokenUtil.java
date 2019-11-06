package com.lazhu.ecp.utils;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.lazhu.core.util.CacheUtil;
import com.lazhu.core.util.DateUtil;
import com.lazhu.core.util.HttpUtil;
import com.lazhu.core.util.PropertiesUtil;

/**
 * 获取阿里云accessToken
 * 
 * @author 庞红
 *
 */
public class AliyunAccessTokenUtil {
	
	protected static final Logger logger = LogManager.getLogger(AliyunAccessTokenUtil.class);
	// 储存token到redis的key
	private final static String key = "ALIYUN_ACCESS_TOKEN";

	// 获取token, 先从redis中获取如果失效则重新获取并保存到Redis中
	public static String getAccessToken() {
		// 先从redis中获取token
		AccessToken accessToken = (AccessToken) CacheUtil.getCache().get(key);
		// 是否过期
		if (accessToken == null || isInvalid(accessToken.getInvalidTime())) {
			// token过期或者为空==========================================
			// 用户DATAIDE中的accessId
			String accessId = PropertiesUtil.getString("aliyun.token.accessId");
			// 用户DATAIDE中的accessKey
			String accessKey = PropertiesUtil.getString("aliyun.token.accessKey");
			// 登录DATAIDE时使用的阿里云账号
			String aliyunId = PropertiesUtil.getString("aliyun.token.aliyunId");
			// token失效时间 默认是60分钟；如传值5 表示五分钟后失效，如传值120 表示120分钟后失效；最长不超过4小时240
			String validityTime = PropertiesUtil.getString("aliyun.token.validityTime");
			// 获取阿里云token的URL
			String url = PropertiesUtil.getString("aliyun.token.url");
			url = url.replace("#ACCESSID#", accessId).replace("#ACCESSKEY#", accessKey).replace("#ALIYUNID#", aliyunId)
					.replace("#VALIDITYTIME#", validityTime);
			// 执行http请求获取
			String res = HttpUtil.httpClientPost(url);
			logger.debug("aliyun_token=" + res);
			JSONObject json = JSONObject.parseObject(res);
			if (json.containsKey("data")){
				json = json.getJSONObject("data");
				accessToken = JSONObject.parseObject(json.getString("accessToken"), AccessToken.class);
				// 保存到redis中
				CacheUtil.getCache().set(key, accessToken);
			}
		}
		// 返回token
		return accessToken.getAccessToken();
	}

	/**
	 * 是否过期
	 * 
	 * @param invalidTime
	 * @return
	 */
	private static boolean isInvalid(String invalidTime) {
		long invalid = DateUtil.stringToDate(invalidTime).getTime() ;
		long current = System.currentTimeMillis() + 600000;
		return invalid < current;
	}

	/**
	 * 阿里云token对象
	 */
	@SuppressWarnings({ "unused", "serial" })
	private static class AccessToken implements Serializable{
		private String accessToken;// token
		private String invalidTime;// 失效时间
		private String registerTime;// 注册时间

		public String getAccessToken() {
			return accessToken;
		}

		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}

		public String getInvalidTime() {
			return invalidTime;
		}

		public void setInvalidTime(String invalidTime) {
			this.invalidTime = invalidTime;
		}

		public String getRegisterTime() {
			return registerTime;
		}

		public void setRegisterTime(String registerTime) {
			this.registerTime = registerTime;
		}

	}
}
