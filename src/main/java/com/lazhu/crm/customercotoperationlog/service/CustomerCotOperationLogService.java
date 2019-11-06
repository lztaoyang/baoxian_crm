package com.lazhu.crm.customercotoperationlog.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.core.base.BaseService;
import com.lazhu.crm.customercotoperationlog.entity.CustomerCotOperationLog;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author taoyang
 * @since 2018-11-16
 */
@Service
@CacheConfig(cacheNames = "customerCotOperationLog")
public class CustomerCotOperationLogService extends BaseService<CustomerCotOperationLog> {
	
}
