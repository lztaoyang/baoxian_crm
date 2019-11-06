package com.lazhu.sys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.sys.model.SysRoleMenu;

public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
	List<String> queryMenuIdsByRoleId(@Param("roleId") Long roleId);

	List<String> queryPermissions(@Param("roleId") Long roleId, @Param("permission") String permission);

	List<String> queryPermission(@Param("roleId") Long id);
}
