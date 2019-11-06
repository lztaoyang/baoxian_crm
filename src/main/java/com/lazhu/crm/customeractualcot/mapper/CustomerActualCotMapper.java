package com.lazhu.crm.customeractualcot.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.lazhu.crm.customeractualcot.entity.CustomerActualCot;
import com.lazhu.core.base.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author taoyang
 * @since 2018-12-17
 */
public interface CustomerActualCotMapper extends BaseMapper<CustomerActualCot> {
	
	/**
	 * 查询是否持有该股票
	 * @param param
	 * @return
	 */
	public List<Long> queryByStockCode(@Param("stockCode") String stockCode ,@Param("customerId") Long customerId );

	/**
	 * 查询所有
	 * @return
	 */
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
	public List<CustomerActualCot> queryGroupStock(RowBounds bounds,@Param("cm")Map<String, Object> param);
}
