package com.lazhu.crm.signapply.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

/**
 * <p>
 * 客户申请签单表
 * </p>
 *
 * @author hz
 * @since 2017-07-10
 */
@ApiModel(value = "客户申请签单表",description="signApply")
@TableName("crm_sign_apply")
public class SignApply extends BaseModel {
    private static final long serialVersionUID = 1L;
    //customer_id 客户ID 
    @ApiModelProperty(value = "客户ID", required = false)
    @TableField("customer_id")
    private Long customerId;
    //customer_name 客户姓名 
    @ApiModelProperty(value = "客户姓名", required = false)
    @TableField("customer_name")
    private String customerName;
    //wechat_no 客户微信 
    @ApiModelProperty(value = "客户微信", required = false)
    @TableField("wechat_no")
    private String wechatNo;
    //mobile 客户电话号码 
    @ApiModelProperty(value = "手机号", required = false)
    @TableField(exist = false)
    private String mobile;
    @ApiModelProperty(value = "MD5加密", required = false)
    @TableField("md5_mobile")
    private String md5Mobile;
    @ApiModelProperty(value = "掩码手机号", required = false)
    @TableField("fuzzy_mobile")
    private String fuzzyMobile;
    //from_info 来源渠道 
    @ApiModelProperty(value = "来源渠道", required = false)
    @TableField("from_info")
    private String fromInfo;
    //insure_num 当前投保次数 
    @ApiModelProperty(value = "当前投保次数", required = false)
    @TableField("insure_num")
    private Integer insureNum;
    //insurance_id 投保产品ID 
    @ApiModelProperty(value = "产品ID", required = false)
    @TableField("insurance_id")
    private Long insuranceId;
    //insurance_name 投保产品名称 
    @ApiModelProperty(value = "产品名称", required = false)
    @TableField("insurance_name")
    private String insuranceName;
    //amount 缴费金额 
    @ApiModelProperty(value = "缴费金额", required = false)
    @TableField("amount")
    private Double amount;
    @ApiModelProperty(value = "服务开始日期", required = false)
    @TableField("start_date")
    private Date startDate;
    @ApiModelProperty(value = "服务结束日期", required = false)
    @TableField("end_date")
    private Date endDate;
    //saler_id 业务员ID 
    @ApiModelProperty(value = "业务员ID", required = false)
    @TableField("saler_id")
    private Long salerId;
    //manager_id 经理ID 
    @ApiModelProperty(value = "经理ID", required = false)
    @TableField("manager_id")
    private Long managerId;
    //director_id 总监ID 
    @ApiModelProperty(value = "总监ID", required = false)
    @TableField("director_id")
    private Long directorId;
    //minister_id 总经理ID 
    @ApiModelProperty(value = "总经理ID", required = false)
    @TableField("minister_id")
    private Long ministerId;
    //saler_name 业务员姓名 
    @ApiModelProperty(value = "业务员姓名", required = false)
    @TableField("saler_name")
    private String salerName;
    //manager_name 经理姓名 
    @ApiModelProperty(value = "经理姓名", required = false)
    @TableField("manager_name")
    private String managerName;
    //director_name 总监姓名 
    @ApiModelProperty(value = "总监姓名", required = false)
    @TableField("director_name")
    private String directorName;
    //minister_name 总经理姓名 
    @ApiModelProperty(value = "总经理姓名", required = false)
    @TableField("minister_name")
    private String ministerName;
    //belong 市场部归属 
    @ApiModelProperty(value = "市场部归属", required = false)
    @TableField("belong")
    private String belong;
    //sign_status 签单状态 
    @ApiModelProperty(value = "签单状态", required = false)
    @TableField("sign_status")
    private Integer signStatus;
    //audit_id 审核人ID 
    @ApiModelProperty(value = "审核人ID", required = false)
    @TableField("audit_id")
    private Long auditId;
    //audit_name 审核人姓名 
    @ApiModelProperty(value = "审核人姓名", required = false)
    @TableField("audit_name")
    private String auditName;
    //audit_reason 审核意见 
    @ApiModelProperty(value = "审核意见", required = false)
    @TableField("audit_reason")
    private String auditReason;
    //audit_time 审核时间 
    @ApiModelProperty(value = "审核时间", required = false)
    @TableField("audit_time")
    private Date auditTime;
    //compliance_id 合规人ID 
    @ApiModelProperty(value = "合规人ID", required = false)
    @TableField("compliance_id")
    private Long complianceId;
    //compliance_name 合规人姓名 
    @ApiModelProperty(value = "合规人姓名", required = false)
    @TableField("compliance_name")
    private String complianceName;
    //compliance_reason 合规意见 
    @ApiModelProperty(value = "合规意见", required = false)
    @TableField("compliance_reason")
    private String complianceReason;
    //compliance_time 合规时间 
    @ApiModelProperty(value = "合规时间", required = false)
    @TableField("compliance_time")
    private Date complianceTime;
    
    @ApiModelProperty(value = "升级人员ID", required = false)
    @TableField("upgrader_id")
    private Long upgraderId;
    
    @ApiModelProperty(value = "升级经理ID", required = false)
    @TableField("upgrade_manager_id")
    private Long upgradeManagerId;
    
    @ApiModelProperty(value = "升级总监ID", required = false)
    @TableField("upgrade_director_id")
    private Long upgradeDirectorId;

    @ApiModelProperty(value = "升级总经理ID", required = false)
    @TableField("upgrade_minister_id")
    private Long upgradeMinisterId;
    
    @ApiModelProperty(value = "升级人员姓名", required = false)
    @TableField("upgrader_name")
    private String upgraderName;
    
    @ApiModelProperty(value = "升级经理姓名", required = false)
    @TableField("upgrade_manager_name")
    private String upgradeManagerName;
    
    @ApiModelProperty(value = "升级总监姓名", required = false)
    @TableField("upgrade_director_name")
    private String upgradeDirectorName;
    
    @ApiModelProperty(value = "升级总经理姓名", required = false)
    @TableField("upgrade_minister_name")    
    private String upgradeMinisterName;
    
    @ApiModelProperty(value = "是否升级签单", required = false)
    @TableField("is_upgrade")
    private Integer isUpgrade;
    
    @ApiModelProperty(value = "第几次升级签单", required = false)
    @TableField("upgrade_num")
    private Integer upgradeNum;

    @ApiModelProperty(value = "签单方式（1.资源首次签单，2.会员再次签单，3.首次升级签单，4.再次升级签单）", required = false)
    @TableField("type")
    private Integer type;
    
    @ApiModelProperty(value = "签单消息提交码", required = false)
    @TableField("submit_code")
    private String submitCode;
    
    @TableField(exist = false)
    private String submitCodeOld;
    
    //产品名称和金额
    @TableField(exist = false)
    private String nameAndAmount;
    
    @ApiModelProperty(value = "是否投顾", required = false)
    @TableField("is_interest")
    private Integer isInterest;
    
    @ApiModelProperty(value = "省份", required = false)
    @TableField("province")
    private String province;
    
    @ApiModelProperty(value = "城市", required = false)
    @TableField("city")
    private String city;
    
    @ApiModelProperty(value = "是否退款", required = false)
    @TableField("is_refund")
    private Integer isRefund;
    
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getIsInterest() {
		return isInterest;
	}

	public void setIsInterest(Integer isInterest) {
		this.isInterest = isInterest;
	}

	public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public Long getCustomerId()
    {
        return this.customerId;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public String getCustomerName()
    {
        return this.customerName;
    }

    public void setWechatNo(String wechatNo)
    {
        this.wechatNo = wechatNo;
    }

    public String getWechatNo()
    {
        return this.wechatNo;
    }
    
    public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMd5Mobile() {
		return md5Mobile;
	}

	public void setMd5Mobile(String md5Mobile) {
		this.md5Mobile = md5Mobile;
	}

	public String getFuzzyMobile() {
		return fuzzyMobile;
	}

	public void setFuzzyMobile(String fuzzyMobile) {
		this.fuzzyMobile = fuzzyMobile;
	}

	public void setFromInfo(String fromInfo)
    {
        this.fromInfo = fromInfo;
    }

    public String getFromInfo()
    {
        return this.fromInfo;
    }

    public void setInsureNum(Integer insureNum)
    {
        this.insureNum = insureNum;
    }

    public Integer getInsureNum()
    {
        return this.insureNum;
    }

    public void setInsuranceId(Long insuranceId)
    {
        this.insuranceId = insuranceId;
    }

    public Long getInsuranceId()
    {
        return this.insuranceId;
    }

    public void setInsuranceName(String insuranceName)
    {
        this.insuranceName = insuranceName;
    }

    public String getInsuranceName()
    {
        return this.insuranceName;
    }

    public void setAmount(Double amount)
    {
        this.amount = amount;
    }

    public Double getAmount()
    {
        return this.amount;
    }

    public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setSalerId(Long salerId)
    {
        this.salerId = salerId;
    }

    public Long getSalerId()
    {
        return this.salerId;
    }

    public void setManagerId(Long managerId)
    {
        this.managerId = managerId;
    }

    public Long getManagerId()
    {
        return this.managerId;
    }

    public void setDirectorId(Long directorId)
    {
        this.directorId = directorId;
    }

    public Long getDirectorId()
    {
        return this.directorId;
    }

    public void setMinisterId(Long ministerId)
    {
        this.ministerId = ministerId;
    }

    public Long getMinisterId()
    {
        return this.ministerId;
    }

    public void setSalerName(String salerName)
    {
        this.salerName = salerName;
    }

    public String getSalerName()
    {
        return this.salerName;
    }

    public void setManagerName(String managerName)
    {
        this.managerName = managerName;
    }

    public String getManagerName()
    {
        return this.managerName;
    }

    public void setDirectorName(String directorName)
    {
        this.directorName = directorName;
    }

    public String getDirectorName()
    {
        return this.directorName;
    }

    public void setMinisterName(String ministerName)
    {
        this.ministerName = ministerName;
    }

    public String getMinisterName()
    {
        return this.ministerName;
    }

    public String getBelong() {
		return belong;
	}

	public void setBelong(String belong) {
		this.belong = belong;
	}

	public void setSignStatus(Integer signStatus)
    {
        this.signStatus = signStatus;
    }

    public Integer getSignStatus()
    {
        return this.signStatus;
    }

    public void setAuditId(Long auditId)
    {
        this.auditId = auditId;
    }

    public Long getAuditId()
    {
        return this.auditId;
    }

    public void setAuditName(String auditName)
    {
        this.auditName = auditName;
    }

    public String getAuditName()
    {
        return this.auditName;
    }

    public void setAuditReason(String auditReason)
    {
        this.auditReason = auditReason;
    }

    public String getAuditReason()
    {
        return this.auditReason;
    }

    public void setAuditTime(Date auditTime)
    {
        this.auditTime = auditTime;
    }

    public Date getAuditTime()
    {
        return this.auditTime;
    }

    public void setComplianceId(Long complianceId)
    {
        this.complianceId = complianceId;
    }

    public Long getComplianceId()
    {
        return this.complianceId;
    }

    public void setComplianceName(String complianceName)
    {
        this.complianceName = complianceName;
    }

    public String getComplianceName()
    {
        return this.complianceName;
    }

    public void setComplianceReason(String complianceReason)
    {
        this.complianceReason = complianceReason;
    }

    public String getComplianceReason()
    {
        return this.complianceReason;
    }

    public void setComplianceTime(Date complianceTime)
    {
        this.complianceTime = complianceTime;
    }

    public Date getComplianceTime()
    {
        return this.complianceTime;
    }

	public Long getUpgraderId() {
		return upgraderId;
	}

	public void setUpgraderId(Long upgraderId) {
		this.upgraderId = upgraderId;
	}

	public Long getUpgradeManagerId() {
		return upgradeManagerId;
	}

	public void setUpgradeManagerId(Long upgradeManagerId) {
		this.upgradeManagerId = upgradeManagerId;
	}

	public Long getUpgradeDirectorId() {
		return upgradeDirectorId;
	}

	public void setUpgradeDirectorId(Long upgradeDirectorId) {
		this.upgradeDirectorId = upgradeDirectorId;
	}

	public Long getUpgradeMinisterId() {
		return upgradeMinisterId;
	}

	public void setUpgradeMinisterId(Long upgradeMinisterId) {
		this.upgradeMinisterId = upgradeMinisterId;
	}

	public String getUpgraderName() {
		return upgraderName;
	}

	public void setUpgraderName(String upgraderName) {
		this.upgraderName = upgraderName;
	}

	public String getUpgradeManagerName() {
		return upgradeManagerName;
	}

	public void setUpgradeManagerName(String upgradeManagerName) {
		this.upgradeManagerName = upgradeManagerName;
	}

	public String getUpgradeDirectorName() {
		return upgradeDirectorName;
	}

	public void setUpgradeDirectorName(String upgradeDirectorName) {
		this.upgradeDirectorName = upgradeDirectorName;
	}

	public String getUpgradeMinisterName() {
		return upgradeMinisterName;
	}

	public void setUpgradeMinisterName(String upgradeMinisterName) {
		this.upgradeMinisterName = upgradeMinisterName;
	}

	public Integer getIsUpgrade() {
		return isUpgrade;
	}

	public void setIsUpgrade(Integer isUpgrade) {
		this.isUpgrade = isUpgrade;
	}

	public Integer getUpgradeNum() {
		return upgradeNum;
	}

	public void setUpgradeNum(Integer upgradeNum) {
		this.upgradeNum = upgradeNum;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getSubmitCode() {
		return submitCode;
	}

	public void setSubmitCode(String submitCode) {
		this.submitCode = submitCode;
	}

	public String getNameAndAmount() {
		return nameAndAmount;
	}

	public void setNameAndAmount(String nameAndAmount) {
		this.nameAndAmount = nameAndAmount;
	}
	
	
	public Integer getIsRefund() {
		return isRefund;
	}

	public void setIsRefund(Integer isRefund) {
		this.isRefund = isRefund;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		//sb.append(getClass().getSimpleName());
		sb.append(" [");
		//sb.append("id=").append(getId());
		sb.append("姓名=").append(getCustomerName());
    	sb.append("\t缴费金额=").append(getAmount());
    	sb.append("\t手机号=").append(getFuzzyMobile());
    	sb.append("\t统计=").append(getRemark());
		sb.append("]");
		return sb.toString();
	}

	public String getSubmitCodeOld() {
		return submitCodeOld;
	}

	public void setSubmitCodeOld(String submitCodeOld) {
		this.submitCodeOld = submitCodeOld;
	}

}
