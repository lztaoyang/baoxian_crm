package com.lazhu.crm.salerrecord.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.crm.salerrecord.entity.SalerRecord;
/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2017-08-03
 */
public interface SalerRecordMapper extends BaseMapper<SalerRecord> {

	/**查询市场部通话记录（通过会员ID）**/
	List<Long> queryByCustomerId (@Param("customerId") Long customerId);
	
	/** 查询市场部成功通话记录（通过会员ID） **/
	List<Long> queryRealByCustomerId (@Param("customerId") Long customerId);
	
	/** 查询市场部成功通话记录（通过会员ID） **/
	List<Long> queryRealByCustomerId1 (@Param("customerId") Long customerId);
	
	/** 查询市场部新年（2018）成功通话记录（通过会员ID） **/
	List<Long> queryNewYearRealByCustomerId (@Param("customerId") Long customerId);
	
	/** 查询市场部通话记录有录音文件，无通话状态或无通话时长的记录 **/
	List<Long> queryOnlyCallFile();
	
	/** 通过文件名模糊查询通话记录  **/
	List<Long> queryLikeCallName (@Param("callName") String callName);

}
