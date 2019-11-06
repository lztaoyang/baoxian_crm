package com.lazhu.sys.service;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.core.base.BaseService;
import com.lazhu.sys.mapper.SysUserRoleMapper;
import com.lazhu.sys.model.SysUserRole;

/**
 * 
 * @author hz
 * @date 2018年3月6日  
 * @version 1.0.0
 */
@Service
@CacheConfig(cacheNames = "sysUserRole")
public class SysUserRoleService extends BaseService<SysUserRole> {

	public List<Long> queryByUserId(Long userId) {
		return ((SysUserRoleMapper)mapper).queryByUserId(userId);
	}

}
