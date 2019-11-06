package com.lazhu.aladdinpbx.agentevent.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.aladdinpbx.agentevent.entity.AgentEvent;
import com.lazhu.core.base.BaseService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hz
 * @since 2018-06-12
 */
@Service
@CacheConfig(cacheNames = "agentEvent")
public class AgentEventService extends BaseService<AgentEvent> {
	
}
