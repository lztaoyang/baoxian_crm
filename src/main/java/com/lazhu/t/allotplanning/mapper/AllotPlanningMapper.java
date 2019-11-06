package com.lazhu.t.allotplanning.mapper;

import java.util.List;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.t.allotplanning.entity.AllotPlanning;
/**
 * <p>
 * 推广分配计划表 Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2018-05-10
 */
public interface AllotPlanningMapper extends BaseMapper<AllotPlanning> {

	List<Long> selectAllEnable();
	
}
