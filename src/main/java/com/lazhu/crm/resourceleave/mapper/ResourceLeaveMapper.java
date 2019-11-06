package com.lazhu.crm.resourceleave.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.crm.resourceleave.entity.ResourceLeave;
/**
 * <p>
 * 资源闲置表 Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2018-04-13
 */
public interface ResourceLeaveMapper extends BaseMapper<ResourceLeave> {
	
	List<Long> queryExtractResource();

	List<Long> queryExtractMobileResource(@Param("spn") int spn);

	List<Long> queryIdByMd5Mobile(@Param("md5Mobile") String md5Mobile);
	
	/** 定时每天回收共享池资源到垃圾池（备注为：空号、停机） **/
	Integer syncRecycleRubbish();
	
	/** 查询剩余可抽取资源数 **/
	Integer remainExtractNum();
	
	/** 今日丢弃共享池资源数 **/
	Integer queryToDayLoseResourceNum(@Param("userId") Long userId);
	
}
