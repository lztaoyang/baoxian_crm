package com.lazhu.crm.worktrailnormal.mapper;

import com.lazhu.crm.worktrailnormal.entity.WorkTrailNormal;
import com.lazhu.core.base.BaseMapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
/**
 * <p>
 * 市场部正常工作轨迹 Mapper 接口
 * </p>
 *
 * @author LAPTOP-HDRAA393
 * @since 2018-02-01
 */
public interface WorkTrailNormalMapper extends BaseMapper<WorkTrailNormal> {
	// 统计查询
	List<Map<String, String>> queryTotal(RowBounds rowBounds, @Param("cm") Map<String, Object> params);

	/** 通过通话记录ID查询正常轨迹 **/
	List<Long> queryBySalerRecordId (@Param("salerRecordId") Long salerRecordId);
}
