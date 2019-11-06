package com.lazhu.aladdinpbx.agentevent.entity;

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
 * @since 2018-06-12
 */
@ApiModel(value = "",description="agentEvent")
@TableName("aladdinpbx_agent_event")
public class AgentEvent extends BaseModel {
    private static final long serialVersionUID = 1L;
    //code CODE 
    @ApiModelProperty(value = "CODE", required = false)
    @TableField("code")
    private Integer code;
    //agent_ext ext分机号 
    @ApiModelProperty(value = "ext分机号", required = false)
    @TableField("agent_ext")
    private Integer agentExt;
    //agent_from from分机号 
    @ApiModelProperty(value = "from分机号", required = false)
    @TableField("agent_from")
    private Integer agentFrom;
    //agent_to to分机号 
    @ApiModelProperty(value = "to 分机号", required = false)
    @TableField("agent_to")
    private Integer agentTo;
    //body body 
    @ApiModelProperty(value = "body 录音文件名", required = false)
    @TableField("body")
    private String body;
    //p1 p1 
    @ApiModelProperty(value = "p1 呼出号码", required = false)
    @TableField("p1")
    private String p1;
    //p2 p2 
    @ApiModelProperty(value = "p2", required = false)
    @TableField("p2")
    private String p2;
    //p3 p3 
    @ApiModelProperty(value = "p3", required = false)
    @TableField("p3")
    private String p3;
    //p4 p4 
    @ApiModelProperty(value = "p4", required = false)
    @TableField("p4")
    private String p4;
    //p5 p5 
    @ApiModelProperty(value = "p5", required = false)
    @TableField("p5")
    private String p5;
    //p6 p6 
    @ApiModelProperty(value = "p6", required = false)
    @TableField("p6")
    private String p6;
    //p7 p7 
    @ApiModelProperty(value = "p7", required = false)
    @TableField("p7")
    private String p7;
    //dT 事件时间 
    @ApiModelProperty(value = "事件时间", required = false)
    @TableField("dT")
    private Date dt;

    public void setCode(Integer code)
    {
        this.code = code;
    }

    public Integer getCode()
    {
        return this.code;
    }

    public void setAgentExt(Integer agentExt)
    {
        this.agentExt = agentExt;
    }

    public Integer getAgentExt()
    {
        return this.agentExt;
    }

    public void setAgentFrom(Integer agentFrom)
    {
        this.agentFrom = agentFrom;
    }

    public Integer getAgentFrom()
    {
        return this.agentFrom;
    }

    public void setAgentTo(Integer agentTo)
    {
        this.agentTo = agentTo;
    }

    public Integer getAgentTo()
    {
        return this.agentTo;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getBody()
    {
        return this.body;
    }

    public void setP1(String p1)
    {
        this.p1 = p1;
    }

    public String getP1()
    {
        return this.p1;
    }

    public void setP2(String p2)
    {
        this.p2 = p2;
    }

    public String getP2()
    {
        return this.p2;
    }

    public void setP3(String p3)
    {
        this.p3 = p3;
    }

    public String getP3()
    {
        return this.p3;
    }

    public void setP4(String p4)
    {
        this.p4 = p4;
    }

    public String getP4()
    {
        return this.p4;
    }

    public void setP5(String p5)
    {
        this.p5 = p5;
    }

    public String getP5()
    {
        return this.p5;
    }

    public void setP6(String p6)
    {
        this.p6 = p6;
    }

    public String getP6()
    {
        return this.p6;
    }

    public void setP7(String p7)
    {
        this.p7 = p7;
    }

    public String getP7()
    {
        return this.p7;
    }

    public void setDt(Date dt)
    {
        this.dt = dt;
    }

    public Date getDt()
    {
        return this.dt;
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
    	sb.append(", code=").append(getCode());
    	sb.append(", agentExt=").append(getAgentExt());
    	sb.append(", agentFrom=").append(getAgentFrom());
    	sb.append(", agentTo=").append(getAgentTo());
    	sb.append(", body=").append(getBody());
    	sb.append(", p1=").append(getP1());
    	sb.append(", p2=").append(getP2());
    	sb.append(", p3=").append(getP3());
    	sb.append(", p4=").append(getP4());
    	sb.append(", p5=").append(getP5());
    	sb.append(", p6=").append(getP6());
    	sb.append(", p7=").append(getP7());
    	sb.append(", dt=").append(getDt());
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
		AgentEvent other = (AgentEvent) that;
		return (1==1
	    	&&this.getCode() == null ? other.getCode() == null : this.getCode().equals(other.getCode())
	    	&&this.getAgentExt() == null ? other.getAgentExt() == null : this.getAgentExt().equals(other.getAgentExt())
	    	&&this.getAgentFrom() == null ? other.getAgentFrom() == null : this.getAgentFrom().equals(other.getAgentFrom())
	    	&&this.getAgentTo() == null ? other.getAgentTo() == null : this.getAgentTo().equals(other.getAgentTo())
	    	&&this.getBody() == null ? other.getBody() == null : this.getBody().equals(other.getBody())
	    	&&this.getP1() == null ? other.getP1() == null : this.getP1().equals(other.getP1())
	    	&&this.getP2() == null ? other.getP2() == null : this.getP2().equals(other.getP2())
	    	&&this.getP3() == null ? other.getP3() == null : this.getP3().equals(other.getP3())
	    	&&this.getP4() == null ? other.getP4() == null : this.getP4().equals(other.getP4())
	    	&&this.getP5() == null ? other.getP5() == null : this.getP5().equals(other.getP5())
	    	&&this.getP6() == null ? other.getP6() == null : this.getP6().equals(other.getP6())
	    	&&this.getP7() == null ? other.getP7() == null : this.getP7().equals(other.getP7())
	    	&&this.getDt() == null ? other.getDt() == null : this.getDt().equals(other.getDt())
				);
	}

	/**
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
    	result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
    	result = prime * result + ((getAgentExt() == null) ? 0 : getAgentExt().hashCode());
    	result = prime * result + ((getAgentFrom() == null) ? 0 : getAgentFrom().hashCode());
    	result = prime * result + ((getAgentTo() == null) ? 0 : getAgentTo().hashCode());
    	result = prime * result + ((getBody() == null) ? 0 : getBody().hashCode());
    	result = prime * result + ((getP1() == null) ? 0 : getP1().hashCode());
    	result = prime * result + ((getP2() == null) ? 0 : getP2().hashCode());
    	result = prime * result + ((getP3() == null) ? 0 : getP3().hashCode());
    	result = prime * result + ((getP4() == null) ? 0 : getP4().hashCode());
    	result = prime * result + ((getP5() == null) ? 0 : getP5().hashCode());
    	result = prime * result + ((getP6() == null) ? 0 : getP6().hashCode());
    	result = prime * result + ((getP7() == null) ? 0 : getP7().hashCode());
    	result = prime * result + ((getDt() == null) ? 0 : getDt().hashCode());
		return result;
	}
}
