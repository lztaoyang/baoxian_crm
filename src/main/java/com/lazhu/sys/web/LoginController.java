package com.lazhu.sys.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dingtalk.chatbot.message.TextMessage;
import com.lazhu.core.Constants;
import com.lazhu.core.base.AbstractController;
import com.lazhu.core.config.Resources;
import com.lazhu.core.exception.LoginException;
import com.lazhu.core.support.Assert;
import com.lazhu.core.support.HttpCode;
import com.lazhu.core.support.login.LoginHelper;
import com.lazhu.core.util.SecurityUtil;
import com.lazhu.core.util.WebUtil;
import com.lazhu.crm.Constant;
import com.lazhu.ecp.utils.ConstantUtils;
import com.lazhu.ecp.utils.DateUtils;
import com.lazhu.ecp.utils.DingUtil;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.model.SysUser;
import com.lazhu.sys.service.SysSessionService;
import com.lazhu.sys.service.SysUserService;

/**
 * 用户登录
 * 
 * @author naxj
 * 
 */
@RestController
@Api(value = "登录接口", description = "登录接口")
@RequestMapping(value = "sys")
public class LoginController extends AbstractController {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysSessionService sysSessionService;
	
	// 登录
	@ApiOperation(value = "用户登录")
	@PostMapping("/login")
	//@RequestMapping(value="/login", method = RequestMethod.POST)
	public Object login(@RequestBody SysUser sysUser, ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) {
		Assert.notNull(sysUser.getAccount(), "ACCOUNT");
		Assert.notNull(sysUser.getPassword(), "PASSWORD");
		String host = WebUtil.getHost(request);
		if (LoginHelper.adminLogin(sysUser.getAccount(), SecurityUtil.encryptPassword(sysUser.getPassword()))) {
			//管理员账号信息不保存
			if (sysUser.getAccount().indexOf("admin") < 0){
				Cookie name = new Cookie("account", sysUser.getAccount());
				Cookie pwd = new Cookie("password", sysUser.getPassword());
				//设置为全局Cookie，10年有效期，返回到客户端
				name.setPath("/");
				pwd.setPath("/");
				name.setMaxAge(10 * 365 * 24 * 3600);
				pwd.setMaxAge(10 * 365 * 24 * 3600);
				response.addCookie(name);
				response.addCookie(pwd);
			}
			request.setAttribute("msg", "[" + sysUser.getAccount() + "]登录成功.");
			Long userId = WebUtil.getCurrentUser();
			if (StrUtils.isNotNullOrBlank(userId)) {
				sysUser = sysUserService.queryById(WebUtil.getCurrentUser());
			}
			
			if (null != sysUser.getUserGroup() 
					&& "product".equals(ConstantUtils.getConstant("system.version"))
					&& (sysUser.getUserGroup() == Constant.USER_GROUP_JL
						|| sysUser.getUserGroup() == Constant.USER_GROUP_YWY
						|| sysUser.getUserGroup() == Constant.USER_GROUP_JBJL
						|| sysUser.getUserGroup() == Constant.USER_GROUP_JB
						|| sysUser.getUserGroup() == Constant.USER_GROUP_KF)
			) {
				SysUser parentUser = sysUserService.queryById(sysUser.getParentId());
				String parentUserName = "上级";
				if (null != parentUser) {
					parentUserName += "：" + parentUser.getAccount() + " ";
				}
				if (StrUtils.isNotNullOrBlank(sysUser.getDingId())) {
					DingUtil.sendMsg("用户："+sysUser.getAccount()+"，登录成功。时间：" + DateUtils.DateToStr(new Date(), DateUtils.TIME_FORMAT_CHINESE_SECOND),sysUser.getDingId());
					//钉钉ID可能不正确
					if (!StrUtils.dingIdCheck(sysUser.getDingId())) {
						TextMessage message = new TextMessage("用户："+sysUser.getAccount()+"，登录时检查钉钉ID为["+ sysUser.getDingId() +"]可能不正确。请尽快在系统首页设置，由" + parentUserName + "监督 。");
						DingUtil.sendGroupMesg("ding.id.check", message);
					}
				} else {
					TextMessage message = new TextMessage("用户："+sysUser.getAccount()+"，登录时检查钉钉ID为空。请尽快在系统首页设置，由" + parentUserName + "监督。");
					DingUtil.sendGroupMesg("ding.id.check", message);
				}
			}
			System.out.println("用户："+sysUser.getAccount()+"，登录成功。钉钉ID：" + sysUser.getDingId());
			//更新状态为已登录
			sysUser.setIsOnline(1);
			sysUser.setLastOnlineTime(new Date());
			sysUserService.update(sysUser);
			return setSuccessModelMap(modelMap);
		}
		request.setAttribute("msg", "[" + sysUser.getAccount() + "]登录失败.");
		throw new LoginException(Resources.getMessage("LOGIN_FAIL"));
	}

	// 登出
	/*@ApiOperation(value = "用户登出")
	@GetMapping("/logout")
	public Object logout(ModelMap modelMap) {
		Long id = WebUtil.getCurrentUser();
		SecurityUtils.getSubject().logout();
		if (id != null) {
			sysSessionService.delete(id);
		}
		return setSuccessModelMap(modelMap);
	}*/
	@ApiOperation(value = "用户登出")
	@GetMapping("/logout")
	public Object logout(ModelMap modelMap) {
		Long id = WebUtil.getCurrentUser();
		Subject subject = SecurityUtils.getSubject();
	    if (subject.isAuthenticated()) {
	        subject.logout(); // session 会销毁，在SessionListener监听session销毁，清理权限缓存
	    }
	    //删除数据库session
		if (id != null) {
			sysSessionService.delete(id);
			System.out.println("用户："+sysUserService.queryById(id).getAccount()+"，退出登录");
		}
		//更新状态为已退出
		SysUser sysUser = sysUserService.queryById(id);
		sysUser.setIsOnline(0);
		sysUserService.update(sysUser);
		return setSuccessModelMap(modelMap);
	}

	// 注册
	@ApiOperation(value = "用户注册")
	@PostMapping("/regin")
	public Object regin(ModelMap modelMap, @RequestBody SysUser sysUser) {
		Assert.notNull(sysUser.getAccount(), "ACCOUNT");
		Assert.notNull(sysUser.getPassword(), "PASSWORD");
		sysUserService.update(sysUser);
		if (LoginHelper.adminLogin(sysUser.getAccount(), sysUser.getPassword())) {
			return setSuccessModelMap(modelMap);
		}
		throw new IllegalArgumentException(Resources.getMessage("LOGIN_FAIL"));
	}

	// 没有登录
	@ApiOperation(value = "没有登录")
	@RequestMapping(value = "/unauthorized", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT })
	public Object unauthorized(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String requestType = request.getHeader("X-Requested-With"); 
		if (requestType==null){
			response.sendRedirect(Constants.LOGIN_URL);
			return null;
		}else{
			return setModelMap(modelMap, HttpCode.UNAUTHORIZED);
		}
	}

	// 没有权限
	@ApiOperation(value = "没有权限")
	@RequestMapping(value = "/forbidden", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT })
	public Object forbidden(ModelMap modelMap) {
		return setModelMap(modelMap, HttpCode.FORBIDDEN);
	}
}
