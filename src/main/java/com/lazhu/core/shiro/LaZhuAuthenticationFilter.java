package com.lazhu.core.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

public class LaZhuAuthenticationFilter extends FormAuthenticationFilter {

	private String adminLoginUrl;
	private String customerLoginUrl;
	protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
		String loginUrl = super.getLoginUrl();
		if (request instanceof HttpServletRequest){
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			if (httpRequest.getRequestURI().startsWith("/sys/")){
				loginUrl = getAdminLoginUrl();
			}else{
				loginUrl = getCustomerLoginUrl();
			}
		}
		WebUtils.issueRedirect(request, response, loginUrl);
	}

	public String getAdminLoginUrl() {
		return adminLoginUrl;
	}

	public void setAdminLoginUrl(String adminLoginUrl) {
		this.adminLoginUrl = adminLoginUrl;
	}

	

	public String getCustomerLoginUrl() {
		return customerLoginUrl;
	}

	public void setCustomerLoginUrl(String customerLoginUrl) {
		this.customerLoginUrl = customerLoginUrl;
	}

	

}
