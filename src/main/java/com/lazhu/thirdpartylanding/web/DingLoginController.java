package com.lazhu.thirdpartylanding.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lazhu.core.base.AbstractController;
import com.lazhu.core.support.Assert;
import com.lazhu.core.support.HttpCode;
import com.lazhu.core.support.login.LoginHelper;
import com.lazhu.core.util.WebUtil;
import com.lazhu.ecp.utils.CollectionUtil;
import com.lazhu.ecp.utils.DateUtils;
import com.lazhu.ecp.utils.DingUtil;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.model.SysUser;
import com.lazhu.thirdpartylanding.service.DingLoginService;

/**
 * 用户登录
 * 
 * @author hz
 * @since 2019-06-27
 */
@RestController
@Api(value = "钉钉登录接口", description = "钉钉登录接口")
@RequestMapping(value = "dd")
public class DingLoginController extends AbstractController {
	
	@Autowired
	private DingLoginService dingLoginService;
	
	/** 适用于钉钉ID对应多个用户 **/
	//1.根据钉钉ID匹配所有用户
	@ApiOperation(value = "匹配用户")
	@PostMapping("/match")
	//@RequestMapping(value="/login", method = RequestMethod.POST)
	public Object ddMatch(ModelMap modelMap, @RequestBody Map<String,Object> param, HttpServletRequest request,HttpServletResponse response) {
		System.out.println("匹配用户");
		String code = StrUtils.toString(param.get("code"));
		Assert.notNull(code, "扫码异常");
		CollectionUtil.mapPrint(param);
		Map<String,Object> map = dingLoginService.match(code);
		return setSuccessModelMap(modelMap,map);
	}
	
	/** 适用于钉钉ID对应多个用户  **/
	//2.选择一个用户登陆
	@ApiOperation(value = "钉钉匹配后选择用户登录")
	@PostMapping("/dd_login")
	//@RequestMapping(value="/login", method = RequestMethod.POST)
	public Object ddLogin(ModelMap modelMap, @RequestBody Map<String,Object> param, HttpServletRequest request,HttpServletResponse response) {
		System.out.println("钉钉匹配后选择用户登录");
		CollectionUtil.mapPrint(param);
		Long uid = StrUtils.toLong(param.get("uid"));
		String did = StrUtils.toString(param.get("did"));
		Assert.notNull(uid, "uid异常");
		Assert.notNull(did, "did异常");
		SysUser user = dingLoginService.ddLogin(uid,did);
		if (null != user) {
			return setSuccessModelMap(modelMap, user);
		} else {
			return setModelMap(modelMap, HttpCode.FORBIDDEN);
		}
	}
	
	/** 扫码后直接登陆 **/
	//单点登陆，不允许钉钉ID重复
	@ApiOperation(value = "扫码登录")
	@PostMapping("/login")
	//@RequestMapping(value="/login", method = RequestMethod.POST)
	public Object login(ModelMap modelMap, @RequestBody Map<String,Object> param, HttpServletRequest request,HttpServletResponse response) {
		//当前IP
		String ip = WebUtil.getHost(request);
		System.out.println("ip："+ip);
		//当前mac
		//String mac = WebUtil.getLocalMacAddress(request);
		/*String mac = null;
		try {
			UdpGetClientMacAddr uglma = new UdpGetClientMacAddr(ip);
			mac = uglma.GetRemoteMacAddr();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("mac："+mac);*/
		String code = StrUtils.toString(param.get("code"));
		Assert.notNull(code, "扫码异常");
		List<SysUser> userList = dingLoginService.login(code);
		if (null != userList && userList.size() > 0) {
			if (userList.size() == 1) {
				SysUser sysUser = userList.get(0);
				if (!sysUser.getEnable()) {
					return setModelMap(modelMap, HttpCode.GONE, "本地扫码，用户已删除");
				}
				if (sysUser.getLocked() == 1) {
					return setModelMap(modelMap, HttpCode.LOCKED, "本地扫码，用户已离职");
				}
				boolean macPermit = false;
				String mac = ip;
				if (StrUtils.isNotNullOrBlank(mac)) {
					String userMac = sysUser.getMac();
					if (StrUtils.isNullOrBlank(userMac)) {
						sysUser.setMac(mac);
						macPermit = true;
					} else {
						if (mac.equals(userMac)) {
							macPermit = true;
						} else {
							System.out.println(mac+"："+userMac);
						}
					}
				} else {
					System.out.println("本地扫码，未获取mac地址");
					return setModelMap(modelMap, HttpCode.LOGIN_ADDRESS_NULL, "本地扫码，未获取mac地址，无权限登陆");/** 403没有权限 */
				}
				macPermit = true;
				if (macPermit) {
					//登陆成功
					LoginHelper.adminLogin(userList.get(0).getAccount(), userList.get(0).getPassword());
					System.out.println("扫码登陆："+sysUser.getAccount());
					sysUser.setPasswordLose(0);
					sysUser.setLoginType(1);
					sysUser = dingLoginService.updateSysUser(sysUser);
					OperationLogTool.operationLog("LOGIN", "扫码登陆："+sysUser.getAccount()+"，"+DateUtils.DateToStr(new Date(), DateUtils.TIME_FORMAT));
					if (StrUtils.isNotNullOrBlank(sysUser.getDingId())) {
						DingUtil.sendMsg("用户："+sysUser.getAccount()+"，登录成功。时间：" + DateUtils.DateToStr(new Date(), DateUtils.TIME_FORMAT_CHINESE_SECOND),sysUser.getDingId());
					}
					return setSuccessModelMap(modelMap, sysUser);
				} else {
					System.out.println("本地扫码，非本机登陆，无权限登陆");
					return setModelMap(modelMap, HttpCode.LOGIN_ADDRESS_NUMATCH, "非本机登陆，无权限登陆");/** 403没有权限 */
				}
			} else {
				String userNames = "";
				for (SysUser sysUser : userList) {
					userNames += sysUser.getAccount();
				}
				return setModelMap(modelMap, HttpCode.LOGIN_DING_REPEAT, "查询到多个账号：" + userNames);
			}
		} else {
			return setModelMap(modelMap, HttpCode.LOGIN_DING_UNMATCH, "非编制人员，无权限登陆");/** 403没有权限 */
		}
	}
	//系统检测您的登录电脑跟上次不一致，是否申请更换登录电脑
	
}