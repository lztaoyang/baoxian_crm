package com.lazhu.crm.customerlog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lazhu.crm.customerlog.entity.CustomerLog;
import com.lazhu.core.base.BaseMapper;
/**
 * <p>
 * 资源会员操作流程日志  Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
public interface CustomerLogMapper extends BaseMapper<CustomerLog> {
	
	/**
	 * 批量更新客户资源日志表
	 * @param customerLogList
	 */
	public void insertBatchCustomerLog(@Param("list") List<CustomerLog> customerLogList);

}
