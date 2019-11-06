package com.lazhu.task.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.lazhu.crm.alarm.service.AlarmService;
import com.lazhu.crm.auditmobile.service.AuditMobileService;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.customeractualcot.service.CustomerActualCotService;
import com.lazhu.crm.developdaycall.service.DevelopDayCallService;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.crm.resourceleave.service.ResourceLeaveService;
import com.lazhu.crm.salercallinrecord.service.SalerCallinRecordService;
import com.lazhu.crm.salerrecord.service.SalerRecordService;
import com.lazhu.crm.serverrecordmobile.service.ServerRecordMobileService;
import com.lazhu.crm.teacherdirectivecot.service.TeacherDirectiveCotService;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.product.insuranceproduct.service.InsuranceProductService;
import com.lazhu.sys.model.Properties;
import com.lazhu.sys.service.PropertiesService;
import com.lazhu.sys.service.SysUserService;
import com.lazhu.t.resourceallot.service.ResourceAllotService;

/**
 * @author naxj
 * 
 */
@Service
@EnableScheduling
public class CoreTaskService {
    
    @Autowired//资源
    private ResourceService resourceService;
    
    @Autowired//闲置资源
    private ResourceLeaveService resourceLeaveService;
    
    @Autowired//会员
    private CustomerService customerService;
    
    @Autowired//产品
    private InsuranceProductService productService;
    
    @Autowired//推广资源分配
    private ResourceAllotService resourceAllotService;
    
    @Autowired//用户
    private SysUserService userService;
    
    @Autowired//钉钉闹钟
    private AlarmService alarmService;
    
    @Autowired//市场部通话记录
    private SalerRecordService salerRecordService;
    
    @Autowired//配置
    private PropertiesService propertiesService;
    
    @Autowired//业务员日通话统计
    private DevelopDayCallService developDayCallService;

    @Autowired//市场部来电记录
    private SalerCallinRecordService salerCallinRecordService;
	
    @Autowired//客服部来电记录
    private ServerRecordMobileService serverRecordMobileService;
    
    @Autowired
    private CustomerActualCotService customerCotService;
    
    @Autowired
    private TeacherDirectiveCotService teacherCotService;
    
    @Autowired
    private AuditMobileService auditMobileService;

    
    /** 每天重置分配资源数，最大分配数，控制分配数，剩余奖励资源数，每天抽取次数（默认时间：每天00:00） **/
	@Scheduled(cron = "0 0 0 * * ?")
	public void syncResetUserAllotNum() {
		Properties properties = propertiesService.queryByKeyString("task.syncResetUserAllotNum");
		String allotNum = properties.getValueString();
		int num = StrUtils.toInt(allotNum);
		if (num >= 0) {
			try {
				userService.resetUserAllotNum(num);
				System.out.println("调度：每天重置分配资源数，最大分配数，控制分配数，每天抽取次数");
			} catch (Exception e) {
				System.out.println("###定时每天重置分配资源数，最大分配数，控制分配数，每天抽取次数，发生错误！");
			}
		} else {
			System.out.println("###每天重置最大分配数失败，无参数或参数小于0");
		}
		propertiesService.update(properties);
	}
	@Scheduled(cron = "0 30 21 * * ?")
	public void syncStopUserAllot() {
		Properties properties = propertiesService.queryByKeyString("task.syncStopUserAllot");
		String value = properties.getValueString();
		if (null != value && "1".equals(value)) {
			try {
				userService.stopUserAllot();
				System.out.println("调度：每天关闭个人推广分配");
			} catch (Exception e) {
				System.out.println("###定时每天关闭个人推广分配，发生错误！");
			}
		} else {
			System.out.println("###每天关闭个人推广分配失败，无参数或参数不等于1");
		}
		propertiesService.update(properties);
	}
	
	/** 每天9：20统一修改所有业务员推广资源最大分配数 **/
	@Scheduled(cron = "0 20 9 * * ?")
	public void syncChangeTResourceMax() {
		Properties properties = propertiesService.queryByKeyString("task.syncChangeTResourceMax");
		String value = properties.getValueString();
		if (null != value && "1".equals(value)) {
			Properties firstNum = propertiesService.queryByKeyString("task.syncResourceFirstWeek");
			int firstWeek = StrUtils.toInt(firstNum.getValueString());
			Properties secondNum = propertiesService.queryByKeyString("task.syncResourceSecondWeek");
			int secondWeek = StrUtils.toInt(secondNum.getValueString());
			Properties maxNum = propertiesService.queryByKeyString("task.syncResourceMax");
			int num = StrUtils.toInt(maxNum.getValueString());
			try {
				userService.syncChangeTResourceMax(firstWeek,secondWeek,num);
				System.out.println("调度：每天9：20统一修改所有业务员推广资源最大分配数");
			} catch (Exception e) {
				System.out.println("###定时每天9：20统一修改所有业务员推广资源最大分配数，发生错误！");
			}
		} else {
			System.out.println("###每天统一修改所有业务员推广资源最大分配数失败，无参数或参数不等于1");
		}
		propertiesService.update(properties);
	}
	
	
	/** 每天执行产品签单情况统计（默认时间：每天02:00） **/
	@Scheduled(cron = "0 0 2 * * ?")
	public void productSignStatistics() {
		Properties properties = propertiesService.queryByKeyString("task.productSignStatistics");
		String value = properties.getValueString();
		if (null != value && "1".equals(value)) {
			try {
				productService.product();
				System.out.println("调度：每天执行产品签单情况统计");
			} catch (Exception e) {
				System.out.println("###定时每天执行产品签单情况统计，发生错误！");
			}
		} else {
			System.out.println("###每天执行产品签单情况统计失败，无参数或参数不等于1");
		}
		propertiesService.update(properties);
	}
	
	/** 每天回收超过保护期的资源到共享池（默认时间：每天03:00） **/
	@Scheduled(cron = "0 0 3 * * ?")
	public void syncRecycleSharedPool() {
		Properties properties = propertiesService.queryByKeyString("task.syncRecycleSharedPool");
		String value = properties.getValueString();
		if (null != value && "1".equals(value)) {
			try {
				resourceService.syncRecycleSharedPool();
				System.out.println("调度：每天回收超过保护期的资源到共享池");
			} catch (Exception e) {
				System.out.println("###定时每天回收超过保护期的资源到共享池，发生错误！");
			}
		} else {
			System.out.println("###每天回收超过保护期的资源到共享池失败，无参数或参数不等于1");
		}
		propertiesService.update(properties);
	}
	
	/** 每天回收共享池资源到垃圾池（备注为：空号、停机）（默认时间：每天04:00） **/
	@Scheduled(cron = "0 0 4 * * ?")
	public void syncRecycleRubbish() {
		Properties properties = propertiesService.queryByKeyString("task.syncRecycleRubbish");
		String value = properties.getValueString();
		if (null != value && "1".equals(value)) {
			try {
				resourceLeaveService.syncRecycleRubbish();
				System.out.println("调度：每天回收共享池资源到垃圾池");
			} catch (Exception e) {
				System.out.println("###定时每天回收共享池资源到垃圾池，发生错误！");
			}
		} else {
			System.out.println("###每天回收共享池资源到垃圾池失败，无参数或参数不等于1");
		}
		propertiesService.update(properties);
	}
	
	/** 每10分钟查询是否有闹钟，并发送钉钉消息（默认时间：每10分钟） **/
	@Scheduled(fixedRate = 600000)
	public void syncAlarmTime() {
		Properties properties = propertiesService.queryByKeyString("task.syncAlarmTime");
		String value = properties.getValueString();
		if (null != value && "1".equals(value)) {
			try {
				alarmService.syncAlarmTime();
				System.out.println("调度：每10分钟查询是否有闹钟，并发送钉钉消息");
			} catch (Exception e) {
				System.out.println("###定时每10分钟查询是否有闹钟，并发送钉钉消息，发生错误！");
			}
		} else {
			System.out.println("###每10分钟查询是否有闹钟，并发送钉钉消息失败，无参数或参数不等于1");
		}
		propertiesService.update(properties);
	}
	
	/** 定时每5分钟同步市场部通话记录和PBX通话记录（默认时间：每5分钟） **/
	@Scheduled(fixedRate = 300000)
	public void syncSalerRecord() {
		Properties properties = propertiesService.queryByKeyString("task.syncSalerRecord");
		String value = properties.getValueString();
		if (null != value && "1".equals(value)) {
			try {
				salerRecordService.syncSalerRecord();
				System.out.println("调度：定时每5分钟同步市场部通话记录和PBX通话记录");

				/**
				 * 执行客服部通话记录
				 * */
				serverRecordMobileService.syncServerRecordMobile();
				System.out.println("调度：定时每5分钟同步客服部通话记录和PBX通话记录");
				
			} catch (Exception e) {
				System.out.println("###定时每5分钟同步市场部通话记录和PBX通话记录，发生错误！");
			}
		} else {
			System.out.println("###定时每5分钟同步市场部通话记录和PBX通话记录失败，无参数或参数不等于1");
		}
		propertiesService.update(properties);
	}
	
	/** 定时每10分钟发送分配信息到推广群中（默认时间：7点到10点，每10分钟发一次） **/
	@Scheduled(fixedRate = 600000)
	public void promotionSendDingMsg() {
		Properties properties = propertiesService.queryByKeyString("task.promotionSendDingMsg");
		String value = properties.getValueString();
		if (null != value && "1".equals(value)) {
			try {
				resourceAllotService.sendTgGroupMsg();
				System.out.println("调度：执行每10分钟发送分配信息到推广群中");
			} catch (Exception e) {
				System.out.println("###定时执行发送分配信息到推广群，发生错误！");
			}
		} else {
			System.out.println("###定时每10分钟发送分配信息到推广群中失败，无参数或参数不等于1");
		}
		propertiesService.update(properties);
	}
	
	/** 执行业务员日通话统计（默认时间：每天23:30） **/
	@Scheduled(cron = "0 0 23 * * ?")
	public void developDayCall() {
		Properties properties = propertiesService.queryByKeyString("task.developDayCall");
		String value = properties.getValueString();
		if (null != value && "1".equals(value)) {
			try {
				developDayCallService.developDayCall();
				System.out.println("调度：执行业务员日通话统计");
			} catch (Exception e) {
				System.out.println("###定时执行业务员日通话统计，发生错误！");
			}
		} else {
			System.out.println("###执行业务员日通话统计失败，无参数或参数不等于1");
		}
		propertiesService.update(properties);
	}
	
	/** 执行市场部来电记录收集 **/
	@Scheduled(fixedRate = 300000)
	public void salerCallinLog () {
		Properties properties = propertiesService.queryByKeyString("task.salerCallinLog");
		String value = properties.getValueString();
		if (null != value && "1".equals(value)) {
			try {
				salerCallinRecordService.salerCallinLog();
				System.out.println("调度：定时5分钟执行市场部来电记录收集");
			} catch (Exception e) {
				System.out.println("###定时5分钟执行市场部来电记录收集，发生错误！");
			}
		} else {
			System.out.println("###定时5分钟执行市场部来电记录收集失败，无参数或参数不等于1");
		}
		propertiesService.update(properties);
	}
	
	/** 定时周一至周五8：30通知业务员上班后开启推广分配（默认时间：周日至周五早上8：30） **/
	@Scheduled(cron = "0 30 8 ? * SUN-FRI")//（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT）
	public void msgSalerToOpenAllotResource() {
		Properties properties = propertiesService.queryByKeyString("task.msgSalerToOpenAllotResource");
		String value = properties.getValueString();
		if (null != value && "1".equals(value)) {
			try {
				userService.msgSalerToOpenAllotResource();
				System.out.println("调度：定时周一至周五8：30通知业务员上班后开启推广分配");
			} catch (Exception e) {
				System.out.println("###定时周一至周五8：30通知业务员上班后开启推广分配，发生错误！");
			}
		} else {
			System.out.println("###定时周一至周五8：30通知业务员上班后开启推广分配失败，无参数或参数不等于1");
		}
		propertiesService.update(properties);
	}
	
	/**
	 * 周一至周五，每天八点半开启股票信息同步
	 */
	@Scheduled(cron = "0 30 8 ? * MON-FRI")
	public void startStock(){
		Properties state = propertiesService.queryByKeyString("task.stateStockCustomer");
		String value = state.getValueString();
		Properties properties = propertiesService.queryByKeyString("task.updataStockCustomer");
		try {
			if (null != value && "1".equals(value)) {
				properties.setValueString("1");
				propertiesService.update(properties);
				System.out.println("调度：周一至周五，每天八点半开启股票信息同步");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("调度 周一至周五，每天八点半开启股票信息同步，发生错误");
		}
		
	}
	
	/**
	 * 周一至周五，每天下午5点半关闭同步股票信息
	 */
	@Scheduled(cron = "0 30 17 ? * MON-FRI")
	public void stopStock(){
		Properties state = propertiesService.queryByKeyString("task.stateStockCustomer");
		String value = state.getValueString();
		Properties properties = propertiesService.queryByKeyString("task.updataStockCustomer");
		try {
			if (null != value && "1".equals(value)) {
				properties.setValueString("0");
				propertiesService.update(properties);
				System.out.println("调度： 周一至周五，每天下午五点半关闭股票信息同步");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("调度 周一至周五，每天下午五点半关闭股票信息同步，发生错误");
		}
		
	}
	
	
	/**
	 * 同步正常服务客户股票相关的方法
	 * 调度（正在服务客户3分钟更新一次）
	 */
	@Scheduled(fixedRate = 180000)
	public void synchrStockCustomer() {
		Properties properties = propertiesService.queryByKeyString("task.updataStockCustomer");
		String value = properties.getValueString();
		//更新所有会员股票相关信息（会员资金表）
		try {
			if (null != value && "1".equals(value)) {
				customerCotService.updateAllCustomerStock();
				System.out.println("调度：三分钟更新同步服务客户股票相关的信息");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("调度 三分钟更新同步服务客户股票相关的信息，发生错误");
		}
		propertiesService.update(properties);
	}
	

	/**
	 *  周天至周四下午15点半执行成交客户自动分配给升级人员 
	 *  @author ty
	 **/
	//@Scheduled(cron = "0 0 15 ? * SUN-THU")
	@Scheduled(cron = "0 0 18 * * ?")
	public void customerToUpgrade() {
		Properties isOrNotUpgrader = propertiesService.queryByKeyString("task.isOrNotUpgrader");
		String value = isOrNotUpgrader.getValueString();
		try {
			if (null != value && "1".equals(value)) {
				try {
					//customerService.allotUpgrade();
					customerService.waitAllotUpgrade();
					System.out.println("调度：成交客户自动分配给升级人员");
				} catch (Exception e) {
					System.out.println("###执行成交客户自动分配给升级人员，发生错误！");
				}
			} else {
				System.out.println("###执行成交客户自动分配给升级人员，无参数或参数小于0");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("调度 每天执行成交客户自动分配给升级人员，发生错误");
		}
		
		propertiesService.update(isOrNotUpgrader);
	}
	
	 /**  
	  * 
	  *	每天3点重置升级分配资源数（默认时间：每天03:00）
	  *	@author ty
	  *
	  **/
	@Scheduled(cron = "0 0 3 * * ?")
	public void syncResetUserAllotClubNum() {
		Properties properties = propertiesService.queryByKeyString("task.syncResetUserAllotClubNum");
		String value = properties.getValueString();
		if (null != value && "1".equals(value)) {
			try {
				userService.resetUserAllotClubNum();
				System.out.println("调度：每天3点重置升级分配资源数");
			} catch (Exception e) {
				System.out.println("###定时每天3点重置升级分配资源数，发生错误！");
			}
		} else {
			System.out.println("###每天3点重置升级分配资源数，无参数或参数小于0");
		}
		propertiesService.update(properties);
	}
	/**  
	 * 
	 *	每天4点校正投顾客户可用资金（默认时间：每天04:00）
	 *	@author ty
	 *
	 **/
	@Scheduled(cron = "0 0 4 * * ?")
	public void syncUpdateUserMoney() {
		Properties properties = propertiesService.queryByKeyString("task.syncUpdateUserMoney");
		String value = properties.getValueString();
		if (null != value && "1".equals(value)) {
			try {
				customerService.updateAllUserMoney();
				System.out.println("调度：每天4点校正投顾客户可用资金");
			} catch (Exception e) {
				System.out.println("###定时每天4点校正投顾客户可用资金，发生错误！");
			}
		} else {
			System.out.println("###每天4点校正投顾客户可用资金，无参数或参数小于0");
		}
		propertiesService.update(properties);
	}
	
	/**
	 * 10分钟同步客服通话记录到会员表
	 */
	@Scheduled(fixedRate = 600000)
	public void synchToCustomer() {
		try {
			serverRecordMobileService.toCustomer();
			System.out.println("调度：10分钟同步客服通话记录到会员表");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("调度10分钟同步客服通话记录到会员表，发生错误");
		}
	}
	
	/**
	 *  周天至周四上午九点半执行升级客户自动分配给二开升级人员 
	 *  @author ty
	 **/
	@Scheduled(cron = "0 40 9 * * ?")
	public void customerToTwoUpgrade() {
		Properties isOrNotTwoUpgrader = propertiesService.queryByKeyString("task.isOrNotTwoUpgrader");
		String value = isOrNotTwoUpgrader.getValueString();
		try {
			if (null != value && "1".equals(value)) {
				try {
					customerService.moveTenDaysCustomer();
					System.out.println("调度：周天至周四上午九点半执行升级客户自动分配给二开升级人员");
				} catch (Exception e) {
					System.out.println("###执行升级客户自动分配给二开升级人员，发生错误！");
				}
			} else {
				System.out.println("###执行升级客户自动分配给二开升级人员，无参数或参数小于0");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("调度 周天至周四上午九点半执行成交客户自动分配给二开升级人员，发生错误");
		}
		propertiesService.update(isOrNotTwoUpgrader);
	}
	
	/**
	 * 每60分钟查询查询失效事项
	 */
	@Scheduled(fixedRate = 3600000)
	public void synchUpdateStatus() {
		try {
			auditMobileService.updateStatus();
			System.out.println("调度：每60分钟查询查询失效事项");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("调度每60分钟查询查询失效事项，发生错误");
		}
	}
}