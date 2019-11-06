package com.lazhu.core.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;  


/**
 * 登录token，扩展了loginType
 * @author naxj
 *
 */
public class CaptchaAuthenticationToken extends UsernamePasswordToken {  
  
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String captcha;  
	public String getCaptchaResource() {
		return captchaResource;
	}

	public void setCaptchaResource(String captchaResource) {
		this.captchaResource = captchaResource;
	}

	private String captchaResource;
    /** 
     * 用来区分前后台登录的标记 
     */  
    private String loginType;  
      
    public CaptchaAuthenticationToken(String username, String password, boolean rememberMe, String host, String captcha,String captchaResource) {  
        super(username, password, rememberMe, host);  
        this.captcha = captcha;  
        this.captchaResource = captchaResource;
    }  
    public CaptchaAuthenticationToken(String username, String password, boolean rememberMe, String host) {  
        super(username, password, rememberMe, host);  
        this.captcha = "1";  
        this.captchaResource = "1";
    }  
    public String getCaptcha() {  
        return captcha;  
    }  
  
    public void setCaptcha(String captcha) {  
        this.captcha = captcha;  
    }  
  
    public String getLoginType() {  
        return loginType;  
    }  
  
    public void setLoginType(String loginType) {  
        this.loginType = loginType;  
    }  
  
}  