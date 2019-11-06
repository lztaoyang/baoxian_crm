package com.lazhu.ad.cost.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.lazhu.ad.cost.entity.Cost;
import com.lazhu.core.base.BaseMapper;
/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author phong
 * @since 2017-11-02
 */
public interface CostMapper extends BaseMapper<Cost> {
	
	// 日统计报表查询
	List<Map<String, String>> queryReortDay(RowBounds rowBounds, @Param("cm") Map<String, Object> params);
	// 推广客户明细表
	List<Map<String, String>> queryReportCustomerDetail(RowBounds rowBounds, @Param("cm") Map<String, Object> params);
	// 查询客户明细
	List<Map<String, Object>> queryCustomerDetail(@Param("cm") Map<String, Object> params);
	// 渠道统计
	List<Map<String, Object>> queryChannelTotal(@Param("cm") Map<String, Object> params);
	// 推广执行人统计
	List<Map<String, Object>> queryExecuterTotal(@Param("cm") Map<String, Object> params);
	// 市场人员统计
	List<Map<String, String>> queryUserTotal(RowBounds rowBounds, @Param("cm") Map<String, Object> params);
	//推广签单明细
	List<Map<String, Object>> exportApplyDetail(@Param("cm") Map<String, Object> param);
	
}
