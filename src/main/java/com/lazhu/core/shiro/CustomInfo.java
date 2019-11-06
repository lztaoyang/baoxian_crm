package com.lazhu.core.shiro;
/**
 * 会员
 * @author naxj
 *
 */
public class CustomInfo {
	private Long userId;
	private String userName;
	private String password;
	private String account;
	private Integer enable = 1;
	private Integer Locked = 0;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public Integer isEnable() {
		return enable;
	}
	public void setEnable(Integer enable) {
		this.enable = enable;
	}
	public Integer getLocked() {
		return Locked;
	}
	public void setLocked(Integer locked) {
		Locked = locked;
	}
	
}
