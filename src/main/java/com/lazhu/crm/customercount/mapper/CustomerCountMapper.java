package com.lazhu.crm.customercount.mapper;

import com.lazhu.crm.customercount.entity.CustomerCount;
import com.lazhu.core.base.BaseMapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
/**
 *  会员统计表 Mapper 接口
 *
 * @author ty
 * @date 2018年11月21日
 */
public interface CustomerCountMapper extends BaseMapper<CustomerCount> {

	/**
	 * 查询会员是否已经被统计
	 * @param customerId 
	 */
	public List<Long> selectCountByCustomerId(@Param("customerId") Long customerId);
	
	/**
	 * 获取资源分配id
	 * 
	 * @param customerId
	 * @return
	 */
	public Map<String, Object> selectResourceAllot(@Param("customerId") Long customerId);


	/**
	 * 再次签单客户
	 * 
	 * @param bigDecimal
	 * @param isDel 0 +  1 -
	 */
	public void updateCustomerApply(@Param("applyAmount") Double applyAmount,@Param("customerId") Long customerId,@Param("isDel") int isDel);
	
	/**
	 * 升级客户操作
	 * 
	 * @param applyAmount 升级金额
	 * @param customerId 升级客户id
	 * @param isDel 0 +  1 -
	 */
	public void updateCustomerUpgrade(@Param("upgradeAmount") Double upgradeAmount,@Param("customerId") Long customerId,@Param("isDel") int isDel);


	/**
	 * 退保客户
	 * 
	 * @param customerId
	 * @param isWaver 是否犹豫期
	 */
	public void updateCustomerRefund(@Param("refundAmount") Double  refundAmount ,@Param("customerId") Long customerId,@Param("isWaver") int isWaver);
	
	/**
	 * 通过维度查询数据
	 * @param param 
	 * 
	 * @return
	 */
	public List<Map<String,Object>> queryByDimension(@Param("cc") Map<String, Object> param);

	/**
	 * 同步的时候删除已存在的客户
	 * 
	 * 
	 * @param customerId
	 */
	public void deleteByCustomerId(@Param("customerId") Long customerId);
	
	
}
