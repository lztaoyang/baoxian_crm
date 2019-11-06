package com.lazhu.crm.resource.entity;

import java.util.List;

import com.lazhu.crm.salerrecord.entity.SalerRecord;

public class ResourceDetailsVo {
	
	Resource resource;
	// 全部通话
	List<SalerRecord> salerRecord;
	// 30秒以上的接通电话
	List<SalerRecord> salerRecord1;
	
	public ResourceDetailsVo() {
		super();
	}

//	public ResourceDetailsVo(Resource resource, List<SalerRecord> salerRecord) {
//		super();
//		this.resource = resource;
//		this.salerRecord = salerRecord;
//	}
	
	public ResourceDetailsVo(Resource resource, List<SalerRecord> salerRecord, List<SalerRecord> salerRecord1) {
		super();
		this.resource = resource;
		this.salerRecord = salerRecord;
		this.salerRecord1 = salerRecord1;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
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

}
