package com.lazhu.crm.alarm.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.util.InstanceUtil;
import com.lazhu.crm.alarm.entity.Alarm;
import com.lazhu.crm.alarm.mapper.AlarmMapper;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.ecp.utils.DateUtils;
import com.lazhu.ecp.utils.DingUtil;
import com.lazhu.ecp.utils.StrUtils;

/**
 * <p>
 * 钉钉闹钟 服务实现类
 * </p>
 *
 * @author hz
 * @since 2017-11-16
 */
@Service
@CacheConfig(cacheNames = "alarm")
public class AlarmService extends BusinessBaseService<Alarm> {
	
	@Autowired
	private AlarmMapper mapper;
	
	/**
	 * 当前用户查询当前客户钉钉闹钟
	 * @param param
	 * @return
	 */
	public List<Alarm> queryValid (Map<String, Object> param) {
		List<Alarm> alarmList = InstanceUtil.newArrayList();
		List<Long> ids = mapper.queryValid(param);
		if (null != ids && ids.size() > 0) {
			for (Long id : ids) {
				Alarm alarm = this.queryById(id);
				alarmList.add(alarm);
			}
		}
		return alarmList;
	}
	
	/**
	 * 查询钉钉闹钟列表
	 * @param param
	 * @param currUser 
	 * @return
	 */
	public Page<Alarm> allList (Map<String, Object> params, Long currUser) {
		//市场部权限
		super.setBusiness(params, currUser);
		//加保部权限
		super.setUpgradeToBusiness(params, currUser);
		//客服权限
		super.setServerToBusiness(params, currUser);
		//下属查询
		if (StrUtils.isNotNullOrBlank(params.get("subordinate"))) {
			super.setBusiness(params, StrUtils.toLong(params.get("subordinate")));
			super.setUpgradeToBusiness(params, StrUtils.toLong(params.get("subordinate")));
		}
		//删除下属key
		params.remove("subordinate");
		return super.query(params);
	}
	
	/**
	 * 查询闹钟时间为10分钟以前和10分钟以后的闹钟
	 * @return
	 */
	public List<Long> queryAlarmByAlarmTime () {
		return mapper.queryAlarmByAlarmTime();
	}

	/**
	 * 定时每10分钟查询是否有闹钟，并发送钉钉消息
	 */
	public void syncAlarmTime() {
		List<Long> ids = queryAlarmByAlarmTime();
		Alarm alarm = null;
		if (null != ids && ids.size() > 0) {
			for (Long id : ids) {
				alarm = this.queryById(id);
				String content = alarm.getSummary()==null?"":alarm.getSummary() +"，"+ alarm.getContent()==null?"":alarm.getContent();
				//发送钉钉消息
				DingUtil.sendMsg(content +"，提醒时间【"+DateUtils.DateToStr(alarm.getAlarmTime(),DateUtils.TIME_FORMAT_CHINESE)+"】", alarm.getDingId());
				//回写已发送
				alarm.setIsSend(1);
				this.update(alarm);
				alarm = null;
			}
		}
	}

	public List<Long> queryUserTodayNotCall(Long customerId, Long userId) {
		return mapper.queryUserTodayNotCall(customerId,userId);
	}
	
}
