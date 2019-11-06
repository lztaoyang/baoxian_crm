package com.lazhu.aladdinpbx.recordlogs.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 电话录音记录表
 * </p>
 *
 * @author hz
 * @since 2017-12-02
 */
@ApiModel(value = "电话录音记录表",description="recordlogs")
@TableName("aladdinpbx_recordlogs")
public class Recordlogs extends BaseModel {
    private static final long serialVersionUID = 1L;
    //REC_ID  
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_ID")
    private String recId;
    //REC_EXTNUMBER 
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_EXTNUMBER")
    private String recExtnumber;
    //REC_AGENTID
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_AGENTID")
    private String recAgentid;
    //REC_STARTTIME
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_STARTTIME")
    private Date recStarttime;
    //REC_ENDTIME
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_ENDTIME")
    private Date recEndtime;
    //REC_RECORDFILE 录音文件名
    @ApiModelProperty(value = "录音文件名", required = false)
    @TableField("REC_RECORDFILE")
    private String recRecordfile;
    //REC_RECORDLENGTH  
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_RECORDLENGTH")
    private Long recRecordlength;
    //REC_CALLEDERID  
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_CALLEDERID")
    private String recCallederid;
    //REC_RECORDPATH  
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_RECORDPATH")
    private String recRecordpath;
    //REC_PHONENUMBER  
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_PHONENUMBER")
    private String recPhonenumber;
    //REC_HANGUP  
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_HANGUP")
    private String recHangup;
    //REC_CALLTYPE  
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_CALLTYPE")
    private String recCalltype;
    //REC_RINGWAITTIME  
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_RINGWAITTIME")
    private String recRingwaittime;
    //REC_AREA  
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_AREA")
    private String recArea;
    //REC_AREACODE  
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_AREACODE")
    private String recAreacode;
    //REC_SERVERIP  
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_SERVERIP")
    private String recServerip;
    //REC_TALKTYPE  
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_TALKTYPE")
    private String recTalktype;
    //REC_PBXIP  
    @ApiModelProperty(value = "", required = false)
    @TableField("REC_PBXIP")
    private String recPbxip;
    
    @ApiModelProperty(value = "客户ID", required = false)
    @TableField(exist = false)
    private Long customerId;
    
    @ApiModelProperty(value = "客户姓名", required = false)
    @TableField(exist = false)
    private String customerName;
    
    @ApiModelProperty(value = "是否签单", required = false)
    @TableField(exist = false)
    private Integer isClub;
    
    @ApiModelProperty(value = "开发流程", required = false)
    @TableField(exist = false)
    private Integer flowId;
	
	@ApiModelProperty(value = "入库时间", required = false)
    @TableField(exist = false)
    private Date enterTime;
	
	@ApiModelProperty(value = "市场部归属", required = false)
    @TableField(exist = false)
    private String belong;
	
	@ApiModelProperty(value = "服务人姓名", required = false)
	@TableField(exist = false)
	private String serverName;
	
	@ApiModelProperty(value = "签单次数", required = false)
    @TableField(exist = false)
    private Integer signNum;

    public void setRecId(String recId)
    {
        this.recId = recId;
    }

    public String getRecId()
    {
        return this.recId;
    }

    public void setRecExtnumber(String recExtnumber)
    {
        this.recExtnumber = recExtnumber;
    }

    public String getRecExtnumber()
    {
        return this.recExtnumber;
    }

    public void setRecAgentid(String recAgentid)
    {
        this.recAgentid = recAgentid;
    }

    public String getRecAgentid()
    {
        return this.recAgentid;
    }

    public void setRecStarttime(Date recStarttime)
    {
        this.recStarttime = recStarttime;
    }

    public Date getRecStarttime()
    {
        return this.recStarttime;
    }

    public void setRecEndtime(Date recEndtime)
    {
        this.recEndtime = recEndtime;
    }

    public Date getRecEndtime()
    {
        return this.recEndtime;
    }

    public void setRecRecordfile(String recRecordfile)
    {
        this.recRecordfile = recRecordfile;
    }

    public String getRecRecordfile()
    {
        return this.recRecordfile;
    }

    public void setRecRecordlength(Long recRecordlength)
    {
        this.recRecordlength = recRecordlength;
    }

    public Long getRecRecordlength()
    {
        return this.recRecordlength;
    }

    public void setRecCallederid(String recCallederid)
    {
        this.recCallederid = recCallederid;
    }

    public String getRecCallederid()
    {
        return this.recCallederid;
    }

    public void setRecRecordpath(String recRecordpath)
    {
        this.recRecordpath = recRecordpath;
    }

    public String getRecRecordpath()
    {
        return this.recRecordpath;
    }

    public void setRecPhonenumber(String recPhonenumber)
    {
        this.recPhonenumber = recPhonenumber;
    }

    public String getRecPhonenumber()
    {
        return this.recPhonenumber;
    }

    public void setRecHangup(String recHangup)
    {
        this.recHangup = recHangup;
    }

    public String getRecHangup()
    {
        return this.recHangup;
    }

    public void setRecCalltype(String recCalltype)
    {
        this.recCalltype = recCalltype;
    }

    public String getRecCalltype()
    {
        return this.recCalltype;
    }

    public void setRecRingwaittime(String recRingwaittime)
    {
        this.recRingwaittime = recRingwaittime;
    }

    public String getRecRingwaittime()
    {
        return this.recRingwaittime;
    }

    public void setRecArea(String recArea)
    {
        this.recArea = recArea;
    }

    public String getRecArea()
    {
        return this.recArea;
    }

    public void setRecAreacode(String recAreacode)
    {
        this.recAreacode = recAreacode;
    }

    public String getRecAreacode()
    {
        return this.recAreacode;
    }

    public void setRecServerip(String recServerip)
    {
        this.recServerip = recServerip;
    }

    public String getRecServerip()
    {
        return this.recServerip;
    }

    public void setRecTalktype(String recTalktype)
    {
        this.recTalktype = recTalktype;
    }

    public String getRecTalktype()
    {
        return this.recTalktype;
    }

    public void setRecPbxip(String recPbxip)
    {
        this.recPbxip = recPbxip;
    }

    public String getRecPbxip()
    {
        return this.recPbxip;
    }

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Integer getIsClub() {
		return isClub;
	}

	public void setIsClub(Integer isClub) {
		this.isClub = isClub;
	}

	public Integer getFlowId() {
		return flowId;
	}

	public void setFlowId(Integer flowId) {
		this.flowId = flowId;
	}

	public Date getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(Date enterTime) {
		this.enterTime = enterTime;
	}

	public String getBelong() {
		return belong;
	}

	public void setBelong(String belong) {
		this.belong = belong;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public Integer getSignNum() {
		return signNum;
	}

	public void setSignNum(Integer signNum) {
		this.signNum = signNum;
	}

}
