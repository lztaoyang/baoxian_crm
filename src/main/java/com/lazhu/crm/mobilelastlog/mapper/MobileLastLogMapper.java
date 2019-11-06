package com.lazhu.crm.mobilelastlog.mapper;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.crm.mobilelastlog.entity.MobileLastLog;
/**
 * <p>
 * 最近拨打记录 Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2018-01-07
 */
public interface MobileLastLogMapper extends BaseMapper<MobileLastLog> {

	/** 查询手机号最近拨打记录表 **/
	Long queryLastByMd5Mobile(@Param("md5Mobile") String md5Mobile);

}
