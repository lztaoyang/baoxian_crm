package com.lazhu.crm.resourceleave.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.support.Assert;
import com.lazhu.core.util.CacheUtil;
import com.lazhu.core.util.ChineseCalendar;
import com.lazhu.crm.Constant;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.customerlog.entity.CustomerLog;
import com.lazhu.crm.resource.entity.Resource;
import com.lazhu.crm.resource.mapper.ResourceMapper;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.crm.resourceleave.entity.ResourceLeave;
import com.lazhu.crm.resourceleave.mapper.ResourceLeaveMapper;
import com.lazhu.ecp.utils.ConstantUtils;
import com.lazhu.ecp.utils.DateUtils;
import com.lazhu.ecp.utils.DingUtil;
import com.lazhu.ecp.utils.JsonUtil;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.model.SysUser;
import com.lazhu.t.resourceallot.service.ResourceAllotService;

/**
 * <p>
 * 资源闲置表 服务实现类
 * </p>
 *
 * @author hz
 * @since 2018-04-13
 */
@Service
@CacheConfig(cacheNames = "resourceLeave")
public class ResourceLeaveService extends BusinessBaseService<ResourceLeave> {

	@Autowired//闲置资源
	ResourceLeaveMapper resourceLeaveMapper;
	
	@Autowired//开发资源
	ResourceMapper resourceMapper;//必须insert
	
	@Autowired//闲置资源
	ResourceService resourceService;
	
	@Autowired//分配表
	ResourceAllotService resourceAllotService;
	
	public Page resourceLeaveList(Map<String, Object> params, Long userId) {
		//共享池资源无归属限制
		//手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		//下属查询
		if (StrUtils.isNotNullOrBlank(params.get("subordinate"))) {
			super.setBusiness(params, StrUtils.toLong(params.get("subordinate")));
		}
		//删除下属key
		params.remove("subordinate");
		Page<Long> page = getPage(params);
		page.setRecords(resourceLeaveMapper.selectIdPage(page, params));
		return super.getPage(page);
	}
	
	
	public Integer deleteResource(String md5Mobile , List<CustomerLog> customerLogList) {
		List<Long> ids = resourceLeaveMapper.queryIdByMd5Mobile(md5Mobile);
		Integer flowId = 0;
		if (null != ids && ids.size() > 0) {
			for (Long id : ids) {
				ResourceLeave resourceLeave = queryById(id);
				if (resourceLeave.getFlowId() == Constant.FLOW_GXC || resourceLeave.getFlowId() == Constant.FLOW_LJ) {
					/** 
					 * 如果共享池中有同一MD5，则回收共享池资源并生成记录
					 ***/
					//2019-1-15 16:03:59
					CustomerLog customerLog = new CustomerLog();
					customerLog.setCustomerId(resourceLeave.getId());
					customerLog.setCustomerName(resourceLeave.getName());
					customerLog.setOldUser(resourceLeave.getSalerId());
					customerLog.setOldFlow(resourceLeave.getFlowId());
					customerLog.setType("自动回收共享池");
					customerLog.setCreateTime(new Date());
					customerLog.setUpdateTime(new Date());
					customerLogList.add(customerLog);
					
					this.delete(id);
					String flowString = Constant.getFlowString(resourceLeave.getFlowId());
					System.out.println("删除"+flowString+"："+resourceLeave.getId()+"，"+resourceLeave.getName()+"，"+resourceLeave.getFuzzyMobile()+"，"+resourceLeave.getMd5Mobile());
					OperationLogTool.operationLog(Constant.RESOURCE_DELETE,"删除"+flowString+"："+resourceLeave.getId()+"，"+resourceLeave.getName()+"，"+resourceLeave.getFuzzyMobile()+"，"+resourceLeave.getMd5Mobile());
				} else {
					return resourceLeave.getFlowId();
				}
			}
		}
		return flowId;
	}

	/**
	 * 资源调配
	 * @param ids		List<资源ID>
	 * @param userId	用户ID
	 * @param currUser	操作者ID
	 * @param ip		操作值IP
	 * @return
	 */
	@Transactional
	public synchronized List<ResourceLeave> allot(List<Long> ids, Long userId, Long currUser, String ip) {
		//1.当前操作值，权限
		Long curr = super.getUserById(currUser).getUserGroup();
		if (curr == Constant.USER_GROUP_YWY) {
			Assert.notNull(null, Constant.GROUPISNULL);
		}
		
		//2.查询所有实体
		List<ResourceLeave> resourceLeaveList = resourceLeaveMapper.selectBatchIds(ids);
		for (ResourceLeave resourceLeave : resourceLeaveList) {
			Long oldUserId = null;
			if (resourceLeave.getSalerId() != null && resourceLeave.getSalerId() > 0) {
				oldUserId = resourceLeave.getSalerId();
			} else if (resourceLeave.getManagerId() != null && resourceLeave.getManagerId() > 0) {
				oldUserId = resourceLeave.getManagerId();
			} else if (resourceLeave.getDirectorId() != null && resourceLeave.getDirectorId() > 0) {
				oldUserId = resourceLeave.getDirectorId();
			}
			String type = "资源调配";
			if (resourceLeave.getFlowId()==Constant.FLOW_GXC) {
				type = "共享池资源调配";
			} else if (resourceLeave.getFlowId()==Constant.FLOW_BLACK) {
				type = "黑名单资源调配";
			} else if (resourceLeave.getFlowId()==Constant.FLOW_LJ) {
				type = "垃圾池资源调配";
			}
			//3.保存分配日志
			CustomerLog log = new CustomerLog();
			log.setCustomerId(resourceLeave.getId());
			log.setCustomerName(resourceLeave.getName());
			log.setOldUser(oldUserId);
			log.setNewUser(userId);
			log.setType(type);
			log.setIp(ip);
			log.setUpdateBy(currUser);
			super.saveCustomerLog(log);
			
			Resource resource = new Resource();
			//拷贝闲置表数据到资源表实体
			setResourceLeaveToResource(resource,resourceLeave);
			//resource.setFlowId(Constant.FLOW_YX);	//开发流程初始化（已添加微信）
			//resource.setFlowId(Constant.FLOW_KF);	//开发流程初始化（推广资源）
			resource.setFlowId(Constant.FLOW_CQ);	//开发流程初始化（抽取资源）
			resource.setCreateTime(new Date());		//入库时间初始化
			//long retainDay3 = StrUtils.toLong(ConstantUtils.getLoadConstant("resource.retain.day3"));
			//long retainDay2 = StrUtils.toLong(ConstantUtils.getLoadConstant("resource.retain.day2"));//推广资源有效期
			Integer retainDay1 = StrUtils.toInteger(ConstantUtils.getLoadConstant("resource.retain.day1"));//抽取资源有效期
			//到期时间初始化
			resource.setRetainTime(ChineseCalendar.exactWeekdays(resource.getCreateTime(),retainDay1));
			//抽取次数加1
			resource.setExtractNum(resource.getExtractNum() + 1);
			//4.根据被分配者的用户组，设置市场部信息
			resourceService.setAllBusiness(resource,userId);
			//插入开发资源
			resourceMapper.insert(resource);
			//删除闲置资源
			this.delete(resourceLeave.getId());
			//更新新归属资源会员数
			//businessNumService.updateDevelopDataNumByBusinessIds(0,resource.getFlowId(),1,businessNumService.ListAllBusinessUserId(resource),false);
			//回写分配表资源开发流程
			resourceAllotService.updateFlowId(resource.getId(),resource.getFlowId());
		}
		return resourceLeaveList;
	}

	/**
	 * 拷贝闲置表数据到资源表实体
	 * @param resource
	 * @param resourceLeave
	 */
	private void setResourceLeaveToResource(Resource resource,ResourceLeave resourceLeave) {
		resource.setId(resourceLeave.getId());
		resource.setFromInfo(resourceLeave.getFromInfo());
		resource.setAllotId(resourceLeave.getAllotId());
		resource.setFlowId(resourceLeave.getFlowId());
		resource.setOldFlowId(resourceLeave.getOldFlowId());
		resource.setName(resourceLeave.getName());
		resource.setEnterTime(resourceLeave.getEnterTime());
		resource.setWechatNo(resourceLeave.getWechatNo());
		resource.setQq(resourceLeave.getQq());
		resource.setMobile(resourceLeave.getMobile());
		resource.setMd5Mobile(resourceLeave.getMd5Mobile());
		resource.setFuzzyMobile(resourceLeave.getFuzzyMobile());
		resource.setPhone(resourceLeave.getPhone());
		resource.setBirthday(resourceLeave.getBirthday());
		resource.setAge(resourceLeave.getAge());
		resource.setSex(resourceLeave.getSex());
		resource.setDuty(resourceLeave.getDuty());
		resource.setProvince(resourceLeave.getProvince());
		resource.setCity(resourceLeave.getCity());
		resource.setSalerId(resourceLeave.getSalerId());
		resource.setSalerName(resourceLeave.getSalerName());
		resource.setManagerId(resourceLeave.getManagerId());
		resource.setManagerName(resourceLeave.getManagerName());
		resource.setDirectorId(resourceLeave.getDirectorId());
		resource.setDirectorName(resourceLeave.getDirectorName());
		resource.setMinisterId(resourceLeave.getMinisterId());
		resource.setMinisterName(resourceLeave.getMinisterName());
		resource.setDemand(resourceLeave.getDemand());
		resource.setPlanCode(resourceLeave.getPlanCode());
		resource.setExtractTime(resourceLeave.getExtractTime());
		resource.setExtractNum(resourceLeave.getExtractNum());
		resource.setRetainTime(resourceLeave.getRetainTime());
		resource.setLossTime(resourceLeave.getLossTime());
		resource.setIsLose(resourceLeave.getIsLose());
		resource.setLoseNum(resourceLeave.getLoseNum());
		resource.setFirstCallTime(resourceLeave.getFirstCallTime());
		resource.setLastCallTime(resourceLeave.getLastCallTime());
		resource.setLastCallRecord(resourceLeave.getLastCallRecord());
		resource.setIsTalk(resourceLeave.getIsTalk());
		resource.setTalkTime(resourceLeave.getTalkTime());
		resource.setBlackRemark(resourceLeave.getBlackRemark());
		
		resource.setRemark(resourceLeave.getRemark());
		resource.setEnable(resourceLeave.getEnable());
		resource.setCreateBy(resourceLeave.getCreateBy());
		resource.setCreateTime(resourceLeave.getCreateTime());
		resource.setUpdateBy(resourceLeave.getUpdateBy());
		resource.setUpdateTime(resourceLeave.getUpdateTime());

		resource.setKeywords(resourceLeave.getKeywords());
	}

	/**
	 * 模拟推广分配
	 * 拷贝闲置表数据到资源表实体
	 * @param resource
	 * @param resourceLeave
	 */
	private void setResourceLeaveToResourceClean (Resource resource,ResourceLeave resourceLeave) {
		resource.setId(resourceLeave.getId());
		resource.setFromInfo(resourceLeave.getFromInfo());
		resource.setAllotId(resourceLeave.getAllotId());
		resource.setFlowId(resourceLeave.getFlowId());
		resource.setName(resourceLeave.getName());
		resource.setEnterTime(resourceLeave.getEnterTime());
		resource.setMobile(resourceLeave.getMobile());
		resource.setMd5Mobile(resourceLeave.getMd5Mobile());
		resource.setFuzzyMobile(resourceLeave.getFuzzyMobile());
		resource.setProvince(resourceLeave.getProvince());
		resource.setCity(resourceLeave.getCity());
		resource.setDemand(resourceLeave.getDemand());
		resource.setPlanCode(resourceLeave.getPlanCode());

		resource.setKeywords(resourceLeave.getKeywords());
	}

	/**
	 * 定时每天回收共享池资源到垃圾池（备注包含：空号、停机）
	 */
	public void syncRecycleRubbish() {
		Integer num = resourceLeaveMapper.syncRecycleRubbish();
		//清理缓存
		CacheUtil.getCache().delAll("*:resourceLeave:*");
		OperationLogTool.operationLog(Constant.AUTO_LOG,"回收共享池资源资源（备注包含：空号、停机）到垃圾池："+(num==null?0:num)+"条");
	}


	/**
	 * 查询剩余可抽取资源数
	 * @return
	 */
	public Integer remainExtractNum() {
		return resourceLeaveMapper.remainExtractNum();
	}

	/**
	 * 今日丢弃共享池资源数
	 * @param userId
	 * @return
	 */
	public Integer queryToDayLoseResourceNum(Long userId) {
		return resourceLeaveMapper.queryToDayLoseResourceNum(userId);
	}


	/**
	 * 手机号精准搜索客户
	 * @param md5Mobile
	 * @return
	 */
	public ResourceLeave queryByMd5Mobile(String md5Mobile) {
		ResourceLeave resourceLeave = null;
		List<Long> ids = resourceLeaveMapper.queryIdByMd5Mobile(md5Mobile);
		if (null != ids && ids.size() > 0) {
			if (ids.size() == 1) {
				resourceLeave = super.queryById(ids.get(0));
			} else {
				OperationLogTool.operationLog(Constant.ERROR_LOG, "【闲置资源】手机号精准搜索发现重复手机号客户："+md5Mobile+"，个数："+ids.size());
			}
		} else {
			OperationLogTool.operationLog(Constant.ERROR_LOG, "【闲置资源】手机号精准搜索失败："+md5Mobile);
		}
		return resourceLeave;
	}
	
	/**
	 * 抽取共享池资源
	 * 
	 * 5条、2天前到期被抽取字数少的资源
	 * @param currUser
	 */
	public synchronized Integer getSharedPoolResource(Long currUser,int num,boolean isExtract) {
		int successNum = 0;
		if (isExtract) {
			successNum = extractResource(currUser);
		} else {
			allotSharedPoolResource(num);
		}
		return successNum;
	}
	
	/**
	 * 抽取共享池资源
	 * 
	 * 5条、2天前到期被抽取字数少的资源
	 * @param currUser
	 */
	@Transactional
	public Integer extractResource(Long currUser) {
		//查询共享池资源（20条、3天前到期被抽取字数少的资源）
		List<Long> ids = resourceLeaveMapper.queryExtractResource();
		if (null == ids || ids.size() <= 0) {
			return 0;
		}
		//2.查询所有实体
		List<ResourceLeave> resourceLeaveList = resourceLeaveMapper.selectBatchIds(ids);
		if (null == resourceLeaveList || resourceLeaveList.size() <= 0) {
			return 0;
		}
		for (ResourceLeave resourceLeave : resourceLeaveList) {
			Resource resource = new Resource();
			Integer flowId = resourceLeave.getFlowId();
			//拷贝闲置表数据到资源表实体
			setResourceLeaveToResource(resource,resourceLeave);
			//设置市场部归属
			resourceService.setAllBusiness(resource, currUser);
			resource.setFlowId(Constant.FLOW_CQ);
			//修改创建时间（为了页面排序）
			resource.setCreateTime(new Date());
			//抽取时间
			resource.setExtractTime(new Date());
			//修改资源保护期到期时间
			resourceService.setRetainTime(resource);
			//被抽取次数+1
			resource.setExtractNum(resource.getExtractNum() + 1);
			//插入开发资源
			resourceMapper.insert(resource);
			//删除闲置资源
			this.delete(resourceLeave.getId());
			//保存日志
			CustomerLog log = new CustomerLog();
			log.setCustomerId(resource.getId());
			log.setCustomerName(resource.getName());
			log.setOldFlow(flowId);
			log.setNewFlow(Constant.FLOW_CQ);
			log.setType("抽取资源");
			log.setUpdateBy(currUser);
			super.saveCustomerLog(log);
			
			//回写分配表资源开发流程
			resourceAllotService.updateFlowId(resource.getId(),resource.getFlowId());
		}
		//更新用户抽取次数（减1）
		SysUser user = super.getUserById(currUser);
		user.setExtractNum(user.getExtractNum() - 1);
		userService.update(user);
		OperationLogTool.operationLog(super.getUserById(currUser).getAccount()+"，抽取资源："+ids.size()+"条");
		return ids.size();
	}

	/**
	 * 分配共享池资源给市场部
	 * 
	 * 2天前到期被抽取字数少的资源
	 * @param spn
	 */
	@Transactional
	public int allotSharedPoolResource (int spn) {
		//查询共享池资源（2天前到期被抽取字数少的资源）（手机号资源）
		List<Long> ids = resourceLeaveMapper.queryExtractMobileResource(spn);
		if (null == ids || ids.size() <= 0) {
			return 0;
		}
		//2.查询所有实体
		List<ResourceLeave> resourceLeaveList = resourceLeaveMapper.selectBatchIds(ids);
		if (null == resourceLeaveList || resourceLeaveList.size() <= 0) {
			return 0;
		}
		Resource resource = null;
		Long userId = null;
		for (ResourceLeave resourceLeave : resourceLeaveList) {
			// 2.查询分配次数最少的用户（经纪人）
			List<Long> userIds = userService.querySalerAllotResourceMin();
			if (null != userIds && userIds.size() > 0) {
				userId = userIds.get(0);
				resource = new Resource();
				Integer flowId = resourceLeave.getFlowId();
				//拷贝闲置表数据到资源表实体
				setResourceLeaveToResourceClean(resource,resourceLeave);
				//设置市场部归属
				resourceService.setAllBusiness(resource, userId);
				resource.setFlowId(Constant.FLOW_KF);
				//修改创建时间（为了页面排序）
				resource.setCreateTime(new Date());
				resource.setUpdateTime(new Date());
				//修改资源保护期到期时间
				resourceService.setRetainTime(resource);
				//添加分配类型标识
				resource.setIsSharedpoolAllot(1);
				//插入开发资源
				resourceMapper.insert(resource);
				//删除闲置资源
				this.delete(resourceLeave.getId());
				//保存日志
				CustomerLog log = new CustomerLog();
				log.setCustomerId(resource.getId());
				log.setCustomerName(resource.getName());
				log.setOldFlow(flowId);
				log.setNewFlow(Constant.FLOW_KF);
				log.setType("分配共享池资源");
				log.setUpdateBy(resource.getUpdateBy());
				super.saveCustomerLog(log);
				
				//回写分配表资源开发流程
				resourceAllotService.updateFlowId(resource.getId(),resource.getFlowId());
				
				// 7.【可分配时】更新用户的分配资源数
				SysUser user = super.getUserById(userId);
				user.setAllotResourceNum(user.getAllotResourceNum() + 1);
				userService.update(user);
				// 8.【可分配时】消息通知
				try {
					String mobile = null;
					// 1.解密推广资源RSA手机号
					mobile = super.getMobileByMd5Mobile(resource.getMd5Mobile());
					sendMessage(resource, user.getDingId(), mobile);
					sendMessageManager(user.getAccount(),user.getDingId(), mobile);// 经理
				} catch (Exception e) {
					System.out.println("钉钉消息，推广分配资源消息通知，发送失败!");
				}
			} else {
				System.out.println("穿插分配共享池资源时，没有可分配用户");
			}
		}
		
		return ids.size();
	}
	
	/**
	 * 分配推广资源通知经纪人
	 * 
	 * @param resource
	 * @param userId
	 * @param mobile
	 */
	private void sendMessage(Resource resource, String dingId, String mobile) {
		StringBuffer content = new StringBuffer("给你分配了一条推广资源：");
		content.append(mobile + "，");
		if (StrUtils.isNotNullOrBlank(resource.getDemand())) {
			String content2 = "";
			if (StrUtils.isNotNullOrBlank(resource.getDemand())) {
				try {
					List<Map<String, Object>> list = JsonUtil.parseJSON2List(resource.getDemand());
					if (null != list && list.size() > 0) {
						for (Map<String, Object> map2 : list) {
							content2 += map2.get("key") + "：" + map2.get("value") + "；";
						}
					}
				} catch (Exception e) {
					OperationLogTool.operationLog(Constant.ERROR_LOG,
							"分配共享池资源：" + resource.getId() + "，解析json内容发送错误！");
				}
			}
			content.append(content2 + "，");
		}
		if (StrUtils.isNotNullOrBlank(resource.getPlanCode())) {
			content.append("专题：" + resource.getPlanCode() + "，");
		}
		content.append("请立即拨打，并且完善客户信息【"
				+ DateUtils.DateToStr(new Date(), DateUtils.TIME_FORMAT_CHINESE) + "】");
		//发送钉钉消息
		DingUtil.sendMsg(content.toString(), dingId);
	}

	/**
	 * 分配推广资源通知经纪人的经理
	 * 
	 * @param userId
	 * @param mobile
	 */
	private void sendMessageManager(String account,String dingId, String mobile) {
		// 经理
		StringBuffer content = new StringBuffer("给你的组员：");
		content.append(account + "，分配了一条来源渠道推广资源：" + mobile + "，请立即拨打【"
				+ DateUtils.DateToStr(new Date(), DateUtils.TIME_FORMAT_CHINESE) + "】");
		//发送钉钉消息
		DingUtil.sendMsg(content.toString(), dingId);
		
	}

}
