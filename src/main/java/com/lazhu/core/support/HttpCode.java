package com.lazhu.core.support;

import com.lazhu.core.config.Resources;

/**
 * Ajax 请求时的自定义查询状态码，主要参考Http状态码，但并不完全对应
 * 
 * @author naxj
 * 
 */
public enum HttpCode {
	/** 200请求成功 */
	OK(200),
	/** 207频繁操作 */
	MULTI_STATUS(207),
	/** 303登录失败 */
	LOGIN_FAIL(303),
	/** 400请求参数出错 */
	BAD_REQUEST(400),
	/** 401没有登录 */
	UNAUTHORIZED(401),
	/** 403没有权限 */
	FORBIDDEN(403),
	/** 404找不到页面 */
	NOT_FOUND(404),
	/** 408请求超时 */
	REQUEST_TIMEOUT(408),
	/** 409发生冲突 */
	CONFLICT(409),
	/** 410已被删除 */
	GONE(410),
	/** 423已被锁定 */
	LOCKED(423),
	/** 500服务器出错 */
	INTERNAL_SERVER_ERROR(500),
	
	/** 登陆钉钉重复冲突 */
	LOGIN_DING_REPEAT(431),
	/** 登陆钉钉不匹配 */
	LOGIN_DING_UNMATCH(432),
	/** 登陆地址未识别 */
	LOGIN_ADDRESS_NULL(433),
	/** 登陆地址不匹配 */
	LOGIN_ADDRESS_NUMATCH(434),
	/** 登陆用户名不匹配 */
	LOGIN_ACCOUNT_NUMATCH(435);

	private final Integer value;

	private HttpCode(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

	public String msg() {
		return Resources.getMessage("HTTPCODE_" + this.value);
	}

	public String toString() {
		return this.value.toString();
	}
}
