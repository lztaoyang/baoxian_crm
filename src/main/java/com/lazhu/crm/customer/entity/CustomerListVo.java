package com.lazhu.crm.customer.entity;

import com.lazhu.crm.signapply.entity.SignApply;

/**
 * 成交客户和当前订单 VO实体类
 *
 * @author hz
 * @date 2018年1月4日  
 * @version 1.0.0 
 */
public class CustomerListVo {
	
	//成交客户
	private Customer customer;
	
	//当前订单
	private SignApply apply;
	
	public CustomerListVo() {
		super();
	}
	
	public CustomerListVo(Customer customer, SignApply apply) {
		super();
		this.customer = customer;
		this.apply = apply;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public SignApply getApply() {
		return apply;
	}
	public void setApply(SignApply apply) {
		this.apply = apply;
	}

}
