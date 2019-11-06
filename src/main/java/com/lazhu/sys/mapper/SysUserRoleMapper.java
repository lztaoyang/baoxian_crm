package com.lazhu.sys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.sys.model.SysUserRole;

public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

	List<Long> queryByUserId(@Param("userId") Long userId);
	
}