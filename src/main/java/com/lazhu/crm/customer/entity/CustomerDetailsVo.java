package com.lazhu.crm.customer.entity;

import java.util.List;

import com.lazhu.crm.customerrefund.entity.CustomerRefund;
import com.lazhu.crm.salerrecord.entity.SalerRecord;
import com.lazhu.crm.serverrecord.entity.ServerRecord;
import com.lazhu.crm.serverrecordmobile.entity.ServerRecordMobile;
import com.lazhu.crm.signapply.entity.SignApply;
/**
 * 成交客户详情 VO实体类
 *
 * @author hz
 * @date 2018年1月3日  
 * @version 1.0.0
 */
public class CustomerDetailsVo {
	//成交客户
	private Customer customer;
	
	//订单
	private List<SignApply> apply;
	
	//客服服务记录
	private List<ServerRecord> serverRecord;
	
	//客服服务退保记录
	private List<CustomerRefund> refund;
	
	//市场部电话记录
	private List<SalerRecord> salerRecord;
	
	//市场部电话记录-30秒内成功通话记录
	private List<SalerRecord> salerRecord1;
	
	//客服部电话记录
	private List<ServerRecordMobile> serverRecordMobile;
	
	public CustomerDetailsVo() {
		super();
	}

	public CustomerDetailsVo(Customer customer, List<SignApply> apply,
			List<ServerRecord> serverRecord, List<CustomerRefund> refund,
			List<SalerRecord> salerRecord) {
		super();
		this.customer = customer;
		this.apply = apply;
		this.serverRecord = serverRecord;
		this.refund = refund;
		this.salerRecord = salerRecord;
	}
	
//	public CustomerDetailsVo(Customer customer, List<SignApply> apply,
//			List<ServerRecord> serverRecord, List<CustomerRefund> refund,
//			List<CustomerCompensation> compensation,List<SalerRecord> salerRecord,
//			List<ServerRecordMobile> serverRecordMobile) {
//		this.customer = customer;
//		this.apply = apply;
//		this.serverRecord = serverRecord;
//		this.refund = refund;
//		this.compensation = compensation;
//		this.salerRecord = salerRecord;
//		this.serverRecordMobile = serverRecordMobile;
//	}
	
	public CustomerDetailsVo(Customer customer, List<SignApply> apply,
			List<ServerRecord> serverRecord, List<CustomerRefund> refund,
			List<SalerRecord> salerRecord,
			List<SalerRecord> salerRecord1, List<ServerRecordMobile> serverRecordMobile) {
		this.customer = customer;
		this.apply = apply;
		this.serverRecord = serverRecord;
		this.refund = refund;
		this.salerRecord = salerRecord;
		this.salerRecord1 = salerRecord1;
		this.serverRecordMobile = serverRecordMobile;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<SignApply> getApply() {
		return apply;
	}

	public void setApply(List<SignApply> apply) {
		this.apply = apply;
	}

	public List<ServerRecord> getServerRecord() {
		return serverRecord;
	}

	public void setServerRecord(List<ServerRecord> serverRecord) {
		this.serverRecord = serverRecord;
	}

	public List<CustomerRefund> getRefund() {
		return refund;
	}

	public void setRefund(List<CustomerRefund> refund) {
		this.refund = refund;
	}

	public List<SalerRecord> getSalerRecord() {
		return salerRecord;
	}

	public void setSalerRecord(List<SalerRecord> salerRecord) {
		this.salerRecord = salerRecord;
	}

	public List<SalerRecord> getSalerRecord1() {
		return salerRecord1;
	}

	public void setSalerRecord1(List<SalerRecord> salerRecord1) {
		this.salerRecord1 = salerRecord1;
	}

	public List<ServerRecordMobile> getServerRecordMobile() {
		return serverRecordMobile;
	}

	public void setServerRecordMobile(List<ServerRecordMobile> serverRecordMobile) {
		this.serverRecordMobile = serverRecordMobile;
	}

}
