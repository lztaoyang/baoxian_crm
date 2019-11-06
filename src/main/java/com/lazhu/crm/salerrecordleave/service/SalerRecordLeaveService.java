package com.lazhu.crm.salerrecordleave.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.core.base.BaseService;
import com.lazhu.crm.salerrecordleave.entity.SalerRecordLeave;

/**
 * <p>
 * 市场部通话记录闲置表 服务实现类
 * </p>
 *
 * @author hz
 * @since 2018-04-13
 */
@Service
@CacheConfig(cacheNames = "salerRecordLeave")
public class SalerRecordLeaveService extends BaseService<SalerRecordLeave> {
	
}
