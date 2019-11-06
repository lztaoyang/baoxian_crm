package com.lazhu.crm.salercallinrecord.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 市场部来电记录表
 * </p>
 *
 * @author hz
 * @since 2018-01-18
 */
@ApiModel(value = "市场部来电记录表",description="salerCallinRecord")
@TableName("crm_saler_callin_record")
public class SalerCallinRecord extends BaseModel {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "已接ID", required = false)
    @TableField("answer_id")
    private Long answerId;
    @ApiModelProperty(value = "未接ID", required = false)
    @TableField("noanswer_id")
    private Long noanswerId;
    //mobile 来电号码 
    @ApiModelProperty(value = "来电号码", required = false)
    @TableField("mobile")
    private String mobile;
    //saler_id 所属顾问ID 
    @ApiModelProperty(value = "所属顾问ID", required = false)
    @TableField("saler_id")
    private Long salerId;
    //saler_name 所属顾问姓名 
    @ApiModelProperty(value = "所属顾问姓名", required = false)
    @TableField("saler_name")
    private String salerName;
    //manager_id 所属经理ID 
    @ApiModelProperty(value = "所属经理ID", required = false)
    @TableField("manager_id")
    private Long managerId;
    //manager_name 所属经理姓名 
    @ApiModelProperty(value = "所属经理姓名", required = false)
    @TableField("manager_name")
    private String managerName;
    //director_id 所属总监ID 
    @ApiModelProperty(value = "所属总监ID", required = false)
    @TableField("director_id")
    private Long directorId;
    //director_name 所属总监姓名 
    @ApiModelProperty(value = "所属总监姓名", required = false)
    @TableField("director_name")
    private String directorName;
    //minister_id 所属总经理ID 
    @ApiModelProperty(value = "所属总经理ID", required = false)
    @TableField("minister_id")
    private Long ministerId;
    //minister_name 所属总经理姓名 
    @ApiModelProperty(value = "所属总经理姓名", required = false)
    @TableField("minister_name")
    private String ministerName;
    //call_time 来电时间 
    @ApiModelProperty(value = "来电时间", required = false)
    @TableField("call_time")
    private Date callTime;
    //is_through 是否已接 
    @ApiModelProperty(value = "是否已接", required = false)
    @TableField("is_through")
    private Integer isThrough;
    //ring_length 响铃时长 
    @ApiModelProperty(value = "响铃时长", required = false)
    @TableField("ring_length")
    private Long ringLength;
    @ApiModelProperty(value = "录音文件", required = false)
    @TableField("call_file")
    private String callFile;
    //call_length 通话时长 
    @ApiModelProperty(value = "通话时长", required = false)
    @TableField("call_length")
    private Long callLength;
    //through_id 接听人ID 
    @ApiModelProperty(value = "接听人ID", required = false)
    @TableField("through_id")
    private Long throughId;
    //through_name 接听人姓名 
    @ApiModelProperty(value = "接听人姓名", required = false)
    @TableField("through_name")
    private String throughName;
    //through_record 接听通话内容 
    @ApiModelProperty(value = "接听通话内容", required = false)
    @TableField("through_record")
    private String throughRecord;
    @ApiModelProperty(value = "是否会员来电", required = false)
    @TableField("is_club")
    private Integer isClub;

    public Long getAnswerId() {
		return answerId;
	}

	public void setAnswerId(Long answerId) {
		this.answerId = answerId;
	}

	public Long getNoanswerId() {
		return noanswerId;
	}

	public void setNoanswerId(Long noanswerId) {
		this.noanswerId = noanswerId;
	}

	public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getMobile()
    {
        return this.mobile;
    }

    public void setSalerId(Long salerId)
    {
        this.salerId = salerId;
    }

    public Long getSalerId()
    {
        return this.salerId;
    }

    public void setSalerName(String salerName)
    {
        this.salerName = salerName;
    }

    public String getSalerName()
    {
        return this.salerName;
    }

    public void setManagerId(Long managerId)
    {
        this.managerId = managerId;
    }

    public Long getManagerId()
    {
        return this.managerId;
    }

    public void setManagerName(String managerName)
    {
        this.managerName = managerName;
    }

    public String getManagerName()
    {
        return this.managerName;
    }

    public void setDirectorId(Long directorId)
    {
        this.directorId = directorId;
    }

    public Long getDirectorId()
    {
        return this.directorId;
    }

    public void setDirectorName(String directorName)
    {
        this.directorName = directorName;
    }

    public String getDirectorName()
    {
        return this.directorName;
    }

    public void setMinisterId(Long ministerId)
    {
        this.ministerId = ministerId;
    }

    public Long getMinisterId()
    {
        return this.ministerId;
    }

    public void setMinisterName(String ministerName)
    {
        this.ministerName = ministerName;
    }

    public String getMinisterName()
    {
        return this.ministerName;
    }

    public void setCallTime(Date callTime)
    {
        this.callTime = callTime;
    }

    public Date getCallTime()
    {
        return this.callTime;
    }

    public void setIsThrough(Integer isThrough)
    {
        this.isThrough = isThrough;
    }

    public Integer getIsThrough()
    {
        return this.isThrough;
    }

    public void setRingLength(Long ringLength)
    {
        this.ringLength = ringLength;
    }

    public Long getRingLength()
    {
        return this.ringLength;
    }

    public String getCallFile() {
		return callFile;
	}

	public void setCallFile(String callFile) {
		this.callFile = callFile;
	}

	public void setCallLength(Long callLength)
    {
        this.callLength = callLength;
    }

    public Long getCallLength()
    {
        return this.callLength;
    }

    public void setThroughId(Long throughId)
    {
        this.throughId = throughId;
    }

    public Long getThroughId()
    {
        return this.throughId;
    }

    public void setThroughName(String throughName)
    {
        this.throughName = throughName;
    }

    public String getThroughName()
    {
        return this.throughName;
    }

    public void setThroughRecord(String throughRecord)
    {
        this.throughRecord = throughRecord;
    }

    public String getThroughRecord()
    {
        return this.throughRecord;
    }

	public Integer getIsClub() {
		return isClub;
	}

	public void setIsClub(Integer isClub) {
		this.isClub = isClub;
	}
 
}
