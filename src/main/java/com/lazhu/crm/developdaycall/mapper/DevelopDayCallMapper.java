package com.lazhu.crm.developdaycall.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.crm.developdaycall.entity.DevelopDayCall;
/**
 * <p>
 * 经纪人日通话统计表 Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2018-01-09
 */
public interface DevelopDayCallMapper extends BaseMapper<DevelopDayCall> {

	List<Map<String, Object>> queryByBusiness (@Param("cm") Map<String, Object> param);
	
}
