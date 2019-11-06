package com.lazhu.crm.customerlog.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.core.base.BaseService;
import com.lazhu.crm.customerlog.entity.CustomerLog;
import com.lazhu.crm.customerlog.mapper.CustomerLogMapper;

/**
 * <p>
 * 资源会员操作流程日志  服务实现类
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@Service
@CacheConfig(cacheNames = "customerLog")
public class CustomerLogService extends BaseService<CustomerLog> {
	
	@Autowired
	private CustomerLogMapper customerMapper;
	
	/**
	 * 批量更新客户资源日志表
	 * @param customerLogList
	 */
	public void insertBatchCustomerLog(List<CustomerLog> customerLogList) {
		//批量更新日志
		customerMapper.insertBatchCustomerLog(customerLogList);
	}
	
}
