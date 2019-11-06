package com.lazhu.crm.developdaycall.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.developdaycall.entity.DevelopDayCall;
import com.lazhu.crm.developdaycall.mapper.DevelopDayCallMapper;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.crm.resourceleave.service.ResourceLeaveService;
import com.lazhu.crm.salerrecordday.service.SalerRecordDayService;
import com.lazhu.crm.signapply.service.SignApplyService;
import com.lazhu.ecp.utils.StrUtils;

/**
 * <p>
 * 经纪人日通话统计表 服务实现类
 * </p>
 *
 * @author hz
 * @since 2018-01-09
 */
@Service
@CacheConfig(cacheNames = "developDayCall")
public class DevelopDayCallService extends BusinessBaseService<DevelopDayCall> {
	
	@Autowired//市场部通话记录今日表
	private DevelopDayCallMapper developDayCallMapper;
	
	@Autowired//市场部通话记录今日表
	private SalerRecordDayService salerRecordDayService;
	
	@Autowired//开发资源
	private ResourceService resourceService;
	
	@Autowired//闲置资源
	private ResourceLeaveService resourceLeaveService;
	
	@Autowired//订单
	private SignApplyService signApplyService;
	
	/**
	 * 市场部网销开发详情视图
	 * @param param
	 * @param currUser
	 * @return
	 */
	public List<Map<String, Object>> developVo(Map<String, Object> param, Long currUser) {
		//市场部归属
		super.setBusiness(param, currUser);
		//下属查询
		if (StrUtils.isNotNullOrBlank(param.get("subordinate"))) {
			super.setBusiness(param, StrUtils.toLong(param.get("subordinate")));
		}
		//今天数据
		List<Map<String,Object>> dayList = salerRecordDayService.callGroup(param,88002001L, currUser);
		if (null != dayList && dayList.size() > 0) {
			for (Map<String, Object> dayMap : dayList) {
				if (StrUtils.isNullOrBlank(dayMap.get("todayAddChatNum"))) {//今日已添加微信
					dayMap.put("todayAddChatNum",0);
				}
				if (StrUtils.isNullOrBlank(dayMap.get("todayDisposedNum"))) {//今日意向资源
					dayMap.put("todayDisposedNum",0);
				}
				if (StrUtils.isNullOrBlank(dayMap.get("todayChatNum"))) {//今日可聊资源
					dayMap.put("todayChatNum",0);
				}
				if (StrUtils.isNullOrBlank(dayMap.get("yesterdayAddChatNum"))) {//昨天已添加微信
					dayMap.put("yesterdayAddChatNum",0);
				}
				if (StrUtils.isNullOrBlank(dayMap.get("yesterdayNum"))) {//昨天意向资源
					dayMap.put("yesterdayNum",0);
				}
				if (StrUtils.isNullOrBlank(dayMap.get("yesterdayChatNum"))) {//昨天可聊资源
					dayMap.put("yesterdayChatNum",0);
				}
				/*if (StrUtils.isNullOrBlank(dayMap.get("vorgesternNum"))) {//前天意向资源
					dayMap.put("vorgesternNum",0);
				}
				if (StrUtils.isNullOrBlank(dayMap.get("vorgesternChatNum"))) {//前天可聊资源
					dayMap.put("vorgesternChatNum",0);
				}*/
			}
			//按照今天已添加微信数倒序
			Collections.sort(dayList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				if (null != o1 && null != o2 && null != o1.get("todayDisposedNum") && null != o2.get("todayDisposedNum")) {
					int num1 = StrUtils.toInt(o1.get("todayDisposedNum"));
	                int num2 = StrUtils.toInt(o2.get("todayDisposedNum"));
					if (num1 < num2) {
						return 1;
					} else if (num1 == num2) {
						return 0;
					} else {
						return -1;
					}
				}
				return -1;
			}
		});
		}
		return dayList;
	}
	/**
	 * 市场部电销开发详情视图
	 * @param param
	 * @param currUser
	 * @return
	 */
	public List<Map<String, Object>> developTelemarketingVo(Map<String, Object> param, Long currUser) {
		//市场部归属
		super.setBusiness(param, currUser);
		//下属查询
		if (StrUtils.isNotNullOrBlank(param.get("subordinate"))) {
			super.setBusiness(param, StrUtils.toLong(param.get("subordinate")));
		}
		//历史数据
		List<Map<String, Object>> developList = developDayCallMapper.queryByBusiness(param);
		//今天数据
		List<Map<String,Object>> dayList = salerRecordDayService.callGroup(param,88002001L, currUser);
		if (null != dayList && dayList.size() > 0) {
			for (Map<String, Object> dayMap : dayList) {
				for (Map<String, Object> developMap : developList) {
					if (StrUtils.toLong(dayMap.get("id")).longValue() == StrUtils.toLong(developMap.get("salerId")).longValue()) {
						dayMap.put("weekNum", StrUtils.toLong(developMap.get("weekNum"),0L) + StrUtils.toLong(dayMap.get("dayNum"),0L));
						dayMap.put("weekValidLength", StrUtils.toLong(developMap.get("weekValidLength"),0L) + StrUtils.toLong(dayMap.get("dayValidLength"),0L));
						
						dayMap.put("yesterdayNum", developMap.get("yesterdayNum"));
						dayMap.put("vorgesternNum", developMap.get("vorgesternNum"));
					}
					
				}
			}
		}
		return dayList;
	}
	
	/**
	 * 升级部开发详情视图
	 * @param param
	 * @param currUser
	 * @return
	 */
	public List<Map<String, Object>> upgradeDevelopVo(Map<String, Object> param, Long currUser) {
		//归属
		setUpgradeToBusiness(param, currUser);
		//下属查询
		if (StrUtils.isNotNullOrBlank(param.get("subordinate"))) {
			super.setBusiness(param, StrUtils.toLong(param.get("subordinate")));
		}
		//历史数据
		List<Map<String, Object>> developList = developDayCallMapper.queryByBusiness(param);
		//今天数据
		List<Map<String,Object>> dayList = salerRecordDayService.callGroup(param,88002666L, currUser);
		if (null != dayList && dayList.size() > 0) {
			for (Map<String, Object> dayMap : dayList) {
				for (Map<String, Object> developMap : developList) {
					if (StrUtils.toLong(dayMap.get("id")).longValue() == StrUtils.toLong(developMap.get("salerId")).longValue()) {
						dayMap.put("weekNum", StrUtils.toLong(developMap.get("weekNum"),0L) + StrUtils.toLong(dayMap.get("dayNum"),0L));
						dayMap.put("weekValidLength", StrUtils.toLong(developMap.get("weekValidLength"),0L) + StrUtils.toLong(dayMap.get("dayValidLength"),0L));
						
						dayMap.put("yesterdayNum", developMap.get("yesterdayNum"));
						dayMap.put("vorgesternNum", developMap.get("vorgesternNum"));
					}
				}
			}
		}
		return dayList;
	}
	
	/**
	 * 每天调度统计数据
	 */
	public void developDayCall () {
	    //所有保险经纪人
	    List<Long> salerList = userService.queryAllMan();
	    //所有加保人员
	    List<Long> upgraderList = userService.queryAllUpgradeMan();
	    //日期
	    Date dayDate = new Date();
	    //
	    DevelopDayCall developDayCall = null;
	    /** 保险经纪人 **/
	    for (Long id : salerList) {
	    	developDayCall = new DevelopDayCall();
	    	//日期
	    	developDayCall.setDayDate(dayDate);
	    	
	    	//保险经纪人ID
	    	developDayCall.setSalerId(id);
	    	//经理ID
	    	Long managerId = userService.queryById(id).getParentId();
	    	developDayCall.setManagerId(managerId);
	    	//总监ID
	    	Long directorId = userService.queryById(managerId).getParentId();
	    	developDayCall.setDirectorId(directorId);
	    	//总经理ID
	    	Long ministerId = userService.queryById(directorId).getParentId();
	    	developDayCall.setMinisterId(ministerId);
	    	/** 个人每日通话统计 **/
	    	Map<String,Object> toDayCallStatistics = salerRecordDayService.toDayCallStatistics(id);
	    	//今日总拨打次数
	    	developDayCall.setCallNum(StrUtils.toInteger(toDayCallStatistics.get("callNum")));
	    	//今日拨打接通次数
	    	developDayCall.setThroughNum(StrUtils.toInteger(toDayCallStatistics.get("throughNum")));
	    	//今日有效拨打次数（时长大于等于1分钟）
	    	developDayCall.setCallValidNum(StrUtils.toInteger(toDayCallStatistics.get("callValidNum")));
	    	//今日总通话时长
	    	developDayCall.setTotalLength(StrUtils.toLong(toDayCallStatistics.get("totalLength")));
	    	//今日有效时长（时长大于等于1分钟）
	    	developDayCall.setValidLength(StrUtils.toLong(toDayCallStatistics.get("validLength")));
	    	
	    	
	    	//今日未添加微信资源数
	    	Integer tochatNum = resourceService.queryToDayTochatNum(id);
	    	developDayCall.setTochatNum(tochatNum);
	    	//今日已添加微信资源数
	    	Integer disposedNum = resourceService.queryToDayDisposedNum(id);
	    	developDayCall.setDisposedNum(disposedNum);
	    	//今日推广资源数
	    	Integer promoteResourceNum = resourceService.queryToDayPromoteResourceNum(id);
	    	developDayCall.setPromoteResourceNum(promoteResourceNum);
	    	//今日抽取资源数
	    	Integer extractNum = resourceService.queryToDayExtractNum(id);
	    	developDayCall.setExtractNum(extractNum);
	    	//今日丢弃资源数
	    	Integer loseResourceNum = resourceLeaveService.queryToDayLoseResourceNum(id);
	    	developDayCall.setLoseResourceNum(loseResourceNum);
	    	//今日成交资源数（保险经纪人）
	    	Integer signNum = signApplyService.queryToDaySignNumBySalerId(id);
	    	developDayCall.setSignNum(signNum);
	    	//插入
	    	this.update(developDayCall);
		}
	    /** 加保人员 **/
	    for (Long id : upgraderList) {
	    	developDayCall = new DevelopDayCall();
	    	//日期
	    	developDayCall.setDayDate(dayDate);
	    	
	    	//升级ID
	    	developDayCall.setSalerId(id);
	    	//升级经理ID
	    	Long managerId = userService.queryById(id).getParentId();
	    	developDayCall.setManagerId(managerId);
	    	//升级总监ID
	    	Long directorId = userService.queryById(managerId).getParentId();
	    	developDayCall.setDirectorId(directorId);
	    	//升级总经理ID
	    	Long ministerId = userService.queryById(directorId).getParentId();
	    	developDayCall.setMinisterId(ministerId);
	    	/** 个人每日通话统计 **/
	    	Map<String,Object> toDayCallStatistics = salerRecordDayService.toDayCallStatistics(id);
	    	//今日总拨打次数
	    	developDayCall.setCallNum(StrUtils.toInteger(toDayCallStatistics.get("callNum")));
	    	//今日拨打接通次数
	    	developDayCall.setThroughNum(StrUtils.toInteger(toDayCallStatistics.get("throughNum")));
	    	//今日有效拨打次数（时长大于等于1分钟）
	    	developDayCall.setCallValidNum(StrUtils.toInteger(toDayCallStatistics.get("callValidNum")));
	    	//今日总通话时长
	    	developDayCall.setTotalLength(StrUtils.toLong(toDayCallStatistics.get("totalLength")));
	    	//今日有效时长（时长大于等于1分钟）
	    	developDayCall.setValidLength(StrUtils.toLong(toDayCallStatistics.get("validLength")));
	    	
	    	//今日成交资源数（加保人员）
	    	Integer signNum = signApplyService.queryToDaySignNumByUpgraderId(id);
	    	developDayCall.setSignNum(signNum);
	    	//插入
	    	this.update(developDayCall);
		}
		
	}
	
}
