package com.lazhu.crm.salerrecordleave.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

/**
 * <p>
 * 市场部通话记录闲置表
 * </p>
 *
 * @author hz
 * @since 2018-04-13
 */
@ApiModel(value = "市场部通话记录闲置表",description="salerRecordLeave")
@TableName("crm_saler_record_leave")
public class SalerRecordLeave extends BaseModel {
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
    //mobile  
    @ApiModelProperty(value = "", required = false)
    @TableField("mobile")
    private String mobile;
    //fuzzy_mobile 掩码手机号 
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
    private Integer timeLength;
    //test_time_length  
    @ApiModelProperty(value = "", required = false)
    @TableField("test_time_length")
    private Integer testTimeLength;
    //test_update_time 挂机时间 
    @ApiModelProperty(value = "挂机时间", required = false)
    @TableField("test_update_time")
    private Date testUpdateTime;
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
    @ApiModelProperty(value = "服务类型", required = false)
    @TableField("type")
    private String type;
    //agent_no 分机号 
    @ApiModelProperty(value = "分机号", required = false)
    @TableField("agent_no")
    private String agentNo;
    //direct 呼入呼出 
    @ApiModelProperty(value = "呼入呼出", required = false)
    @TableField("direct")
    private Integer direct;
    //sync_num 同步次数 
    @ApiModelProperty(value = "同步次数", required = false)
    @TableField("sync_num")
    private Integer syncNum;
    //manager_id 服务人上级ID 
    @ApiModelProperty(value = "服务人上级ID", required = false)
    @TableField("manager_id")
    private Long managerId;
    //flow_id 开发流程 
    @ApiModelProperty(value = "开发流程", required = false)
    @TableField("flow_id")
    private Integer flowId;
    //from_info 来源渠道 
    @ApiModelProperty(value = "来源渠道", required = false)
    @TableField("from_info")
    private String fromInfo;
    //resource_time 入库时间 
    @ApiModelProperty(value = "入库时间", required = false)
    @TableField("resource_time")
    private Date resourceTime;

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

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getMobile()
    {
        return this.mobile;
    }

    public void setFuzzyMobile(String fuzzyMobile)
    {
        this.fuzzyMobile = fuzzyMobile;
    }

    public String getFuzzyMobile()
    {
        return this.fuzzyMobile;
    }

    public void setServerRecord(String serverRecord)
    {
        this.serverRecord = serverRecord;
    }

    public String getServerRecord()
    {
        return this.serverRecord;
    }

    public void setTimeLength(Integer timeLength)
    {
        this.timeLength = timeLength;
    }

    public Integer getTimeLength()
    {
        return this.timeLength;
    }

    public void setTestTimeLength(Integer testTimeLength)
    {
        this.testTimeLength = testTimeLength;
    }

    public Integer getTestTimeLength()
    {
        return this.testTimeLength;
    }

    public void setTestUpdateTime(Date testUpdateTime)
    {
        this.testUpdateTime = testUpdateTime;
    }

    public Date getTestUpdateTime()
    {
        return this.testUpdateTime;
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

    public void setAgentNo(String agentNo)
    {
        this.agentNo = agentNo;
    }

    public String getAgentNo()
    {
        return this.agentNo;
    }

    public void setDirect(Integer direct)
    {
        this.direct = direct;
    }

    public Integer getDirect()
    {
        return this.direct;
    }

    public void setSyncNum(Integer syncNum)
    {
        this.syncNum = syncNum;
    }

    public Integer getSyncNum()
    {
        return this.syncNum;
    }

    public void setManagerId(Long managerId)
    {
        this.managerId = managerId;
    }

    public Long getManagerId()
    {
        return this.managerId;
    }

    public void setFlowId(Integer flowId)
    {
        this.flowId = flowId;
    }

    public Integer getFlowId()
    {
        return this.flowId;
    }

    public void setFromInfo(String fromInfo)
    {
        this.fromInfo = fromInfo;
    }

    public String getFromInfo()
    {
        return this.fromInfo;
    }

    public void setResourceTime(Date resourceTime)
    {
        this.resourceTime = resourceTime;
    }

    public Date getResourceTime()
    {
        return this.resourceTime;
    }

}
