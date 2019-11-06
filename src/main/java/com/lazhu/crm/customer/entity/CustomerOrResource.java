package com.lazhu.crm.customer.entity;

import java.util.Date;

/**
 * 
 * 会员、资源共用实体类
 *
 * @author hz
 * @date 2018年1月8日  
 * @version 1.0.0
 */
public class CustomerOrResource {

	//姓名
	private String name;
	//手机号
	private String mobile;
	//市场部归属
	private String belong;
	//是否签单
	private Integer isSign;
	//入库时间（客户提交时间）
	private Date enterTime;
	//开发流程
	private Integer flowId;
	
	public CustomerOrResource() {
		super();
	}

	public CustomerOrResource(String name, String mobile, String belong,
			Integer isSign, Date enterTime, Integer flowId) {
		super();
		this.name = name;
		this.mobile = mobile;
		this.belong = belong;
		this.isSign = isSign;
		this.enterTime = enterTime;
		this.flowId = flowId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getBelong() {
		return belong;
	}

	public void setBelong(String belong) {
		this.belong = belong;
	}

	public Integer getIsSign() {
		return isSign;
	}

	public void setIsSign(Integer isSign) {
		this.isSign = isSign;
	}

	public Date getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(Date enterTime) {
		this.enterTime = enterTime;
	}

	public Integer getFlowId() {
		return flowId;
	}

	public void setFlowId(Integer flowId) {
		this.flowId = flowId;
	}
	
}
