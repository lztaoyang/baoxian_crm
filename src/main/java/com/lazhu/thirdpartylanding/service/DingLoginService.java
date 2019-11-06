package com.lazhu.thirdpartylanding.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.request.OapiUserGetUseridByUnionidRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import com.dingtalk.api.response.OapiUserGetUseridByUnionidResponse;
import com.lazhu.core.support.login.LoginHelper;
import com.lazhu.core.util.ComplexMD5Util;
import com.lazhu.core.util.InstanceUtil;
import com.lazhu.core.util.PropertiesUtil;
import com.lazhu.ecp.utils.DateUtils;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.model.SysUser;
import com.lazhu.sys.service.SysUserService;
import com.taobao.api.ApiException;

/**
 * <p>
 * 
 * </p>
 *
 * @author hz
 * @since 2019-06-27
 */
@Service
public class DingLoginService {
	
	@Autowired
	private SysUserService sysUserService;
	
	// 用于扫码登录后获取用户姓名和unionid、openid
	private static String appId = PropertiesUtil.getString("oauth.ding.appId");
	private static String appSecret = PropertiesUtil.getString("oauth.ding.appSecret");
	
	// 用于获取accessToken，授权
	private static String app_key = PropertiesUtil.getString("token.ding.app_key");
	private static String app_secret = PropertiesUtil.getString("token.ding.app_secret");
	
	//private static final String DING_TOKENKEY = "ding:accesstoken";
	
	private String DING_TOKENKEY = null;
	
	private Date DING_TOKENKEY_CREATETIME = null;
	
	/**
	 * match
	 */
	public Map<String,Object> match(String code) {
		Map<String,Object> map = InstanceUtil.newHashMap();
		try {
			DefaultDingTalkClient  client = new DefaultDingTalkClient("https://oapi.dingtalk.com/sns/getuserinfo_bycode");
			OapiSnsGetuserinfoBycodeRequest dingReq = new OapiSnsGetuserinfoBycodeRequest();
			dingReq.setTmpAuthCode(code);
			OapiSnsGetuserinfoBycodeResponse dingResp = client.execute(dingReq,appId,appSecret);
			//System.out.println("钉钉授权返回用户信息：" + dingResp.getBody());
			OperationLogTool.operationLog("LOGIN", "钉钉授权返回用户信息："+ dingResp.getBody() +"，"+DateUtils.DateToStr(new Date(), DateUtils.TIME_FORMAT));
			JSONObject json = JSONObject.parseObject(dingResp.getBody());
			JSONObject infoJson = JSONObject.parseObject(json.getString("user_info"));
			String unionid = infoJson.getString("unionid");//用户在当前开放应用所属的钉钉开放平台账号内的唯一标识
			if (StrUtils.isNotNullOrBlank(unionid)) {
				//更新授权
				String accessToken = getAccessToken();
				String userId = getUserId(unionid, accessToken);
				List<Long> userIds = sysUserService.queryByDIds(userId);
				if (null != userIds && userIds.size() > 0) {
					List<SysUser> userList = sysUserService.getList(userIds);
					if (null != userList && userList.size() > 0) {
						for (SysUser sysUser : userList) {
							//返回可选择的用户
							map.put(StrUtils.toString(sysUser.getId()), ComplexMD5Util.MD5Encode(sysUser.getDingId()));
						}
					}
				}
			}
		} catch (ApiException e) {
			System.out.println("调用钉钉获取用户信息接口失败===,{}");
			System.out.println(e.getErrMsg());
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * ddLogin
	 */
	public SysUser ddLogin(Long uid, String did) {
		SysUser user = sysUserService.queryById(uid);
		if (null != user && StrUtils.isNotNullOrBlank(user.getDingId())) {
			String md5_dingId = ComplexMD5Util.MD5Encode(user.getDingId());
			if (md5_dingId.equals(did)) {
				//登陆成功
				LoginHelper.adminLogin(user.getAccount(), user.getPassword());
				return user;
			}
		}
		return null;
	}

	/**
	 * login
	 */
	public List<SysUser> login(String code) {
		List<SysUser> userList = null;
		try {
			DefaultDingTalkClient  client = new DefaultDingTalkClient("https://oapi.dingtalk.com/sns/getuserinfo_bycode");
			OapiSnsGetuserinfoBycodeRequest dingReq = new OapiSnsGetuserinfoBycodeRequest();
			dingReq.setTmpAuthCode(code);
			OapiSnsGetuserinfoBycodeResponse dingResp = client.execute(dingReq,appId,appSecret);
			//System.out.println("钉钉授权返回用户信息：" + dingResp.getBody());
			OperationLogTool.operationLog("LOGIN", "钉钉授权返回用户信息："+ dingResp.getBody() +"，"+DateUtils.DateToStr(new Date(), DateUtils.TIME_FORMAT));
			JSONObject json = JSONObject.parseObject(dingResp.getBody());
			JSONObject infoJson = JSONObject.parseObject(json.getString("user_info"));
			String unionid = infoJson.getString("unionid");//用户在当前开放应用所属的钉钉开放平台账号内的唯一标识
			if (StrUtils.isNotNullOrBlank(unionid)) {
				//更新授权
				String accessToken = getAccessToken();
				String userId = getUserId(unionid, accessToken);
				if (StrUtils.isNotNullOrBlank(userId)) {
					List<Long> userIds = sysUserService.queryByDIds(userId);
					if (null != userIds && userIds.size() > 0) {
						if (null != userIds && userIds.size() > 0) {
							userList = sysUserService.getList(userIds);
						}
					}
				}
				
			}
		} catch (ApiException e) {
			System.out.println("调用钉钉获取用户信息接口失败===,{}");
			System.out.println(e.getErrMsg());
			e.printStackTrace();
		}
		return userList;
	}
	
	/**
	 * 获取AccessToken
	 * @param tokenKey
	 * @return
	 * @throws Exception
	 * https://open-doc.dingtalk.com/microapp/serverapi2/eev437
	 */
	public String getAccessToken(){
		try {
			//验证是否超时100分钟，如果超时重新获取
			if (null == DING_TOKENKEY || null == DING_TOKENKEY_CREATETIME || !DateUtils.timeOut(DING_TOKENKEY_CREATETIME, 100L)) {
				DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
				OapiGettokenRequest request = new OapiGettokenRequest();
				request.setAppkey(app_key);
				request.setAppsecret(app_secret);
				request.setHttpMethod("GET");
				OapiGettokenResponse response;
				response = client.execute(request);
				DING_TOKENKEY = response.getAccessToken();
				//正常情况下access_token有效期为7200秒，有效期内重复获取返回相同结果，并自动续期
				DING_TOKENKEY_CREATETIME = new Date();
				System.out.println( "accessToken失效，重新获取：" + DateUtils.DateToStr(new Date(), DateUtils.TIME_FORMAT));
				OperationLogTool.operationLog("LOGIN", "accessToken失效，重新获取：" + DING_TOKENKEY);
			}
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return DING_TOKENKEY;
	}
	
	/**
	 * 通过unionid获取dingId
	 * @param unionid
	 * @param accessToken
	 * @return
	 * @throws ApiException
	 */
	private String getUserId(String unionid, String accessToken) throws ApiException {
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getUseridByUnionid");
		OapiUserGetUseridByUnionidRequest request = new OapiUserGetUseridByUnionidRequest();
		request.setUnionid(unionid);
		request.setHttpMethod("GET");
		OapiUserGetUseridByUnionidResponse response = client.execute(request, accessToken);
		System.out.println(response.getBody());
		JSONObject bodyJson = JSONObject.parseObject(response.getBody());
		String errcode = StrUtils.toString(bodyJson.get("errcode"));
		String userId = null;
		if (errcode.equals("0")) {
			userId = StrUtils.toString(bodyJson.get("userid"));
		}
		return userId;
	}
	
	
	public SysUser updateSysUser (SysUser sysUser) {
		return sysUserService.update(sysUser);
	}
}
