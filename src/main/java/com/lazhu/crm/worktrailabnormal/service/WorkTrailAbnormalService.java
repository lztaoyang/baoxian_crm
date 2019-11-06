package com.lazhu.crm.worktrailabnormal.service;

import java.util.Map;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.worktrailabnormal.entity.WorkTrailAbnormal;
import com.lazhu.crm.worktrailabnormal.mapper.WorkTrailAbnormalMapper;

/**
 * <p>
 * 市场部异常工作轨迹表 服务实现类
 * </p>
 *
 * @author LAPTOP-HDRAA393
 * @since 2018-02-01
 */
@Service
@CacheConfig(cacheNames = "workTrailAbnormal")
public class WorkTrailAbnormalService extends BusinessBaseService<WorkTrailAbnormal> {
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
		page.setRecords(((WorkTrailAbnormalMapper) mapper).queryDetail(page, param));
		return page;
	}
}
