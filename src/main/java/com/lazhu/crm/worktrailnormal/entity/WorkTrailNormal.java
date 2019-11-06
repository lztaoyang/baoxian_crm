package com.lazhu.crm.worktrailnormal.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 市场部正常工作轨迹
 * </p>
 *
 * @author LAPTOP-HDRAA393
 * @since 2018-02-01
 */
@ApiModel(value = "市场部正常工作轨迹",description="workTrailNormal")
@TableName("crm_work_trail_normal")
public class WorkTrailNormal extends BaseModel {
    private static final long serialVersionUID = 1L;
    //saler_id 经纪人ID 
    @ApiModelProperty(value = "经纪人ID", required = false)
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
    //customer_id 客户ID 
    @ApiModelProperty(value = "客户ID", required = false)
    @TableField("customer_id")
    private Long customerId;
    //saler_record_id 通话记录ID 
    @ApiModelProperty(value = "通话记录ID", required = false)
    @TableField("saler_record_id")
    private Long salerRecordId;
    //before_flow_id 前流程ID 
    @ApiModelProperty(value = "前流程ID", required = false)
    @TableField("before_flow_id")
    private Integer beforeFlowId;
    //after_flow_id 后流程ID 
    @ApiModelProperty(value = "后流程ID", required = false)
    @TableField("after_flow_id")
    private Integer afterFlowId;
  //time_length 通话时长 
    @ApiModelProperty(value = "通话时长", required = false)
    @TableField("time_length")
    private Long timeLength;
  //enter_time 入库时间 
    @ApiModelProperty(value = "入库时间", required = false)
    @TableField("enter_time")
    private Date enterTime;

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

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public Long getCustomerId()
    {
        return this.customerId;
    }

    public void setSalerRecordId(Long salerRecordId)
    {
        this.salerRecordId = salerRecordId;
    }

    public Long getSalerRecordId()
    {
        return this.salerRecordId;
    }

    public void setBeforeFlowId(Integer beforeFlowId)
    {
        this.beforeFlowId = beforeFlowId;
    }

    public Integer getBeforeFlowId()
    {
        return this.beforeFlowId;
    }

    public void setAfterFlowId(Integer afterFlowId)
    {
        this.afterFlowId = afterFlowId;
    }

    public Integer getAfterFlowId()
    {
        return this.afterFlowId;
    }
    
	public Long getTimeLength() {
		return timeLength;
	}

	public void setTimeLength(Long timeLength) {
		this.timeLength = timeLength;
	}

	public Date getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(Date enterTime) {
		this.enterTime = enterTime;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
    	sb.append(", salerId=").append(getSalerId());
    	sb.append(", managerId=").append(getManagerId());
    	sb.append(", directorId=").append(getDirectorId());
    	sb.append(", ministerId=").append(getMinisterId());
    	sb.append(", customerId=").append(getCustomerId());
    	sb.append(", salerRecordId=").append(getSalerRecordId());
    	sb.append(", beforeFlowId=").append(getBeforeFlowId());
    	sb.append(", afterFlowId=").append(getAfterFlowId());
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
		WorkTrailNormal other = (WorkTrailNormal) that;
		return (1==1
	    	&&this.getSalerId() == null ? other.getSalerId() == null : this.getSalerId().equals(other.getSalerId())
	    	&&this.getManagerId() == null ? other.getManagerId() == null : this.getManagerId().equals(other.getManagerId())
	    	&&this.getDirectorId() == null ? other.getDirectorId() == null : this.getDirectorId().equals(other.getDirectorId())
	    	&&this.getMinisterId() == null ? other.getMinisterId() == null : this.getMinisterId().equals(other.getMinisterId())
	    	&&this.getCustomerId() == null ? other.getCustomerId() == null : this.getCustomerId().equals(other.getCustomerId())
	    	&&this.getSalerRecordId() == null ? other.getSalerRecordId() == null : this.getSalerRecordId().equals(other.getSalerRecordId())
	    	&&this.getBeforeFlowId() == null ? other.getBeforeFlowId() == null : this.getBeforeFlowId().equals(other.getBeforeFlowId())
	    	&&this.getAfterFlowId() == null ? other.getAfterFlowId() == null : this.getAfterFlowId().equals(other.getAfterFlowId())
				);
	}

	/**
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
    	result = prime * result + ((getSalerId() == null) ? 0 : getSalerId().hashCode());
    	result = prime * result + ((getManagerId() == null) ? 0 : getManagerId().hashCode());
    	result = prime * result + ((getDirectorId() == null) ? 0 : getDirectorId().hashCode());
    	result = prime * result + ((getMinisterId() == null) ? 0 : getMinisterId().hashCode());
    	result = prime * result + ((getCustomerId() == null) ? 0 : getCustomerId().hashCode());
    	result = prime * result + ((getSalerRecordId() == null) ? 0 : getSalerRecordId().hashCode());
    	result = prime * result + ((getBeforeFlowId() == null) ? 0 : getBeforeFlowId().hashCode());
    	result = prime * result + ((getAfterFlowId() == null) ? 0 : getAfterFlowId().hashCode());
		return result;
	}
}
