package com.lazhu.crm.salerrecordday.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.crm.salerrecordday.entity.SalerRecordDay;
/**
 * <p>
 * 市场部通话记录今天表 Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2018-04-13
 */
public interface SalerRecordDayMapper extends BaseMapper<SalerRecordDay> {

	/** 经理归属查询 **/
	List<Map<String, Object>> queryManagerGroup(@Param("userGroup") Long userGroup,@Param("validTiemLength") Integer validTiemLength,@Param("userId") Long userId);
	
	/** 总监归属查询 **/
	List<Map<String, Object>> queryDirectorGroup(@Param("userGroup") Long userGroup,@Param("validTiemLength") Integer validTiemLength,@Param("userId") Long userId);
	
	/** 总经理归属查询 **/
	List<Map<String, Object>> queryMinisterGroup(@Param("userGroup") Long userGroup,@Param("validTiemLength") Integer validTiemLength,@Param("userId") Long userId);
	
	/** 管理员归属查询 **/
	List<Map<String, Object>> queryAllNormalBusiness(@Param("userGroup") Long userGroup,@Param("validTiemLength") Integer validTiemLength);
	
	/** 今日总拨打次数 **/
	Map<String, Object> toDayCallStatistics(@Param("userId") Long userId);
}
