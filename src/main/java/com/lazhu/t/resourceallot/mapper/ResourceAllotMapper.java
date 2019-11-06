package com.lazhu.t.resourceallot.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.t.resourceallot.entity.ResourceAllot;

/**
 * <p>
 * 推广资源分配表 Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2017-10-27
 */
public interface ResourceAllotMapper extends BaseMapper<ResourceAllot> {

	long queryMaxTId();

	/** 查询未分配资源 **/
	List<Long> queryAllotResource();

	/** 查询用户群当前正在分配人数 **/
	Integer queryUserAllotMan();
	/** 查询用户群当前等待分配空闲数 **/
	Integer queryUserWaitNum();
	
	/** 查询用户群今天所有资源数 **/
	Integer queryResourceNum();
	/** 查询用户群今天已分配资源数 **/
	Integer queryResourceAllotNum();
	/** 查询用户群今天无效资源数 **/
	Integer queryResourceAInvalidNum();
	/** 查询当前推广资源待分配资源数 **/
	Integer queryResourceWaitNum();
	
	/** 查询当前共享池资源分配数 **/
	Integer querySharedpoolAllotNum();

	/**
	 * 查询最近一条入库的推广资源
	 * 
	 * @return
	 */
	Date queryMaxUpdateTime();

	/** 分配ID查询 **/
	Long queryByTId(@Param("tId") Long tId);

	/** 资源ID查询 **/
	Long queryByResourceId(@Param("resourceId") Long resourceId);

	List<Map<String, Object>> queryMd5Mobile(@Param("cm") Map<String, Object> param);
	
	List<Map<String, Object>> queryAllotDetail(@Param("cm") Map<String, Object> param);
	
	/**
	 * 批量更新推广资源分配表
	 * @param allotList
	 */
	public void updateBatchAllot(@Param("list") List<ResourceAllot> allotList);

}
