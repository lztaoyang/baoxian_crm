package com.lazhu.t.allotplanning.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.core.base.BaseService;
import com.lazhu.ecp.utils.DateUtils;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.t.allotplanning.entity.AllotPlanning;
import com.lazhu.t.allotplanning.mapper.AllotPlanningMapper;

/**
 * <p>
 * 推广分配计划表 服务实现类
 * </p>
 *
 * @author hz
 * @since 2018-05-10
 */
@Service
@CacheConfig(cacheNames = "allotPlanning")
public class AllotPlanningService extends BaseService<AllotPlanning> {
	
	@Autowired
	AllotPlanningMapper mapper;

	public boolean isBetweenEnable(Date date) {
		List<Long> ids = mapper.selectAllEnable();
		if (null == ids || ids.size() <= 0) {
			return false;
		}
		for (Long id : ids) {
			AllotPlanning ap = this.queryById(id);
			if (StrUtils.isNotNullOrBlank(ap.getStartTime()) && StrUtils.isNotNullOrBlank(ap.getEndTime())) {
				return DateUtils.dayBelongCalendar(new Date(), ap.getStartTime(), ap.getEndTime());
			} else {
				System.out.println("分配计划必须有开始时间和结束时间");
				return false;
			}
		}
		return false;
	}
	
}
