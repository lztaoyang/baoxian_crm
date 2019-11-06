package com.lazhu.crm.customerrefund.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;
import com.lazhu.crm.signapply.entity.SignApply;

/**
 * <p>
 * 
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@ApiModel(value = "", description = "customerRefund")
@TableName("crm_customer_refund")
public class CustomerRefund extends BaseModel {
	private static final long serialVersionUID = 1L;
	// CUSTOMER_ID 客户ID
	@ApiModelProperty(value = "客户ID", required = false)
	@TableField("CUSTOMER_ID")
	private Long customerId;
	// SINGAPPLY_ID 订单ID
	@ApiModelProperty(value = "订单ID", required = false)
	@TableField("SIGNAPPLY_ID")
	private Long signApplyId;
	// CUSTOMER_NAME 客户姓名
	@ApiModelProperty(value = "客户姓名", required = false)
	@TableField("CUSTOMER_NAME")
	private String customerName;
	// REFUND_MONEY 退款金额
	@ApiModelProperty(value = "退款金额", required = false)
	@TableField("REFUND_MONEY")
	private BigDecimal refundMoney;
	// DEALER_ID 退款处理人ID
	@ApiModelProperty(value = "退款处理人ID", required = false)
	@TableField("DEALER_ID")
	private Long dealerId;
	// DEALER_NAME 退款处理人姓名
	@ApiModelProperty(value = "退款处理人姓名", required = false)
	@TableField("DEALER_NAME")
	private String dealerName;
	// DEALER_STATUS 处理状态
	@ApiModelProperty(value = "处理状态", required = false)
	@TableField("DEALER_STATUS")
	private Integer dealerStatus;
	// DEALER_RESULT 处理意见
	@ApiModelProperty(value = "处理意见", required = false)
	@TableField("DEALER_RESULT")
	private String dealerResult;
	@ApiModelProperty(value = "是否退款成功", required = false)
	@TableField("is_success")
	private Integer isSuccess;
	@ApiModelProperty(value = "退款类型", required = false)
	@TableField("REFUND_TYPE")
	private Integer refundType;
	@ApiModelProperty(value = "满意度", required = false)
	@TableField("satisfaction")
	private Integer satisfaction;

	// 订单信息
	@TableField(exist = false)
	private SignApply signApply;

	// belong 市场部归属
	@TableField(exist = false)
	private String belong;


	public String getBelong() {
		return belong;
	}

	public void setBelong(String belong) {
		this.belong = belong;
	}

	public SignApply getSignApply() {
		return signApply;
	}

	public void setSignApply(SignApply signApply) {
		this.signApply = signApply;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getCustomerId() {
		return this.customerId;
	}

	public Long getSignApplyId() {
		return signApplyId;
	}

	public void setSignApplyId(Long signApplyId) {
		this.signApplyId = signApplyId;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerName() {
		return this.customerName;
	}

	public void setRefundMoney(BigDecimal refundMoney) {
		this.refundMoney = refundMoney;
	}

	public BigDecimal getRefundMoney() {
		return this.refundMoney;
	}

	public void setDealerId(Long dealerId) {
		this.dealerId = dealerId;
	}

	public Long getDealerId() {
		return this.dealerId;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getDealerName() {
		return this.dealerName;
	}

	public void setDealerStatus(Integer dealerStatus) {
		this.dealerStatus = dealerStatus;
	}

	public Integer getDealerStatus() {
		return this.dealerStatus;
	}

	public void setDealerResult(String dealerResult) {
		this.dealerResult = dealerResult;
	}

	public String getDealerResult() {
		return this.dealerResult;
	}

	public Integer getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Integer isSuccess) {
		this.isSuccess = isSuccess;
	}

	public Integer getRefundType() {
		return refundType;
	}

	public void setRefundType(Integer refundType) {
		this.refundType = refundType;
	}

	public Integer getSatisfaction() {
		return satisfaction;
	}

	public void setSatisfaction(Integer satisfaction) {
		this.satisfaction = satisfaction;
	}

}
