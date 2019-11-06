package com.lazhu.crm.mobilelastlog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

/**
 * <p>
 * 最近拨打记录
 * </p>
 *
 * @author hz
 * @since 2018-01-07
 */
@ApiModel(value = "最近拨打记录",description="mobileLastLog")
@TableName("crm_mobile_last_log")
public class MobileLastLog extends BaseModel {
    private static final long serialVersionUID = 1L;
    //md5_mobile MD5手机号 
    @ApiModelProperty(value = "MD5手机号", required = false)
    @TableField("md5_mobile")
    private String md5Mobile;
    //agent_no 分机号 
    @ApiModelProperty(value = "分机号", required = false)
    @TableField("agent_no")
    private String agentNo;
    //type 类型 
    @ApiModelProperty(value = "类型", required = false)
    @TableField("type")
    private Integer type;


    public String getMd5Mobile() {
		return md5Mobile;
	}

	public void setMd5Mobile(String md5Mobile) {
		this.md5Mobile = md5Mobile;
	}

	public void setAgentNo(String agentNo)
    {
        this.agentNo = agentNo;
    }

    public String getAgentNo()
    {
        return this.agentNo;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getType()
    {
        return this.type;
    }

}
