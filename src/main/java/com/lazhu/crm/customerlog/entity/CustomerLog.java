package com.lazhu.crm.customerlog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

/**
 * <p>
 * 资源会员操作流程日志 实体类
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@ApiModel(value = "",description="customerLog")
@TableName("crm_customer_log")
public class CustomerLog extends BaseModel {
    private static final long serialVersionUID = 1L;
    //CUSTOMER_ID 客户ID 
    @ApiModelProperty(value = "客户ID", required = false)
    @TableField("CUSTOMER_ID")
    private Long customerId;
    //CUSTOMER_NAME 客户姓名 
    @ApiModelProperty(value = "客户姓名", required = false)
    @TableField("CUSTOMER_NAME")
    private String customerName;
    //OLD_USER 原用户ID 
    @ApiModelProperty(value = "原用户ID", required = false)
    @TableField("OLD_USER")
    private Long oldUser;
    //NEW_USER 新用户ID 
    @ApiModelProperty(value = "新用户ID", required = false)
    @TableField("NEW_USER")
    private Long newUser;
    //OLD_FLOW 原流程ID 
    @ApiModelProperty(value = "原流程ID", required = false)
    @TableField("OLD_FLOW")
    private Integer oldFlow;
    //NEW_FLOW 新流程ID 
    @ApiModelProperty(value = "新流程ID", required = false)
    @TableField("NEW_FLOW")
    private Integer newFlow;
    //TYPE 操作类型 
    @ApiModelProperty(value = "操作类型", required = false)
    @TableField("TYPE")
    private String type;
    //IP 操作者IP 
    @ApiModelProperty(value = "操作者IP", required = false)
    @TableField("IP")
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

    public void setOldUser(Long oldUser)
    {
        this.oldUser = oldUser;
    }

    public Long getOldUser()
    {
        return this.oldUser;
    }

    public void setNewUser(Long newUser)
    {
        this.newUser = newUser;
    }

    public Long getNewUser()
    {
        return this.newUser;
    }

    public void setOldFlow(Integer oldFlow)
    {
        this.oldFlow = oldFlow;
    }

    public Integer getOldFlow()
    {
        return this.oldFlow;
    }

    public void setNewFlow(Integer newFlow)
    {
        this.newFlow = newFlow;
    }

    public Integer getNewFlow()
    {
        return this.newFlow;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return this.type;
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
    	sb.append(", oldUser=").append(getOldUser());
    	sb.append(", newUser=").append(getNewUser());
    	sb.append(", oldFlow=").append(getOldFlow());
    	sb.append(", newFlow=").append(getNewFlow());
    	sb.append(", type=").append(getType());
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
		CustomerLog other = (CustomerLog) that;
		return (1==1
	    	&&this.getCustomerId() == null ? other.getCustomerId() == null : this.getCustomerId().equals(other.getCustomerId())
	    	&&this.getCustomerName() == null ? other.getCustomerName() == null : this.getCustomerName().equals(other.getCustomerName())
	    	&&this.getOldUser() == null ? other.getOldUser() == null : this.getOldUser().equals(other.getOldUser())
	    	&&this.getNewUser() == null ? other.getNewUser() == null : this.getNewUser().equals(other.getNewUser())
	    	&&this.getOldFlow() == null ? other.getOldFlow() == null : this.getOldFlow().equals(other.getOldFlow())
	    	&&this.getNewFlow() == null ? other.getNewFlow() == null : this.getNewFlow().equals(other.getNewFlow())
	    	&&this.getType() == null ? other.getType() == null : this.getType().equals(other.getType())
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
    	result = prime * result + ((getOldUser() == null) ? 0 : getOldUser().hashCode());
    	result = prime * result + ((getNewUser() == null) ? 0 : getNewUser().hashCode());
    	result = prime * result + ((getOldFlow() == null) ? 0 : getOldFlow().hashCode());
    	result = prime * result + ((getNewFlow() == null) ? 0 : getNewFlow().hashCode());
    	result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
    	result = prime * result + ((getIp() == null) ? 0 : getIp().hashCode());
		return result;
	}
}
