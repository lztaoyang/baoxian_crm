package com.lazhu.ad.cost.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.ad.channelcost.entity.ChannelCost;
import com.lazhu.core.base.BaseModel;

/**
 * <p>
 * 
 * </p>
 *
 * @author phong
 * @since 2017-11-02
 */
@ApiModel(value = "",description="cost")
@TableName("ad_cost")
public class Cost extends BaseModel {
    private static final long serialVersionUID = 1L;
    //cost_time 日期 
    @ApiModelProperty(value = "日期", required = false)
    @TableField("cost_time")
    private Date costTime;
    //total_cost 总费用 
    @ApiModelProperty(value = "总费用", required = false)
    @TableField("total_cost")
    private BigDecimal totalCost;
    
    @TableField(exist=false)
    private List<ChannelCost> costs;

    public void setCostTime(Date costTime)
    {
        this.costTime = costTime;
    }

    public Date getCostTime()
    {
        return this.costTime;
    }

    public void setTotalCost(BigDecimal totalCost)
    {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalCost()
    {
        return this.totalCost;
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
    	sb.append(", costTime=").append(getCostTime());
    	sb.append(", totalCost=").append(getTotalCost());
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
		Cost other = (Cost) that;
		return (1==1
	    	&&this.getCostTime() == null ? other.getCostTime() == null : this.getCostTime().equals(other.getCostTime())
	    	&&this.getTotalCost() == null ? other.getTotalCost() == null : this.getTotalCost().equals(other.getTotalCost())
				);
	}

	/**
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
    	result = prime * result + ((getCostTime() == null) ? 0 : getCostTime().hashCode());
    	result = prime * result + ((getTotalCost() == null) ? 0 : getTotalCost().hashCode());
		return result;
	}

	public List<ChannelCost> getCosts() {
		return costs;
	}

	public void setCosts(List<ChannelCost> costs) {
		this.costs = costs;
	}
}
