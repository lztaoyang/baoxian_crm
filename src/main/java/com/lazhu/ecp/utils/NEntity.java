package com.lazhu.ecp.utils;

import java.util.Date;

public class NEntity {
	
	private String name;
	private int age;
	private Date brithday;
	
	
	
	public NEntity() {
		super();
	}
	
	public NEntity(String name, int age, Date brithday) {
		super();
		this.name = name;
		this.age = age;
		this.brithday = brithday;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Date getBrithday() {
		return brithday;
	}
	public void setBrithday(Date brithday) {
		this.brithday = brithday;
	}

	
}
