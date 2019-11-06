package com.lazhu.sys.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

@TableName("sys_role")
@SuppressWarnings("serial")
public class SysRole extends BaseModel {
	private String roleName;
	private Long deptId;
	private Integer roleType;
	private Integer isMobileVisible;
	private Integer isAllowDetailsCall;

	@TableField(exist = false)
	private String deptName;
	@TableField(exist = false)
	private String permission;

	/**
	 * @return the value of sys_role.role_name
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName
	 *            the value for sys_role.role_name
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName == null ? null : roleName.trim();
	}

	/**
	 * @return the value of sys_role.dept_id
	 */
	public Long getDeptId() {
		return deptId;
	}

	/**
	 * @param deptId
	 *            the value for sys_role.dept_id
	 */
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	/**
	 * @return the value of sys_role.role_type
	 */
	public Integer getRoleType() {
		return roleType;
	}

	/**
	 * @param roleType
	 *            the value for sys_role.role_type
	 */
	public void setRoleType(Integer roleType) {
		this.roleType = roleType;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public Integer getIsMobileVisible() {
		return isMobileVisible;
	}

	public void setIsMobileVisible(Integer isMobileVisible) {
		this.isMobileVisible = isMobileVisible;
	}

	public Integer getIsAllowDetailsCall() {
		return isAllowDetailsCall;
	}

	public void setIsAllowDetailsCall(Integer isAllowDetailsCall) {
		this.isAllowDetailsCall = isAllowDetailsCall;
	}

}