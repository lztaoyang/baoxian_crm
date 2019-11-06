package com.lazhu.crm.salerrecordday.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.crm.Constant;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.salerrecordday.entity.SalerRecordDay;
import com.lazhu.crm.salerrecordday.mapper.SalerRecordDayMapper;
import com.lazhu.ecp.utils.StrUtils;

/**
 * <p>
 * 市场部通话记录今天表 服务实现类
 * </p>
 *
 * @author hz
 * @since 2018-04-13
 */
@Service
@CacheConfig(cacheNames = "salerRecordDay")
public class SalerRecordDayService extends BusinessBaseService<SalerRecordDay> {
	
	@Autowired
	SalerRecordDayMapper salerRecordDayMapper;
	
	//有效通话时长参考值
	private Integer validTiemLength = 60;
	
	/**
	 * 统计今天数据
	 * 今日拨打次数，今日有效通话时长
	 * (id,userName,todayToChatNum,todayDisposedNum,dayNum,dayValidLength)
	 * @param param
	 * @param currUser
	 * @return
	 */
	public List<Map<String, Object>> callGroup(Map<String, Object> param,Long group, Long currUser) {
		//今天是否有效时长默认值
		if (!param.containsKey("validTiemLength") || StrUtils.toInt(param.get("validTiemLength")) <= 0) {
			validTiemLength = 60;
		} else {
			validTiemLength = StrUtils.toInteger(param.get("validTiemLength"),60);
		}
		//1.查询用户
		List<Map<String, Object>> callGroup = null;
		Long userGroup = userService.queryById(currUser).getUserGroup();
		if (userGroup == Constant.USER_GROUP_JL) {
			callGroup = salerRecordDayMapper.queryManagerGroup(group,validTiemLength,currUser);
		} else if (userGroup == Constant.USER_GROUP_ZJ) {
			if (param.containsKey("managerId")) {
				callGroup = salerRecordDayMapper.queryManagerGroup(group,validTiemLength,StrUtils.toLong(param.get("managerId")));
			} else {
				callGroup = salerRecordDayMapper.queryDirectorGroup(group,validTiemLength,currUser);
			}
		} else if (userGroup == Constant.USER_GROUP_ZJL) {
			if (param.containsKey("managerId")) {
				callGroup = salerRecordDayMapper.queryManagerGroup(group,validTiemLength,StrUtils.toLong(param.get("managerId")));
			} else if (param.containsKey("directorId")) {
				callGroup = salerRecordDayMapper.queryDirectorGroup(group,validTiemLength,StrUtils.toLong(param.get("directorId")));
			} else {
				callGroup = salerRecordDayMapper.queryMinisterGroup(group,validTiemLength,currUser);
			}
		} else {
			if (param.containsKey("managerId")) {
				callGroup = salerRecordDayMapper.queryManagerGroup(group,validTiemLength,StrUtils.toLong(param.get("managerId")));
			} else if (param.containsKey("directorId")) {
				callGroup = salerRecordDayMapper.queryDirectorGroup(group,validTiemLength,StrUtils.toLong(param.get("directorId")));
			} else if (param.containsKey("ministerId")) {
				callGroup = salerRecordDayMapper.queryMinisterGroup(group,validTiemLength,StrUtils.toLong(param.get("ministerId")));
			} else {
				callGroup = salerRecordDayMapper.queryAllNormalBusiness(group,validTiemLength);
			}
		}
		
		return callGroup;
	}
	
	/**
	 * 今日总拨打次数						callNum
	 * 今日拨打接通次数						throughNum
	 * 今日有效拨打次数（时长大于等于1分钟）		callValidNum
	 * 今日总通话时长						totalLength
	 * 今日有效时长（时长大于等于1分钟）		validLength
	 * @param userId
	 * @return
	 */
	public Map<String, Object> toDayCallStatistics(Long userId) {
		return salerRecordDayMapper.toDayCallStatistics(userId);
	}
	
}
