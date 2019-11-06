package com.lazhu.crm.serverrecordmobile.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.aladdinpbx.recordlogs.service.RecordlogsService;
import com.lazhu.core.util.ComplexMD5Util;
import com.lazhu.crm.Constant;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.mobilelastlog.entity.MobileLastLog;
import com.lazhu.crm.mobilelastlog.service.MobileLastLogService;
import com.lazhu.crm.resource.entity.Resource;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.crm.serverrecordmobile.entity.ServerRecordMobile;
import com.lazhu.crm.serverrecordmobile.mapper.ServerRecordMobileMapper;
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
public class ServerRecordMobileService extends BusinessBaseService<ServerRecordMobile> {
	
	@Autowired
	private MobileLastLogService mobileLastLogService;

	@Autowired
	private RecordlogsService recordlogsService;
	
	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private CustomerService customerService;
	
	/**
	 * 保存通话记录（目前只有新增）
	 * @param param
	 * @param currUser
	 */
	public ServerRecordMobile add(ServerRecordMobile param,Long currUser) {
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
		//保存最近拨打记录
		MobileLastLog mobileLastLog = new MobileLastLog();
		mobileLastLog.setMd5Mobile(param.getCustomerMobile());
		mobileLastLog.setAgentNo(user.getAgentNo());
		int type = super.getUserGroupType(user.getUserGroup());
		mobileLastLog.setType(type);
		mobileLastLog.setUpdateBy(currUser);
		mobileLastLogService.update(mobileLastLog);
		return super.update(param);
	}

	/**
	 * 查询所有通话记录（默认查询自己的）
	 * @param params
	 * @param currUser
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Page queryRecord(Map<String, Object> params, Long currUser) {
		//设置权限（默认查询自己的）
		if (StrUtils.isNullOrBlank(params.get("serverName")) && StrUtils.isNullOrBlank(params.get("managerId")) && StrUtils.isNullOrBlank(params.get("customerId"))) {
			Long userGroup = super.getUserById(currUser).getUserGroup();
			if (StrUtils.isNotNullOrBlank(userGroup)) {
				if (userGroup == Constant.USER_GROUP_JB) {
					params.put("serverId", currUser);
				} else if (userGroup == Constant.USER_GROUP_JBJL) {
					params.put("managerId", currUser);
				}
			} else {
				OperationLogTool.operationLog(Constant.ERROR_LOG, "用户ID:" + currUser + "，查询通话记录时，没有获取其用户组信息");
			}
		}
		Page<Long> page = getPage(params);
		page.setRecords(((ServerRecordMobileMapper)mapper).selectIdPage(page, params));
		Page<ServerRecordMobile> recordPage = super.getPage(page);
		if (recordPage != null && recordPage.getRecords() != null && recordPage.getRecords().size() > 0) {
			List<ServerRecordMobile> newList = new ArrayList<ServerRecordMobile>();
			for (ServerRecordMobile item : recordPage.getRecords()) {
				//隐藏客户电话号码中间部分
				item.setCustomerMobile(StrUtils.hideMobile(item.getCustomerMobile()));
				newList.add(item);
			}
			//暂时不需要重新排序
			//recordPage.setRecords(newList);
		}
		return recordPage;
	}

	public List<Long> queryByCustomerId(Long customerId) {
		return ((ServerRecordMobileMapper)mapper).queryByCustomerId(customerId);
	}

	public List<ServerRecordMobile> queryRecordByCustomerId(Map<String, Object> param) {
		Long customerId = StrUtils.toLong(param.get("customerId"));
		if (null != customerId && customerId > 0) {
			List<Long> ids = queryByCustomerId(customerId);
			return super.getList(ids);
		} else {
			return null;
		}
	}

	/**
	 * 通过录音文件名查询服务备注
	 * @param callFile
	 * @return
	 */
	public String queryServerRecordByCallFile(String callFile) {
		List<Long> ids = ((ServerRecordMobileMapper)mapper).queryByCallFile(callFile);
		if (null != ids && ids.size() > 0) {
			if (ids.size() == 1) {
				return this.queryById(ids.get(0)).getServerRecord();
			} else {
				OperationLogTool.operationLog(Constant.ERROR_LOG, "客服通话记录表存在重复录音文件名：" + callFile);
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * 通过录音文件名查询ID
	 * @param callFile
	 * @return
	 */
	public List<Long> queryByCallFile(String callFile) {
		return ((ServerRecordMobileMapper)mapper).queryByCallFile(callFile);
	}
	
	/**
	 * 同步服务部通话记录和PBX通话记录
	 * 1.查询市场部通话记录有录音文件，无通话状态或无通话时长的记录
	 * 2.通过录音文件查询PBX通话记录，获取通话时长
	 * 
	 */
	public void syncServerRecordMobile() {
		List<Long> ids = ((ServerRecordMobileMapper)mapper).queryOnlyCallFile();
		if (null != ids && ids.size() > 0) {
			System.out.println("需匹配客服通话记录："+ids.size()+"条");
			ServerRecordMobile serverRecordMobile = null;
			Long recordLength = null;
			Integer num = 0;
			for (Long id : ids) {
				
				serverRecordMobile = this.queryById(id);
				String callFile = null;
				if (StrUtils.isNotNullOrBlank(serverRecordMobile.getCallFile()) && serverRecordMobile.getCallFile().length() > 8) {
					serverRecordMobile.setSyncNum(serverRecordMobile.getSyncNum()+1);
					callFile = serverRecordMobile.getCallFile().substring(8);
					recordLength = recordlogsService.queryLengthByCallFile(callFile);
					if (null != recordLength && recordLength > 0) {
						num = num + 1;
						serverRecordMobile.setTimeLength(recordLength);
						serverRecordMobile.setType("1");
						//System.out.println("成功同步"+num+"条客服录音记录！！");
						if (serverRecordMobile.getSyncNum() >= 3
								&& recordLength.longValue() == serverRecordMobile.getTimeLength().longValue()) {
							serverRecordMobile.setEnable(false);
						}
						OperationLogTool.operationLog(Constant.CALL_LOG, "同步一条通话记录，ID："+serverRecordMobile.getId()+"，时长："+recordLength);
					}
					if (serverRecordMobile.getSyncNum() > 120) {
						OperationLogTool.operationLog(Constant.CALL_LOG, "###警告：120次尝试已满，同步失败一条通话记录，ID："+serverRecordMobile.getId());
						serverRecordMobile.setEnable(false);
					}
				} else {
					OperationLogTool.operationLog(Constant.CALL_LOG, "###警告：录音文件名异常，同步失败一条通话记录，ID："+serverRecordMobile.getId());
					serverRecordMobile.setEnable(false);
				}
				this.update(serverRecordMobile);
				serverRecordMobile = null;
				recordLength = null;
			}
		}
	}
	
	/**
	 * 更新通话记录
	 * @param param
	 */
	public ServerRecordMobile save(ServerRecordMobile param) {
		ServerRecordMobile serverRecordMobile = this.queryById(param.getId());
		Date date = new Date();
		Long testTimeLength =((date.getTime() - serverRecordMobile.getCreateTime().getTime())/1000);
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
		return param;
	}

	public void toCustomer() {
		List<Long> cusIds = customerService.queryAll();
		for (Long cusId : cusIds) {
			List<Long> recordIds = this.queryServerByCustomerId(cusId);
			if (StrUtils.isNotNullOrBlank(recordIds) && recordIds.size() > 0) {
				ServerRecordMobile mobile = this.queryById(recordIds.get(0));
				if (StrUtils.isNotNullOrBlank(mobile)) {
					Customer cus = customerService.queryById(cusId);
					cus.setLastCallTime(mobile.getCreateTime());
					cus.setLastCallRecord(mobile.getServerRecord());
					customerService.update(cus);
				}
			}
		}
	}
	
	public List<Long> queryServerByCustomerId(Long customerId) {
		return ((ServerRecordMobileMapper)mapper).queryServerByCustomerId(customerId);
	}
}
