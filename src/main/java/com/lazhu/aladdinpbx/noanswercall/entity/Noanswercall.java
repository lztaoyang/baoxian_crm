package com.lazhu.aladdinpbx.noanswercall.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 未接电话记录表
 * </p>
 *
 * @author hz
 * @since 2017-12-18
 */
@ApiModel(value = "未接电话记录表",description="noanswercall")
@TableName("aladdinpbx_noanswercall")
public class Noanswercall extends BaseModel {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "", required = false)
    @TableField("NAS_ID")
    private String nasId;

    @ApiModelProperty(value = "呼入号码", required = false)
    @TableField("NAS_CALLEDERID")
    private String nasCallederid;

    @ApiModelProperty(value = "分机号", required = false)
    @TableField("NAS_EXTNUMBER")
    private String nasExtnumber;

    @ApiModelProperty(value = "开始时间", required = false)
    @TableField("NAS_STARTTIME")
    private Date nasStarttime;

    @ApiModelProperty(value = "结束时间", required = false)
    @TableField("NAS_ENDTIME")
    private Date nasEndtime;

    @ApiModelProperty(value = "呼入时长", required = false)
    @TableField("NAS_LENGTH")
    private Long nasLength;

    @ApiModelProperty(value = "", required = false)
    @TableField("NAS_DEAL")
    private String nasDeal;

    @ApiModelProperty(value = "分机号码", required = false)
    @TableField("NAS_PHONENUMBER")
    private String nasPhonenumber;

    @ApiModelProperty(value = "", required = false)
    @TableField("NAS_WAITELENGTH")
    private Long nasWaitelength;

    @ApiModelProperty(value = "CRM用户ID", required = false)
    @TableField("NAS_AGENTID")
    private String nasAgentid;

    @ApiModelProperty(value = "ip", required = false)
    @TableField("NAS_PBXIP")
    private String nasPbxip;

    @ApiModelProperty(value = "", required = false)
    @TableField("NAS_RINGLENGTH")
    private String nasRinglength;
    
    
    @ApiModelProperty(value = "关联会员或资源ID", required = false)
    @TableField(exist = false)
    private Long customerId;
    @ApiModelProperty(value = "关联会员或资源姓名", required = false)
    @TableField(exist = false)
    private String customerName;
    @ApiModelProperty(value = "是否会员", required = false)
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
    @ApiModelProperty(value = "所属客服姓名", required = false)
    @TableField(exist = false)
    private String serverName;
    @ApiModelProperty(value = "签单次数", required = false)
    @TableField(exist = false)
    private Integer signNum;
    
    
    
	public String getNasId() {
		return nasId;
	}
	public void setNasId(String nasId) {
		this.nasId = nasId;
	}
	public String getNasCallederid() {
		return nasCallederid;
	}
	public void setNasCallederid(String nasCallederid) {
		this.nasCallederid = nasCallederid;
	}
	public String getNasExtnumber() {
		return nasExtnumber;
	}
	public void setNasExtnumber(String nasExtnumber) {
		this.nasExtnumber = nasExtnumber;
	}
	public Date getNasStarttime() {
		return nasStarttime;
	}
	public void setNasStarttime(Date nasStarttime) {
		this.nasStarttime = nasStarttime;
	}
	public Date getNasEndtime() {
		return nasEndtime;
	}
	public void setNasEndtime(Date nasEndtime) {
		this.nasEndtime = nasEndtime;
	}
	public Long getNasLength() {
		return nasLength;
	}
	public void setNasLength(Long nasLength) {
		this.nasLength = nasLength;
	}
	public String getNasDeal() {
		return nasDeal;
	}
	public void setNasDeal(String nasDeal) {
		this.nasDeal = nasDeal;
	}
	public String getNasPhonenumber() {
		return nasPhonenumber;
	}
	public void setNasPhonenumber(String nasPhonenumber) {
		this.nasPhonenumber = nasPhonenumber;
	}
	public Long getNasWaitelength() {
		return nasWaitelength;
	}
	public void setNasWaitelength(Long nasWaitelength) {
		this.nasWaitelength = nasWaitelength;
	}
	public String getNasAgentid() {
		return nasAgentid;
	}
	public void setNasAgentid(String nasAgentid) {
		this.nasAgentid = nasAgentid;
	}
	public String getNasPbxip() {
		return nasPbxip;
	}
	public void setNasPbxip(String nasPbxip) {
		this.nasPbxip = nasPbxip;
	}
	public String getNasRinglength() {
		return nasRinglength;
	}
	public void setNasRinglength(String nasRinglength) {
		this.nasRinglength = nasRinglength;
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
