package com.lazhu.t.resourceallot.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

/**
 * <p>
 * 推广资源分配表
 * </p>
 *
 * @author hz
 * @since 2017-10-27
 */
@ApiModel(value = "推广资源分配表", description = "resourceAllot")
@TableName("t_resource_allot")
public class ResourceAllot extends BaseModel {
	private static final long serialVersionUID = 1L;
	// t_id 推广ID
	@ApiModelProperty(value = "推广ID", required = false)
	@TableField("t_id")
	private Long tId;
	// name 姓名
	@ApiModelProperty(value = "姓名", required = false)
	@TableField("name")
	private String name;
	//
	@ApiModelProperty(value = "手机掩码", required = false)
	@TableField("fuzzy_mobile")
	private String fuzzyMobile;
	//
	@ApiModelProperty(value = "MD5加密", required = false)
	@TableField("md5_mobile")
	private String md5Mobile;
	@ApiModelProperty(value = "省份", required = false)
	@TableField("province")
	private String province;
	@ApiModelProperty(value = "城市", required = false)
	@TableField("city")
	private String city;
	// content 内容
	@ApiModelProperty(value = "内容", required = false)
	@TableField("content")
	private String content;
	// plan_name 计划名
	@ApiModelProperty(value = "计划名", required = false)
	@TableField("plan_name")
	private String planName;
	// plan_code 专题名
	@ApiModelProperty(value = "专题名", required = false)
	@TableField("plan_code")
	private String planCode;
	// source_url 来源地址
	@ApiModelProperty(value = "来源地址", required = false)
	@TableField("source_url")
	private String sourceUrl;
	// referer_url referer来源
	@ApiModelProperty(value = "referer来源", required = false)
	@TableField("referer_url")
	private String refererUrl;
	// user_agent UserAgent浏览器
	@ApiModelProperty(value = "UserAgent浏览器", required = false)
	@TableField("user_agent")
	private String userAgent;
	// executer 执行人
	@ApiModelProperty(value = "执行人", required = false)
	@TableField("executer")
	private String executer;
	// creater_time 创建日期
	@ApiModelProperty(value = "创建日期", required = false)
	@TableField("creater_time")
	private Date createrTime;
	// channel 渠道
	@ApiModelProperty(value = "渠道", required = false)
	@TableField("channel")
	private String channel;
	// ip ip
	@ApiModelProperty(value = "ip", required = false)
	@TableField("ip")
	private String ip;
	// is_allot 是否已分配
	@ApiModelProperty(value = "分配状态", required = false)
	@TableField("type")
	private Integer type;
	@ApiModelProperty(value = "分配时间", required = false)
	@TableField("allot_time")
	private Date allotTime;
	// saler_id 所属顾问ID
	@ApiModelProperty(value = "所属顾问ID", required = false)
	@TableField("saler_id")
	private Long salerId;
	// saler_name 所属顾问姓名
	@ApiModelProperty(value = "所属顾问姓名", required = false)
	@TableField("saler_name")
	private String salerName;
	// manager_id 所属经理ID
	@ApiModelProperty(value = "所属经理ID", required = false)
	@TableField("manager_id")
	private Long managerId;
	// manager_name 所属经理姓名
	@ApiModelProperty(value = "所属经理姓名", required = false)
	@TableField("manager_name")
	private String managerName;
	// director_id 所属总监ID
	@ApiModelProperty(value = "所属总监ID", required = false)
	@TableField("director_id")
	private Long directorId;
	// director_name 所属总监姓名
	@ApiModelProperty(value = "所属总监姓名", required = false)
	@TableField("director_name")
	private String directorName;
	// minister_id 所属总经理ID
	@ApiModelProperty(value = "所属总经理ID", required = false)
	@TableField("minister_id")
	private Long ministerId;
	// minister_name 所属总经理姓名
	@ApiModelProperty(value = "所属总经理姓名", required = false)
	@TableField("minister_name")
	private String ministerName;

	@ApiModelProperty(value = "资源ID", required = false)
	@TableField("resource_id")
	private Long resourceId;
	@ApiModelProperty(value = "资源流程", required = false)
	@TableField("resource_flow")
	private Integer resourceFlow;
	@ApiModelProperty(value = "资源备注", required = false)
	@TableField(exist = false)
	private String resourceRemark;
	@ApiModelProperty(value = "拨打次数", required = false)
	@TableField(exist = false)
	private Integer callNum;
	@ApiModelProperty(value = "签单次数", required = false)
	@TableField(exist = false)
	private Integer signNum;
	@ApiModelProperty(value = "签单总保费", required = false)
	@TableField(exist = false)
	private Double signAmount;

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

	@ApiModelProperty(value = "推广资源更新日期", required = false)
	@TableField("source_update_time")
	private Date sourceUpdateTime;
    
    @ApiModelProperty(value = "搜索关键词", required = false)
    @TableField("keywords")
    private String keywords;

    @ApiModelProperty(value = "是否可聊过", required = false)
    @TableField("is_talk")
    private Integer isTalk;
    
    //是否测试数据，手机号明文，是否已成交，是否资源（资源流程ID），是否共享池资源（已删除资源流程ID）
    @ApiModelProperty(value = "是否测试数据", required = false)
    @TableField(exist = false)
    private Integer isTest;
    @ApiModelProperty(value = "手机号明文", required = false)
    @TableField(exist = false)
    private String mobile;
    @ApiModelProperty(value = "是否已成交", required = false)
    @TableField(exist = false)
    private Integer isClub;
    @ApiModelProperty(value = "是否资源（资源流程ID）", required = false)
    @TableField(exist = false)
    private Integer resourceFlowId;
    @ApiModelProperty(value = "是否闲置资源", required = false)
    @TableField(exist = false)
    private Integer isLeaveResource;

	public Integer getIsTest() {
		return isTest;
	}

	public void setIsTest(Integer isTest) {
		this.isTest = isTest;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getIsClub() {
		return isClub;
	}

	public void setIsClub(Integer isClub) {
		this.isClub = isClub;
	}

	public Integer getResourceFlowId() {
		return resourceFlowId;
	}

	public void setResourceFlowId(Integer resourceFlowId) {
		this.resourceFlowId = resourceFlowId;
	}

	public Integer getIsLeaveResource() {
		return isLeaveResource;
	}

	public void setIsLeaveResource(Integer isLeaveResource) {
		this.isLeaveResource = isLeaveResource;
	}

	public Long gettId() {
		return tId;
	}

	public void settId(Long tId) {
		this.tId = tId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFuzzyMobile() {
		return fuzzyMobile;
	}

	public void setFuzzyMobile(String fuzzyMobile) {
		this.fuzzyMobile = fuzzyMobile;
	}

	public String getMd5Mobile() {
		return md5Mobile;
	}

	public void setMd5Mobile(String md5Mobile) {
		this.md5Mobile = md5Mobile;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getRefererUrl() {
		return refererUrl;
	}

	public void setRefererUrl(String refererUrl) {
		this.refererUrl = refererUrl;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getExecuter() {
		return executer;
	}

	public void setExecuter(String executer) {
		this.executer = executer;
	}

	public Date getCreaterTime() {
		return createrTime;
	}

	public void setCreaterTime(Date createrTime) {
		this.createrTime = createrTime;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getAllotTime() {
		return allotTime;
	}

	public void setAllotTime(Date allotTime) {
		this.allotTime = allotTime;
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

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public Integer getResourceFlow() {
		return resourceFlow;
	}

	public void setResourceFlow(Integer resourceFlow) {
		this.resourceFlow = resourceFlow;
	}

	public String getResourceRemark() {
		return resourceRemark;
	}

	public void setResourceRemark(String resourceRemark) {
		this.resourceRemark = resourceRemark;
	}

	public Integer getCallNum() {
		return callNum;
	}

	public void setCallNum(Integer callNum) {
		this.callNum = callNum;
	}

	public Integer getSignNum() {
		return signNum;
	}

	public void setSignNum(Integer signNum) {
		this.signNum = signNum;
	}

	public Double getSignAmount() {
		return signAmount;
	}

	public void setSignAmount(Double signAmount) {
		this.signAmount = signAmount;
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

	public Date getSourceUpdateTime() {
		return sourceUpdateTime;
	}

	public void setSourceUpdateTime(Date sourceUpdateTime) {
		this.sourceUpdateTime = sourceUpdateTime;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public Integer getIsTalk() {
		return isTalk;
	}

	public void setIsTalk(Integer isTalk) {
		this.isTalk = isTalk;
	}

}
