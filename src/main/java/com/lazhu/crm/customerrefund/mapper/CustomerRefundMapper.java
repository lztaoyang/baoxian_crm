package com.lazhu.crm.customerrefund.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.crm.customerrefund.entity.CustomerRefund;
/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
public interface CustomerRefundMapper extends BaseMapper<CustomerRefund> {

	/**查询退款记录（通过会员ID）**/
	List<Long> queryByCustomerId(@Param("customerId") Long customerId);
	// 退保订单查询 
	List<Map<String, String>> queryRefundSignApply(RowBounds rowBounds, @Param("cm") Map<String, Object> params);
	// XX中心退保查询
	List<Map<String, String>> list4zx(RowBounds rowBounds, @Param("cm") Map<String, Object> params);
				
}
