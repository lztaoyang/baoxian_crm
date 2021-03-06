package com.lazhu.crm.serverrecordmobile.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

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
@ApiModel(value = "",description="serverRecordMobile")
@TableName("crm_server_record_mobile")
public class ServerRecordMobile extends BaseModel {
	private static final long serialVersionUID = 1L;
    //customer_id 客户ID 
    @ApiModelProperty(value = "客户ID", required = false)
    @TableField("customer_id")
    private Long customerId;
    //customer_name 客户姓名 
    @ApiModelProperty(value = "客户姓名", required = false)
    @TableField("customer_name")
    private String customerName;
    //customer_mobile 客户电话 
    @ApiModelProperty(value = "客户电话", required = false)
    @TableField("customer_mobile")
    private String customerMobile;
    @ApiModelProperty(value = "掩码手机号", required = false)
    @TableField("fuzzy_mobile")
    private String fuzzyMobile;
    //server_record 服务记录 
    @ApiModelProperty(value = "服务记录", required = false)
    @TableField("server_record")
    private String serverRecord;
    //time_length 通话时长 
    @ApiModelProperty(value = "通话时长", required = false)
    @TableField("time_length")
    private Long timeLength;
    //call_file 录音文件 
    @ApiModelProperty(value = "录音文件", required = false)
    @TableField("call_file")
    private String callFile;
    //server_id 服务人人ID 
    @ApiModelProperty(value = "服务人人ID", required = false)
    @TableField("server_id")
    private Long serverId;
    //server_name 服务人姓名 
    @ApiModelProperty(value = "服务人姓名", required = false)
    @TableField("server_name")
    private String serverName;
    //server_mobile 服务人电话 
    @ApiModelProperty(value = "服务人电话", required = false)
    @TableField("server_mobile")
    private String serverMobile;
    //type 服务类型 
    @ApiModelProperty(value = "是否接通", required = false)
    @TableField("type")
    private String type;
    @ApiModelProperty(value = "分机号", required = false)
    @TableField("agent_no")
    private String agentNo;
    @ApiModelProperty(value = "呼入呼出", required = false)
    @TableField("direct")
    private Integer direct;
    
    @ApiModelProperty(value = "上级ID", required = false)
    @TableField("manager_id")
    private Long managerId;

    @ApiModelProperty(value = "同步次数", required = false)
    @TableField("sync_num")
    private Integer syncNum;
    
    @ApiModelProperty(value = "测试通话时长", required = false)
    @TableField("test_time_length")
    private Long testTimeLength;
    
    @ApiModelProperty(value = "挂机时间", required = false)
    @TableField("test_update_time")
    private Date testUpdateTime;
    
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

    public void setCustomerMobile(String customerMobile)
    {
        this.customerMobile = customerMobile;
    }

    public String getCustomerMobile()
    {
        return this.customerMobile;
    }

    public String getFuzzyMobile() {
    	return this.fuzzyMobile;
	}

	public void setFuzzyMobile(String fuzzyMobile) {
		this.fuzzyMobile = fuzzyMobile;
	}

	public void setServerRecord(String serverRecord)
    {
        this.serverRecord = serverRecord;
    }

    public String getServerRecord()
    {
        return this.serverRecord;
    }

    public void setTimeLength(Long timeLength)
    {
        this.timeLength = timeLength;
    }

    public Long getTimeLength()
    {
        return this.timeLength;
    }

    public void setCallFile(String callFile)
    {
        this.callFile = callFile;
    }

    public String getCallFile()
    {
        return this.callFile;
    }

    public void setServerId(Long serverId)
    {
        this.serverId = serverId;
    }

    public Long getServerId()
    {
        return this.serverId;
    }

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public String getServerName()
    {
        return this.serverName;
    }

    public void setServerMobile(String serverMobile)
    {
        this.serverMobile = serverMobile;
    }

    public String getServerMobile()
    {
        return this.serverMobile;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return this.type;
    }

	public String getAgentNo() {
		return agentNo;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}

	public Integer getDirect() {
		return direct;
	}

	public void setDirect(Integer direct) {
		this.direct = direct;
	}

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public Integer getSyncNum() {
		return syncNum;
	}

	public void setSyncNum(Integer syncNum) {
		this.syncNum = syncNum;
	}

	public Long getTestTimeLength() {
		return testTimeLength;
	}

	public void setTestTimeLength(Long testTimeLength) {
		this.testTimeLength = testTimeLength;
	}

	public Date getTestUpdateTime() {
		return testUpdateTime;
	}

	public void setTestUpdateTime(Date testUpdateTime) {
		this.testUpdateTime = testUpdateTime;
	}
	
}
