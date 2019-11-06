package com.lazhu.crm.mobilelastlog.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.core.base.BaseService;
import com.lazhu.crm.mobilelastlog.entity.MobileLastLog;
import com.lazhu.crm.mobilelastlog.mapper.MobileLastLogMapper;
import com.lazhu.ecp.utils.StrUtils;

/**
 * <p>
 * 最近拨打记录 服务实现类
 * </p>
 *
 * @author hz
 * @since 2018-01-07
 */
@Service
@CacheConfig(cacheNames = "mobileLastLog")
public class MobileLastLogService extends BaseService<MobileLastLog> {
	
	
	@Override
	public MobileLastLog update(MobileLastLog record) {
		if (null != record && StrUtils.isNotNullOrBlank(record.getMd5Mobile())) {
			Long lastId = queryLastByMd5Mobile(record.getMd5Mobile());
			if (null != lastId && lastId > 0) {
				record.setId(lastId);
			}
		}
		return super.update(record);
	}

	/**
	 * 查询手机号最近拨打记录表
	 * @param mobile
	 * @return
	 */
	public Long queryLastByMd5Mobile(String md5Mobile) {
		return ((MobileLastLogMapper)mapper).queryLastByMd5Mobile(md5Mobile);
	}
	
}
