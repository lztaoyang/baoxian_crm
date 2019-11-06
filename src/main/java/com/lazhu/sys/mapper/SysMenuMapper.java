package com.lazhu.sys.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.sys.model.SysMenu;

public interface SysMenuMapper extends BaseMapper<SysMenu> {
	/** 获取所有权限 */
	public List<Map<String, String>> getPermissions();

	public void isShow(@Param("isShow") Long isShow,@Param("menuId") Long menuId);
}