package com.lazhu.t.resourcreport.entity;

import com.lazhu.core.base.BaseModel;

/**
 * 推广资源报表数据展现实体
 *
 * @author ty
 * @date 2018年11月21日
 * 
 */
public class ResourceReportVO extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 推广维度
	 */
	String dimension;
	
	/**
	 * 资源总条数
	 */
	Integer resourceNum = 0;

	/**
	 * 资源可聊数
	 */
	Integer talkNum = 0;
	
	/**
	 * 资源订单数
	 */
	Integer orderNum = 0;
	
	/**
	 * 资源会员数
	 */
	Integer customerNum = 0;
	
	/**
	 * 总保费
	 */
	Double insureMoney = 0.0;

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public Integer getResourceNum() {
		return resourceNum;
	}

	public void setResourceNum(Integer resourceNum) {
		this.resourceNum = resourceNum;
	}

	public Integer getTalkNum() {
		return talkNum;
	}

	public void setTalkNum(Integer talkNum) {
		this.talkNum = talkNum;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public Integer getCustomerNum() {
		return customerNum;
	}

	public void setCustomerNum(Integer customerNum) {
		this.customerNum = customerNum;
	}

	public Double getInsureMoney() {
		return insureMoney;
	}

	public void setInsureMoney(Double insureMoney) {
		this.insureMoney = insureMoney;
	}
	
	
}
