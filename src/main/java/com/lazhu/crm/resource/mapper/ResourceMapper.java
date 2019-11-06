package com.lazhu.crm.resource.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import com.lazhu.core.base.BaseMapper;
import com.lazhu.crm.resource.entity.Resource;
/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
public interface ResourceMapper extends BaseMapper<Resource> {
	
	/** 查询存在该号码 （新增时自己也不能有）**/
	public Long isExist(@Param("cm") Resource params);
	
	/** 查询存在该号码（自己除外） **/
	public Long isExists(@Param("cm") Resource params);
	
	/**通过手机号查找客户所在省份**/
	public String queryProvinceByMobile(@Param("mobile") String mobile);
	
	/**通过手机号查找客户所在城市**/
	public String queryCityByMobile(@Param("mobile") String mobile);


	List<Long> queryIdByMd5Mobile(@Param("md5Mobile") String md5Mobile);


	List<Long> queryAllRetain();
	
	Integer syncRecycleSharedPool();

	/** 通过分配ID查询资源ID **/
	List<Long> queryByAllotId(@Param("allotId") Long allotId);

	/** 今日未添加微信资源数 **/
	Integer queryToDayTochatNum(@Param("userId") Long userId);
	
	/** 今日已添加微信资源数 **/
	Integer queryToDayDisposedNum(@Param("userId") Long userId);
	
	/** 今日推广资源数 **/
	Integer queryToDayPromoteResourceNum(@Param("userId") Long userId);
	
	/** 今日抽取资源数 **/
	Integer queryToDayExtractNum(@Param("userId") Long userId);
	
	/** 今日拨打客户数 **/
	Long todayCallResourceNum(@Param("cm") Map<String, Object> param);

	/** 删除共享池资源 **/
	void deleteSharedPool();

	public List<Map<String, Object>> queryBySql(@Param("sql") String sql);

	/** 查询当前经纪人有几条推广资源 **/
	public Integer resourceInitNum(@Param("salerId") Long currUser);
	
	/**
	 * 批量插入资源
	 * @param resourceList
	 */
	public void insertBatchResource(@Param("list") List<Resource> resourceList);
	
}
