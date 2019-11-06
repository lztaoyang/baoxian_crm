package com.lazhu.crm.resourcechange.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.core.base.BaseService;
import com.lazhu.crm.resourcechange.entity.ResourceChange;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@Service
@CacheConfig(cacheNames = "resourceChange")
public class ResourceChangeService extends BaseService<ResourceChange> {
	
}
