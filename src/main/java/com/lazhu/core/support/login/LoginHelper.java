package com.lazhu.core.support.login;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;

import com.lazhu.core.config.Resources;
import com.lazhu.core.exception.LoginException;
import com.lazhu.core.shiro.CaptchaAuthenticationToken;
import com.lazhu.core.shiro.CaptchaException;
import com.lazhu.core.shiro.LoginEnum;

/**
 * @author naxj
 * 
 */
public final class LoginHelper {
	private LoginHelper() {
	}
	/** 用户登录 */
	public static final Boolean customLogin(String account, String password) {
		return LoginHelper.login(account, password, null, "1", "1", LoginEnum.CUSTOMER.toString());
	}
	/** 用户登录 */
	public static final Boolean adminLogin(String account, String password) {
		return LoginHelper.login(account, password, null, "1", "1", LoginEnum.ADMIN.toString());
	}
	/** 用户登录 */
	public static final Boolean login(String account, String password,String host,String captcha,String captchaResource,String loginType) {
		CaptchaAuthenticationToken token = new CaptchaAuthenticationToken(account, password,true,host,captcha,captchaResource);
		token.setLoginType(loginType);
//		token.setRememberMe(true);
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(token);
			return subject.isAuthenticated();
		} catch (CaptchaException e) {
			throw new LoginException(Resources.getMessage("ACCOUNT_CAPTCHERROR", token.getPrincipal()));
		} catch (IncorrectCredentialsException e) {
			throw new LoginException(Resources.getMessage("ACCOUNT_PASSWORDERROR", token.getPrincipal()));
		} catch (UnknownAccountException e) {
			throw new LoginException(Resources.getMessage("ACCOUNT_UNKOWN", token.getPrincipal()));
		} catch (LockedAccountException e) {
			throw new LoginException(Resources.getMessage("ACCOUNT_LOCKED", token.getPrincipal()));
		} catch (DisabledAccountException e) {
			throw new LoginException(Resources.getMessage("ACCOUNT_DISABLED", token.getPrincipal()));
		} catch (ExpiredCredentialsException e) {
			throw new LoginException(Resources.getMessage("ACCOUNT_EXPIRED", token.getPrincipal()));
		} catch (Exception e) {
			throw new LoginException(Resources.getMessage("LOGIN_FAIL"), e);
		}
	}
}
