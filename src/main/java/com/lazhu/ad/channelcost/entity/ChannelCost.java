package com.lazhu.ad.channelcost.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;
import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author phong
 * @since 2017-11-02
 */
@ApiModel(value = "渠道费用明细",description="channelCost")
@TableName("ad_channel_cost")
public class ChannelCost extends BaseModel {
    private static final long serialVersionUID = 1L;
    //cost_time 日期 
    @ApiModelProperty(value = "日期", required = false)
    @TableField("cost_time")
    private Date costTime;
    //channel  
    @ApiModelProperty(value = "渠道", required = false)
    @TableField("channel")
    private String channel;
    //executer 推广人 
    @ApiModelProperty(value = "推广人", required = false)
    @TableField("executer")
    private String executer;
    //cost 推广费 
    @ApiModelProperty(value = "推广费", required = false)
    @TableField("cost")
    private BigDecimal cost;
    //cost_id 总费用ID 
    @ApiModelProperty(value = "总费用ID", required = false)
    @TableField("cost_id")
    private Long costId;
    
    @ApiModelProperty(value = "旧费用", required = false)
    @TableField(exist = false)
    private BigDecimal oldCost;
    

    public void setCostTime(Date costTime)
    {
        this.costTime = costTime;
    }

    public Date getCostTime()
    {
        return this.costTime;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getChannel()
    {
        return this.channel;
    }

    public void setExecuter(String executer)
    {
        this.executer = executer;
    }

    public String getExecuter()
    {
        return this.executer;
    }

    public void setCost(BigDecimal cost)
    {
        this.cost = cost;
    }

    public BigDecimal getCost()
    {
        return this.cost;
    }

    public void setCostId(Long costId)
    {
        this.costId = costId;
    }

    public Long getCostId()
    {
        return this.costId;
    }
    
	public BigDecimal getOldCost() {
		return oldCost;
	}

	public void setOldCost(BigDecimal oldCost) {
		this.oldCost = oldCost;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
    	sb.append(", costTime=").append(getCostTime());
    	sb.append(", channel=").append(getChannel());
    	sb.append(", executer=").append(getExecuter());
    	sb.append(", cost=").append(getCost());
    	sb.append(", costId=").append(getCostId());
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
		ChannelCost other = (ChannelCost) that;
		return (1==1
	    	&&this.getCostTime() == null ? other.getCostTime() == null : this.getCostTime().equals(other.getCostTime())
	    	&&this.getChannel() == null ? other.getChannel() == null : this.getChannel().equals(other.getChannel())
	    	&&this.getExecuter() == null ? other.getExecuter() == null : this.getExecuter().equals(other.getExecuter())
	    	&&this.getCost() == null ? other.getCost() == null : this.getCost().equals(other.getCost())
	    	&&this.getCostId() == null ? other.getCostId() == null : this.getCostId().equals(other.getCostId())
				);
	}

	/**
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
    	result = prime * result + ((getCostTime() == null) ? 0 : getCostTime().hashCode());
    	result = prime * result + ((getChannel() == null) ? 0 : getChannel().hashCode());
    	result = prime * result + ((getExecuter() == null) ? 0 : getExecuter().hashCode());
    	result = prime * result + ((getCost() == null) ? 0 : getCost().hashCode());
    	result = prime * result + ((getCostId() == null) ? 0 : getCostId().hashCode());
		return result;
	}
}
