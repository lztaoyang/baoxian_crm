package com.lazhu.aladdinpbx.recordlogs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lazhu.aladdinpbx.recordlogs.entity.Recordlogs;
import com.lazhu.core.base.BaseMapper;
/**
 * <p>
 * 电话录音记录表 Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2017-12-02
 */
public interface RecordlogsMapper extends BaseMapper<Recordlogs> {

	/** 通过录音文件查询ID **/
	List<Long> queryByCallFile(@Param("callFile") String callFile);
	
	/** 通过录音文件查询通话时长 **/
	List<Long> queryLengthByCallFile(@Param("callFile") String callFile);
	/** 查询已接电话录音记录表 **/
	//List<Long> answerList(@Param("crmId") Long crmId);

	/** 查询最新已经通话记录**/
	List<Long> queryAnswerByMaxId(@Param("answerMaxId") Long answerMaxId);

}
