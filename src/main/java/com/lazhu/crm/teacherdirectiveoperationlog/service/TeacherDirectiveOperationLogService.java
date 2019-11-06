package com.lazhu.crm.teacherdirectiveoperationlog.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.core.base.BaseService;
import com.lazhu.crm.teacherdirectiveoperationlog.entity.TeacherDirectiveOperationLog;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author taoyang
 * @since 2018-11-16
 */
@Service
@CacheConfig(cacheNames = "teacherDirectiveOperationLog")
public class TeacherDirectiveOperationLogService extends BaseService<TeacherDirectiveOperationLog> {
	
}
