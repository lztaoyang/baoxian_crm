package com.lazhu.crm.alarm.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

/**
 * <p>
 * 钉钉闹钟
 * </p>
 *
 * @author hz
 * @since 2017-11-16
 */
@ApiModel(value = "钉钉闹钟",description="alarm")
@TableName("crm_alarm")
public class Alarm extends BaseModel {
	private static final long serialVersionUID = 1L;
    //resource_id 资源会员ID 
    @ApiModelProperty(value = "资源会员ID", required = false)
    @TableField("resource_id")
    private Long resourceId;
    //user_id 用户ID 
    @ApiModelProperty(value = "用户ID", required = false)
    @TableField("user_id")
    private Long userId;
    //user_name 用户姓名 
    @ApiModelProperty(value = "用户姓名", required = false)
    @TableField("user_name")
    private String userName;
    //content 消息内容 
    @ApiModelProperty(value = "概要", required = false)
    @TableField("summary")
    private String summary;
    @ApiModelProperty(value = "消息内容", required = false)
    @TableField("content")
    private String content;
    //ding_id 钉钉ID 
    @ApiModelProperty(value = "钉钉ID", required = false)
    @TableField("ding_id")
    private String dingId;
    //ding_msg 钉钉消息 
    @ApiModelProperty(value = "钉钉消息", required = false)
    @TableField("ding_msg")
    private String dingMsg;
    @ApiModelProperty(value = "是否已发送", required = false)
    @TableField("is_send")
    private Integer isSend;
    @ApiModelProperty(value = "闹钟时间", required = false)
    @TableField("alarm_time")
    private Date alarmTime;

    @ApiModelProperty(value = "用户组", required = false)
    @TableField("user_group")
    private Long userGroup;
    @ApiModelProperty(value = "是否回拨", required = false)
    @TableField("is_call")
    private Integer isCall;
    
    @ApiModelProperty(value = "经理ID", required = false)
    @TableField("manager_id")
    private Long managerId;
    
    @ApiModelProperty(value = "总监ID", required = false)
    @TableField("director_id")
    private Long directorId;
    
    
    public void setResourceId(Long resourceId)
    {
        this.resourceId = resourceId;
    }

    public Long getResourceId()
    {
        return this.resourceId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return this.userId;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return this.userName;
    }

    public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return this.content;
    }

    public void setDingId(String dingId)
    {
        this.dingId = dingId;
    }

    public String getDingId()
    {
        return this.dingId;
    }

    public void setDingMsg(String dingMsg)
    {
        this.dingMsg = dingMsg;
    }

    public String getDingMsg()
    {
        return this.dingMsg;
    }

	public Integer getIsSend() {
		return isSend;
	}

	public void setIsSend(Integer isSend) {
		this.isSend = isSend;
	}

	public Date getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(Date alarmTime) {
		this.alarmTime = alarmTime;
	}

	public Long getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(Long userGroup) {
		this.userGroup = userGroup;
	}

	public Integer getIsCall() {
		return isCall;
	}

	public void setIsCall(Integer isCall) {
		this.isCall = isCall;
	}

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public Long getDirectorId() {
		return directorId;
	}

	public void setDirectorId(Long directorId) {
		this.directorId = directorId;
	}

}
