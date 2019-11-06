package com.lazhu.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.core.base.BaseService;
import com.lazhu.crm.Constant;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.sys.mapper.PropertiesMapper;
import com.lazhu.sys.model.Properties;

/**
 * <p>
 * 配置表 服务实现类
 * </p>
 *
 * @author hz
 * @since 2018-01-07
 */
@Service
@CacheConfig(cacheNames = "properties")
public class PropertiesService extends BaseService<Properties> {

	@Autowired
	private PropertiesMapper mapper;
	
	/**
	 * 键查询值
	 * @param key
	 * @return
	 */
	public String queryKeyString(String key) {
		List<String> valueString = ((PropertiesMapper)mapper).queryKeyString(key);
		if (valueString != null && valueString.size() > 0) {
			if (valueString.size() == 1) {
				return valueString.get(0);
			} else {
				OperationLogTool.operationLog(Constant.ERROR_LOG, "配置表重复键：" + key);
				return null;
			}
		} else {
			OperationLogTool.operationLog(Constant.ERROR_LOG, "配置表查无此键：" + key);
			return null;
		}
	}

	/**查询配置value**/
	public Properties queryByKeyString(String keyString) {
		List<Long> ids = mapper.queryIdByKey(keyString);
		if (null != ids && ids.size() == 1) {
			return this.queryById(ids.get(0));
		}
		return null;
	}
	
}
