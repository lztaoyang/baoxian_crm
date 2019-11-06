package com.lazhu.sys.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;



import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

/**
 * SysUser实体类
 * @author naxj
 * 
 */
@TableName("sys_user")
@SuppressWarnings("serial")
public class SysUser extends BaseModel {
    @TableField("account_")
	private String account;
    @TableField("password_")
	private String password;
    //分机号
    @TableField("agent_no")
    private String agentNo;
    
    @TableField("phone_")
	private String phone;
    @TableField("sex_")
	private Integer sex;
	private String userName;
	private String namePinyin;
    @TableField("email_")
	private String email;
	private Date birthDay;
    @TableField("position_")
	private Integer position;
    @TableField("address_")
	private String address;
    @TableField("avatar_")
	private String avatar;
	private Integer userType;
	private Long deptId;
    @TableField("locked_")
	private Integer locked;
    @TableField("education")
    private String education;
    
    @TableField(exist = false)
    private String oldPassword;
    @TableField(exist = false)
    private String repeat;
    @TableField(exist = false)
	private String deptName;
    @TableField(exist = false)
	private String userTypeText;
    @TableField(exist = false)
	private String permission;
    @TableField("staff_no")
    private String staffNo;
    
    @TableField("club_num")
    private Integer clubNum;//拥有客户数
    
    
    //新增
    private Long parentId;//上级ID
    private Long userGroup;//用户组
    @TableField("upgrader_type")
    private Integer upgraderType;//升级人员类型
    @TableField("manager_ids")
    private String managerIds;//经理ID数组
    
    @ApiModelProperty(value = "分配升级客户次数", required = false)
    @TableField("allot_club_num")
    private Integer allotClubNum;
    /*@ApiModelProperty(value = "分配阶梯", required = false)
    @TableField("allot_stair")
    private Double allotStair;
    //分配阶梯人数 = 当前分配阶梯 * 分配客户数
    @ApiModelProperty(value = "分配阶梯人数", required = false)
    @TableField("allot_stair_num")
    private Double allotStairNum;*/
    
    @ApiModelProperty(value = "是否分配资源", required = false)
    @TableField("is_allot_resource")
    private Integer isAllotResource;
    @ApiModelProperty(value = "是否禁止分配资源", required = false)
    @TableField("disable_allot_resource")
    private Integer disableAllotResource;
    @ApiModelProperty(value = "设定最大分配资源次数", required = false)
    @TableField("allot_resource_max")
    private Integer allotResourceMax;
    @ApiModelProperty(value = "分配资源次数", required = false)
    @TableField("allot_resource_num")
    private Integer allotResourceNum;
    
    @ApiModelProperty(value = "控制分配资源数", required = false)
    @TableField("control_allot_resource_num")
    private Integer controlAllotResourceNum;
    
    @ApiModelProperty(value = "今日奖励资源数", required = false)
    @TableField("reward_resource_num")
    private Integer rewardResourceNum;
    @ApiModelProperty(value = "剩余奖励资源数", required = false)
    @TableField("over_reward_resource_num")
    private Integer overRewardResourceNum;
    
    @ApiModelProperty(value = "抽取资源次数", required = false)
    @TableField("extract_num")
    private Integer extractNum;
    @ApiModelProperty(value = "延期资源次数", required = false)
    @TableField("extension_num")
    private Integer extensionNum;

    @ApiModelProperty(value = "总监姓名", required = false)
    @TableField(exist = false)
	private String directorName;
    @ApiModelProperty(value = "经理姓名", required = false)
    @TableField(exist = false)
    private String managerName;
    
    @ApiModelProperty(value = "钉钉ID", required = false)
    @TableField("ding_id")
    private String dingId;
    
    @ApiModelProperty(value = "入职日期", required = false)
    @TableField("entry_time")
    private String entryTime;
    
    @ApiModelProperty(value = "是否在线", required = false)
    @TableField("is_online")
    private Integer isOnline;
    
    @ApiModelProperty(value = "最后登陆时间", required = false)
    @TableField("last_online_time")
    private Date lastOnlineTime;

    @ApiModelProperty(value = "登陆方式（0账号密码，1钉钉扫码）", required = false)
    @TableField("login_type")
    private Integer loginType;
    
    @ApiModelProperty(value = "密码是否失效", required = false)
    @TableField("password_lose")
    private Integer passwordLose;
    
    @ApiModelProperty(value = "mac地址", required = false)
    @TableField("mac")
    private String mac;
    
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAgentNo() {
		return agentNo;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNamePinyin() {
		return namePinyin;
	}

	public void setNamePinyin(String namePinyin) {
		this.namePinyin = namePinyin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public Integer getLocked() {
		return locked;
	}

	public void setLocked(Integer locked) {
		this.locked = locked;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getRepeat() {
		return repeat;
	}

	public void setRepeat(String repeat) {
		this.repeat = repeat;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getUserTypeText() {
		return userTypeText;
	}

	public void setUserTypeText(String userTypeText) {
		this.userTypeText = userTypeText;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getStaffNo() {
		return staffNo;
	}

	public void setStaffNo(String staffNo) {
		this.staffNo = staffNo;
	}

	public Integer getClubNum() {
		return clubNum;
	}

	public void setClubNum(Integer clubNum) {
		this.clubNum = clubNum;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(Long userGroup) {
		this.userGroup = userGroup;
	}

	public Integer getUpgraderType() {
		return upgraderType;
	}

	public void setUpgraderType(Integer upgraderType) {
		this.upgraderType = upgraderType;
	}

	public String getManagerIds() {
		return managerIds;
	}

	public void setManagerIds(String managerIds) {
		this.managerIds = managerIds;
	}

	public Integer getAllotClubNum() {
		return allotClubNum;
	}

	public void setAllotClubNum(Integer allotClubNum) {
		this.allotClubNum = allotClubNum;
	}

	public Integer getIsAllotResource() {
		return isAllotResource;
	}

	public void setIsAllotResource(Integer isAllotResource) {
		this.isAllotResource = isAllotResource;
	}

	public Integer getDisableAllotResource() {
		return disableAllotResource;
	}

	public void setDisableAllotResource(Integer disableAllotResource) {
		this.disableAllotResource = disableAllotResource;
	}

	public Integer getAllotResourceMax() {
		return allotResourceMax;
	}

	public void setAllotResourceMax(Integer allotResourceMax) {
		this.allotResourceMax = allotResourceMax;
	}

	public Integer getAllotResourceNum() {
		return allotResourceNum;
	}

	public void setAllotResourceNum(Integer allotResourceNum) {
		this.allotResourceNum = allotResourceNum;
	}

	public Integer getControlAllotResourceNum() {
		return controlAllotResourceNum;
	}

	public void setControlAllotResourceNum(Integer controlAllotResourceNum) {
		this.controlAllotResourceNum = controlAllotResourceNum;
	}

	public Integer getRewardResourceNum() {
		return rewardResourceNum;
	}

	public void setRewardResourceNum(Integer rewardResourceNum) {
		this.rewardResourceNum = rewardResourceNum;
	}

	public Integer getOverRewardResourceNum() {
		return overRewardResourceNum;
	}

	public void setOverRewardResourceNum(Integer overRewardResourceNum) {
		this.overRewardResourceNum = overRewardResourceNum;
	}

	public Integer getExtractNum() {
		return extractNum;
	}

	public void setExtractNum(Integer extractNum) {
		this.extractNum = extractNum;
	}

	public Integer getExtensionNum() {
		return extensionNum;
	}

	public void setExtensionNum(Integer extensionNum) {
		this.extensionNum = extensionNum;
	}

	public String getDirectorName() {
		return directorName;
	}

	public void setDirectorName(String directorName) {
		this.directorName = directorName;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getDingId() {
		return dingId;
	}

	public void setDingId(String dingId) {
		this.dingId = dingId;
	}

	public String getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(String entryTime) {
		this.entryTime = entryTime;
	}

	public Integer getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(Integer isOnline) {
		this.isOnline = isOnline;
	}

	public Date getLastOnlineTime() {
		return lastOnlineTime;
	}

	public void setLastOnlineTime(Date lastOnlineTime) {
		this.lastOnlineTime = lastOnlineTime;
	}

	public Integer getLoginType() {
		return loginType;
	}

	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}

	public Integer getPasswordLose() {
		return passwordLose;
	}

	public void setPasswordLose(Integer passwordLose) {
		this.passwordLose = passwordLose;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
	
}
