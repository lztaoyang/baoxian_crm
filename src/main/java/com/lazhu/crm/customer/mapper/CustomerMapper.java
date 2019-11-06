package com.lazhu.crm.customer.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.crm.customer.entity.Customer;
/**
 * <p>
 * 成交客户  Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2017-05-27
 */
public interface CustomerMapper extends BaseMapper<Customer> {

	/** 查询存在该号码 （新增时自己也不能有）**/
	public Long isExist(@Param("cm") Customer params);
	
	/** 查询存在该号码（自己除外） **/
	public Long isExists(@Param("cm") Customer params);
	
	/** 矫正市场部归属（通过保险经纪人ID） **/
	public void updateBelongBySalerId(@Param("cm") Map<String, Object> params);
	
	/**查询所有客户的省份**/
	public List<Long> queryCustomerProvincePage(@Param("cm")Map<String,Object> param);
	
	/**查询所有客户的城市**/
	public List<Long> queryCustomerCityPage(@Param("cm")Map<String,Object> param);
	
	/**手机号精准搜索客户**/
	public List<Long> queryByMd5Mobile(@Param("md5Mobile") String md5Mobile);
	
	/** 生日提醒 **/
	public List<Map<String, String>> queryBirthday(RowBounds rowBounds, @Param("cm") Map<String, Object> params);

	/**查询市场部最早开发的20个客户**/
	public List<Long> upgradeExtractCustomerId();
	
	/**查询未升级客户 **/
	public List<Long> queryNotUpdateCustomer();

	public List<Map<String, Object>> queryGroupFromInfo(@Param("cm") Map<String, Object> param);

	public List<Map<String, Object>> queryGroupProvince(@Param("cm") Map<String, Object> param);

	public List<Map<String, Object>> queryGroupCity(@Param("cm") Map<String, Object> param);

	public List<Map<String, Object>> queryGroupAge(@Param("cm") Map<String, Object> param);
	
	public List<Long> queryAll();

	public List<Long> queryTenDaysCusomer();

}