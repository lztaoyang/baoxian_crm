package com.lazhu.t.resourceallot.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.dingtalk.chatbot.message.MarkdownMessage;
import com.dingtalk.chatbot.message.TextMessage;
import com.lazhu.core.support.Assert;
import com.lazhu.core.util.CacheUtil;
import com.lazhu.core.util.DateUtil;
import com.lazhu.core.util.InstanceUtil;
import com.lazhu.crm.Constant;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.customerlog.entity.CustomerLog;
import com.lazhu.crm.mobile.entity.Mobile;
import com.lazhu.crm.resource.entity.Resource;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.crm.resourceleave.entity.ResourceLeave;
import com.lazhu.crm.resourceleave.service.ResourceLeaveService;
import com.lazhu.crm.salerrecord.service.SalerRecordService;
import com.lazhu.ecp.utils.AliyunCoder;
import com.lazhu.ecp.utils.DateUtils;
import com.lazhu.ecp.utils.DingUtil;
import com.lazhu.ecp.utils.JsonUtil;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.model.Properties;
import com.lazhu.sys.model.SysUser;
import com.lazhu.sys.service.PropertiesService;
import com.lazhu.sys.service.SysDicService;
import com.lazhu.sys.service.SysUserService;
import com.lazhu.t.allotplanning.service.AllotPlanningService;
import com.lazhu.t.resource.entity.TResource;
import com.lazhu.t.resource.service.TResourceService;
import com.lazhu.t.resourceallot.entity.ResourceAllot;
import com.lazhu.t.resourceallot.mapper.ResourceAllotMapper;

/**
 * <p>
 * 推广资源分配表 服务实现类
 * </p>
 * 
 * @author hz
 * @since 2017-10-27
 */
/**
 * @author Administrator
 *
 */
@Service
@CacheConfig(cacheNames = "resourceAllot")
public class ResourceAllotService extends BusinessBaseService<ResourceAllot> {

	@Autowired
	private ResourceAllotMapper resourceAllotMapper;

	@Autowired
	// 推广资源
	private TResourceService tResourceService;

	@Autowired
	// 会员
	private CustomerService customerService;

	@Autowired
	// 开发资源
	private ResourceService resourceService;
	
	@Autowired
	// 闲置资源
	private ResourceLeaveService resourceLeaveService;

	@Autowired
	// 市场部通话记录
	private SalerRecordService salerRecordService;

	@Autowired
	// 字典
	private SysDicService dicService;

	@Autowired
	// 配置表
	private PropertiesService propertiesService;
	
	@Autowired
	// 推广计划表
	private AllotPlanningService allotPlanningService;
	
	@Autowired
	private SysUserService userService;
	
	/**
	 * 同步推广资源表数据到本地推广资源分配表 1：CRM资源表新增 推广资源更新日期（sourceUpdateTime）
	 * 2：同步之前先查询CRM表中最大的推广资源更新日期 3：查询推广资源表更新日期大于CRM最大推广资源更新日期的资源（10个一批）
	 * 4：新增/更新CRM资源表
	 * 
	 * @param num
	 *            查询条数
	 */
	public int newSyncTResource() {
		Long maxId = resourceAllotMapper.queryMaxTId();
		List<Long> TResourceNewIds = tResourceService.queryNewTResouce(maxId);
		if (null != TResourceNewIds && TResourceNewIds.size() > 0) {
			for (Long id : TResourceNewIds) {
				TResource tResource = tResourceService.queryById(id);
				ResourceAllot resourceAllot = new ResourceAllot();
				setTResourceToResourceAllot(resourceAllot, tResource);
				try {
					update(resourceAllot);
				} catch (Exception e) {
					e.printStackTrace();
					TextMessage message = new TextMessage("【数据同步异常】: 推广数据同步发生异常情况！异常数据ID：" + id);
					DingUtil.sendGroupMesg("dd.group.tg", message);
				}
			}
			System.out.println(DateUtil.format(new Date(), DateUtil.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS) + "，同步资源数："
					+ TResourceNewIds.size());
		}
		return TResourceNewIds == null ? 0 : TResourceNewIds.size();
	}

	/**
	 * 插入推广资源的信息到推广资源分配实体
	 * 
	 * @param resourceAllot
	 *            推广资源分配表
	 * @param tResource
	 *            推广资源表
	 * @param type
	 * @param ip
	 * @param currUser
	 * @param userId
	 */
	public void setTResourceToResourceAllot(ResourceAllot resourceAllot, TResource tResource) {

		resourceAllot.setFuzzyMobile(tResource.getFuzzyMobile());
		resourceAllot.setMd5Mobile(tResource.getMd5Mobile());
		resourceAllot.setSmsCheck(tResource.getSmsCheck());
		resourceAllot.setSessionId(tResource.getSessionId());
		resourceAllot.setPhoneStatus(tResource.getPhoneStatus());
		resourceAllot.setBizId(tResource.getBizId());
		resourceAllot.setSecretMobile(tResource.getSecretMobile());
		resourceAllot.setSmsErrCode(tResource.getSmsErrCode());

		resourceAllot.settId(tResource.getId());
		//
		if (StrUtils.isNotNullOrBlank(tResource.getName()) && tResource.getName().length() > 10) {
			resourceAllot.setName(tResource.getName().substring(0, 10));
		} else {
			resourceAllot.setName(tResource.getName());
		}
		resourceAllot.setContent(tResource.getContent());
		resourceAllot.setPlanName(tResource.getPlanName());
		resourceAllot.setPlanCode(tResource.getPlanCode());
		resourceAllot.setSourceUrl(tResource.getSourceUrl());
		resourceAllot.setRefererUrl(tResource.getRefererUrl());
		resourceAllot.setUserAgent(tResource.getUserAgent());
		resourceAllot.setExecuter(tResource.getExecuter());
		resourceAllot.setCreaterTime(tResource.getCreaterTime());
		resourceAllot.setChannel(tResource.getChannel());
		resourceAllot.setIp(tResource.getIp());
		resourceAllot.setSmsCheck(tResource.getSmsCheck());
		resourceAllot.setCreateTime(tResource.getCreateTime());
		resourceAllot.setSourceUpdateTime(tResource.getUpdateTime());
		
		resourceAllot.setKeywords(tResource.getKeywords());
		resourceAllot.setProvince(tResource.getProvince());
		resourceAllot.setCity(tResource.getCity());
	}

	/**
	 * 手动分配推广资源
	 * 
	 * 1.判断手机号是否已成交 2.判断手机号是否在资源表已存在（当前判断为是否在保护期） 3.分配结果插入分配记录表 4.【可分配时】插入到资源表
	 * 
	 * @param ids
	 * @param userId
	 * @param currUser
	 * @param ip
	 */
	@Transactional
	public Integer resourceAllot(List<Long> ids, Long userId, Long currUser, String ip) {
		int num = 0;
		for (Long id : ids) {
			// 使用同步方法来分配
			num += resourceAllotSynchronized(id, userId);
		}
		OperationLogTool.operationLog("用户ID：" + currUser + "，执行手动分配推广资源 （" + num + "）条");
		return num;
	}

	/**
	 * 手动分配和自动分配线程同步
	 * 
	 * @param id
	 *            推广资源ID
	 * @param userId
	 *            被分配人ID
	 * @param date
	 *            日期时间
	 * @return 成功分配数量（成功1，失败0）
	 */
	@Transactional
	private synchronized Integer resourceAllotSynchronized(Long id, Long userId) {
		int num = 0;
		// 查询当前推广资源实体
		ResourceAllot resourceAllot = this.queryById(id);
		if (StrUtils.isNullOrBlank(resourceAllot.getSecretMobile())) {
			setTResourceAllotInfo(resourceAllot, -1, null, null);
			this.update(resourceAllot);
			return 0;
		}
		//批量插入记录表
		List<CustomerLog> customerLogList = InstanceUtil.newArrayList();
		if (resourceAllot.getType() == 0) {
			// 只分配正常数据（过滤测试数据）
			if (null != resourceAllot.getName() 
					&& (resourceAllot.getName().indexOf("测试") >= 0
					|| resourceAllot.getName().toLowerCase().indexOf("ceshi") >= 0
					|| resourceAllot.getName().toLowerCase().indexOf("test") >= 0)) {
				// 1.插入分配状态
				setTResourceAllotInfo(resourceAllot, 6, null, null);
				this.update(resourceAllot);
			} else {
				String mobile = null;
				// 1.解密推广资源RSA手机号
				try {
					mobile = AliyunCoder.testSimpleDecode(resourceAllot.getSecretMobile());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (null == mobile || mobile.length() != 11) {
					OperationLogTool.operationLog(Constant.ERROR_LOG,
							"分配推广资源时，解密手机号错误：" + resourceAllot.getSecretMobile());
					Assert.notNull(null, "分配推广资源时，解密手机号错误：" + resourceAllot.getSecretMobile());
				}
				if (Constant.isTestMobile(mobile)) {
					setTResourceAllotInfo(resourceAllot, 6, null, null);
					this.update(resourceAllot);
				} else {
					// 2.分配结果插入分配记录表
					int type = 1;
					//查询归属地
					//addAreaByMobile(resourceAllot,mobile);
					//
					//resourceAllot.setMd5Mobile(ComplexMD5Util.MD5Encode(mobile));
					// 3.判断手机号是否已成交
					boolean isExistCustomer = customerIsExist(resourceAllot.getMd5Mobile());
					// 4.判断手机号是否在资源表已存在（当前判断为是否在保护期）
					Integer flowId = resourceService.resourceAllotIsRetain(resourceAllot.getMd5Mobile(),customerLogList);
					if (null == flowId || flowId == 0) {
						//删除闲置表资源
						flowId = resourceLeaveService.deleteResource(resourceAllot.getMd5Mobile(),customerLogList);
					}
					
					if (isExistCustomer) {
						type = 2;
						// 钉钉消息通知经理、总监，该客户又来咨询了，开发升级
						try {
							sendClubMessageToManagerAndDirector(resourceAllot, mobile);
						} catch (Exception e) {
							System.out.println("钉钉消息群，客户又来咨询了，开发升级，发送失败!");
						}
					} else if (flowId != 0) {
						System.out.println("资源再次咨询："+flowId);
						type = 3;
						// 钉钉消息通知经理、总监，该客户又来咨询了，再次咨询
						try {
							sendResourceMessageToManagerAndDirector(flowId, resourceAllot, mobile);
						} catch (Exception e) {
							System.out.println("钉钉消息群，客户又来咨询了，再次咨询，发送失败!");
						}
					}
					// 6.【可分配时】【可分配时】【可分配时】
					if (type == 1) {
						Resource resource = new Resource();
						resource.setMobile(mobile);
						// 插入推广资源分配的信息到资源实体
						setResourceAllotToResource(resourceAllot, resource, userId);
						// 6.【可分配时】插入到资源表
						resource = resourceService.update(resource);
						// 7.【可分配时】更新用户的分配资源数
						SysUser user = userService.queryById(userId);
						user.setAllotResourceNum(user.getAllotResourceNum() + 1);
						user.setControlAllotResourceNum(user.getControlAllotResourceNum() + 1);
						userService.update(user);
						// 8.【可分配时】消息通知
						try {
							sendMessage(resourceAllot, userId, mobile);
							sendMessageManager(resourceAllot,user.getAccount(), userId, mobile);// 经理
						} catch (Exception e) {
							System.out.println("钉钉消息，推广分配资源消息通知，发送失败!");
						}
						num += 1;
						// /// 生成客户信息记录表数据（资源ID，掩码手机号，MD5手机号，RSA加密手机号）
						Mobile mobileInfo = new Mobile();
						mobileInfo.setCustomerId(resource.getId());
						mobileInfo.setFuzzyMobile(resourceAllot.getFuzzyMobile());
						mobileInfo.setMd5Mobile(resourceAllot.getMd5Mobile());
						mobileInfo.setRsaMobile(resourceAllot.getSecretMobile());
						mobileService.update(mobileInfo);
						// 5.1插入分配归属、分配状态
						setTResourceAllotInfo(resourceAllot, type, userId, null);
						//回写资源ID
						resourceAllot.setResourceId(resource.getId());
						this.update(resourceAllot);
					} else {
						// 5.2插入分配状态
						setTResourceAllotInfo(resourceAllot, type, null, null);
						this.update(resourceAllot);
					}
				}
			}
		} else {
			// 更新一下（防止Redis缓存）
			this.update(resourceAllot);
			OperationLogTool.operationLog(Constant.ERROR_LOG, "手动分配和自动分配冲突，分配失败，分配ID：" + id + "，请管理员核查");
		}
		return num;
	}

	/**
	 * 设置资源分配实体分配后的信息
	 * 
	 * @param resourceAllot
	 * @param type
	 * @param userId
	 * @param currUser
	 */
	private void setTResourceAllotInfo(ResourceAllot resourceAllot, int type, Long userId, Long currUser) {
		resourceAllot.setType(type);
		resourceAllot.setUpdateBy(currUser);
		resourceAllot.setAllotTime(new Date());
		// 市场部归属
		setAllBusiness(resourceAllot, userId);
	}

	/**
	 * 添加市场部归属
	 * 
	 * @param resourceAllot
	 * @param userId
	 */
	private void setAllBusiness(ResourceAllot resourceAllot, Long userId) {
		if (null != userId) {
			SysUser manager = null;
			SysUser director = null;
			SysUser minister = null;
			// 业务员
			SysUser user = super.getUserById(userId);
			if (user.getUserGroup() == Constant.USER_GROUP_YWY) {
				resourceAllot.setSalerId(userId);
				resourceAllot.setSalerName(user.getUserName());
				// 经理
				manager = super.getUserById(user.getParentId());
				resourceAllot.setManagerId(manager.getId());
				resourceAllot.setManagerName(manager.getUserName());
				// 总监
				director = super.getUserById(manager.getParentId());
				resourceAllot.setDirectorId(director.getId());
				resourceAllot.setDirectorName(director.getUserName());
				// 总经理
				minister = super.getUserById(director.getParentId());
				resourceAllot.setMinisterId(minister.getId());
				resourceAllot.setMinisterName(minister.getUserName());
			} else if (user.getUserGroup() == Constant.USER_GROUP_JL) {
				// 经纪人
				resourceAllot.setSalerId(0L);
				resourceAllot.setSalerName("未分配");
				// 经理
				manager = super.getUserById(userId);
				resourceAllot.setManagerId(manager.getId());
				resourceAllot.setManagerName(manager.getUserName());
				// 总监
				director = super.getUserById(manager.getParentId());
				resourceAllot.setDirectorId(director.getId());
				resourceAllot.setDirectorName(director.getUserName());
				// 总经理
				minister = super.getUserById(director.getParentId());
				resourceAllot.setMinisterId(minister.getId());
				resourceAllot.setMinisterName(minister.getUserName());
			} else if (user.getUserGroup() == Constant.USER_GROUP_ZJ) {
				// 经纪人
				resourceAllot.setSalerId(0L);
				resourceAllot.setSalerName("未分配");
				// 经理
				resourceAllot.setManagerId(0L);
				resourceAllot.setManagerName("未分配");
				// 总监
				director = super.getUserById(userId);
				resourceAllot.setDirectorId(director.getId());
				resourceAllot.setDirectorName(director.getUserName());
				// 总经理
				minister = super.getUserById(director.getParentId());
				resourceAllot.setMinisterId(minister.getId());
				resourceAllot.setMinisterName(minister.getUserName());
			}
			user = null;
			manager = null;
			director = null;
			minister = null;
		}
	}

	/**
	 * 拷贝推广资源分配实体到资源实体
	 * 
	 * @param resourceAllot
	 * @param resource
	 * @param userId
	 */
	private void setResourceAllotToResource(ResourceAllot resourceAllot, Resource resource, Long userId) {
		resource.setAllotId(resourceAllot.getId());
		resource.setName(resourceAllot.getName());
		resource.setFuzzyMobile(resourceAllot.getFuzzyMobile());// 掩码手机号
		resource.setMd5Mobile(resourceAllot.getMd5Mobile());// MD5手机号
		resource.setFromInfo(resourceAllot.getChannel());// 推广渠道
		resource.setEnterTime(resourceAllot.getCreaterTime());// 推广时间
		resource.setDemand(resourceAllot.getContent());// 内容
		resource.setPlanCode(resourceAllot.getPlanCode());// 专题名
		
		resource.setKeywords(resourceAllot.getKeywords());
		resource.setProvince(resourceAllot.getProvince());
		resource.setCity(resourceAllot.getCity());
		// 设置业务，经理，总监，总经理
		resourceService.setAllBusiness(resource, userId);
		// 添加到期时间（忽略周六周日）
		resourceService.setRetainTimeTG(resource);
		resource.setRemark(resourceAllot.getRemark());
	}

	/**
	 * 判断手机号是否已成交
	 * 
	 * @param md5Mobile
	 * @return
	 */
	private Boolean customerIsExist(String md5Mobile) {
		Long clubNum = null;
		// 判断会员表里面是否已经存在
		Customer customer = new Customer();
		customer.setMd5Mobile(md5Mobile);
		// 同时验证微信，手机号
		clubNum = customerService.isExist(customer);
		if (clubNum != null && clubNum > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param resourceIds
	 * @param userIds
	 * @return
	 */
	@Transactional
	public int resourceAllot(List<Long> resourceIds,List<Long> userIds) {
		int num = 0;
		//0.关闭录入资源功能（防止出现重复手机号）
		Properties resourceAllotStatus = propertiesService.queryByKeyString("t.resource.allot.status");
		if (null == resourceAllotStatus || null == resourceAllotStatus.getValueString() || ("1".equals(resourceAllotStatus.getValueString()))) {
			System.out.println("当前有资源正在录入，此次推广资源分配跳过");
			//通知到群
			
			return 0;
		} else {
			//锁定添加资源状态（正在使用）
			resourceAllotStatus.setValueString("1");
			propertiesService.update(resourceAllotStatus);
		}
		//1.查询所有待分配记录（默认：100条）
		List<ResourceAllot> resourceAllotList = this.getList(resourceIds);
		//批量插入记录表
		List<CustomerLog> customerLogList = InstanceUtil.newArrayList();
		//2.待分配资源仓库（是否测试数据，手机号明文，是否已成交，是否资源（资源流程ID），是否共享池资源（已删除资源流程ID））
		for (ResourceAllot resourceAllot : resourceAllotList) {
			//状态初始化（是否测试数据，手机号明文，是否已成交，是否资源（资源流程ID），是否共享池资源（已删除资源流程ID））
			resourceAllot.setIsTest(0);
			resourceAllot.setMobile(null);
			resourceAllot.setIsClub(0);
			resourceAllot.setResourceFlowId(0);
			resourceAllot.setIsLeaveResource(0);
			if (resourceAllot.getType() == 0) {
				// 只分配正常数据（过滤测试数据）
				String mobile = null;
				// 1.解密推广资源RSA手机号
				try {
					mobile = AliyunCoder.testSimpleDecode(resourceAllot.getSecretMobile());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (null == mobile || mobile.length() != 11) {
					System.out.println("解密一个手机异常，此次推广资源分配调过");
					System.out.println(resourceAllot.getId()+"---"+resourceAllot.getSecretMobile());
					return 0;
				}
				if (	(null != resourceAllot.getName() 
						&& (resourceAllot.getName().indexOf("测试") >= 0
						|| resourceAllot.getName().toLowerCase().indexOf("ceshi") >= 0
						|| resourceAllot.getName().toLowerCase().indexOf("test") >= 0)
						)
						|| Constant.isTestMobile(mobile)
						) {
					// 记录为测试资源
					resourceAllot.setIsTest(1);
				} else {
					// 记录为手机号
					resourceAllot.setMobile(mobile);
					// 3.判断手机号是否已成交
					boolean isExistCustomer = customerIsExist(resourceAllot.getMd5Mobile());
					if (isExistCustomer) {
						// 记录是否已成交
						resourceAllot.setIsClub(1);
					} else {
						// 4.判断手机号是否在资源表已存在（当前判断为是否在保护期）
						Integer flowId = resourceService.resourceAllotIsRetain(resourceAllot.getMd5Mobile(), customerLogList);
						if (null != flowId && flowId > 0) {
							// 记录是否资源（资源流程）
							resourceAllot.setResourceFlowId(flowId);//如果存在我就不分该条推广分配资源
						} else {
							//删除闲置表资源
							flowId = resourceLeaveService.deleteResource(resourceAllot.getMd5Mobile(),customerLogList);
							// 记录是否共享池资源（已删除资源流程ID）
							resourceAllot.setIsLeaveResource(flowId);
						}
						
					}
				}
			}
		}
		//准备开始分配推广资源
		
		//批量分配成功的推广分配资源
		List<ResourceAllot> allotList = InstanceUtil.newArrayList();
		//批量插入的资源
		List<Resource> resourceList = InstanceUtil.newArrayList();
		//批量分配的用户
		List<Long> userList = InstanceUtil.newArrayList();
		//批量插入的客户信息
		List<Mobile> mobileList = InstanceUtil.newArrayList();
		
		//相邻推广资源手机号去重
		for (int i = 0; i < resourceAllotList.size(); i++) {
			String md5Mobile = resourceAllotList.get(i).getMd5Mobile()==null?"xx":resourceAllotList.get(i).getMd5Mobile();
			for (int j = 0; j < resourceAllotList.size(); j++) {
				if (i != j && md5Mobile.equals(resourceAllotList.get(j).getMd5Mobile())) {
					resourceAllotList.get(j).setResourceFlowId(Constant.FLOW_KF);
				}
			}
		}
		
		for (int i = 0; i < resourceAllotList.size(); i++) {
			//分配时间
			resourceAllotList.get(i).setAllotTime(new Date());
			if (resourceAllotList.get(i).getType() == 0 
					&& resourceAllotList.get(i).getIsTest() == 0 
					&& resourceAllotList.get(i).getIsClub() == 0
					&& resourceAllotList.get(i).getResourceFlowId() == 0) {
				//
				Resource resource = new Resource();
				resource.setId(resourceAllotList.get(i).getId());
				resource.setAllotId(resourceAllotList.get(i).getId());
				// 插入推广资源分配的信息到资源实体
				resource.setMobile(resourceAllotList.get(i).getMobile());//暂时无用
				setResourceAllotToResource(resourceAllotList.get(i), resource, userIds.get(i));
				resource.setCreateTime(new Date());
				resource.setUpdateTime(new Date());
				//1.保存准备插入的资源
				resourceList.add(resource);
				//2.保存分配到资源的用户
				userList.add(userIds.get(i));
				
				//生成客户信息记录表数据（资源ID，掩码手机号，MD5手机号，RSA加密手机号）
				Mobile mobileInfo = new Mobile();
				mobileInfo.setId(resourceAllotList.get(i).getId());
				mobileInfo.setCustomerId(resource.getId());
				mobileInfo.setFuzzyMobile(resourceAllotList.get(i).getFuzzyMobile());
				mobileInfo.setMd5Mobile(resourceAllotList.get(i).getMd5Mobile());
				mobileInfo.setRsaMobile(resourceAllotList.get(i).getSecretMobile());
				mobileInfo.setCreateTime(new Date());
				mobileInfo.setUpdateTime(new Date());
				//3.保存准备插入的客户信息（客户手机号表）
				mobileList.add(mobileInfo);
				
				/** 生成记录表   保存客户id/客户姓名/新流程/移动类型  
				 * 	@author liyanda
				 *  @updateTime 2019-1-15 14:52:27
				 **/
				CustomerLog customerLog = new CustomerLog();
				customerLog.setCustomerId(resource.getId());
				customerLog.setCustomerName(resource.getName());
				customerLog.setType("自动分配推广资源");
				customerLog.setCreateTime(new Date());
				customerLog.setUpdateTime(new Date());
				
				//
				resourceAllotList.get(i).setType(1);
				resourceAllotList.get(i).setResourceId(resource.getId());
				resourceAllotList.get(i).setResourceFlow(101);
				resourceAllotList.get(i).setSalerId(resource.getSalerId());
				resourceAllotList.get(i).setSalerName(resource.getSalerName());
				resourceAllotList.get(i).setManagerId(resource.getManagerId());
				resourceAllotList.get(i).setManagerName(resource.getManagerName());
				resourceAllotList.get(i).setDirectorId(resource.getDirectorId());
				resourceAllotList.get(i).setDirectorName(resource.getDirectorName());
				resourceAllotList.get(i).setMinisterId(resource.getMinisterId());
				resourceAllotList.get(i).setMinisterName(resource.getMinisterName());
				
				customerLog.setNewFlow(101);
				customerLog.setNewUser(resource.getSalerId());
				customerLogList.add(customerLog);
				//4.保存资源分配表已确认分配成功的资源
				allotList.add(resourceAllotList.get(i));
				//记录成功分配数
				num += 1;
			} else {
				//测试资源
				if (resourceAllotList.get(i).getIsTest() == 1) {
					resourceAllotList.get(i).setType(6);
					this.update(resourceAllotList.get(i));
				}
				//已成交客户再次咨询
				if (resourceAllotList.get(i).getIsClub() == 1) {
					// 钉钉消息通知经理、总监，该客户又来咨询了，开发加保
					try {
						sendClubMessageToManagerAndDirector(resourceAllotList.get(i), resourceAllotList.get(i).getMobile());
					} catch (Exception e) {
						System.out.println("钉钉消息群，客户又来咨询了，开发加保，发送失败!");
					}
					resourceAllotList.get(i).setType(2);
					this.update(resourceAllotList.get(i));
				}
				//保护期资源再次咨询
				if (resourceAllotList.get(i).getResourceFlowId() > 0) {
					// 钉钉消息通知经理、总监，该客户又来咨询了，再次咨询
					try {
						sendResourceMessageToManagerAndDirector(resourceAllotList.get(i).getResourceFlowId(), resourceAllotList.get(i), resourceAllotList.get(i).getMobile());
					} catch (Exception e) {
						System.out.println("钉钉消息群，客户又来咨询了，再次咨询，发送失败!");
					}
					resourceAllotList.get(i).setType(3);
					this.update(resourceAllotList.get(i));
				}
				
			}
		}
		if (null != resourceList && resourceList.size() > 0) {
			try {
				//批量更新客户资源日志表
				customerLogService.insertBatchCustomerLog(customerLogList);
				//批量插入资源
				resourceService.insertBatchResource(resourceList);
				//批量插入客户信息管理表
				mobileService.insertBatchMobile(mobileList);
				//批量更新用户分配记录
				userService.updateResourceAllot(userList);
				//批量更新推广资源分配表
				this.updateBatchAllot(allotList);
			} catch (Exception e) {
				System.out.println(DateUtil.getDateTime() + "####推广资源批量分配，批量操作发生错误!");
				throw new RuntimeException();//事务回滚，待观察
			}
			// 8.【可分配时】消息通知
			for (int i = 0; i < allotList.size(); i++) {
				try {
					sendMessage(allotList.get(i), userList.get(i), allotList.get(i).getMobile());
					sendMessageManager(allotList.get(i), allotList.get(i).getSalerName(), allotList.get(i).getManagerId(), allotList.get(i).getMobile());// 经理
				} catch (Exception e) {
					System.out.println("钉钉消息，推广分配资源消息通知，发送失败!");
				}
				
				//用户缓存更新
				CacheUtil.getCache().delAll("*:SysUser:" + userList.get(i));
				
				//分配表缓存更新
				CacheUtil.getCache().delAll("*:resourceAllot:" + allotList.get(i).getId());
			}
			
		}
		
		//更新添加资源状态（空闲中）
		resourceAllotStatus.setValueString("0");
		propertiesService.update(resourceAllotStatus);
		System.out.println(DateUtil.format(new Date(), DateUtil.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS) + "，分配资源数："+ num);
		return num;
	}
	/**
	 * 批量更新推广资源分配表
	 * @param allotList
	 */
	private void updateBatchAllot(List<ResourceAllot> allotList) {
		resourceAllotMapper.updateBatchAllot(allotList);
	}

	/**
	 * 定时分配推广资源给经纪人
	 * 
	 * 1.查询未分配资源 2.查询分配次数最少的用户（经纪人） 过滤姓名包含（测试、ceshi）的数据 3.判断手机号是否已成交
	 * 4.判断手机号是否在资源表已存在（当前判断为是否在保护期） 5.分配结果插入分配记录表 6.【可分配时】插入到资源表
	 * 7.【可分配时】更新用户的分配资源数 8.【可分配时】消息通知
	 * 
	 */
	@Transactional
	public Integer syncAllotResource(Integer allotNum) {
		int num = 0;
		//0.查询当前等待分配用户数
		List<Long> userIds = userService.querySalerAllotResourceMin();
		if (null == userIds || userIds.size() <= 0) {
			System.out.println(DateUtil.getDateTime() + "  推广资源无可分配的用户");
			return 0;
		}
		//需求量不足时，按量查询
		if (userIds.size() < allotNum) {
			allotNum = userIds.size();
		}
		// 1.查询未分配资源
		List<Long> resourceIds = ((ResourceAllotMapper) mapper).queryAllotResource();
		if (null != resourceIds && resourceIds.size() > 0) {
			try {
				// 使用同步方法来分配
				num = resourceAllot(resourceIds,userIds);
			} catch (Exception e) {
				System.out.println(DateUtil.getDateTime()+"###使用同步方法来分配，发生错误！");
				e.printStackTrace();
			}
		} else {
			if (null == resourceIds || resourceIds.size() <= 0) {
				OperationLogTool.operationLog(Constant.AUTO_LOG, DateUtil.getDateTime()+"无可分配推广资源");
			}
		}
		return num;
	}

	/**
	 * 分配推广资源通知经纪人
	 * 
	 * @param resourceAllot
	 * @param userId
	 * @param mobile
	 */
	private void sendMessage(ResourceAllot resourceAllot, Long userId, String mobile) {
		SysUser user = userService.queryById(userId);
		StringBuffer content = new StringBuffer("给你分配了一条推广资源：");
		content.append(mobile + "，");
		if (StrUtils.isNotNullOrBlank(resourceAllot.getContent())) {
			String content2 = "";
			if (StrUtils.isNotNullOrBlank(resourceAllot.getContent())) {
				try {
					List<Map<String, Object>> list = JsonUtil.parseJSON2List(resourceAllot.getContent());
					if (null != list && list.size() > 0) {
						for (Map<String, Object> map2 : list) {
							content2 += map2.get("key") + "：" + map2.get("value") + "；";
						}
					}
				} catch (Exception e) {
					OperationLogTool.operationLog(Constant.ERROR_LOG,
							"分配推广资源：" + resourceAllot.gettId() + "，解析json内容发送错误！");
				}
			}
			content.append(content2 + "，");
		}
		if (StrUtils.isNotNullOrBlank(resourceAllot.getPlanCode())) {
			content.append("专题：" + resourceAllot.getPlanCode() + "，");
		}
		content.append("请立即拨打，并且完善客户信息【"
				+ DateUtils.DateToStr(resourceAllot.getCreaterTime(), DateUtils.TIME_FORMAT_CHINESE) + "】");
		//发送钉钉消息
		DingUtil.sendMsg(content.toString(), user.getDingId());
	}

	/**
	 * 分配推广资源通知经纪人的经理
	 * 
	 * @param resourceAllot
	 * @param userId
	 * @param mobile
	 */
	private void sendMessageManager(ResourceAllot resourceAllot,String salerName, Long userId, String mobile) {
		SysUser user = userService.queryById(userId);
		// 经理
		StringBuffer content = new StringBuffer("给你的组员：");
		content.append(user.getAccount() + "，分配了一条来源渠道推广资源：" + mobile + "，请立即拨打【"
				+ DateUtils.DateToStr(resourceAllot.getCreaterTime(), DateUtils.TIME_FORMAT_CHINESE) + "】");
		//发送钉钉消息
		DingUtil.sendMsg(content.toString(), user.getDingId());
		
	}

	/**
	 * 钉钉消息通知经理、总监，该客户又来咨询了，开发升级
	 * 
	 * @param resourceAllot
	 * @param mobile
	 */
	public void sendClubMessageToManagerAndDirector(ResourceAllot resourceAllot, String mobile) {
		Customer customer = customerService.queryByMd5Mobile(resourceAllot.getMd5Mobile());
		if (null != customer) {
			MarkdownMessage message = new MarkdownMessage();
			message.setTitle("客户再次咨询！");
			message.add(MarkdownMessage.getHeaderText(5, "成交客户再次发起咨询了，赶紧升级啦！"));
			message.add(MarkdownMessage.getBoldText("客户姓名："));
			message.add(customer.getName());
			message.add("\n");
			message.add(MarkdownMessage.getBoldText("客户手机："));
			message.add(mobile);
			message.add("\n");
			message.add(MarkdownMessage.getBoldText("客户签单次数："));
			message.add(customer.getInsureNum().toString() + " 次");
			message.add("\n");
			message.add(MarkdownMessage.getBoldText("客户总缴费："));
			message.add(customer.getInsureMoney().toString() + " 元");
			message.add("\n");
			message.add(MarkdownMessage.getBoldText("咨询时间："));
			message.add(DateUtils.DateToStr(resourceAllot.getCreaterTime(), DateUtils.TIME_FORMAT_CHINESE));
			message.add("\n");
			if (StrUtils.isNotNullOrBlank(resourceAllot.getContent())) {
				String content = "";
				if (StrUtils.isNotNullOrBlank(resourceAllot.getContent())) {
					try {
						List<Map<String, Object>> list = JsonUtil.parseJSON2List(resourceAllot.getContent());
						if (null != list && list.size() > 0) {
							for (Map<String, Object> map2 : list) {
								content += map2.get("key") + "：" + map2.get("value") + "；";
							}
						}
					} catch (Exception e) {
						OperationLogTool.operationLog(Constant.ERROR_LOG,
								"分配推广资源：" + resourceAllot.gettId() + "，解析json内容发送错误！");
					}
				}
				message.add(MarkdownMessage.getBoldText("咨询需求："));
				message.add(content);
				message.add("\n");
			}
			if (StrUtils.isNotNullOrBlank(resourceAllot.getChannel())) {
				String channel = dicService.queryCodeTextByTypeCode("RESOURCE_CHANNEL", resourceAllot.getChannel());
				if (StrUtils.isNotNullOrBlank(channel)) {
					message.add(MarkdownMessage.getBoldText("来源渠道："));
					message.add(channel);
					message.add("\n");
				}
			}
			if (StrUtils.isNotNullOrBlank(resourceAllot.getPlanCode())) {
				message.add(MarkdownMessage.getBoldText("咨询专题："));
				message.add(resourceAllot.getPlanCode());
				message.add("\n");
			}
			String directorName = customer.getDirectorName() == null ? "未分配" : customer.getDirectorName();
			String managerName = customer.getManagerName() == null ? "未分配" : customer.getManagerName();
			String salerName = customer.getSalerName() == null ? "未分配" : customer.getSalerName();
			message.add(MarkdownMessage.getBoldText("市场部归属："));
			message.add(directorName + "-" + managerName + "-" + salerName);
			message.add("\n");
			DingUtil.sendGroupMesg("dd.group.tg.business", message);
		}

	}

	/**
	 * 钉钉消息通知经理、总监，该客户又来咨询了，重新咨询
	 * 
	 * 发群、发经理钉钉号、发业务员钉钉号
	 * 
	 * @param flowId
	 * @param resourceAllot
	 * @param mobile
	 */
	public void sendResourceMessageToManagerAndDirector(Integer flowId, ResourceAllot resourceAllot, String mobile) {
		Resource resource = resourceService.queryByMd5Mobile(resourceAllot.getMd5Mobile());
		String flowString = Constant.getFlowString(flowId);
		if (null != resource) {
			MarkdownMessage message = new MarkdownMessage();
			message.setTitle(flowString + "再次咨询！");
			message.add(MarkdownMessage.getHeaderText(5, flowString + "再次发起咨询了，这是优质资源！"));
			message.add(MarkdownMessage.getBoldText("客户姓名："));
			message.add(resource.getName());
			message.add("\n");
			message.add(MarkdownMessage.getBoldText("客户手机："));
			message.add(mobile);
			message.add("\n");
			message.add(MarkdownMessage.getBoldText("咨询时间："));
			message.add(DateUtils.DateToStr(resourceAllot.getCreaterTime(), DateUtils.TIME_FORMAT_CHINESE));
			message.add("\n");
			if (StrUtils.isNotNullOrBlank(resourceAllot.getContent())) {
				String content = "";
				if (StrUtils.isNotNullOrBlank(resourceAllot.getContent())) {
					try {
						List<Map<String, Object>> list = JsonUtil.parseJSON2List(resourceAllot.getContent());
						if (null != list && list.size() > 0) {
							for (Map<String, Object> map2 : list) {
								content += map2.get("key") + "：" + map2.get("value") + "；";
							}
						}
					} catch (Exception e) {
						OperationLogTool.operationLog(Constant.ERROR_LOG,
								"分配推广资源：" + resourceAllot.gettId() + "，解析json内容发送错误！");
					}
				}
				message.add(MarkdownMessage.getBoldText("咨询需求："));
				message.add(content);
				message.add("\n");
			}
			if (StrUtils.isNotNullOrBlank(resourceAllot.getChannel())) {
				String channel = dicService.queryCodeTextByTypeCode("RESOURCE_CHANNEL", resourceAllot.getChannel());
				if (StrUtils.isNotNullOrBlank(channel)) {
					message.add(MarkdownMessage.getBoldText("来源渠道："));
					message.add(channel);
					message.add("\n");
				}
			}
			if (StrUtils.isNotNullOrBlank(resourceAllot.getPlanCode())) {
				message.add(MarkdownMessage.getBoldText("咨询专题："));
				message.add(resourceAllot.getPlanCode());
				message.add("\n");
			}
			String directorName = resource.getDirectorName() == null ? "未分配" : resource.getDirectorName();
			String managerName = resource.getManagerName() == null ? "未分配" : resource.getManagerName();
			String salerName = resource.getSalerName() == null ? "未分配" : resource.getSalerName();
			message.add(MarkdownMessage.getBoldText("市场部归属："));
			message.add(directorName + "-" + managerName + "-" + salerName);
			message.add("\n");
			// 发送到群聊
			DingUtil.sendGroupMesg("dd.group.tg.business", message);
			//
			StringBuffer sb = new StringBuffer("开发资源再次咨询！");
			sb.append("客户姓名：").append(resource.getName());
			sb.append("，客户手机：").append(mobile);
			sb.append("，咨询时间：")
					.append(DateUtils.DateToStr(resourceAllot.getCreaterTime(), DateUtils.TIME_FORMAT_CHINESE));
			if (StrUtils.isNotNullOrBlank(resourceAllot.getContent())) {
				String content = "";
				if (StrUtils.isNotNullOrBlank(resourceAllot.getContent())) {
					try {
						List<Map<String, Object>> list = JsonUtil.parseJSON2List(resourceAllot.getContent());
						if (null != list && list.size() > 0) {
							for (Map<String, Object> map2 : list) {
								content += map2.get("key") + "：" + map2.get("value") + "；";
							}
						}
					} catch (Exception e) {
						OperationLogTool.operationLog(Constant.ERROR_LOG,
								"分配推广资源：" + resourceAllot.gettId() + "，解析json内容发送错误！");
					}
				}
				sb.append("，咨询需求：").append(content);
			}
			if (StrUtils.isNotNullOrBlank(resourceAllot.getChannel())) {
				String channel = dicService.queryCodeTextByTypeCode("RESOURCE_CHANNEL", resourceAllot.getChannel());
				if (StrUtils.isNotNullOrBlank(channel)) {
					sb.append("，来源渠道：").append(channel);
				}
			}
			if (StrUtils.isNotNullOrBlank(resourceAllot.getPlanCode())) {
				sb.append("，咨询专题：").append(resourceAllot.getPlanCode());
			}
			sb.append("，市场部归属：").append(directorName + "-" + managerName + "-" + salerName);
			try {
				if (resource.getSalerId() > 0) {
					String salerDingId = userService.queryById(resource.getSalerId()).getDingId();
					if (StrUtils.isNotNullOrBlank(salerDingId)) {
						DingUtil.sendMsg(sb.toString(), salerDingId);
					}
				}
				if (resource.getManagerId() > 0) {
					String managerDingId = userService.queryById(resource.getManagerId()).getDingId();
					if (StrUtils.isNotNullOrBlank(managerDingId)) {
						DingUtil.sendMsg(sb.toString(), managerDingId);
					}
				}
			} catch (Exception e) {
				System.out.println("资源再次咨询，发送钉钉消息给业务、经理时发生异常");
			}
		} else {
			ResourceLeave resourceLeave = resourceLeaveService.queryByMd5Mobile(resourceAllot.getMd5Mobile());
			MarkdownMessage message = new MarkdownMessage();
			message.setTitle(flowString + "再次咨询！");
			message.add(MarkdownMessage.getHeaderText(5, flowString + "再次发起咨询了，这是优质资源！"));
			message.add(MarkdownMessage.getBoldText("客户姓名："));
			message.add(resourceLeave.getName());
			message.add("\n");
			message.add(MarkdownMessage.getBoldText("客户手机："));
			message.add(mobile);
			message.add("\n");
			message.add(MarkdownMessage.getBoldText("咨询时间："));
			message.add(DateUtils.DateToStr(resourceAllot.getCreaterTime(), DateUtils.TIME_FORMAT_CHINESE));
			message.add("\n");
			if (StrUtils.isNotNullOrBlank(resourceAllot.getContent())) {
				String content = "";
				if (StrUtils.isNotNullOrBlank(resourceAllot.getContent())) {
					try {
						List<Map<String, Object>> list = JsonUtil.parseJSON2List(resourceAllot.getContent());
						if (null != list && list.size() > 0) {
							for (Map<String, Object> map2 : list) {
								content += map2.get("key") + "：" + map2.get("value") + "；";
							}
						}
					} catch (Exception e) {
						OperationLogTool.operationLog(Constant.ERROR_LOG,
								"分配推广资源：" + resourceAllot.gettId() + "，解析json内容发送错误！");
					}
				}
				message.add(MarkdownMessage.getBoldText("咨询需求："));
				message.add(content);
				message.add("\n");
			}
			if (StrUtils.isNotNullOrBlank(resourceAllot.getChannel())) {
				String channel = dicService.queryCodeTextByTypeCode("RESOURCE_CHANNEL", resourceAllot.getChannel());
				if (StrUtils.isNotNullOrBlank(channel)) {
					message.add(MarkdownMessage.getBoldText("来源渠道："));
					message.add(channel);
					message.add("\n");
				}
			}
			if (StrUtils.isNotNullOrBlank(resourceAllot.getPlanCode())) {
				message.add(MarkdownMessage.getBoldText("咨询专题："));
				message.add(resourceAllot.getPlanCode());
				message.add("\n");
			}
			String directorName = resourceLeave.getDirectorName() == null ? "未分配" : resourceLeave.getDirectorName();
			String managerName = resourceLeave.getManagerName() == null ? "未分配" : resourceLeave.getManagerName();
			String salerName = resourceLeave.getSalerName() == null ? "未分配" : resourceLeave.getSalerName();
			message.add(MarkdownMessage.getBoldText("市场部归属："));
			message.add(directorName + "-" + managerName + "-" + salerName);
			message.add("\n");
			// 发送到群聊
			DingUtil.sendGroupMesg("dd.group.tg.business", message);
			//
			StringBuffer sb = new StringBuffer("开发资源再次咨询！");
			sb.append("客户姓名：").append(resourceLeave.getName());
			sb.append("，客户手机：").append(mobile);
			sb.append("，咨询时间：")
					.append(DateUtils.DateToStr(resourceAllot.getCreaterTime(), DateUtils.TIME_FORMAT_CHINESE));
			if (StrUtils.isNotNullOrBlank(resourceAllot.getContent())) {
				String content = "";
				if (StrUtils.isNotNullOrBlank(resourceAllot.getContent())) {
					try {
						List<Map<String, Object>> list = JsonUtil.parseJSON2List(resourceAllot.getContent());
						if (null != list && list.size() > 0) {
							for (Map<String, Object> map2 : list) {
								content += map2.get("key") + "：" + map2.get("value") + "；";
							}
						}
					} catch (Exception e) {
						OperationLogTool.operationLog(Constant.ERROR_LOG,
								"分配推广资源：" + resourceAllot.gettId() + "，解析json内容发送错误！");
					}
				}
				sb.append("，咨询需求：").append(content);
			}
			if (StrUtils.isNotNullOrBlank(resourceAllot.getChannel())) {
				String channel = dicService.queryCodeTextByTypeCode("RESOURCE_CHANNEL", resourceAllot.getChannel());
				if (StrUtils.isNotNullOrBlank(channel)) {
					sb.append("，来源渠道：").append(channel);
				}
			}
			if (StrUtils.isNotNullOrBlank(resourceAllot.getPlanCode())) {
				sb.append("，咨询专题：").append(resourceAllot.getPlanCode());
			}
			sb.append("，市场部归属：").append(directorName + "-" + managerName + "-" + salerName);
			try {
				if (resourceLeave.getSalerId() > 0) {
					String salerDingId = userService.queryById(resourceLeave.getSalerId()).getDingId();
					if (StrUtils.isNotNullOrBlank(salerDingId)) {
						DingUtil.sendMsg(sb.toString(), salerDingId);
					}
				}
				if (resourceLeave.getManagerId() > 0) {
					String managerDingId = userService.queryById(resourceLeave.getManagerId()).getDingId();
					if (StrUtils.isNotNullOrBlank(managerDingId)) {
						DingUtil.sendMsg(sb.toString(), managerDingId);
					}
				}
			} catch (Exception e) {
				System.out.println("资源再次咨询，发送钉钉消息给业务、经理时发生异常");
			}
		}

	}

	/**查询用户群当前正在分配人数**/
	public Integer queryUserAllotMan() {
		return ((ResourceAllotMapper) mapper).queryUserAllotMan();
	}
	/**查询用户群当前等待分配空闲数**/
	public Integer queryUserWaitNum() {
		return ((ResourceAllotMapper) mapper).queryUserWaitNum();
	}

	/**查询用户群今天所有资源数**/
	public Integer queryResourceNum() {
		return ((ResourceAllotMapper) mapper).queryResourceNum();
	}
	/**查询用户群今天已分配资源数**/
	public Integer queryResourceAllotNum() {
		return ((ResourceAllotMapper) mapper).queryResourceAllotNum();
	}
	/**查询用户群今天无效资源数**/
	public Integer queryResourceAInvalidNum() {
		return ((ResourceAllotMapper) mapper).queryResourceAInvalidNum();
	}
	/**查询当前推广资源待分配资源数**/
	public Integer queryResourceWaitNum() {
		return ((ResourceAllotMapper) mapper).queryResourceWaitNum();
	}

	/** 查询当前共享池资源分配数 **/
	public Integer querySharedpoolAllotNum() {
		return ((ResourceAllotMapper) mapper).querySharedpoolAllotNum();
	}
	
	/**
	 * 查询推广资源分配表
	 * 
	 * @param param
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Page readList(Map<String, Object> params) {
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		Page<Long> page = getPage(params);
		page.setRecords(((ResourceAllotMapper) mapper).selectIdPage(page, params));
		return super.getPage(page);
	}

	@Transactional
	public void syncUpdate(ResourceAllot record) {
		try {
			record.setUpdateTime(new Date());
			if (record.getId() == null) {
				if (null == record.getCreateTime()) {
					record.setCreateTime(new Date());
				}
				record.setCreateBy(record.getUpdateBy());
				record.setEnable(true);
				mapper.insert(record);
			} else {
				mapper.updateById(record);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 回写分配表资源开发流程
	 *
	 * @param resourceId
	 * @param flowId
	 */
	public void updateFlowId(Long resourceId, Integer flowId) {
		Long resourceAllotId = resourceAllotMapper.queryByResourceId(resourceId);
		if (null != resourceAllotId && resourceAllotId > 0) {
			ResourceAllot resourceAllot = this.queryById(resourceAllotId);
			if (null != resourceAllot && resourceAllot.getId() > 0) {
				resourceAllot.setResourceFlow(flowId);
				if (flowId == Constant.FLOW_YX) {
					resourceAllot.setIsTalk(1);
				}
				super.update(resourceAllot);
			}
		}
	}

	/**
	 * 发送钉钉消息到推广消息群
	 */
	public void sendTgGroupMsg() {
		int userAllotMan = StrUtils.toInt(queryUserAllotMan());
		int userWaitNum = StrUtils.toInt(queryUserWaitNum());
		int resourceNum = StrUtils.toInt(queryResourceNum());
		int resourceAllotNum = StrUtils.toInt(queryResourceAllotNum());
		int resourceAInvalidNum = StrUtils.toInt(queryResourceAInvalidNum());
		int resourceWaitNum = StrUtils.toInt(queryResourceWaitNum());
		
		MarkdownMessage message = new MarkdownMessage();
		message.setTitle("推广资源提醒");
		message.add(MarkdownMessage.getHeaderText(5, "推广资源提醒！"));
		
		message.add(MarkdownMessage.getBoldText("当前用户正在分配："));
		message.add(userAllotMan + "人");
		message.add("\n");
		message.add(MarkdownMessage.getBoldText("当前用户需求分配数："));
		message.add(userWaitNum + "条");
		message.add("\n");
		message.add(MarkdownMessage.getBoldText("今天总推广资源数："));
		message.add(resourceNum + "条");
		message.add("\n");
		message.add(MarkdownMessage.getBoldText("今天已分配推广资源数："));
		message.add(resourceAllotNum + "条");
		message.add("\n");
		message.add(MarkdownMessage.getBoldText("今天无效资源数："));
		message.add(resourceAInvalidNum + "条");
		message.add("\n");
		message.add(MarkdownMessage.getBoldText("今天未分配资源数："));
		message.add(resourceWaitNum + "条");
		message.add("\n");
		message.add(MarkdownMessage.getBoldText("统计时间："));
		message.add(DateUtils.DateToStr(new Date(), DateUtils.TIME_FORMAT_CHINESE));
		message.add("\n");
		DingUtil.sendGroupMesg("dd.group.tg", message);
	}

	/**
	 * 查询ResourceAllot实体
	 * 
	 * @param resourceId 资源id
	 * @return
	 */
	public ResourceAllot queryByResourceId(Long resourceId){
		return this.queryById(resourceAllotMapper.queryByResourceId(resourceId));
	}
	
	/**
	 * 录入资源时，增加一条推广资源分配记录
	 * 
	 * @param param 录入的资源
	 * @return
	 */
	public ResourceAllot addResource(Resource resource) {
		ResourceAllot resourceAllot = new ResourceAllot();
		resourceAllot.setName(resource.getName());
		resourceAllot.setFuzzyMobile(resource.getFuzzyMobile());// 掩码手机号
		resourceAllot.setMd5Mobile(resource.getMd5Mobile());// MD5手机号
		resourceAllot.setChannel("personal");// 推广渠道
		resourceAllot.setContent(resource.getDemand());// 内容
		
		resourceAllot.setProvince(resource.getProvince());
		resourceAllot.setCity(resource.getCity());
		// 设置业务，经理，总监，总经理
		resourceAllot.setSalerId(resource.getSalerId());
		resourceAllot.setSalerName(resource.getSalerName());
		resourceAllot.setManagerId(resource.getManagerId());
		resourceAllot.setManagerName(resource.getManagerName());
		resourceAllot.setDirectorId(resource.getDirectorId());
		resourceAllot.setDirectorName(resource.getDirectorName());
		resourceAllot.setMinisterId(resource.getMinisterId());
		resourceAllot.setMinisterName(resource.getMinisterName());
		//
		resourceAllot.setRemark(resource.getRemark());
		
		resourceAllot.setType(9);
		return super.update(resourceAllot);
	}

	public List<Map<String, Object>> queryMd5Mobile(Map<String, Object> param) {
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(param);
		return resourceAllotMapper.queryMd5Mobile(param);
	}
	
	/**
	 * 导出推广明细
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> queryAllotDetail(Map<String, Object> param) {
		return resourceAllotMapper.queryAllotDetail(param);
	}
}
