package com.lazhu.crm.resourceleave.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

/**
 * <p>
 * 资源闲置表
 * </p>
 *
 * @author hz
 * @since 2018-04-13
 */
@ApiModel(value = "资源闲置表",description="resourceLeave")
@TableName("crm_resource_leave")
public class ResourceLeave extends BaseModel {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "分配表ID", required = false)
    @TableField("allot_id")
    private Long allotId;
    //flowId 流程
    @ApiModelProperty(value = "流程ID", required = false)
    @TableField("flow_id")
    private Integer flowId;
    @ApiModelProperty(value = "旧流程ID", required = false)
    @TableField("old_flow_id")
    private Integer oldFlowId;
    //name 录入人姓名 
    @ApiModelProperty(value = "录入人姓名", required = false)
    @TableField("name")
    private String name;
    //from_info 来源渠道 
    @ApiModelProperty(value = "来源渠道", required = false)
    @TableField("from_info")
    private String fromInfo;
    //enter_time 入库时间 
    @ApiModelProperty(value = "入库时间", required = false)
    @TableField("enter_time")
    private Date enterTime;
    //wechat_no 微信号 
    @ApiModelProperty(value = "微信号", required = false)
    @TableField("wechat_no")
    private String wechatNo;
    //qq qq号 
    @ApiModelProperty(value = "qq号", required = false)
    @TableField("qq")
    private String qq;
    //mobile 手机号 
    @ApiModelProperty(value = "手机号", required = false)
    @TableField(exist = false)
    private String mobile;
    @ApiModelProperty(value = "MD5加密", required = false)
    @TableField("md5_mobile")
    private String md5Mobile;
    @ApiModelProperty(value = "掩码手机号", required = false)
    @TableField("fuzzy_mobile")
    private String fuzzyMobile;
    //phone 其他电话 
    @ApiModelProperty(value = "其他电话", required = false)
    @TableField("phone")
    private String phone;
    @ApiModelProperty(value = "生日", required = false)
    @TableField("birthday")
    private Date birthday;
    @ApiModelProperty(value = "年龄", required = false)
    @TableField("age")
    private Integer age;
    @ApiModelProperty(value = "性别", required = false)
    @TableField("sex")
    private Integer sex;
    @ApiModelProperty(value = "职业", required = false)
    @TableField("duty")
    private String duty;
    //province 省份 
    @ApiModelProperty(value = "省份", required = false)
    @TableField("province")
    private String province;
    //city 城市 
    @ApiModelProperty(value = "城市", required = false)
    @TableField("city")
    private String city;
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
    
    @ApiModelProperty(value = "客户需求", required = false)
    @TableField("demand")
    private String demand;
    @ApiModelProperty(value = "专题名", required = false)
    @TableField("plan_code")
    private String planCode;

    @ApiModelProperty(value = "抽取次数", required = false)
    @TableField("extract_num")
    private Integer extractNum;
    
    @ApiModelProperty(value = "抽取时间", required = false)
    @TableField("extract_time")
    private Date extractTime;
    
    @ApiModelProperty(value = "到期日期", required = false)
    @TableField("retain_time")
    private Date retainTime;
    
    @ApiModelProperty(value = "丢失日期（丢弃、自动回收）", required = false)
    @TableField("loss_time")
    private Date lossTime;
    
    @ApiModelProperty(value = "是否丢弃资源", required = false)
    @TableField("is_lose")
    private Integer isLose;
    
    @ApiModelProperty(value = "丢弃次数", required = false)
    @TableField("lose_num")
    private Integer loseNum;
    
    @ApiModelProperty(value = "首次通话时间", required = false)
    @TableField("first_call_time")
    private Date firstCallTime;
    
    @ApiModelProperty(value = "最后通话时间", required = false)
    @TableField("last_call_time")
    private Date lastCallTime;
    
    @ApiModelProperty(value = "最后通话内容", required = false)
    @TableField("last_call_record")
    private String lastCallRecord;
    
    @ApiModelProperty(value = "是否可聊过", required = false)
    @TableField("is_talk")
    private Integer isTalk;
    
    @ApiModelProperty(value = "可聊过时间", required = false)
    @TableField("talk_time")
    private Date talkTime;

    @ApiModelProperty(value = "当前资源创建时间（变通）", required = false)
    @TableField(exist = false)
    private Date resourceTime;
    
    @ApiModelProperty(value = "黑名单备注", required = false)
    @TableField("black_remark")
    private String blackRemark;
    
    @ApiModelProperty(value = "正常工作轨迹ID", required = false)
    @TableField(exist = false)
	private Long workTrailNormalId;
    
    @ApiModelProperty(value = "搜索关键词", required = false)
    @TableField("keywords")
    private String keywords;
    
    

	public Long getAllotId() {
		return allotId;
	}

	public void setAllotId(Long allotId) {
		this.allotId = allotId;
	}

	public Integer getFlowId() {
		return flowId;
	}

	public void setFlowId(Integer flowId) {
		this.flowId = flowId;
	}

	public Integer getOldFlowId() {
		return oldFlowId;
	}

	public void setOldFlowId(Integer oldFlowId) {
		this.oldFlowId = oldFlowId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFromInfo() {
		return fromInfo;
	}

	public void setFromInfo(String fromInfo) {
		this.fromInfo = fromInfo;
	}

	public Date getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(Date enterTime) {
		this.enterTime = enterTime;
	}

	public String getWechatNo() {
		return wechatNo;
	}

	public void setWechatNo(String wechatNo) {
		this.wechatNo = wechatNo;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMd5Mobile() {
		return md5Mobile;
	}

	public void setMd5Mobile(String md5Mobile) {
		this.md5Mobile = md5Mobile;
	}

	public String getFuzzyMobile() {
		return fuzzyMobile;
	}

	public void setFuzzyMobile(String fuzzyMobile) {
		this.fuzzyMobile = fuzzyMobile;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
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

	public Long getSalerId() {
		return salerId;
	}

	public void setSalerId(Long salerId) {
		this.salerId = salerId;
	}

	public String getSalerName() {
		return salerName;
	}

	public void setSalerName(String salerName) {
		this.salerName = salerName;
	}

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public Long getDirectorId() {
		return directorId;
	}

	public void setDirectorId(Long directorId) {
		this.directorId = directorId;
	}

	public String getDirectorName() {
		return directorName;
	}

	public void setDirectorName(String directorName) {
		this.directorName = directorName;
	}

	public Long getMinisterId() {
		return ministerId;
	}

	public void setMinisterId(Long ministerId) {
		this.ministerId = ministerId;
	}

	public String getMinisterName() {
		return ministerName;
	}

	public void setMinisterName(String ministerName) {
		this.ministerName = ministerName;
	}

	public String getDemand() {
		return demand;
	}

	public void setDemand(String demand) {
		this.demand = demand;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public Integer getExtractNum() {
		return extractNum;
	}

	public void setExtractNum(Integer extractNum) {
		this.extractNum = extractNum;
	}

	public Date getExtractTime() {
		return extractTime;
	}

	public void setExtractTime(Date extractTime) {
		this.extractTime = extractTime;
	}

	public Date getRetainTime() {
		return retainTime;
	}

	public void setRetainTime(Date retainTime) {
		this.retainTime = retainTime;
	}

	public Date getLossTime() {
		return lossTime;
	}

	public void setLossTime(Date lossTime) {
		this.lossTime = lossTime;
	}

	public Integer getIsLose() {
		return isLose;
	}

	public void setIsLose(Integer isLose) {
		this.isLose = isLose;
	}

	public Integer getLoseNum() {
		return loseNum;
	}

	public void setLoseNum(Integer loseNum) {
		this.loseNum = loseNum;
	}

	public Date getFirstCallTime() {
		return firstCallTime;
	}

	public void setFirstCallTime(Date firstCallTime) {
		this.firstCallTime = firstCallTime;
	}

	public Date getLastCallTime() {
		return lastCallTime;
	}

	public void setLastCallTime(Date lastCallTime) {
		this.lastCallTime = lastCallTime;
	}

	public String getLastCallRecord() {
		return lastCallRecord;
	}

	public void setLastCallRecord(String lastCallRecord) {
		this.lastCallRecord = lastCallRecord;
	}

	public Integer getIsTalk() {
		return isTalk;
	}

	public void setIsTalk(Integer isTalk) {
		this.isTalk = isTalk;
	}

	public Date getTalkTime() {
		return talkTime;
	}

	public void setTalkTime(Date talkTime) {
		this.talkTime = talkTime;
	}

	public Date getResourceTime() {
		return resourceTime;
	}

	public void setResourceTime(Date resourceTime) {
		this.resourceTime = resourceTime;
	}

	public String getBlackRemark() {
		return blackRemark;
	}

	public void setBlackRemark(String blackRemark) {
		this.blackRemark = blackRemark;
	}

	public Long getWorkTrailNormalId() {
		return workTrailNormalId;
	}

	public void setWorkTrailNormalId(Long workTrailNormalId) {
		this.workTrailNormalId = workTrailNormalId;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

}
