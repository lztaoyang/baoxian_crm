package com.lazhu.core.shiro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.util.WebUtil;
import com.lazhu.sys.model.SysSession;
import com.lazhu.sys.model.SysUser;
import com.lazhu.sys.service.SysAuthorizeService;
import com.lazhu.sys.service.SysSessionService;
import com.lazhu.sys.service.SysUserService;

/**
 * 权限检查类
 * 
 * @author naxj
 * 
 */
public class Realm extends AuthorizingRealm {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysSessionService sysSessionService;
	@Autowired
	protected SysAuthorizeService sysAuthorizeService;
	@Autowired
	private RedisOperationsSessionRepository sessionRepository;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		if (LoginEnum.CUSTOMER.toString().equals(WebUtil.getCurrentUserType())) {
			
		} else if (LoginEnum.ADMIN.toString().equals(WebUtil.getCurrentUserType())) {
			return this.doGetAuthorizationInfoAdmin(principals);
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
			throws AuthenticationException {
		CaptchaAuthenticationToken token = (CaptchaAuthenticationToken) authcToken;
		if (!token.getCaptchaResource().equals(token.getCaptcha())) {
			throw new CaptchaException("验证码错误");
		}
		if (LoginEnum.ADMIN.toString().equals(token.getLoginType())) {
			return this.doGetAuthenticationInfoAdmin(token);
		} else if (LoginEnum.CUSTOMER.toString().equals(token.getLoginType())) {
			
		}
		return null;
	}


	// 权限 管理员
	protected AuthorizationInfo doGetAuthorizationInfoAdmin(PrincipalCollection principals) {
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		Long userId = WebUtil.getCurrentUser();
		List<?> list = sysAuthorizeService.queryPermissionByUserId(userId);
		for (Object permission : list) {
			if (StringUtils.isNotBlank((String) permission)) {
				// 添加基于Permission的权限信息
				info.addStringPermission((String) permission);
			}
		}
		// 添加用户权限
		info.addStringPermission(LoginEnum.ADMIN.toString());
		return info;
	}

	// 登录验证 管理员
	protected AuthenticationInfo doGetAuthenticationInfoAdmin(AuthenticationToken authcToken)
			throws AuthenticationException {
		CaptchaAuthenticationToken token = (CaptchaAuthenticationToken) authcToken;
		if (!token.getCaptchaResource().equals(token.getCaptcha())) {
			throw new CaptchaException("验证码错误");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("countSql", 0);
		params.put("enable", 1);
		params.put("account", token.getUsername());
		Page<?> pageInfo = sysUserService.query(params);
		if (pageInfo.getTotal() == 1) {
			SysUser user = (SysUser) pageInfo.getRecords().get(0);

			StringBuilder sb = new StringBuilder(100);
			for (int i = 0; i < token.getPassword().length; i++) {
				sb.append(token.getPassword()[i]);
			}
			if (user.getPassword().equals(sb.toString())) {
				if (Integer.valueOf(1).equals(user.getLocked())) {
					throw new LockedAccountException("帐号锁定");// 帐号锁定
				}
				// if (!user.getEnable()){
				// throw new DisabledAccountException("帐号禁用");//帐号禁用
				// }
				WebUtil.saveCurrentUser(user.getId());
				WebUtil.saveCurrentUserType(LoginEnum.ADMIN.toString());
				saveSession(user.getAccount());
				AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user.getAccount(), user.getPassword(),
						user.getUserName());
				return authcInfo;
			} else {
				throw new IncorrectCredentialsException("密码错误");// 密码错误
			}
		} else {
			throw new UnknownAccountException("没找到帐号");// 没找到帐号
		}
	}

	/** 保存session */
	private void saveSession(String account) {
		// 踢出用户
		SysSession record = new SysSession();
		record.setAccount(account);
		// List<?> sessionIds =
		// sysSessionService.querySessionIdByAccount(record);
		// if (sessionIds != null) {
		// for (Object sessionId : sessionIds) {
		// record.setSessionId((String) sessionId);
		// sysSessionService.deleteBySessionId(record);
		// sessionRepository.delete((String) sessionId);
		// sessionRepository.cleanupExpiredSessions();
		// }
		// }
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
		record.setSessionId(session.getId().toString());
		String host = (String) session.getAttribute("HOST");
		record.setIp(StringUtils.isBlank(host) ? session.getHost() : host);
		record.setStartTime(session.getStartTimestamp());
		sysSessionService.update(record);
	}

}
