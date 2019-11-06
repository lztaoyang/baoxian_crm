package com.lazhu.sys.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lazhu.core.base.BaseService;
import com.lazhu.core.util.CacheUtil;
import com.lazhu.core.util.InstanceUtil;
import com.lazhu.core.util.PropertiesUtil;
import com.lazhu.sys.mapper.SysSessionMapper;
import com.lazhu.sys.model.SysSession;

/**
 * @author naxj
 * 
 */
@Service
@CacheConfig(cacheNames = "sysSession")
public class SysSessionService extends BaseService<SysSession> {

	@CachePut
	@Transactional
	public SysSession update(SysSession record) {
		if (record.getId() == null) {
			record.setUpdateTime(new Date());
			Long id = ((SysSessionMapper) mapper).queryBySessionId(record.getSessionId());
			if (id != null) {
				mapper.updateById(record);
			} else {
				record.setCreateTime(new Date());
				mapper.insert(record);
			}
		} else {
			mapper.updateById(record);
		}
		return record;
	}

	// 系统触发,由系统自动管理缓存
	public void deleteBySessionId(final SysSession sysSession) {
		((SysSessionMapper) mapper).deleteBySessionId(sysSession.getSessionId());
	}

	public List<String> querySessionIdByAccount(SysSession sysSession) {
		return ((SysSessionMapper) mapper).querySessionIdByAccount(sysSession.getAccount());
	}

	//
	public void cleanExpiredSessions() {
		String key = "spring:session:" + PropertiesUtil.getString("session.redis.namespace") + ":sessions:expires:";
		Map<String, Object> columnMap = InstanceUtil.newHashMap();
		List<SysSession> sessions = queryList(columnMap);
		for (SysSession sysSession : sessions) {
			logger.info("检查SESSION : {}", sysSession.getSessionId());
			if (!CacheUtil.getCache().exists(key + sysSession.getSessionId())) {
				logger.info("移除SESSION : {}", sysSession.getSessionId());
				mapper.deleteById(sysSession.getId());
			}
		}
	}
	
	
	public List<Long> queryIdByAccount(String account) {
		return ((SysSessionMapper) mapper).queryIdByAccount(account);
	}
	
	//
	public void deleteByAccountId(String account) {
		((SysSessionMapper) mapper).deleteByAccount(account);
	}
}