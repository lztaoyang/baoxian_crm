package com.lazhu.ecp.utils;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.lazhu.core.util.CacheUtil;
import com.lazhu.core.util.HttpUtil;
import com.lazhu.core.util.PropertiesUtil;

/**
 * 钉钉accessToken
 * 
 * @author 庞红
 *
 */
public class DDTokenUtil {
	
	protected static final Logger logger = LogManager.getLogger(DDTokenUtil.class);
	// 储存token到redis的key
	private final static String key = "DD_ACCESS_TOKEN";

	// 获取token, 先从redis中获取如果失效则重新获取并保存到Redis中
	public static String getAccessToken() {
		// 先从redis中获取token
		AccessToken accessToken = (AccessToken) CacheUtil.getCache().get(key);
		// 是否过期
		if (accessToken == null || isInvalid(accessToken.getInvalidTime())) {
			// token过期或者为空==========================================
			// 用户DATAIDE中的accessId
			String accessId = PropertiesUtil.getString("dd.token.accessId");
			// 用户DATAIDE中的accessKey
			String accessKey = PropertiesUtil.getString("dd.token.accessKey");
			// 获取阿里云token的URL
			String url = PropertiesUtil.getString("dd.token.url");
			url = url.replace("#ACCESSID#", accessId).replace("#ACCESSKEY#", accessKey);
			// 执行http请求获取
			String res = HttpUtil.httpClientPost(url);
			logger.debug("aliyun_token=" + res);
			JSONObject json = JSONObject.parseObject(res);
			if (json.containsKey("access_token")){
				accessToken = new AccessToken();
				accessToken.setAccessToken(json.getString("access_token"));
				accessToken.setInvalidTime(System.currentTimeMillis() + 7200000);
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
	private static boolean isInvalid(Long invalidTime) {
		long current = System.currentTimeMillis() + 600000;
		return invalidTime < current;
	}

	/**
	 * 阿里云token对象
	 */
	@SuppressWarnings({ "unused", "serial" })
	private static class AccessToken implements Serializable{
		private String accessToken;// token
		private Long invalidTime;// 失效时间
		private String registerTime;// 注册时间

		public String getAccessToken() {
			return accessToken;
		}

		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}

		public Long getInvalidTime() {
			return invalidTime;
		}

		public void setInvalidTime(Long invalidTime) {
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
