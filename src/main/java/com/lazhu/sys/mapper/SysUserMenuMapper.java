package com.lazhu.sys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.sys.model.SysUserMenu;

public interface SysUserMenuMapper extends BaseMapper<SysUserMenu> {
	List<Long> queryMenuIdsByUserId(@Param("userId") Long userId);

	List<Long> queryPermissions(@Param("userId") Long userId, @Param("permission") String permission);

	List<String> queryPermission(@Param("userId") Long id);
}