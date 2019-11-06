package com.lazhu.crm.salercallinrecord.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.crm.salercallinrecord.entity.SalerCallinRecord;
/**
 * <p>
 * 市场部来电记录表 Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2018-01-18
 */
public interface SalerCallinRecordMapper extends BaseMapper<SalerCallinRecord> {
	
	/** 通过录音文件名查询ID **/
	List<Long> queryByCallFile(@Param("callFile") String callFile);

	/** 查询市场部我的来电表中最大已接电话ID(answer_id) **/
	Long queryMaxAnswerId();
	
	/** 查询市场部我的来电表中最大未接电话ID(noanswer_id) **/
	Long queryMaxNoanswerId();

	List<Long> queryServerRecordIsNull();

}
