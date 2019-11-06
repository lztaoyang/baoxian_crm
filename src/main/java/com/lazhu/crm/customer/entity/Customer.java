package com.lazhu.crm.customer.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

/**
 * <p>
 * 成交客户 实体类
 * </p>
 *
 * @author hz
 * @since 2017-06-02
 */
@ApiModel(value = "",description="customer")
@TableName("crm_customer")
public class Customer extends BaseModel {
    private static final long serialVersionUID = 1L;
   
    //customer_id 客户ID 
    @TableField(exist = false)
    private Object ids;
    //customer_name 客户姓名 
    @TableField(exist = false)
    private Object names;
    
    //name 姓名 
    @ApiModelProperty(value = "姓名", required = false)
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
    //flow_id 流程 
    @ApiModelProperty(value = "流程", required = false)
    @TableField("flow_id")
    private Integer flowId;
    //wechat_no 微信 
    @ApiModelProperty(value = "微信", required = false)
    @TableField("wechat_no")
    private String wechatNo;
    //qq QQ 
    @ApiModelProperty(value = "QQ", required = false)
    @TableField("qq")
    private String qq;
    //mobile 电话 
    @ApiModelProperty(value = "手机号", required = false)
    @TableField(exist = false)
    private String mobile;
    @ApiModelProperty(value = "详情页手机号", required = false)
    @TableField(exist = false)
    private String showMobile;
    @ApiModelProperty(value = "MD5加密", required = false)
    @TableField("md5_mobile")
    private String md5Mobile;
    @ApiModelProperty(value = "掩码手机号", required = false)
    @TableField("fuzzy_mobile")
    private String fuzzyMobile;
    //phone 其他号码 
    @ApiModelProperty(value = "其他号码", required = false)
    @TableField("phone")
    private String phone;
    //duty 职业 
    @ApiModelProperty(value = "职业", required = false)
    @TableField("duty")
    private String duty;
    @ApiModelProperty(value = "性别", required = false)
    @TableField("sex")
    private Integer sex;
    @ApiModelProperty(value = "年龄", required = false)
    @TableField("age")
    private Integer age;
    //birthday 生日 
    @ApiModelProperty(value = "生日", required = false)
    @TableField("birthday")
    private Date birthday;
    //province 省份 
    @ApiModelProperty(value = "省份", required = false)
    @TableField("province")
    private String province;
    //city 城市 
    @ApiModelProperty(value = "城市", required = false)
    @TableField("city")
    private String city;
    //insure_num 投保次数(有多少个保单) 
    @ApiModelProperty(value = "投保次数(有多少个保单)", required = false)
    @TableField("insure_num")
    private Integer insureNum;
    //insure_money 投保总金额(保单金额累加) 
    @ApiModelProperty(value = "投保总金额(保单金额累加)", required = false)
    @TableField("insure_money")
    private Double insureMoney;
    //current_apply_id 当前签单ID 
    @ApiModelProperty(value = "当前签单ID", required = false)
    @TableField("current_apply_id")
    private Long currentApplyId;
    //is_service 是否服务 
    @ApiModelProperty(value = "是否服务", required = false)
    @TableField("is_service")
    private Integer isService;
    //saler_id 所属顾问ID 
    @ApiModelProperty(value = "所属顾问ID", required = false)
    @TableField("saler_id")
    private Long salerId;
    //manager_id 所属经理ID 
    @ApiModelProperty(value = "所属经理ID", required = false)
    @TableField("manager_id")
    private Long managerId;
    //director_id 所属总监ID 
    @ApiModelProperty(value = "所属总监ID", required = false)
    @TableField("director_id")
    private Long directorId;
    //minister_id 所属总经理ID 
    @ApiModelProperty(value = "所属总经理ID", required = false)
    @TableField("minister_id")
    private Long ministerId;
    //saler_name 所属顾问姓名 
    @ApiModelProperty(value = "所属顾问姓名", required = false)
    @TableField("saler_name")
    private String salerName;
    //manager_name 所属经理姓名 
    @ApiModelProperty(value = "所属经理姓名", required = false)
    @TableField("manager_name")
    private String managerName;
    //director_name 所属总监姓名 
    @ApiModelProperty(value = "所属总监姓名", required = false)
    @TableField("director_name")
    private String directorName;
    //minister_name 所属总经理姓名 
    @ApiModelProperty(value = "所属总经理姓名", required = false)
    @TableField("minister_name")
    private String ministerName;
    //CONTRACTER_ID 商务人员ID 
    @ApiModelProperty(value = "商务人员ID", required = false)
    @TableField("CONTRACTER_ID")
    private Long contracterId;
    //CONTRACTER_name 商务人员姓名 
    @ApiModelProperty(value = "商务人员姓名", required = false)
    @TableField("CONTRACTER_name")
    private String contracterName;
    //contracter_remark 商务备注 
    @ApiModelProperty(value = "商务备注", required = false)
    @TableField("contracter_remark")
    private String contracterRemark;
    //server_remark 客服备注 
    @ApiModelProperty(value = "客服备注", required = false)
    @TableField("server_remark")
    private String serverRemark;
    //server_id 客服人员ID 
    @ApiModelProperty(value = "客服经理ID", required = false)
    @TableField("server_manager_id")
    private Long serverManagerId;
    //server_name 客服人员姓名 
    @ApiModelProperty(value = "客服经理姓名", required = false)
    @TableField("server_manager_name")
    private String serverManagerName;
    //server_id 客服人员ID 
    @ApiModelProperty(value = "客服人员ID", required = false)
    @TableField("server_id")
    private Long serverId;
    //server_name 客服人员姓名 
    @ApiModelProperty(value = "客服人员姓名", required = false)
    @TableField("server_name")
    private String serverName;
    //old_apply_id 前一次签单ID 
    @ApiModelProperty(value = "前一次签单ID", required = false)
    @TableField("old_apply_id")
    private Long oldApplyId;
    //old_flow_id 前一次流程 
    @ApiModelProperty(value = "前一次流程", required = false)
    @TableField("old_flow_id")
    private Integer oldFlowId;
    //old_is_service 前一次是否服务 
    @ApiModelProperty(value = "前一次是否服务", required = false)
    @TableField("old_is_service")
    private Integer oldIsService;
    //old_server_id 前一次客服ID 
    @ApiModelProperty(value = "前一次客服ID", required = false)
    @TableField("old_server_id")
    private Long oldServerId;
    //old_server_name 前一次客服姓名 
    @ApiModelProperty(value = "前一次客服姓名", required = false)
    @TableField("old_server_name")
    private String oldServerName;
    //apply_time 签单时间
    @ApiModelProperty(value = "签单时间", required = false)
    @TableField("apply_time")
    private Date applyTime;
    
    //belong 市场部归属
    @TableField(exist = false)
    private String belong;
    
    //belong 产品ID
    @TableField(exist = false)
    private Long insuranceId;
    
    @ApiModelProperty(value = "当前保单ID", required = false)
    @TableField("current_policy_id")
    private Long currentPolicyId;
    
    @ApiModelProperty(value = "首次签单时间", required = false)
    @TableField("first_applyed_time")
    private Date firstApplyedTime;
    
    @ApiModelProperty(value = "商务审核时间", required = false)
    @TableField("audit_time")
    private Date auditTime;
    
    @ApiModelProperty(value = "分配客服时间", required = false)
    @TableField("allot_time")
    private Date allotTime;
    
    @ApiModelProperty(value = "分配升级时间", required = false)
    @TableField("allot_upgrade_time")
    private Date allotUpgradeTime;
    @ApiModelProperty(value = "升级人ID", required = false)
    @TableField("upgrader_id")
    private Long upgraderId;
    @ApiModelProperty(value = "升级人姓名", required = false)
    @TableField("upgrader_name")
    private String upgraderName;
    @ApiModelProperty(value = "升级经理ID", required = false)
    @TableField("upgrade_manager_id")
    private Long upgradeManagerId;
    @ApiModelProperty(value = "升级经理姓名", required = false)
    @TableField("upgrade_manager_name")
    private String upgradeManagerName;
    @ApiModelProperty(value = "升级总监ID", required = false)
    @TableField("upgrade_director_id")
    private Long upgradeDirectorId;
    @ApiModelProperty(value = "升级总监姓名", required = false)
    @TableField("upgrade_director_name")    
    private String upgradeDirectorName;
    @ApiModelProperty(value = "升级总经理ID", required = false)
    @TableField("upgrade_minister_id")
    private Long upgradeMinisterId;
    @ApiModelProperty(value = "升级总经理姓名", required = false)
    @TableField("upgrade_minister_name")    
    private String upgradeMinisterName;
    
    @ApiModelProperty(value = "升级流程（10000原始客户，20000丢弃客户，33333未添加微信，44444已添加微信，  55555待回访客户，66666可聊客户，88888意向客户，99999成交客户）", required = false)
    @TableField("upgrade_flow")
    private Integer upgradeFlow;
    @ApiModelProperty(value = "升级部备注", required = false)
    @TableField("upgrade_remark")
    private String upgradeRemark;
    
    @ApiModelProperty(value = "共几次加保签单", required = false)
    @TableField("insure_upgrade_num")
    private Integer insureUpgradeNum;
	
	@ApiModelProperty(value = "是否归属升级", required = false)
	@TableField("is_upgrade")
	private Integer isUpgrade;
	
	@ApiModelProperty(value = "是否归属二开升级", required = false)
	@TableField("is_two_upgrade")
	private Integer isTwoUpgrade;
	
	@ApiModelProperty(value = "是否等待升级", required = false)
	@TableField("is_wait_upgrade")
	private Integer isWaitUpgrade;
	
	@ApiModelProperty(value = "升级抽取时间", required = false)
    @TableField("upgrade_extrace_time")
    private Date upgradeExtraceTime;
	
	@ApiModelProperty(value = "客户类型", required = false)
	@TableField("type")
	private Integer type;
	
	
	@ApiModelProperty(value = "升级部最后通话时间", required = false)
    @TableField("last_call_time")
    private Date lastCallTime;
    
    @ApiModelProperty(value = "升级部最后通话内容", required = false)
    @TableField("last_call_record")
    private String lastCallRecord;
    
    @ApiModelProperty(value = "服务开始日期", required = false)
    @TableField("start_date")
    private Date startDate;
    
    @ApiModelProperty(value = "服务结束日期", required = false)
    @TableField("end_date")
    private Date endDate;
	
    @ApiModelProperty(value = "是否投顾", required = false)
    @TableField("is_interest")
    private Integer isInterest;
    
    // 投入资金
    @ApiModelProperty(value = "投入资金", required = false)
    @TableField("total_money")
    private Double totalMoney;
    
  /*  //total_assets 实盘总资产 
    @ApiModelProperty(value = "实盘总资产", required = false)
    @TableField("total_assets")
    private Double totalAssets;*/
    
    //use_money 可用资金
    @ApiModelProperty(value = "可用资金", required = false)
    @TableField("use_money")
    private Double useMoney;
    
    //loss_money 可用资金
    @ApiModelProperty(value = "盈亏金额", required = false)
    @TableField("loss_money")
    private Double lossMoney;
    
    //stock_num 实盘持股类型数
    @ApiModelProperty(value = "实盘持股类型数", required = false)
    @TableField("stock_num")
    private Integer stockNum;
    
    //position 实盘仓位
    @ApiModelProperty(value = "实盘仓位", required = false)
    @TableField("position")
    private Double position;
    
    // 虚盘投入资金
    @ApiModelProperty(value = "虚盘投入资金", required = false)
    @TableField("total_teacher_money")
    private Double totalTeacherMoney;
    
   /* //teacher_total_assets 虚盘总资产 
    @ApiModelProperty(value = "虚盘总资产", required = false)
    @TableField("teacher_total_assets")
    private Double teacherTotalAssets;*/
    
    //use_money 虚盘可用资金
    @ApiModelProperty(value = "虚盘可用资金", required = false)
    @TableField("use_teacher_money")
    private Double useTeacherMoney;
    
    //loss_money 虚盘盈亏金额
    @ApiModelProperty(value = "虚盘盈亏金额", required = false)
    @TableField("loss_teacher_money")
    private Double lossTeacherMoney;
    
  //stock_num 虚盘持股类型数
    @ApiModelProperty(value = "虚盘持股类型数", required = false)
    @TableField("teacher_stock_num")
    private Integer teacherStockNum;
    
    //position 实盘仓位
    @ApiModelProperty(value = "实盘仓位", required = false)
    @TableField("teacher_position")
    private Double teacherPosition;
    
    @ApiModelProperty(value = "是否重点客户", required = false)
    @TableField("is_important")
    private Integer isImportant;
    
    @ApiModelProperty(value = "聊天码", required = false)
    private String submitCode;
    
    public String getSubmitCode() {
		return submitCode;
	}

	public void setSubmitCode(String submitCode) {
		this.submitCode = submitCode;
	}

	public Object getIds() {
		return ids;
	}

	public void setIds(Object ids) {
		this.ids = ids;
	}

	public Object getNames() {
		return names;
	}

	public void setNames(Object names) {
		this.names = names;
	}
    
    public Integer getIsInterest() {
		return isInterest;
	}

	public void setIsInterest(Integer isInterest) {
		this.isInterest = isInterest;
	}

	public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setFromInfo(String fromInfo)
    {
        this.fromInfo = fromInfo;
    }

    public String getFromInfo()
    {
        return this.fromInfo;
    }

    public void setEnterTime(Date enterTime)
    {
        this.enterTime = enterTime;
    }

    public Date getEnterTime()
    {
        return this.enterTime;
    }

    public void setFlowId(Integer flowId)
    {
        this.flowId = flowId;
    }

    public Integer getFlowId()
    {
        return this.flowId;
    }

    public void setWechatNo(String wechatNo)
    {
        this.wechatNo = wechatNo;
    }

    public String getWechatNo()
    {
        return this.wechatNo;
    }

    public void setQq(String qq)
    {
        this.qq = qq;
    }

    public String getQq()
    {
        return this.qq;
    }
	
    public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getShowMobile() {
		return showMobile;
	}

	public void setShowMobile(String showMobile) {
		this.showMobile = showMobile;
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

	public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPhone()
    {
        return this.phone;
    }

    public void setDuty(String duty)
    {
        this.duty = duty;
    }

    public String getDuty()
    {
        return this.duty;
    }

    public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    public Date getBirthday()
    {
        return this.birthday;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getProvince()
    {
        return this.province;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCity()
    {
        return this.city;
    }

    public void setInsureNum(Integer insureNum)
    {
        this.insureNum = insureNum;
    }

    public Integer getInsureNum()
    {
        return this.insureNum;
    }

    public void setInsureMoney(Double insureMoney)
    {
        this.insureMoney = insureMoney;
    }

    public Double getInsureMoney()
    {
        return this.insureMoney;
    }

    public void setCurrentApplyId(Long currentApplyId)
    {
        this.currentApplyId = currentApplyId;
    }

    public Long getCurrentApplyId()
    {
        return this.currentApplyId;
    }

    public void setIsService(Integer isService)
    {
        this.isService = isService;
    }

    public Integer getIsService()
    {
        return this.isService;
    }

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

    public void setSalerName(String salerName)
    {
        this.salerName = salerName;
    }

    public String getSalerName()
    {
        return this.salerName;
    }

    public void setManagerName(String managerName)
    {
        this.managerName = managerName;
    }

    public String getManagerName()
    {
        return this.managerName;
    }

    public void setDirectorName(String directorName)
    {
        this.directorName = directorName;
    }

    public String getDirectorName()
    {
        return this.directorName;
    }

    public void setMinisterName(String ministerName)
    {
        this.ministerName = ministerName;
    }

    public String getMinisterName()
    {
        return this.ministerName;
    }

    public void setContracterId(Long contracterId)
    {
        this.contracterId = contracterId;
    }

    public Long getContracterId()
    {
        return this.contracterId;
    }

    public void setContracterName(String contracterName)
    {
        this.contracterName = contracterName;
    }

    public String getContracterName()
    {
        return this.contracterName;
    }

    public void setContracterRemark(String contracterRemark)
    {
        this.contracterRemark = contracterRemark;
    }

    public String getContracterRemark()
    {
        return this.contracterRemark;
    }

    public void setServerRemark(String serverRemark)
    {
        this.serverRemark = serverRemark;
    }

    public String getServerRemark()
    {
        return this.serverRemark;
    }

    public void setServerManagerId(Long serverManagerId)
    {
    	this.serverManagerId = serverManagerId;
    }
    
    public Long getServerManagerId()
    {
    	return this.serverManagerId;
    }
    
    public void setServerManagerName(String serverManagerName)
    {
    	this.serverManagerName = serverManagerName;
    }
    
    public String getServerManagerName()
    {
    	return this.serverManagerName;
    }
    
    public void setServerId(Long serverId)
    {
        this.serverId = serverId;
    }

    public Long getServerId()
    {
        return this.serverId;
    }

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public String getServerName()
    {
        return this.serverName;
    }

    public void setOldApplyId(Long oldApplyId)
    {
        this.oldApplyId = oldApplyId;
    }

    public Long getOldApplyId()
    {
        return this.oldApplyId;
    }

    public void setOldFlowId(Integer oldFlowId)
    {
        this.oldFlowId = oldFlowId;
    }

    public Integer getOldFlowId()
    {
        return this.oldFlowId;
    }

    public void setOldIsService(Integer oldIsService)
    {
        this.oldIsService = oldIsService;
    }

    public Integer getOldIsService()
    {
        return this.oldIsService;
    }

    public void setOldServerId(Long oldServerId)
    {
        this.oldServerId = oldServerId;
    }

    public Long getOldServerId()
    {
        return this.oldServerId;
    }

    public void setOldServerName(String oldServerName)
    {
        this.oldServerName = oldServerName;
    }

    public String getOldServerName()
    {
        return this.oldServerName;
    }

	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public String getBelong() {
		return belong;
	}

	public void setBelong(String belong) {
		this.belong = belong;
	}

	public Long getCurrentPolicyId() {
		return currentPolicyId;
	}

	public void setCurrentPolicyId(Long currentPolicyId) {
		this.currentPolicyId = currentPolicyId;
	}

	public Date getFirstApplyedTime() {
		return firstApplyedTime;
	}

	public void setFirstApplyedTime(Date firstApplyedTime) {
		this.firstApplyedTime = firstApplyedTime;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public Date getAllotTime() {
		return allotTime;
	}

	public void setAllotTime(Date allotTime) {
		this.allotTime = allotTime;
	}

	public Date getAllotUpgradeTime() {
		return allotUpgradeTime;
	}

	public void setAllotUpgradeTime(Date allotUpgradeTime) {
		this.allotUpgradeTime = allotUpgradeTime;
	}

	public Long getUpgraderId() {
		return upgraderId;
	}

	public void setUpgraderId(Long upgraderId) {
		this.upgraderId = upgraderId;
	}
	
	public String getUpgraderName() {
		return upgraderName;
	}

	public void setUpgraderName(String upgraderName) {
		this.upgraderName = upgraderName;
	}

	public Long getUpgradeManagerId() {
		return upgradeManagerId;
	}

	public void setUpgradeManagerId(Long upgradeManagerId) {
		this.upgradeManagerId = upgradeManagerId;
	}

	public String getUpgradeManagerName() {
		return upgradeManagerName;
	}

	public void setUpgradeManagerName(String upgradeManagerName) {
		this.upgradeManagerName = upgradeManagerName;
	}

	public Long getUpgradeDirectorId() {
		return upgradeDirectorId;
	}

	public void setUpgradeDirectorId(Long upgradeDirectorId) {
		this.upgradeDirectorId = upgradeDirectorId;
	}

	public String getUpgradeDirectorName() {
		return upgradeDirectorName;
	}

	public void setUpgradeDirectorName(String upgradeDirectorName) {
		this.upgradeDirectorName = upgradeDirectorName;
	}

	public Long getUpgradeMinisterId() {
		return upgradeMinisterId;
	}

	public void setUpgradeMinisterId(Long upgradeMinisterId) {
		this.upgradeMinisterId = upgradeMinisterId;
	}

	public String getUpgradeMinisterName() {
		return upgradeMinisterName;
	}

	public void setUpgradeMinisterName(String upgradeMinisterName) {
		this.upgradeMinisterName = upgradeMinisterName;
	}

	public Integer getUpgradeFlow() {
		return upgradeFlow;
	}

	public void setUpgradeFlow(Integer upgradeFlow) {
		this.upgradeFlow = upgradeFlow;
	}

	public String getUpgradeRemark() {
		return upgradeRemark;
	}

	public void setUpgradeRemark(String upgradeRemark) {
		this.upgradeRemark = upgradeRemark;
	}

	public Integer getInsureUpgradeNum() {
		return insureUpgradeNum;
	}

	public void setInsureUpgradeNum(Integer insureUpgradeNum) {
		this.insureUpgradeNum = insureUpgradeNum;
	}

	public Integer getIsUpgrade() {
		return isUpgrade;
	}

	public void setIsUpgrade(Integer isUpgrade) {
		this.isUpgrade = isUpgrade;
	}

	public Integer getIsWaitUpgrade() {
		return isWaitUpgrade;
	}

	public void setIsWaitUpgrade(Integer isWaitUpgrade) {
		this.isWaitUpgrade = isWaitUpgrade;
	}

	public Date getUpgradeExtraceTime() {
		return upgradeExtraceTime;
	}

	public void setUpgradeExtraceTime(Date upgradeExtraceTime) {
		this.upgradeExtraceTime = upgradeExtraceTime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(Double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public Double getUseMoney() {
		return useMoney;
	}

	public void setUseMoney(Double useMoney) {
		this.useMoney = useMoney;
	}

	public Double getLossMoney() {
		return lossMoney;
	}

	public void setLossMoney(Double lossMoney) {
		this.lossMoney = lossMoney;
	}

	public Integer getStockNum() {
		return stockNum;
	}

	public void setStockNum(Integer stockNum) {
		this.stockNum = stockNum;
	}

	public Double getPosition() {
		return position;
	}

	public void setPosition(Double position) {
		this.position = position;
	}

	public Double getTotalTeacherMoney() {
		return totalTeacherMoney;
	}

	public void setTotalTeacherMoney(Double totalTeacherMoney) {
		this.totalTeacherMoney = totalTeacherMoney;
	}

	public Double getUseTeacherMoney() {
		return useTeacherMoney;
	}

	public void setUseTeacherMoney(Double useTeacherMoney) {
		this.useTeacherMoney = useTeacherMoney;
	}

	public Double getLossTeacherMoney() {
		return lossTeacherMoney;
	}

	public void setLossTeacherMoney(Double lossTeacherMoney) {
		this.lossTeacherMoney = lossTeacherMoney;
	}

	public Integer getTeacherStockNum() {
		return teacherStockNum;
	}

	public void setTeacherStockNum(Integer teacherStockNum) {
		this.teacherStockNum = teacherStockNum;
	}

	public Double getTeacherPosition() {
		return teacherPosition;
	}

	public void setTeacherPosition(Double teacherPosition) {
		this.teacherPosition = teacherPosition;
	}

	public Long getInsuranceId() {
		return insuranceId;
	}

	public void setInsuranceId(Long insuranceId) {
		this.insuranceId = insuranceId;
	}

	public Integer getIsImportant() {
		return isImportant;
	}

	public void setIsImportant(Integer isImportant) {
		this.isImportant = isImportant;
	}

	public Integer getIsTwoUpgrade() {
		return isTwoUpgrade;
	}

	public void setIsTwoUpgrade(Integer isTwoUpgrade) {
		this.isTwoUpgrade = isTwoUpgrade;
	}
	
}
