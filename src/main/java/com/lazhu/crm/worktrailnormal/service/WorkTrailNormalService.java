package com.lazhu.crm.worktrailnormal.service;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.crm.Constant;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.worktrailnormal.entity.WorkTrailNormal;
import com.lazhu.crm.worktrailnormal.mapper.WorkTrailNormalMapper;
import com.lazhu.ecp.utils.OperationLogTool;

/**
 * <p>
 * 市场部正常工作轨迹 服务实现类
 * </p>
 *
 * @author LAPTOP-HDRAA393
 * @since 2018-02-01
 */
@Service
@CacheConfig(cacheNames = "workTrailNormal")
public class WorkTrailNormalService extends BusinessBaseService<WorkTrailNormal> {
	
	/**
	 * 统计查询
	 * @param param
	 * @return
	 */
	public Page<Map<String, String>> queryTotal(Map<String, Object> param) {
		JSONArray cascaderId = (JSONArray) param.get("cascaderId");
		if (cascaderId != null) {
			if (cascaderId.size() > 3) {//业务
				param.put("salerId", cascaderId.get(3));
			} else if (cascaderId.size() > 2) {//经理
				param.put("managerId", cascaderId.get(2));
			} else if (cascaderId.size() > 1) {//总监
				param.put("directorId", cascaderId.get(1));
			} else if (cascaderId.size() > 0) {//总经理
				param.put("ministerId", cascaderId.get(0));
			}
		}
		Page<Map<String, String>> page = getPageMap(param);
		page.setRecords(((WorkTrailNormalMapper) mapper).queryTotal(page, param));
		return page;
	}

	/**
	 * 通过通话记录ID查询正常轨迹
	 * @param salerRecordId
	 * @return
	 */
	public WorkTrailNormal queryBySalerRecordId (Long salerRecordId) {
		WorkTrailNormal workTrailNormal = null;
		List<Long> normalId = ((WorkTrailNormalMapper) mapper).queryBySalerRecordId(salerRecordId);
		if (null != normalId && normalId.size() > 0) {
			if (normalId.size() == 1) {
				workTrailNormal = this.queryById(normalId.get(0));
			} else {
				OperationLogTool.operationLog(Constant.ERROR_LOG, "通话ID：" + salerRecordId + "，存在多个正常轨迹");
			}
		}
		return workTrailNormal;
	}
	
}
