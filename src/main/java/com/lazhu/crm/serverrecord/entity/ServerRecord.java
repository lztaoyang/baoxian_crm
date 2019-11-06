package com.lazhu.crm.serverrecord.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

/**
 * <p>
 * 
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@ApiModel(value = "",description="serverRecord")
@TableName("crm_server_record")
public class ServerRecord extends BaseModel {
    private static final long serialVersionUID = 1L;
    //customer_id 客户ID 
    @TableField(exist = false)
    private Object ids;
    //customer_name 客户姓名 
    @TableField(exist = false)
    private Object names;
    
    //customer_id 客户ID 
    @ApiModelProperty(value = "客户ID", required = false)
    @TableField("customer_id")
    private Long customerId;
    //customer_name 客户姓名 
    @ApiModelProperty(value = "客户姓名", required = false)
    @TableField("customer_name")
    private String customerName;
    //server_record 服务记录 
    @ApiModelProperty(value = "服务记录", required = false)
    @TableField("server_record")
    private String serverRecord;
    //server_id 服务人人ID 
    @ApiModelProperty(value = "服务人姓名", required = false)
    @TableField("server_name")
    private String serverName;
    //type 服务类型 
    @ApiModelProperty(value = "服务类型", required = false)
    @TableField("type")
    private String type;
    
    
    
    public Object getIds() {
		return ids;
	}

	public void setIds(Object ids) {
		this.ids = ids;
	}

	public Object getNames() {
		return names;
	}

	public void setNames(Object names) {
		this.names = names;
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

    public void setServerRecord(String serverRecord)
    {
        this.serverRecord = serverRecord;
    }

    public String getServerRecord()
    {
        return this.serverRecord;
    }

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public String getServerName()
    {
        return this.serverName;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return this.type;
    }
}
