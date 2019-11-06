package com.lazhu.ecp.utils;

import java.util.Date;

public class MEntity {
	
	private int id;
	private String name;
	private int age;
	private Date brithday;
	private String adress;
	
	
	public MEntity() {
		super();
	}


	public MEntity(int id, String name, int age, Date brithday, String adress) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
		this.brithday = brithday;
		this.adress = adress;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
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


	public String getAdress() {
		return adress;
	}


	public void setAdress(String adress) {
		this.adress = adress;
	}

	
}
