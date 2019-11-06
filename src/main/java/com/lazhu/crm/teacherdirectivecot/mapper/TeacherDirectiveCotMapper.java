package com.lazhu.crm.teacherdirectivecot.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.lazhu.crm.teacherdirectivecot.entity.TeacherDirectiveCot;
import com.lazhu.core.base.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author taoyang
 * @since 2018-11-16
 */
public interface TeacherDirectiveCotMapper extends BaseMapper<TeacherDirectiveCot> {

	/**
	 * 查询是否持有该股票
	 * @param param
	 * @return
	 */
	public List<Long> queryByStockCode(@Param("stockCode") String stockCode,@Param("customerId") Long customerId );

	public List<Long> queryAll();
	
	/**
	 * 根据客户ID获取所持股票信息
	 * @param customerId
	 * @return
	 */
	public List<Long> queryByCustomerId(@Param("customerId") Long customerId);
	
	/**
	 * 股票维度统计
	 * @param param
	 * @return
	 */
	public List<TeacherDirectiveCot> queryGroupStock(RowBounds bounds,@Param("cm")Map<String, Object> param);

}
