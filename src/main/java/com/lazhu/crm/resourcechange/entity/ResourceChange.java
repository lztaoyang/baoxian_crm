package com.lazhu.crm.resourcechange.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@ApiModel(value = "",description="resourceChange")
@TableName("crm_resource_change")
public class ResourceChange extends BaseModel {
    private static final long serialVersionUID = 1L;
    //customer_id 客户ID 
    @ApiModelProperty(value = "客户ID", required = false)
    @TableField("customer_id")
    private Long customerId;
    //customer_name 客户姓名 
    @ApiModelProperty(value = "客户姓名", required = false)
    @TableField("customer_name")
    private String customerName;
    //old_webchat 原微信号 
    @ApiModelProperty(value = "原微信号", required = false)
    @TableField("old_webchat")
    private String oldWebchat;
    //new_webchat 新微信号 
    @ApiModelProperty(value = "新微信号", required = false)
    @TableField("new_webchat")
    private String newWebchat;
    //old_qq 原QQ号 
    @ApiModelProperty(value = "原QQ号", required = false)
    @TableField("old_qq")
    private String oldQq;
    //new_qq 新QQ号 
    @ApiModelProperty(value = "新QQ号", required = false)
    @TableField("new_qq")
    private String newQq;
    //old_mobile 原手机号 
    @ApiModelProperty(value = "原手机号", required = false)
    @TableField("old_mobile")
    private String oldMobile;
    //new_mobile 新手机号 
    @ApiModelProperty(value = "新手机号", required = false)
    @TableField("new_mobile")
    private String newMobile;
    //operator_ip 操作人IP 
    @ApiModelProperty(value = "操作人IP", required = false)
    @TableField("ip")
    private String ip;

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

    public void setOldWebchat(String oldWebchat)
    {
        this.oldWebchat = oldWebchat;
    }

    public String getOldWebchat()
    {
        return this.oldWebchat;
    }

    public void setNewWebchat(String newWebchat)
    {
        this.newWebchat = newWebchat;
    }

    public String getNewWebchat()
    {
        return this.newWebchat;
    }

    public void setOldQq(String oldQq)
    {
        this.oldQq = oldQq;
    }

    public String getOldQq()
    {
        return this.oldQq;
    }

    public void setNewQq(String newQq)
    {
        this.newQq = newQq;
    }

    public String getNewQq()
    {
        return this.newQq;
    }

    public void setOldMobile(String oldMobile)
    {
        this.oldMobile = oldMobile;
    }

    public String getOldMobile()
    {
        return this.oldMobile;
    }

    public void setNewMobile(String newMobile)
    {
        this.newMobile = newMobile;
    }

    public String getNewMobile()
    {
        return this.newMobile;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getIp()
    {
        return this.ip;
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
    	sb.append(", customerId=").append(getCustomerId());
    	sb.append(", customerName=").append(getCustomerName());
    	sb.append(", oldWebchat=").append(getOldWebchat());
    	sb.append(", newWebchat=").append(getNewWebchat());
    	sb.append(", oldQq=").append(getOldQq());
    	sb.append(", newQq=").append(getNewQq());
    	sb.append(", oldMobile=").append(getOldMobile());
    	sb.append(", newMobile=").append(getNewMobile());
    	sb.append(", ip=").append(getIp());
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
		ResourceChange other = (ResourceChange) that;
		return (1==1
	    	&&this.getCustomerId() == null ? other.getCustomerId() == null : this.getCustomerId().equals(other.getCustomerId())
	    	&&this.getCustomerName() == null ? other.getCustomerName() == null : this.getCustomerName().equals(other.getCustomerName())
	    	&&this.getOldWebchat() == null ? other.getOldWebchat() == null : this.getOldWebchat().equals(other.getOldWebchat())
	    	&&this.getNewWebchat() == null ? other.getNewWebchat() == null : this.getNewWebchat().equals(other.getNewWebchat())
	    	&&this.getOldQq() == null ? other.getOldQq() == null : this.getOldQq().equals(other.getOldQq())
	    	&&this.getNewQq() == null ? other.getNewQq() == null : this.getNewQq().equals(other.getNewQq())
	    	&&this.getOldMobile() == null ? other.getOldMobile() == null : this.getOldMobile().equals(other.getOldMobile())
	    	&&this.getNewMobile() == null ? other.getNewMobile() == null : this.getNewMobile().equals(other.getNewMobile())
	    	&&this.getIp() == null ? other.getIp() == null : this.getIp().equals(other.getIp())
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
    	result = prime * result + ((getOldWebchat() == null) ? 0 : getOldWebchat().hashCode());
    	result = prime * result + ((getNewWebchat() == null) ? 0 : getNewWebchat().hashCode());
    	result = prime * result + ((getOldQq() == null) ? 0 : getOldQq().hashCode());
    	result = prime * result + ((getNewQq() == null) ? 0 : getNewQq().hashCode());
    	result = prime * result + ((getOldMobile() == null) ? 0 : getOldMobile().hashCode());
    	result = prime * result + ((getNewMobile() == null) ? 0 : getNewMobile().hashCode());
    	result = prime * result + ((getIp() == null) ? 0 : getIp().hashCode());
		return result;
	}
}
