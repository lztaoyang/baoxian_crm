package com.lazhu.crm.auditmobile.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 手机号明文申请表
 * </p>
 *
 * @author taoyang
 * @since 2019-07-30
 */
@ApiModel(value = "手机号明文申请表",description="auditMobile")
@TableName("crm_audit_mobile")
public class AuditMobile extends BaseModel {
    private static final long serialVersionUID = 1L;
    //customer_id  
    @ApiModelProperty(value = "", required = false)
    @TableField("customer_id")
    private Long customerId;
    //customer_name  
    @ApiModelProperty(value = "", required = false)
    @TableField("customer_name")
    private String customerName;
    //mobile 电话 
    @ApiModelProperty(value = "手机号", required = false)
    @TableField(exist = false)
    private String mobile;
    @ApiModelProperty(value = "详情页手机号", required = false)
    @TableField(exist = false)
    private String showMobile;
    @ApiModelProperty(value = "MD5加密", required = false)
    @TableField("md5_mobile")
    private String md5Mobile;
    //fuzzy_mobile 掩码手机号 
    @ApiModelProperty(value = "掩码手机号", required = false)
    @TableField("fuzzy_mobile")
    private String fuzzyMobile;
    //describe 申请描述（申请查看什么会员的手机掩码） 
    @ApiModelProperty(value = "申请描述（申请查看什么会员的手机掩码）", required = false)
    @TableField("describe")
    private String describe;
    //applicant_id 申请人id 
    @ApiModelProperty(value = "申请人id", required = false)
    @TableField("applicant_id")
    private Long applicantId;
    //applicant_name 申请人 
    @ApiModelProperty(value = "申请人", required = false)
    @TableField("applicant_name")
    private String applicantName;
    //apply_reason 申请理由 
    @ApiModelProperty(value = "申请理由", required = false)
    @TableField("apply_reason")
    private String applyReason;
    //audit_reason 审核驳回原因 
    @ApiModelProperty(value = "审核驳回原因", required = false)
    @TableField("audit_reason")
    private String auditReason;
    //audit_time 审核通过时间 
    @ApiModelProperty(value = "审核通过时间", required = false)
    @TableField("audit_time")
    private Date auditTime;
    //audit_id 审核人id 
    @ApiModelProperty(value = "审核人id", required = false)
    @TableField("audit_id")
    private Long auditId;
    //audit_name 审核人姓名 
    @ApiModelProperty(value = "审核人姓名", required = false)
    @TableField("audit_name")
    private String auditName;
    //status 申请审核状态  0---待审   1---通过   2--驳回   3--失效 
    @ApiModelProperty(value = "申请审核状态  0---待审   1---通过   2--驳回   3--失效", required = false)
    @TableField("status")
    private Integer status;

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
    
    public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getShowMobile() {
		return showMobile;
	}

	public void setShowMobile(String showMobile) {
		this.showMobile = showMobile;
	}

	public String getMd5Mobile() {
		return md5Mobile;
	}

	public void setMd5Mobile(String md5Mobile) {
		this.md5Mobile = md5Mobile;
	}

	public void setFuzzyMobile(String fuzzyMobile)
    {
        this.fuzzyMobile = fuzzyMobile;
    }

    public String getFuzzyMobile()
    {
        return this.fuzzyMobile;
    }

    public void setDescribe(String describe)
    {
        this.describe = describe;
    }

    public String getDescribe()
    {
        return this.describe;
    }

    public void setApplicantId(Long applicantId)
    {
        this.applicantId = applicantId;
    }

    public Long getApplicantId()
    {
        return this.applicantId;
    }

    public void setApplicantName(String applicantName)
    {
        this.applicantName = applicantName;
    }

    public String getApplicantName()
    {
        return this.applicantName;
    }

    public void setApplyReason(String applyReason)
    {
        this.applyReason = applyReason;
    }

    public String getApplyReason()
    {
        return this.applyReason;
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

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return this.status;
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
    	sb.append(", customerId=").append(getCustomerId());
    	sb.append(", customerName=").append(getCustomerName());
    	sb.append(", fuzzyMobile=").append(getFuzzyMobile());
    	sb.append(", describe=").append(getDescribe());
    	sb.append(", applicantId=").append(getApplicantId());
    	sb.append(", applicantName=").append(getApplicantName());
    	sb.append(", applyReason=").append(getApplyReason());
    	sb.append(", auditReason=").append(getAuditReason());
    	sb.append(", auditTime=").append(getAuditTime());
    	sb.append(", auditId=").append(getAuditId());
    	sb.append(", auditName=").append(getAuditName());
    	sb.append(", status=").append(getStatus());
		sb.append("]");
		return sb.toString();
	}

	/**
	 */
	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		if (that == null) {
			return false;
		}
		if (getClass() != that.getClass()) {
			return false;
		}
		AuditMobile other = (AuditMobile) that;
		return (true
	    	&&this.getCustomerId() == null ? other.getCustomerId() == null : this.getCustomerId().equals(other.getCustomerId())
	    	&&this.getCustomerName() == null ? other.getCustomerName() == null : this.getCustomerName().equals(other.getCustomerName())
	    	&&this.getFuzzyMobile() == null ? other.getFuzzyMobile() == null : this.getFuzzyMobile().equals(other.getFuzzyMobile())
	    	&&this.getDescribe() == null ? other.getDescribe() == null : this.getDescribe().equals(other.getDescribe())
	    	&&this.getApplicantId() == null ? other.getApplicantId() == null : this.getApplicantId().equals(other.getApplicantId())
	    	&&this.getApplicantName() == null ? other.getApplicantName() == null : this.getApplicantName().equals(other.getApplicantName())
	    	&&this.getApplyReason() == null ? other.getApplyReason() == null : this.getApplyReason().equals(other.getApplyReason())
	    	&&this.getAuditReason() == null ? other.getAuditReason() == null : this.getAuditReason().equals(other.getAuditReason())
	    	&&this.getAuditTime() == null ? other.getAuditTime() == null : this.getAuditTime().equals(other.getAuditTime())
	    	&&this.getAuditId() == null ? other.getAuditId() == null : this.getAuditId().equals(other.getAuditId())
	    	&&this.getAuditName() == null ? other.getAuditName() == null : this.getAuditName().equals(other.getAuditName())
	    	&&this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus())
				);
	}

	/**
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
    	result = prime * result + ((getCustomerId() == null) ? 0 : getCustomerId().hashCode());
    	result = prime * result + ((getCustomerName() == null) ? 0 : getCustomerName().hashCode());
    	result = prime * result + ((getFuzzyMobile() == null) ? 0 : getFuzzyMobile().hashCode());
    	result = prime * result + ((getDescribe() == null) ? 0 : getDescribe().hashCode());
    	result = prime * result + ((getApplicantId() == null) ? 0 : getApplicantId().hashCode());
    	result = prime * result + ((getApplicantName() == null) ? 0 : getApplicantName().hashCode());
    	result = prime * result + ((getApplyReason() == null) ? 0 : getApplyReason().hashCode());
    	result = prime * result + ((getAuditReason() == null) ? 0 : getAuditReason().hashCode());
    	result = prime * result + ((getAuditTime() == null) ? 0 : getAuditTime().hashCode());
    	result = prime * result + ((getAuditId() == null) ? 0 : getAuditId().hashCode());
    	result = prime * result + ((getAuditName() == null) ? 0 : getAuditName().hashCode());
    	result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
		return result;
	}
}
