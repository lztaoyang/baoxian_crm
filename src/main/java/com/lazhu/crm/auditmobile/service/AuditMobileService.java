package com.lazhu.crm.auditmobile.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.crm.auditmobile.entity.AuditMobile;
import com.lazhu.crm.auditmobile.mapper.AuditMobileMapper;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.ecp.utils.StrUtils;

/**
 * <p>
 * 手机号明文申请表 服务实现类
 * </p>
 *
 * @author taoyang
 * @since 2019-07-30
 */
@Service
@CacheConfig(cacheNames = "auditMobile")
public class AuditMobileService extends BusinessBaseService<AuditMobile> {

	@Autowired
	AuditMobileMapper mapper;
	
	public String getMobileByMd5Mobile(String md5Mobile) {
		String mobile = super.getMobileByMd5Mobile(md5Mobile);
		return mobile;
	}
	
	public void updateStatus(){
		//1、获取失效事项
		List<Long> ids = mapper.queryPassTime();
		//2、更新失效事项
		if (StrUtils.isNotNullOrBlank(ids) && ids.size() > 0 ) {
			for (Long id : ids) {
				AuditMobile auditMobile = this.queryById(id);
				auditMobile.setStatus(3);
				this.update(auditMobile);
			}
		}
	}
}
