package com.lazhu.crm.mobile.mapper;

import com.lazhu.crm.mobile.entity.Mobile;
import com.lazhu.core.base.BaseMapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
/**
 * <p>
 * 客户联系方式表 Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2018-02-01
 */
public interface MobileMapper extends BaseMapper<Mobile> {

	/** 通过MD5手机号查询RSA加密手机号  **/
	List<String> getRsaMobileByMd5(@Param("md5Mobile") String md5Mobile);
	
	/**
	 * 批量插入客户信息管理表
	 * @param mobileList
	 */
	public void insertBatchMobile(@Param("list") List<Mobile> mobileList);

}
