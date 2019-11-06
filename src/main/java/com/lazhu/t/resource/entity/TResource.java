package com.lazhu.t.resource.entity;

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
 * @since 2017-10-27
 */
@ApiModel(value = "",description="resource")
@TableName("t_resource")
public class TResource extends BaseModel {
    private static final long serialVersionUID = 1L;
    //name  
    @ApiModelProperty(value = "", required = false)
    @TableField("name")
    private String name;
    //md5_mobile  
    @ApiModelProperty(value = "", required = false)
    @TableField("md5_mobile")
    private String md5Mobile;
    //fuzzy_mobile  
    @ApiModelProperty(value = "", required = false)
    @TableField("fuzzy_mobile")
    private String fuzzyMobile;
    //content  
    @ApiModelProperty(value = "", required = false)
    @TableField("content")
    private String content;
    //plan_name  
    @ApiModelProperty(value = "", required = false)
    @TableField("plan_name")
    private String planName;
    //plan_code  
    @ApiModelProperty(value = "", required = false)
    @TableField("plan_code")
    private String planCode;
    //source_url  
    @ApiModelProperty(value = "", required = false)
    @TableField("source_url")
    private String sourceUrl;
    //referer_url  
    @ApiModelProperty(value = "", required = false)
    @TableField("referer_url")
    private String refererUrl;
    //user_agent  
    @ApiModelProperty(value = "", required = false)
    @TableField("user_agent")
    private String userAgent;
    //executer  
    @ApiModelProperty(value = "", required = false)
    @TableField("executer")
    private String executer;
    //creater_time  
    @ApiModelProperty(value = "", required = false)
    @TableField("creater_time")
    private Date createrTime;
    //channel  
    @ApiModelProperty(value = "", required = false)
    @TableField("channel")
    private String channel;
    //ip  
    @ApiModelProperty(value = "", required = false)
    @TableField("ip")
    private String ip;
    
    @ApiModelProperty(value = "手机号验证（-1：验证失败  0 ： 不验证  1：未验证  2：验证成功）", required = false)
    @TableField("sms_check")
    private Integer smsCheck;
    
    @ApiModelProperty(value = "sessionId", required = false)
    @TableField("session_id")
    private String sessionId;
    
    @ApiModelProperty(value = "短险验证（-2：验证异常，-1：非法号码，  0 ： 不校验  1：等待验证  2：合法号码）", required = false)
    @TableField("phone_status")
    private Long phoneStatus;
    
    @ApiModelProperty(value = "短险发送ID", required = false)
    @TableField("biz_id")
    private String bizId;
    
    @ApiModelProperty(value = "加密手机号密文", required = false)
    @TableField("secret_mobile")
    private String secretMobile;
    
    @ApiModelProperty(value = "短信错误码", required = false)
    @TableField("sms_err_code")
    private String smsErrCode;
    
    @ApiModelProperty(value = "搜索关键词", required = false)
    @TableField("keywords")
    private String keywords;
    
    @ApiModelProperty(value = "省份", required = false)
    @TableField("province")
    private String province;
    
    @ApiModelProperty(value = "城市", required = false)
    @TableField("city")
    private String city;
    
    

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setMd5Mobile(String md5Mobile)
    {
        this.md5Mobile = md5Mobile;
    }

    public String getMd5Mobile()
    {
        return this.md5Mobile;
    }

    public void setFuzzyMobile(String fuzzyMobile)
    {
        this.fuzzyMobile = fuzzyMobile;
    }

    public String getFuzzyMobile()
    {
        return this.fuzzyMobile;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return this.content;
    }

    public void setPlanName(String planName)
    {
        this.planName = planName;
    }

    public String getPlanName()
    {
        return this.planName;
    }

    public void setPlanCode(String planCode)
    {
        this.planCode = planCode;
    }

    public String getPlanCode()
    {
        return this.planCode;
    }

    public void setSourceUrl(String sourceUrl)
    {
        this.sourceUrl = sourceUrl;
    }

    public String getSourceUrl()
    {
        return this.sourceUrl;
    }

    public void setRefererUrl(String refererUrl)
    {
        this.refererUrl = refererUrl;
    }

    public String getRefererUrl()
    {
        return this.refererUrl;
    }

    public void setUserAgent(String userAgent)
    {
        this.userAgent = userAgent;
    }

    public String getUserAgent()
    {
        return this.userAgent;
    }

    public void setExecuter(String executer)
    {
        this.executer = executer;
    }

    public String getExecuter()
    {
        return this.executer;
    }

    public void setCreaterTime(Date createrTime)
    {
        this.createrTime = createrTime;
    }

    public Date getCreaterTime()
    {
        return this.createrTime;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getChannel()
    {
        return this.channel;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getIp()
    {
        return this.ip;
    }

	public Integer getSmsCheck() {
		return smsCheck;
	}

	public void setSmsCheck(Integer smsCheck) {
		this.smsCheck = smsCheck;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Long getPhoneStatus() {
		return phoneStatus;
	}

	public void setPhoneStatus(Long phoneStatus) {
		this.phoneStatus = phoneStatus;
	}

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public String getSecretMobile() {
		return secretMobile;
	}

	public void setSecretMobile(String secretMobile) {
		this.secretMobile = secretMobile;
	}

	public String getSmsErrCode() {
		return smsErrCode;
	}

	public void setSmsErrCode(String smsErrCode) {
		this.smsErrCode = smsErrCode;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
