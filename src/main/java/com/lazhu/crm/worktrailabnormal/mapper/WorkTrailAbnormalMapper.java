package com.lazhu.crm.worktrailabnormal.mapper;

import com.lazhu.crm.worktrailabnormal.entity.WorkTrailAbnormal;
import com.lazhu.core.base.BaseMapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
/**
 * <p>
 * 市场部异常工作轨迹表 Mapper 接口
 * </p>
 *
 * @author LAPTOP-HDRAA393
 * @since 2018-02-01
 */
public interface WorkTrailAbnormalMapper extends BaseMapper<WorkTrailAbnormal> {
	// 统计查询
	List<Map<String, String>> queryDetail(RowBounds rowBounds, @Param("cm") Map<String, Object> params);
}
