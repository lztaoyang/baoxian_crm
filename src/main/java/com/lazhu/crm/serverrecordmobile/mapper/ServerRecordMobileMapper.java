package com.lazhu.crm.serverrecordmobile.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.crm.serverrecordmobile.entity.ServerRecordMobile;
/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
public interface ServerRecordMobileMapper extends BaseMapper<ServerRecordMobile> {

	/**查询服务记录（通过会员ID）**/
	List<Long> queryByCustomerId(@Param("customerId") Long customerId);
	
	/**查询服务记录（通过会员ID）**/
	List<Long> queryServerByCustomerId(@Param("customerId") Long customerId);

	/** 通过录音文件名查询ID **/
	List<Long> queryByCallFile(@Param("callFile") String callFile);

	/** 查询服务部通话记录有录音文件，无通话状态或无通话时长的记录 **/
	List<Long> queryOnlyCallFile();
}
