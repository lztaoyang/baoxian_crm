package com.lazhu.crm.salerrecord.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.aladdinpbx.recordlogs.service.RecordlogsService;
import com.lazhu.core.util.ComplexMD5Util;
import com.lazhu.crm.Constant;
import com.lazhu.crm.alarm.entity.Alarm;
import com.lazhu.crm.alarm.service.AlarmService;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.mobilelastlog.entity.MobileLastLog;
import com.lazhu.crm.mobilelastlog.service.MobileLastLogService;
import com.lazhu.crm.resource.entity.Resource;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.crm.salerrecord.entity.SalerRecord;
import com.lazhu.crm.salerrecord.mapper.SalerRecordMapper;
import com.lazhu.crm.worktrailnormal.entity.WorkTrailNormal;
import com.lazhu.crm.worktrailnormal.service.WorkTrailNormalService;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.model.SysUser;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@Service
@CacheConfig(cacheNames = "salerRecord")
public class SalerRecordService extends BusinessBaseService<SalerRecord> {
	
	@Autowired
	private ResourceService resourceService;

	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private RecordlogsService recordlogsService;
	
	@Autowired
	private MobileLastLogService mobileLastLogService;
	
	// 正常工作轨迹
	@Autowired
	private WorkTrailNormalService workTrailNormalService;
	
	// 钉钉闹钟
	@Autowired
	private AlarmService alarmService;

	/**
	 * 新增通话记录
	 * @param param
	 * @param currUser
	 */
	@Transactional
	public SalerRecord add(SalerRecord param,Long currUser) {
		String fuzzyMobile = StrUtils.hideMobile(param.getCustomerMobile());
		String md5Mobile = ComplexMD5Util.MD5Encode(param.getCustomerMobile());
		param.setFuzzyMobile(fuzzyMobile);
		param.setCustomerMobile(md5Mobile);
		SysUser user = super.getUserById(currUser);
		param.setServerId(currUser);
		param.setServerName(user.getAccount());
		param.setServerMobile(user.getPhone());
		param.setAgentNo(user.getAgentNo());
		param.setManagerId(user.getParentId());
		param.setUpdateBy(currUser);
		param.setEnable(false);
		//回写资源表首次通话时间、最后通话时间、最后通话内容
		Date date = new Date();
		Resource resource = resourceService.queryById(param.getCustomerId());
		if (null != resource && resource.getFlowId() != Constant.FLOW_CJ) {
			if (StrUtils.isNullOrBlank(resource.getFirstCallTime())) {
				resource.setFirstCallTime(date);
			}
			resource.setLastCallTime(date);
			resource.setLastCallRecord(param.getServerRecord());
			resourceService.update(resource);
		}
		//升级部回写最后通话，处理预约
		if (user.getUserGroup() == Constant.USER_GROUP_JB
				|| user.getUserGroup() == Constant.USER_GROUP_JBJL
				|| user.getUserGroup() == Constant.USER_GROUP_JBZJ) {
			Customer customer = customerService.queryById(param.getCustomerId());
			if (null != customer && customer.getIsUpgrade() == 1) {
				//升级部回写最后通话
				customer.setLastCallTime(date);
				customer.setLastCallRecord(param.getServerRecord());
				customerService.update(customer);
				//升级部评估是否回复预约
				List<Long> ids = alarmService.queryUserTodayNotCall(customer.getId(),user.getId());
				Alarm alarm = null;
				if (null != ids && ids.size() > 0) {
					for (Long id : ids) {
						alarm = alarmService.queryById(id);
						alarm.setIsCall(1);
						alarmService.update(alarm);
					}
				}
			}
		}
		
		
		//插入资源部分信息
		param.setFlowId(resource.getFlowId());//开发流程
		param.setFromInfo(resource.getFromInfo());//来源渠道
		param.setResourceTime(resource.getCreateTime());//入库时间
		
		//保存最近拨打记录
		MobileLastLog mobileLastLog = new MobileLastLog();
		mobileLastLog.setMd5Mobile(md5Mobile);
		mobileLastLog.setAgentNo(user.getAgentNo());
		int type = super.getUserGroupType(user.getUserGroup());
		mobileLastLog.setType(type);
		mobileLastLog.setUpdateBy(currUser);
		mobileLastLogService.update(mobileLastLog);
		super.update(param);
		
		// 保存正常工作轨迹
		WorkTrailNormal bean = new WorkTrailNormal();
		bean.setBeforeFlowId(resource.getFlowId());// 上一次流程
		bean.setCustomerId(param.getCustomerId());// 客户ID
		bean.setDirectorId(resource.getDirectorId());
		bean.setManagerId(resource.getManagerId());
		bean.setMinisterId(resource.getMinisterId());
		bean.setSalerId(user.getUserGroup() == Constant.USER_GROUP_YWY ? resource.getSalerId() : -1L);
		bean.setSalerRecordId(param.getId());
		bean.setTimeLength(param.getTimeLength());
		bean.setEnterTime(resource.getEnterTime());
		bean.setUpdateBy(currUser);
		workTrailNormalService.update(bean);
		param.setWorkTrailNormalId(bean.getId());
		// 返回
		return param;
	}
	
	/**
	 * 更新通话记录
	 * @param param
	 */
	public SalerRecord save(SalerRecord param) {
		SalerRecord salerRecord = this.queryById(param.getId());
		Date date = new Date();
		Long testTimeLength =((date.getTime() - salerRecord.getCreateTime().getTime())/1000);
		//测试自动保存的时长、挂机时间
		param.setTestTimeLength(StrUtils.toLong(testTimeLength));
		param.setTestUpdateTime(date);
		/** 时长处理方案开始 **/
		if ((param.getTimeLength().intValue() - param.getTestTimeLength().intValue() > 10)) {
			param.setTimeLength(param.getTestTimeLength());
			param.setRemark("页面时长小于自动计算时长，赋值为自动时长："+param.getTimeLength()+"/"+param.getTestTimeLength());
		}
		/** 时长处理方案结束 **/
		param.setEnable(true);
		super.update(param);
		//回写资源表首次通话时间、最后通话时间、最后通话内容
		Resource resource = resourceService.queryById(param.getCustomerId());
		if (StrUtils.isNullOrBlank(resource.getFirstCallTime())) {
			resource.setFirstCallTime(date);
		}
		resource.setLastCallTime(date);
		resource.setLastCallRecord(param.getServerRecord());
		resourceService.update(resource);
		/** 同时更新正常开发轨迹的通话时长 **/
		WorkTrailNormal workTrailNormal = workTrailNormalService.queryBySalerRecordId(param.getId());
		if (null != workTrailNormal) {
			workTrailNormal.setTimeLength(StrUtils.toLong(param.getTimeLength()));
			workTrailNormalService.update(workTrailNormal);
		}
		return param;
	}

	/**
	 * 查询所有通话记录（默认查询自己的）
	 * @param params
	 * @param currUser
	 * @return
	 */
	public Page queryRecord(Map<String, Object> params, Long currUser) {
		//设置权限（默认查询自己的）
		if (StrUtils.isNullOrBlank(params.get("serverName")) && StrUtils.isNullOrBlank(params.get("managerId")) && StrUtils.isNullOrBlank(params.get("customerId"))) {
			Long userGroup = super.getUserById(currUser).getUserGroup();
			if (StrUtils.isNotNullOrBlank(userGroup)) {
				if (userGroup == Constant.USER_GROUP_YWY) {
					params.put("serverId", currUser);
				} else if (userGroup == Constant.USER_GROUP_JL) {
					params.put("managerId", currUser);
				} else if (userGroup == Constant.ADMIN) {
					
				} else {
					
				}
			} else {
				OperationLogTool.operationLog(Constant.ERROR_LOG, "用户ID:" + currUser + "，查询通话记录时，没有获取其用户组信息");
			}
		}
		//手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		if (params.containsKey("customerMobile") && StrUtils.isNotNullOrBlank(params.get("customerMobile"))) {
			String mobile = params.get("customerMobile").toString();
			if (mobile.indexOf("****") >= 0) {
				params.put("fuzzyMobile", mobile);
				params.remove("customerMobile");
			} else {
				params.put("customerMobile", ComplexMD5Util.MD5Encode(StrUtils.toString(params.get("customerMobile"))));
			}
		}
		Page<Long> page = getPage(params);
		page.setRecords(((SalerRecordMapper)mapper).selectIdPage(page, params));
		Page<SalerRecord> recordPage = super.getPage(page);
		return recordPage;
	}

	/**
	 * 所有通话记录
	 * @param customerId
	 * @return
	 */
	public List<Long> queryByCustomerId(Long customerId) {
		return ((SalerRecordMapper)mapper).queryByCustomerId(customerId);
	}
	
	/**
	 * 所有通话记录
	 * @param param
	 * @return
	 */
	public List<SalerRecord> queryRecordByCustomerId(Map<String, Object> param) {
		Long customerId = StrUtils.toLong(param.get("customerId"));
		if (null != customerId && customerId > 0) {
			List<Long> ids = queryByCustomerId(customerId);
			return super.getList(ids);
		} else {
			return null;
		}
	}

	/**
	 * 所有接通的通话记录
	 * @param customerId
	 * @return
	 */
	public List<Long> queryRealByCustomerId(Long customerId) {
		return ((SalerRecordMapper)mapper).queryRealByCustomerId(customerId);
	}
	
	public List<Long> queryRealByCustomerId1(Long customerId) {
		return ((SalerRecordMapper)mapper).queryRealByCustomerId1(customerId);
	}
	
	/**
	 * 所有接通的通话记录
	 * @param param
	 * @return
	 */
	public List<SalerRecord> queryRealRecordByCustomerId(Map<String, Object> param) {
		Long customerId = StrUtils.toLong(param.get("customerId"));
		if (null != customerId && customerId > 0) {
			List<Long> ids = queryRealByCustomerId(customerId);
			return super.getList(ids);
		} else {
			return null;
		}
	}
	
	/**
	 * 所有2018接通的通话记录
	 * @param param
	 * @return
	 */
	public List<SalerRecord> queryNewYearRealByCustomerId(Map<String, Object> param) {
		Long customerId = StrUtils.toLong(param.get("customerId"));
		if (null != customerId && customerId > 0) {
			List<Long> ids = ((SalerRecordMapper)mapper).queryNewYearRealByCustomerId(customerId);
			return super.getList(ids);
		} else {
			return null;
		}
	}

	/**
	 * 同步市场部通话记录和PBX通话记录
	 * 
	 * 1.查询市场部通话记录有录音文件，无通话状态或无通话时长的记录
	 * 2.通过录音文件查询PBX通话记录，获取通话时长
	 */
	public void syncSalerRecord() {
		List<Long> ids = ((SalerRecordMapper)mapper).queryOnlyCallFile();
		if (null != ids && ids.size() > 0) {
			System.out.println("需匹配市场通话记录："+ids.size()+"条");
			SalerRecord salerRecord = null;
			Long recordLength = null;
			for (Long id : ids) {
				salerRecord = this.queryById(id);
				String callFile = null;
				if (StrUtils.isNotNullOrBlank(salerRecord.getCallFile()) && salerRecord.getCallFile().length() > 8) {
					salerRecord.setSyncNum(salerRecord.getSyncNum()+1);
					callFile = salerRecord.getCallFile().substring(8);
					recordLength = recordlogsService.queryLengthByCallFile(callFile);
					if (null != recordLength && recordLength > 0) {
						salerRecord.setTimeLength(recordLength);
						salerRecord.setType("1");
						if (salerRecord.getSyncNum() >= 3
								&& recordLength.longValue() == salerRecord.getTimeLength().longValue()) {
							salerRecord.setEnable(false);
						}
						/** 同时校正正常开发轨迹的通话时长 **/
						WorkTrailNormal workTrailNormal = workTrailNormalService.queryBySalerRecordId(id);
						if (null != workTrailNormal) {
							workTrailNormal.setTimeLength(recordLength);
							workTrailNormalService.update(workTrailNormal);
						}
						OperationLogTool.operationLog(Constant.CALL_LOG, "同步一条通话记录，ID："+salerRecord.getId()+"，时长："+recordLength);
					}
					if (salerRecord.getSyncNum() > 120) {
						OperationLogTool.operationLog(Constant.CALL_LOG, "###警告：120次尝试已满，同步失败一条通话记录，ID："+salerRecord.getId());
						salerRecord.setEnable(false);
					}
				} else {
					OperationLogTool.operationLog(Constant.CALL_LOG, "###警告：录音文件名异常，同步失败一条通话记录，ID："+salerRecord.getId());
					salerRecord.setEnable(false);
				}
				this.update(salerRecord);
				salerRecord = null;
				recordLength = null;
			}
		}
	}

}