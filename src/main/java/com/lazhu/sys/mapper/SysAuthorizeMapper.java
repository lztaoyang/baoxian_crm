package com.lazhu.sys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lazhu.sys.model.SysMenu;

public interface SysAuthorizeMapper {

	void deleteUserMenu(@Param("userId") Long userId, @Param("permission") String permission);

	void deleteUserRole(@Param("userId") Long userId);
//去除修改
	void deleteRoleMenu(@Param("roleId") Long roleId);

	List<Long> getAuthorize(@Param("userId") Long userId);

	List<String> queryPermissionByUserId(@Param("userId") Long userId);

	List<SysMenu> queryMenusPermission();
}
