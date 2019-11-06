package com.lazhu.crm.alarm.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.crm.alarm.entity.Alarm;
/**
 * <p>
 * 钉钉闹钟 Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2017-11-16
 */
public interface AlarmMapper extends BaseMapper<Alarm> {

	/** 权限参数查询闹钟 **/
	List<Long> queryValid (@Param("cm") Map<String, Object> param);
	
	/** 查询闹钟时间为10分钟以前和10分钟以后的闹钟 **/
	List<Long> queryAlarmByAlarmTime ();

	/** 查询某客户今天没有回访的闹钟 **/
	List<Long> queryUserTodayNotCall(@Param("customerId") Long customerId,@Param("userId")  Long userId);
	
}
