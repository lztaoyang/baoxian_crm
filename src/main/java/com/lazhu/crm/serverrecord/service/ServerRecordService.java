package com.lazhu.crm.serverrecord.service;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.serverrecord.entity.ServerRecord;
import com.lazhu.crm.serverrecord.mapper.ServerRecordMapper;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@Service
@CacheConfig(cacheNames = "serverRecord")
public class ServerRecordService extends BusinessBaseService<ServerRecord> {

	public void add(ServerRecord param, List<Long> ids, List<String> names,Long currUser) {
		if (ids != null && ids.size() > 0) {
			for (int i = 0 ; i < ids.size() ; i++) {
				ServerRecord record = new ServerRecord();
				record.setCustomerId(ids.get(i));
				record.setCustomerName(names.get(i));
				
				record.setType(param.getType());
				record.setServerRecord(param.getServerRecord());
				record.setRemark(param.getRemark());
				
				record.setServerName(super.getUserById(currUser).getAccount());
				record.setUpdateBy(currUser);
				//循环插入数据
				super.update(record);
			}
		}
	}

	public List<Long> queryByCustomerId(Long customerId) {
		return ((ServerRecordMapper)mapper).queryByCustomerId(customerId);
	}
	
}
