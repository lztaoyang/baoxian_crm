package com.lazhu.t.mobileexport.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.t.mobileexport.entity.ResourceMobileExport;
import com.lazhu.t.mobileexport.mapper.ResourceMobileExportMapper;
import com.lazhu.core.base.BaseService;
import com.lazhu.core.util.InstanceUtil;
import com.lazhu.ecp.utils.CollectionUtil;
import com.lazhu.ecp.utils.StrUtils;

/**
 * 推广资源手机号信息导出业务逻辑
 * 
 * @author ty
 * @date 2018年11月21日
 */
@Service
@CacheConfig(cacheNames = "resourceMobileExport")
public class ResourceMobileExportService extends BaseService<ResourceMobileExport>{

	@Autowired
	ResourceMobileExportMapper resourceMobileExportMapper;
	
	/**
	 * 查询手机号结果集
	 * 
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> queryMobileList(Map<String, Object> param) {
		//处理开始日期为yyyy-MM-dd
		if(param.get("startDate") != null && param.get("startDate").toString().length() > 10){
			param.put("startDate", param.get("startDate").toString().substring(0, 10));
		}
		//处理结束日期为yyyy-MM-dd
		if(param.get("endDate") != null && param.get("endDate").toString().length() > 10){
			param.put("endDate", param.get("endDate").toString().substring(0, 10));
		}
		
		if(param.get("flowId") != null && param.get("flowId").toString().length() > 0){
			String[] flowId = StrUtils.toString(param.get("flowId")).split(",");
			List<String> flowIdList = InstanceUtil.newArrayList();
			flowIdList = CollectionUtil.arrayToList(flowId);
			param.put("flowId", flowIdList);
		}
		
		List<Map<String, Object>> mobileList = resourceMobileExportMapper.queryMobileList(param);
		
		return mobileList;
	}

}
