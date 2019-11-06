package com.lazhu.crm.customer.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.support.Assert;
import com.lazhu.core.util.InstanceUtil;
import com.lazhu.core.util.WxCollectUtil;
import com.lazhu.crm.Constant;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.entity.CustomerDetailsVo;
import com.lazhu.crm.customer.entity.CustomerListVo;
import com.lazhu.crm.customer.mapper.CustomerMapper;
import com.lazhu.crm.customeractualcot.entity.CustomerActualCot;
import com.lazhu.crm.customeractualcot.mapper.CustomerActualCotMapper;
import com.lazhu.crm.customeractualcot.service.CustomerActualCotService;
import com.lazhu.crm.customerlog.entity.CustomerLog;
import com.lazhu.crm.customerrefund.entity.CustomerRefund;
import com.lazhu.crm.customerrefund.service.CustomerRefundService;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.crm.salerrecord.entity.SalerRecord;
import com.lazhu.crm.salerrecord.service.SalerRecordService;
import com.lazhu.crm.serverrecord.entity.ServerRecord;
import com.lazhu.crm.serverrecord.service.ServerRecordService;
import com.lazhu.crm.serverrecordmobile.entity.ServerRecordMobile;
import com.lazhu.crm.serverrecordmobile.service.ServerRecordMobileService;
import com.lazhu.crm.signapply.entity.SignApply;
import com.lazhu.crm.signapply.service.SignApplyService;
import com.lazhu.crm.teacherdirectivecot.entity.TeacherDirectiveCot;
import com.lazhu.crm.teacherdirectivecot.mapper.TeacherDirectiveCotMapper;
import com.lazhu.crm.teacherdirectivecot.service.TeacherDirectiveCotService;
import com.lazhu.ecp.utils.DateUtils;
import com.lazhu.ecp.utils.DingUtil;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.model.SysUser;
import com.lazhu.sys.service.SysDeptService;
import com.lazhu.sys.service.SysDicService;

/**
 * <p>
 * 成交客户 服务实现类
 * </p>
 *
 * @author hz
 * @since 2017-05-27
 */
@Service
@CacheConfig(cacheNames = "customer")
public class CustomerService extends BusinessBaseService<Customer> {
	
	@Autowired //
	CustomerMapper customerMapper;

	@Autowired // 资源
	ResourceService resourceService;

	@Autowired // 订单
	SignApplyService signApplyService;

	@Autowired // 客服服务记录
	ServerRecordService serverRecordService;

	@Autowired // 客服退保记录
	CustomerRefundService refundService;

	@Autowired // 市场部通话记录
	SalerRecordService salerRecordService;

	@Autowired // 字典
	SysDicService dicServcie;

	@Autowired // 客服通话记录
	ServerRecordMobileService serverRecordMobileService;
	
	@Autowired // 实盘仓位记录
	CustomerActualCotService cusService;
	
	@Autowired // 实盘仓位记录
	CustomerActualCotMapper cusMapper;
	
	@Autowired // 虚盘仓位记录
	TeacherDirectiveCotService teaService;
	
	@Autowired // 虚盘仓位记录
	TeacherDirectiveCotMapper teaMapper;
	
	@Autowired //实盘仓位信息
	CustomerActualCotService customerCotService;

	@Autowired //实盘仓位信息
	TeacherDirectiveCotService teacherCotService;
	
	@Autowired //业务所在部门
	SysDeptService deptService;

	/**
	 * MD5手机号精准搜索客户
	 * 
	 * @param md5Mobile MD5手机号码
	 * @return  customer 客户信息对象
	 */
	public Customer queryByMd5Mobile(String md5Mobile) {
		Customer customer = null;
		List<Long> ids = ((CustomerMapper) mapper).queryByMd5Mobile(md5Mobile);
		if (null != ids && ids.size() > 0) {
			if (ids.size() == 1) {
				customer = super.queryById(ids.get(0));
			} else {
				OperationLogTool.operationLog(Constant.ERROR_LOG,
						"【会员】手机号精准搜索发现重复手机号客户：" + md5Mobile + "，个数：" + ids.size());
			}
		} else {
			OperationLogTool.operationLog(Constant.ERROR_LOG, "【会员】手机号精准搜索失败：" + md5Mobile);
		}
		return customer;
	}

	/**
	 * 根据用户角色是否显示手机号，判断显示完整手机号、还是掩码手机号
	 * 
	 * @param customer 客户信息对象
	 * @param mobile   页面明文手机号
	 * @param currUser 当前操作人id
	 */
	private void detailsShowMobile(Customer customer,String mobile,Long currUser) {
		Integer isMobileVisible = userService.queryIsMobileVisibleById(currUser);
		if (null != isMobileVisible && isMobileVisible == 1) {
			//解密手机号
			customer.setShowMobile(mobile);
		} else {
			//掩码手机号
			customer.setShowMobile(customer.getFuzzyMobile());
		}
	}
	
	/**
	 * 市场部成交签单
	 * 
	 * 1.加密手机号成MD5手机，用于查询
	 * 2.根据当前用户，判断是否添加市场部权限
	 * 3.根据下属，更改市场部、加保部、客服部权限
	 * 4.查询成交状态的客户
	 * 
	 * @param params 筛选条件
	 * @param currUser 当前操作人id
	 * @return	CustomerListVoPage 市场部封装分页
	 */
	@SuppressWarnings("rawtypes")
	public Page customerList(Map<String, Object> params, Long currUser) {
		//params.put("isUpgrade", 0);
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		// 设置市场部权限（用户组）
		super.setBusiness(params, currUser);
		// 下属查询
		super.setSubordinate(params);
		//params.put("flowId", Constant.CLUB_ZC);
		return CustomerListVoPage(params,currUser);
	}

	/**
	 * 	商务部成交客户
	 * 1.加密手机号成MD5手机，用于查询
	 * 2.根据当前用户，判断是否添加市场部权限
	 * 3.根据当前用户，判断是否添加商务部权限
	 * 4.根据下属，更改市场部、商务部、客服部权限
	 * 5.查询成交状态的客户
	 * 
	 * @param params 筛选条件
	 * @param currUser	当前用户id
	 * @return CustomerListVoPage 封装分页
	 */
	@SuppressWarnings("rawtypes")
	public Page contracterMore(Map<String, Object> params, Long currUser) {
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		// 设置市场部权限（用户组）
		super.setBusiness(params, currUser);
		// 设置商务部权限（用户组）
		super.setContracter(params, currUser);
		//params.put("flowId", Constant.CLUB_ZC);
		// 下属查询
		super.setSubordinate(params);
		return CustomerListVoPage(params,currUser);
	}

	/**
	 * 客服部成交客户
	 * 
	 * 1.加密手机号成MD5手机，用于查询
	 * 2.根据当前用户，判断是否添加市场部权限
	 * 3.根据当前用户，判断是否添加客服部权限
	 * 4.根据下属，更改市场部、商务部、客服部权限
	 * 5.查询成交状态的客户
	 * 
	 * @param params	筛选条件
	 * @param currUser	当前操作人id
	 * @return	CustomerListVoPage	封装分页
	 */
	@SuppressWarnings("rawtypes")
	public Page serverMore(Map<String, Object> params, Long currUser) {
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		// 设置市场部权限（用户组）
		super.setBusiness(params, currUser);
		// 设置客服部权限（用户组）
		super.setServer(params, currUser);
		//params.put("flowId", Constant.CLUB_ZC);
		params.put("isService", 1);
		// 下属查询
		super.setSubordinate(params);
		//姓名查询(支持多姓名查询)
		String userName = StrUtils.toString(params.get("name"));
		userName = userName.trim().replaceAll("\\s*|\t|\r|\n", "");
		userName = userName.replace("/(^\\s*)|(\\s*$)/g", "").replace("/\\?/g","");
		if(StrUtils.isNotNullOrBlank(userName)) {
			userName = userName.replace("，", ",");
			userName = userName.replace("。", ",");
			userName = userName.replace("、", ",");
			userName = userName.replace(".", ",");
			userName = userName.replace("；", ",");
			userName = userName.replace(";", ",");
			//多用户名搜索
			if (userName.indexOf(",") > 0) {
				List<String> list = new ArrayList<String>();
				String[] names = userName.split(",");//拆分
				for (int i = 0; i < names.length; i++) {
					String name= names[i];
					list.add(name);
				}
				params.put("names", list);
				params.remove("name");
			}
		}
		return CustomerListVoPage(params,currUser);
	}

	/**
	 * 客服部待分配待接收客户
	 * 
	 * @param params	筛选条件
	 * @param currUser	当前操作人id
	 * @return CustomerListVoPage 	封装分页
	 */
	@SuppressWarnings("rawtypes")
	public Page customerAllot(Map<String, Object> params, Long currUser) {
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		// 设置客服部权限（用户组）
		//super.setServer(params, currUser);
		//暂无客服总监
		Long userGroup = userService.queryById(currUser).getUserGroup();
		//防止不设定用户组，越权
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		if (userGroup == Constant.USER_GROUP_KF) {//客服
			params.put("serverId", currUser);
		}
		// 下属查询
		super.setSubordinate(params);
		params.put("flowId", Constant.CLUB_ZC);
		params.put("isService", "0");
		return CustomerListVoPage(params,currUser);
	}

	/**
	 * 升级部【待分配客户】可自由分配
	 * @param param 	筛选条件
	 * @param currUser	当前操作人id
	 * @return CustomerListUpgradeVoPage	封装分页
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public Page allotUpgradePage(Map<String, Object> params, Long currUser) {
		SysUser user = super.getUserById(currUser);
		//未分配升级人员
		params.put("upgradeFlow", 0);
		params.put("isUpgrade", 1);
		//正常服务
		params.put("flowId", Constant.CLUB_ZC);
		return CustomerListVoPage(params,currUser);
	}

	/**
	 * 升级中心【原始客户】【丢弃客户】【可聊客户】【回访客户】【成交客户】
	 * 
	 * @param param		筛选条件
	 * @param currUser	当前操作人id
	 * @return CustomerListUpgradeVoPage	封装分页
	 */
	@SuppressWarnings("rawtypes")
	public Page upgradePage(Map<String, Object> params, Long currUser) {
		params.put("isUpgrade", 1);
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		
		// 设置市场升级中心权限（用户组）
		SysUser user = userService.queryById(currUser);
		Assert.notNull(user.getUserGroup(), Constant.GROUPISNULL);
		Long userGroup = user.getUserGroup();
		if (userGroup == Constant.USER_GROUP_ZJ) {//升级总监
			params.put("upgradeDirectorId", currUser);
		} else if (userGroup == Constant.USER_GROUP_ZJL) {//升级总经理
			params.put("upgradeMinisterId", currUser);
		} else {
			super.setUpgradeBusiness(params, currUser);
		}
		// 下属查询
		this.setUpgradeSubordinate(params);
		// 正常服务
		//params.put("flowId", Constant.CLUB_ZC);
		return CustomerListUpgradeVoPage(params,currUser);
	}

	/** ******************************************** **/

	/**
	 * 资源调配
	 * 
	 * @param ids 	List<资源ID>
	 * @param userId 	用户ID
	 * @param currUser 操作者ID
	 * @param ip 操作值IP
	 * @return customerList	List<客户对象>
	 */
	@Transactional
	public List<Customer> allotCustomer(List<Long> ids, Long userId, Long currUser, String ip) {
		// 1.当前操作值，权限
		Long curr = super.getUserById(currUser).getUserGroup();
		if (curr == Constant.USER_GROUP_YWY) {
			Assert.notNull(null, Constant.GROUPISNULL);
		}

		// 2.查询所有实体
		List<Customer> customerList = ((CustomerMapper) mapper).selectBatchIds(ids);
		for (Customer customer : customerList) {
			// 3.保存分配日志
			CustomerLog log = new CustomerLog();
			log.setCustomerId(customer.getId());
			log.setCustomerName(customer.getName());
			Long oldUserId = null;
			if (customer.getSalerId() != null && customer.getSalerId() > 0) {
				oldUserId = customer.getSalerId();
			} else if (customer.getManagerId() != null && customer.getManagerId() > 0) {
				oldUserId = customer.getManagerId();
			} else if (customer.getDirectorId() != null && customer.getDirectorId() > 0) {
				oldUserId = customer.getDirectorId();
			}
			log.setOldUser(oldUserId);
			log.setNewUser(userId);
			log.setType("资源调配");
			log.setIp(ip);
			log.setUpdateBy(currUser);
			super.saveCustomerLog(log);

			// 4.根据被分配者的用户组，设置市场部信息
			setAllBusiness(customer, userId);
			// 5.【DB业务】更新
			customer.setUpdateBy(currUser);
			super.update(customer);
		}
		return customerList;
	}

	/**
	 * 设置业务，经理，总监，总经理
	 * 
	 * @param param	客户信息对象
	 * @param userId	当前操作人id
	 */
	public void setAllBusiness(Customer param, Long userId) {
		SysUser manager = null;
		SysUser director = null;
		SysUser minister = null;
		// 业务员
		SysUser user = super.getUserById(userId);
		if (user.getUserGroup() == Constant.USER_GROUP_YWY) {
			param.setSalerId(userId);
			param.setSalerName(user.getUserName());
			// 经理
			manager = super.getUserById(user.getParentId());
			param.setManagerId(manager.getId());
			param.setManagerName(manager.getUserName());
			// 总监
			director = super.getUserById(manager.getParentId());
			param.setDirectorId(director.getId());
			param.setDirectorName(director.getUserName());
			// 总经理
			minister = super.getUserById(director.getParentId());
			param.setMinisterId(minister.getId());
			param.setMinisterName(minister.getUserName());
		} else if (user.getUserGroup() == Constant.USER_GROUP_JL) {
			// 业务员
			param.setSalerId(0L);
			param.setSalerName("未分配");
			// 经理
			manager = super.getUserById(userId);
			param.setManagerId(manager.getId());
			param.setManagerName(manager.getUserName());
			// 总监
			director = super.getUserById(manager.getParentId());
			param.setDirectorId(director.getId());
			param.setDirectorName(director.getUserName());
			// 总经理
			minister = super.getUserById(director.getParentId());
			param.setMinisterId(minister.getId());
			param.setMinisterName(minister.getUserName());
		} else if (user.getUserGroup() == Constant.USER_GROUP_ZJ) {
			// 业务员
			param.setSalerId(0L);
			param.setSalerName("未分配");
			// 经理
			param.setManagerId(0L);
			param.setManagerName("未分配");
			// 总监
			director = super.getUserById(userId);
			param.setDirectorId(director.getId());
			param.setDirectorName(director.getUserName());
			// 总经理
			minister = super.getUserById(director.getParentId());
			param.setMinisterId(minister.getId());
			param.setMinisterName(minister.getUserName());
		}
		user = null;
		manager = null;
		director = null;
		minister = null;
	}

	/**
	 * 升级抽取客户
	 * 
	 * @param ids 	List<客户ID>
	 * @param currUser 操作用户ID
	 * @param ip 操作用户IP
	 * @return customerList	List<客户对象>
	 */
	@Transactional
	public synchronized List<Customer> upgradeExtractCustomer(Long currUser, String ip) {
		SysUser user = super.getUserById(currUser);
		//1.被分配人是否升级人员
		if (user.getUserGroup() != Constant.USER_GROUP_JB 
				&& user.getUserGroup() != Constant.USER_GROUP_JBJL
				&& user.getUserGroup() != Constant.USER_GROUP_JBZJ
				&& user.getUserGroup() != Constant.USER_GROUP_JBZJL) {
			Assert.notNull(null, "非升级人员不能抽取");
		}
		//查询市场部最早开发的20个客户
		List<Long> ids = customerMapper.upgradeExtractCustomerId();
		if (null == ids || ids.size() <= 0) {
			return InstanceUtil.newArrayList();
		}
		List<Customer> customerList = customerMapper.selectBatchIds(ids);
		if (null == customerList || customerList.size() <= 0) {
			return InstanceUtil.newArrayList();
		}
		for (Customer customer : customerList) {
			//3.保存分配日志
			saveAllotUpgradeLog(customer,currUser,currUser,ip,"升级抽取客户",null);
			//4.原归属升级人员客户数 --，被分配升级人员客户数 ++
			//changeUserClubNum(customer,user);
			//5.添加客户的升级部归属
			setAllUpgradeBusiness(customer,currUser);
			//6.如果是首次分配，初始化流程
			customer.setIsUpgrade(1);
			customer.setUpgradeFlow(10000);
			customer.setUpgradeExtraceTime(new Date());
			//7.【DB业务】更新
			customer.setUpdateBy(currUser);
			super.update(customer);
		}
		return customerList;
	}
	/**
	 * 调配升级客户
	 * 
	 * @param ids 	List<客户ID>
	 * @param userId 调配升级目标用户
	 * @param currUser 操作用户ID
	 * @param ip 操作用户IP
	 * @return	customerList	List<客户对象>
	 */
	@Transactional
	public List<Customer> allotUpgrade(List<Long> ids, Long userId, Long currUser, String ip) {
		SysUser user = super.getUserById(userId);
		//1.被分配人是否升级人员
		if (user.getUserGroup() != Constant.USER_GROUP_JB && user.getUserGroup() != Constant.USER_GROUP_JBJL) {
			Assert.notNull(null, "被分配人不是升级人员");
		}
		//2.当前操作值，权限（业务员和升级人员均无权限）
		Long userGroup = super.getUserById(currUser).getUserGroup();
		if (userGroup == Constant.USER_GROUP_YWY || userGroup == Constant.USER_GROUP_JB) {
			Assert.notNull(null, Constant.GROUPISNULL);
		}
		//查询所有实体
		List<Customer> customerList = customerMapper.selectBatchIds(ids);
		for (Customer customer : customerList) {
			//3.保存分配日志
			saveAllotUpgradeLog(customer,userId,currUser,ip,"调配升级客户",null);
			//4.原归属升级人员客户数 --，被分配升级人员客户数 ++
			//changeUserClubNum(customer,user);
			//5.修改客户的升级部归属
			this.setAllUpgradeBusiness(customer,userId);
			//6.如果是首次分配，初始化流程
			if (StrUtils.isNullOrBlank(customer.getUpgradeFlow()) || customer.getUpgradeFlow() == 0) {
				customer.setUpgradeFlow(10000);
			}
			//7.【DB业务】更新
			customer.setUpgradeExtraceTime(new Date());
			customer.setUpdateBy(currUser);
			super.update(customer);
		}
		return customerList;
	}

	/**
	 * 修改客户升级部归属
	 * 
	 * @param customer 客户信息对象
	 * @param userId	升级部升级人员id
	 */
	public void setAllUpgradeBusiness(Customer customer,Long userId) {
		SysUser user = super.getUserById(userId);
		if (user.getUserGroup() == Constant.USER_GROUP_JB) {
			customer.setUpgraderId(user.getId());
			customer.setUpgraderName(user.getAccount());
			SysUser upgradeManager = userService.queryById(user.getParentId());
			if (null != upgradeManager) {
				customer.setUpgradeManagerId(upgradeManager.getId());
				customer.setUpgradeManagerName(upgradeManager.getAccount());
				SysUser upgradeDirector = userService.queryById(upgradeManager.getParentId());
				if (null != upgradeDirector) {
					customer.setUpgradeDirectorId(upgradeDirector.getId());
					customer.setUpgradeDirectorName(upgradeDirector.getAccount());
					SysUser upgradeMinister = userService.queryById(upgradeDirector.getParentId());
					if (null != upgradeMinister) {
						customer.setUpgradeMinisterId(upgradeMinister.getId());
						customer.setUpgradeMinisterName(upgradeMinister.getAccount());
					}
				}
			}
		} else if (user.getUserGroup() == Constant.USER_GROUP_JBJL) {
			customer.setUpgraderId(0L);
			customer.setUpgraderName("未分配");
			customer.setUpgradeManagerId(user.getId());
			customer.setUpgradeManagerName(user.getAccount());
			SysUser upgradeDirector = userService.queryById(user.getParentId());
			if (null != upgradeDirector) {
				customer.setUpgradeDirectorId(upgradeDirector.getId());
				customer.setUpgradeDirectorName(upgradeDirector.getAccount());
				SysUser upgradeMinister = userService.queryById(upgradeDirector.getParentId());
				if (null != upgradeMinister) {
					customer.setUpgradeMinisterId(upgradeMinister.getId());
					customer.setUpgradeMinisterName(upgradeMinister.getAccount());
				}
			}
		} else if (user.getUserGroup() == Constant.USER_GROUP_JBZJ) {
			customer.setUpgraderId(0L);
			customer.setUpgraderName("未分配");
			customer.setUpgradeManagerId(0L);
			customer.setUpgradeManagerName("未分配");
			customer.setUpgradeDirectorId(user.getId());
			customer.setUpgradeDirectorName(user.getAccount());
			SysUser upgradeMinister = userService.queryById(user.getParentId());
			if (null != upgradeMinister) {
				customer.setUpgradeMinisterId(upgradeMinister.getId());
				customer.setUpgradeMinisterName(upgradeMinister.getAccount());
			}
		} else if (user.getUserGroup() == Constant.USER_GROUP_JBZJL) {
			SysUser upgradeMinister = userService.queryById(user.getParentId());
			if (null != upgradeMinister) {
				customer.setUpgraderId(0L);
				customer.setUpgraderName("未分配");
				customer.setUpgradeManagerId(0L);
				customer.setUpgradeManagerName("未分配");
				customer.setUpgradeDirectorId(0L);
				customer.setUpgradeDirectorName("未分配");
				customer.setUpgradeMinisterId(upgradeMinister.getId());
				customer.setUpgradeMinisterName(upgradeMinister.getAccount());
			}
		} else {
			OperationLogTool.operationLog(Constant.ERROR_LOG, "修改加保归属时发生错误，用户ID："+user.getAccount());
		}
	}		

	/**
	 * 保存分配升级人员日志
	 * 
	 * @param customer	客户信息对象
	 * @param userId	用户（业务/经理/总监）id
	 * @param currUser	操作人id
	 * @param ip		操作用户IP
	 * @param type 		操作类型
	 * @param oldBusiness	操作内容备注
	 */
	private void saveAllotUpgradeLog(Customer customer, Long userId, Long currUser, String ip, String type, String oldBusiness) {
		CustomerLog log = new CustomerLog();
		log.setCustomerId(customer.getId());
		log.setCustomerName(customer.getName());
		Long oldUserId = null;
		if (customer.getUpgraderId() != null && customer.getUpgraderId() > 0) {
			oldUserId = customer.getUpgraderId();
		} else if (customer.getUpgradeManagerId() != null && customer.getUpgradeManagerId() > 0) {
			oldUserId = customer.getUpgradeManagerId();
		} else if (customer.getUpgradeDirectorId() != null && customer.getUpgradeDirectorId() > 0) {
			oldUserId = customer.getUpgradeDirectorId();
		}
		log.setOldUser(oldUserId);
		log.setNewUser(userId);
		log.setType(type);
		log.setIp(ip);
		log.setRemark(oldBusiness);
		log.setUpdateBy(currUser);
		super.saveCustomerLog(log);
	}

	/**
	 * 分配客服
	 * 
	 * @param ids	List<客户ID>
	 * @param userId	分配客服id
	 * @param currUser	操作用户id
	 * @param ip		操作用户ip
	 * @return customerList		List<客户对象>
	 */
	public List<Customer> allotServer(List<Long> ids, Long userId, Long currUser, String ip) {
		SysUser user = super.getUserById(userId);
		SysUser kfjl = new SysUser();
		if (user.getUserGroup() == Constant.USER_GROUP_KF) {// 客服
			kfjl = super.getUserById(user.getParentId());
		} else {
			kfjl.setId(userId);
			kfjl.setUserName(user.getUserName());
		}
		SysUser curr = super.getUserById(currUser);
		Long userGroup = curr.getUserGroup();
		// 1.权限判断
		if (userGroup != Constant.USER_GROUP_KFJL && userGroup != Constant.USER_GROUP_KFZJ
				&& userGroup != Constant.ADMIN) {
			Assert.notNull(null, Constant.GROUPISNULL);
		}
		List<Customer> customerList = ((CustomerMapper) mapper).selectBatchIds(ids);
		
		for (Customer item : customerList) {
			//重新分配客服时同步校正股票实虚盘客服信息
			if (StrUtils.isNotNullOrBlank(item) && StrUtils.isNotNullOrBlank(item.getId())) {
				//实盘
				List<Long> cusIds = cusMapper.queryByCustomerId(item.getId());
				for (Long cusId : cusIds) {
					if (StrUtils.isNotNullOrBlank(cusId)) {
						CustomerActualCot cusCot = cusService.queryById(cusId);
						cusCot.setServicerId(userId);
						cusCot.setServicerName(user.getUserName());
						cusService.update(cusCot);
						System.out.print("同步校正股票实盘客服信息");
					}
				}
				//虚盘
				List<Long> teaIds = teaMapper.queryByCustomerId(item.getId());
				for (Long teaId : teaIds) {
					if (StrUtils.isNotNullOrBlank(teaId)) {
						TeacherDirectiveCot teaCot = teaService.queryById(teaId);
						teaCot.setServicerId(userId);
						teaCot.setServicerName(user.getUserName());
						teaService.update(teaCot);
						System.out.print("同步校正股票虚盘客服信息");
					}
				}
			}
			
			item.setServerId(userId); // 客服ID
			item.setServerName(user.getUserName()); // 客服姓名
			item.setServerManagerId(kfjl.getId()); // 客服经理ID
			item.setServerManagerName(kfjl.getUserName()); // 客服经理姓名
			// 2.【DB业务】
			item.setUpdateBy(currUser);
			// 分配时间
			item.setAllotTime(new Date());
			Customer customer = super.update(item);
			// 3.保存流程日志
			CustomerLog log = new CustomerLog();
			log.setCustomerId(customer.getId());
			log.setCustomerName(customer.getName());
			log.setOldUser(item.getOldServerId());
			log.setNewUser(userId);
			log.setType("分配客服");
			log.setUpdateBy(currUser);
			log.setIp(ip);
			super.customerLogService.update(log);
			//再次成交客户手动分配，钉钉通知客服
			if (StrUtils.isNotNullOrBlank(user.getDingId())) {
				Double amount = signApplyService.queryById(item.getCurrentApplyId()).getAmount();
				DingUtil.sendMsg("有分配客户啦！请及时查收！姓名："+item.getName()+"，缴费金额："+amount+" 元。",user.getDingId());
			}
		}
		return customerList;
	}

	/**
	 * 接收客户
	 * 
	 * @param ids	List<客户ID>
	 * @param currUser	操作人id
	 * @param ip		操作人ip
	 * @return	customerList	List<客户对象>
	 */
	public List<Customer> receive(List<Long> ids, Long currUser, String ip) {
		SysUser user = super.getUserById(currUser);
		Long userGroup = user.getUserGroup();
		// 1.权限判断
		if (userGroup != Constant.USER_GROUP_KF && userGroup != Constant.USER_GROUP_KFJL
				&& userGroup != Constant.USER_GROUP_KFZJ && userGroup != Constant.ADMIN) {
			Assert.notNull(null, Constant.GROUPISNULL);
		}
		List<Customer> customerList = ((CustomerMapper) mapper).selectBatchIds(ids);
		for (Customer item : customerList) {

			item.setIsService(1);// 开始服务
			// 2.【DB业务】
			item.setUpdateBy(currUser);
			Customer customer = super.update(item);
			// 3.保存流程日志
			CustomerLog log = new CustomerLog();
			log.setCustomerId(customer.getId());
			log.setCustomerName(customer.getName());
			log.setOldFlow(item.getOldIsService());
			log.setNewFlow(1);
			log.setType("接收客户");
			log.setUpdateBy(currUser);
			log.setIp(ip);
			super.customerLogService.update(log);
		}
		return customerList;
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 包装详情类
	 * 
	 * @param param 客户信息对象
	 * @param currUser 当前操作人id
	 * @return vo  CustomerDetailsVo封装对象
	 */
	public CustomerDetailsVo findFullEntity(Customer param, Long currUser) {
		Long userGroup = super.getUserById(currUser).getUserGroup();
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		Customer customer = super.queryById(param.getId());
		// 手机号解密，拨打电话使用
		String mobile = super.getMobileByMd5Mobile(customer.getMd5Mobile());
		customer.setMobile(mobile);
		/** 详情页显示的手机号 **/
		detailsShowMobile(customer,mobile,currUser);
		/** 签单记录（签单完成） **/
		List<Long> signApplyIds = signApplyService.queryDealByCustomerId(customer.getId());
		List<SignApply> signApply = null;
		if (signApplyIds != null && signApplyIds.size() > 0) {
			signApply = signApplyService.getList(signApplyIds);
		}
		// 添加家庭投保情况
		// relationSplit(signApply,customer);
		/** 客服服务记录 **/
		List<Long> serverRecordIds = serverRecordService.queryByCustomerId(customer.getId());
		List<ServerRecord> serverRecord = null;
		if (serverRecordIds != null && serverRecordIds.size() > 0) {
			serverRecord = serverRecordService.getList(serverRecordIds);
		}
		/** 退款记录 **/
		List<Long> refundIds = refundService.queryByCustomerId(customer.getId());
		List<CustomerRefund> refund = null;
		if (refundIds != null && refundIds.size() > 0) {
			refund = refundService.getList(refundIds);
		}
		List<Long> salerRecordIds = null;
		List<SalerRecord> salerRecord = null;
		if (userGroup == Constant.USER_GROUP_JB || userGroup == Constant.USER_GROUP_JBJL
				|| userGroup == Constant.USER_GROUP_ZJ || userGroup == Constant.ADMIN) {
		}
		/** 市场部电话服务记录 **/
		salerRecordIds = salerRecordService.queryByCustomerId(customer.getId());
		if (salerRecordIds != null && salerRecordIds.size() > 0) {
			salerRecord = salerRecordService.getList(salerRecordIds);
		}

		// 市场部30秒内成功通话记录
		List<Long> salerRecord1Ids = null;
		List<SalerRecord> salerRecord1 = null;
		salerRecord1Ids = salerRecordService.queryRealByCustomerId(customer.getId());
		if (salerRecord1Ids != null && salerRecord1Ids.size() > 0) {
			salerRecord1 = salerRecordService.getList(salerRecord1Ids);
		}

		List<Long> serverRecordMobileIds = null;
		List<ServerRecordMobile> serverRecordMobile = null;
		if (userGroup == Constant.USER_GROUP_KF || userGroup == Constant.USER_GROUP_KFJL
				|| userGroup == Constant.USER_GROUP_KFZJ || userGroup == Constant.ADMIN) {
		}
		/** 客服部电话服务记录 **/
		serverRecordMobileIds = serverRecordMobileService.queryByCustomerId(customer.getId());
		if (serverRecordMobileIds != null && serverRecordMobileIds.size() > 0) {
			serverRecordMobile = serverRecordMobileService.getList(serverRecordMobileIds);
		}
		// 排序
		customerDetailsSort(signApply, serverRecord, refund, salerRecord, salerRecord1,
				serverRecordMobile);
		// 封装到新的实体
		CustomerDetailsVo vo = new CustomerDetailsVo(customer, signApply, serverRecord, refund,
				salerRecord, salerRecord1, serverRecordMobile);
		return vo;
	}

	/**
	 * 商务中心-成交客户-双击详细数据
	 * 
	 * @param param	客户信息对象
	 * @param currUser	操作人id
	 * @return	vo  CustomerDetailsVo封装对象
	 */
	public CustomerDetailsVo findOpenEntity(Customer param, Long currUser) {
		Long userGroup = super.getUserById(currUser).getUserGroup();
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		Customer customer = super.queryById(param.getId());
		// 手机号解密，拨打电话使用
		String mobile = super.getMobileByMd5Mobile(customer.getMd5Mobile());
		customer.setMobile(mobile);
		/** 详情页显示的手机号 **/
		detailsShowMobile(customer,mobile,currUser);
		/** 签单记录（签单完成） **/
		List<Long> signApplyIds = signApplyService.queryDealByCustomerId(customer.getId());
		List<SignApply> signApply = null;
		if (signApplyIds != null && signApplyIds.size() > 0) {
			signApply = signApplyService.getList(signApplyIds);
			for (SignApply signApply2 : signApply) {
				if (StrUtils.isNotNullOrBlank(signApply2.getSubmitCode())) {
					customer.setSubmitCode(WxCollectUtil.getUrl(signApply2.getSubmitCode()));
				}
			}
		}
		
		/** 客服部电话服务记录  **/
		List<Long> serverRecordMobileIds = null;
		List<ServerRecordMobile> serverRecordMobile = null;
		serverRecordMobileIds = serverRecordMobileService.queryByCustomerId(customer.getId());
		if (serverRecordMobileIds != null && serverRecordMobileIds.size() > 0) {
			serverRecordMobile = serverRecordMobileService.getList(serverRecordMobileIds);
		}
		
		/** 市场部所有电话服务记录 **/
		List<Long> salerRecord1Ids = null;
		List<SalerRecord> salerRecord1 = null;
		if (userGroup == Constant.USER_GROUP_JB || userGroup == Constant.USER_GROUP_JBJL
				|| userGroup == Constant.USER_GROUP_ZJ || userGroup == Constant.ADMIN) {
		}
		salerRecord1Ids = salerRecordService.queryByCustomerId(customer.getId());
		if (salerRecord1Ids != null && salerRecord1Ids.size() > 0) {
			salerRecord1 = salerRecordService.getList(salerRecord1Ids);
		}
		// 封装到新的实体
		CustomerDetailsVo vo = new CustomerDetailsVo(customer, signApply, null, null, null, salerRecord1, serverRecordMobile);
		return vo;
	}

	/**
	 * 市场中心(升级中心)-退保订单查询-双击详细数据
	 * 
	 * @param param	客户信息对象
	 * @param currUser	操作人id
	 * @return vo	CustomerDetailsVo 封装对象
	 */
	public CustomerDetailsVo findSignApplyEntity(Customer param, Long currUser) {
		Long userGroup = super.getUserById(currUser).getUserGroup();
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		Customer customer = super.queryById(param.getId());
		// 手机号解密，拨打电话使用
		String mobile = super.getMobileByMd5Mobile(customer.getMd5Mobile());
		customer.setMobile(mobile);
		/** 详情页显示的手机号 **/
		detailsShowMobile(customer,mobile,currUser);
		/** 签单记录（签单完成） **/
		List<Long> signApplyIds = signApplyService.queryByCustomerId(customer.getId());
		List<SignApply> signApply = null;
		if (signApplyIds != null && signApplyIds.size() > 0) {
			signApply = signApplyService.getList(signApplyIds);
		}
		// 封装到新的实体
		CustomerDetailsVo vo = new CustomerDetailsVo(customer, signApply, null, null, null, null, null);
		return vo;
	}

	/**
	 * 客服中心-成交客户-双击详细数据
	 * 
	 * @param param	客户信息对象
	 * @param currUser	操作人id
	 * @return	vo 封装对象 CustomerDetailsVo
	 */
	public CustomerDetailsVo findServerEntity(Customer param, Long currUser) {
		Customer customer = super.queryById(param.getId());
		// 手机号解密，拨打电话使用
		String mobile = super.getMobileByMd5Mobile(customer.getMd5Mobile());
		customer.setMobile(mobile);
		/** 详情页显示的手机号 **/
		detailsShowMobile(customer,mobile,currUser);
		/** 签单记录（签单完成） **/
		List<Long> signApplyIds = signApplyService.queryDealByCustomerId(customer.getId());
		List<SignApply> signApply = null;
		if (signApplyIds != null && signApplyIds.size() > 0) {
			signApply = signApplyService.getList(signApplyIds);
		}
		// 添加家庭投保情况
		// relationSplit(signApply,customer);

		/** 客服部电话服务记录 **/
		List<Long> serverRecordMobileIds = null;
		List<ServerRecordMobile> serverRecordMobile = null;
		serverRecordMobileIds = serverRecordMobileService.queryByCustomerId(customer.getId());
		if (serverRecordMobileIds != null && serverRecordMobileIds.size() > 0) {
			serverRecordMobile = serverRecordMobileService.getList(serverRecordMobileIds);
		}

		/** 客服服务记录 **/
		List<Long> serverRecordIds = serverRecordService.queryByCustomerId(customer.getId());
		List<ServerRecord> serverRecord = null;
		if (serverRecordIds != null && serverRecordIds.size() > 0) {
			serverRecord = serverRecordService.getList(serverRecordIds);
		}

		// 封装到新的实体
		CustomerDetailsVo vo = new CustomerDetailsVo(customer, signApply, serverRecord, null, null, null,
				serverRecordMobile);
		return vo;
	}


	/**
	 * 客户详情排序
	 * 
	 * @param signApply			List<订单信息对象>
	 * @param serverRecord		List<客服服务信息对象>
	 * @param refund			List<退款信息对象>
	 * @param salerRecord		List<市场部通话记录对象>
	 * @param salerRecord1		List<市场部通话记录对象>
	 * @param serverRecordMobile	List<客服部通话记录对象>
	 */
	private void customerDetailsSort(List<SignApply> signApply, List<ServerRecord> serverRecord,
			List<CustomerRefund> refund, List<SalerRecord> salerRecord,
			List<SalerRecord> salerRecord1, List<ServerRecordMobile> serverRecordMobile) {
		if (null != signApply && signApply.size() > 0) {
			// 按创建时间降序排序
			Collections.sort(signApply, new Comparator<SignApply>() {
				@Override
				public int compare(SignApply o1, SignApply o2) {
					if (null != o1 && null != o2 && null != o1.getCreateTime() && null != o2.getCreateTime()) {
						if (o1.getCreateTime().getTime() < o2.getCreateTime().getTime()) {
							return 1;
						} else if (o1.getCreateTime().getTime() == o2.getCreateTime().getTime()) {
							return 0;
						} else {
							return -1;
						}
					}
					return -1;
				}
			});
		}
		if (null != serverRecord && serverRecord.size() > 0) {
			// 按创建时间降序排序
			Collections.sort(serverRecord, new Comparator<ServerRecord>() {
				@Override
				public int compare(ServerRecord o1, ServerRecord o2) {
					if (null != o1 && null != o2 && null != o1.getCreateTime() && null != o2.getCreateTime()) {
						if (o1.getCreateTime().getTime() < o2.getCreateTime().getTime()) {
							return 1;
						} else if (o1.getCreateTime().getTime() == o2.getCreateTime().getTime()) {
							return 0;
						} else {
							return -1;
						}
					}
					return -1;
				}
			});
		}
		if (null != refund && refund.size() > 0) {
			// 按创建时间降序排序
			Collections.sort(refund, new Comparator<CustomerRefund>() {
				@Override
				public int compare(CustomerRefund o1, CustomerRefund o2) {
					if (null != o1 && null != o2 && null != o1.getCreateTime() && null != o2.getCreateTime()) {
						if (o1.getCreateTime().getTime() < o2.getCreateTime().getTime()) {
							return 1;
						} else if (o1.getCreateTime().getTime() == o2.getCreateTime().getTime()) {
							return 0;
						} else {
							return -1;
						}
					}
					return -1;
				}
			});
		}
		if (null != salerRecord && salerRecord.size() > 0) {
			// 按创建时间降序排序
			Collections.sort(salerRecord, new Comparator<SalerRecord>() {
				@Override
				public int compare(SalerRecord o1, SalerRecord o2) {
					if (null != o1 && null != o2 && null != o1.getCreateTime() && null != o2.getCreateTime()) {
						if (o1.getCreateTime().getTime() < o2.getCreateTime().getTime()) {
							return 1;
						} else if (o1.getCreateTime().getTime() == o2.getCreateTime().getTime()) {
							return 0;
						} else {
							return -1;
						}
					}
					return -1;
				}
			});
		}
		if (null != salerRecord1 && salerRecord1.size() > 0) {
			// 按创建时间降序排序
			Collections.sort(salerRecord1, new Comparator<SalerRecord>() {
				@Override
				public int compare(SalerRecord o1, SalerRecord o2) {
					if (null != o1 && null != o2 && null != o1.getCreateTime() && null != o2.getCreateTime()) {
						if (o1.getCreateTime().getTime() < o2.getCreateTime().getTime()) {
							return 1;
						} else if (o1.getCreateTime().getTime() == o2.getCreateTime().getTime()) {
							return 0;
						} else {
							return -1;
						}
					}
					return -1;
				}
			});
		}
		if (null != serverRecordMobile && serverRecordMobile.size() > 0) {
			// 按创建时间降序排序
			Collections.sort(serverRecordMobile, new Comparator<ServerRecordMobile>() {
				@Override
				public int compare(ServerRecordMobile o1, ServerRecordMobile o2) {
					if (null != o1 && null != o2 && null != o1.getCreateTime() && null != o2.getCreateTime()) {
						if (o1.getCreateTime().getTime() < o2.getCreateTime().getTime()) {
							return 1;
						} else if (o1.getCreateTime().getTime() == o2.getCreateTime().getTime()) {
							return 0;
						} else {
							return -1;
						}
					}
					return -1;
				}
			});
		}

	}


	/**
	 * 市场部包装分页类
	 * 
	 * @param params 筛选条件
	 * @return	newPage 包装分页
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Page CustomerListVoPage(Map<String, Object> params,Long userId) {
		Page<Customer> customer = super.query(params);
		List<CustomerListVo> newList = new ArrayList<CustomerListVo>();
		/** 当前用户是否可见客户手机号 **/
		for (Customer item : customer.getRecords()) {
			// 申请签单信息
			SignApply apply = signApplyService.queryById(item.getCurrentApplyId());
			String directorName = item.getDirectorName() == null ? "未分配" : item.getDirectorName();
			String managerName = item.getManagerName() == null ? "未分配" : item.getManagerName();
			String salerName = item.getSalerName() == null ? "未分配" : item.getSalerName();
			//String upgradeDirectorName = item.getUpgradeDirectorName() == null ? "未分配" : item.getUpgradeDirectorName();
			String upgradeManagerName = item.getUpgradeManagerName() == null ? "未分配" : item.getUpgradeManagerName();
			String upgradeName = item.getUpgraderName() == null ? "未分配" : item.getUpgraderName();
			if (item.getIsUpgrade() != null && item.getIsUpgrade() == 1 ) {
				item.setBelong(directorName + "-" + managerName + "-" + salerName + '-' + upgradeManagerName + '-' + upgradeName);
			} else {
				item.setBelong(directorName + "-" + managerName + "-" + salerName);
			}
			
			// 客户信息转换处理
			signApplyService.signApplyInfoTransform(apply);
			newList.add(new CustomerListVo(item, apply));
		}
		// 封装到新的实体
		Page<CustomerListVo> newPage = new Page();
		newPage.setCurrent(customer.getCurrent());
		newPage.setSize(customer.getSize());
		newPage.setTotal(customer.getTotal());
		newPage.setRecords(newList);
		return newPage;
	}
	
	/**
	 * 升级部包装分页类
	 * 
	 * @param params 筛选条件
	 * @return	newPage 包装分页
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Page CustomerListUpgradeVoPage(Map<String, Object> params,Long userId) {
		Page<Customer> customer = super.query(params);
		List<CustomerListVo> newList = new ArrayList<CustomerListVo>();
		for (Customer item : customer.getRecords()) {
			// 申请签单信息
			SignApply apply = signApplyService.queryById(item.getCurrentApplyId());
			String upgradeDirectorName = item.getUpgradeDirectorName() == null ? "未分配" : item.getUpgradeDirectorName();
			String upgradeManagerName = item.getUpgradeManagerName() == null ? "未分配" : item.getUpgradeManagerName();
			String upgraderName = item.getUpgraderName() == null ? "未分配" : item.getUpgraderName();
			item.setBelong(upgradeDirectorName + "-" + upgradeManagerName + "-" + upgraderName);
			// 客户信息转换处理
			signApplyService.signApplyInfoTransform(apply);
			newList.add(new CustomerListVo(item, apply));
		}
		// 封装到新的实体
		Page<CustomerListVo> newPage = new Page();
		newPage.setCurrent(customer.getCurrent());
		newPage.setSize(customer.getSize());
		newPage.setTotal(customer.getTotal());
		newPage.setRecords(newList);
		return newPage;
	}

	/**
	 * 全局搜索
	 * 
	 * @param params	筛选条件
	 * @param currUser	操作人id
	 * @return	customer  page<客户信息对象>
	 */
	public Page<Customer> search(Map<String, Object> params, Long currUser) {
		// 记录是否有无限搜索，防止资源泄露
		if (StrUtils.isNullOrBlank(params.get("name")) && StrUtils.isNullOrBlank(params.get("wechatNo"))
				&& StrUtils.isNullOrBlank(params.get("mobile"))) {
			return null;
		}
		OperationLogTool.operationLog(Constant.SEARCH_LOG, super.getUserById(currUser).getAccount() + "，【全局搜索会员】姓名："
				+ params.get("name") + "，微信：" + params.get("wechatNo") + "，手机号：" + params.get("mobile"));
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		Page<Customer> customer = super.query(params);
		return customer;
	}

	/**
	 * 查询存在该号码 （新增时自己也不能有）
	 * 
	 * @param customer
	 * @return
	 */
	public Long isExist(Customer customer) {
		return ((CustomerMapper) mapper).isExist(customer);
	}

	/**
	 * 查询存在该号码 查询存在该号码（自己除外）
	 * 
	 * @param customer
	 * @return
	 */
	public Long isExists(Customer customer) {
		return ((CustomerMapper) mapper).isExists(customer);
	}

	/**
	 * 是否属于自己的会员 （市场部），（升级部），（客服部），（商务部）
	 * 
	 * @param customer
	 *            客户
	 * @param currUser
	 *            用户ID
	 * @return	isMySelfClub 布尔值
	 */
	public boolean isMySelfClub(Customer customer, Long currUser) {
		boolean isMySelfClub = false;
		Long userGroup = userService.queryById(currUser).getUserGroup();
		if (userGroup.longValue() == Constant.USER_GROUP_JB) {
			return true;
		}
		if (null != customer.getSalerId() && customer.getSalerId().longValue() == currUser.longValue()) {
			isMySelfClub = true;
		} else if (null != customer.getManagerId() && customer.getManagerId().longValue() == currUser.longValue()) {
			isMySelfClub = true;
		} else if (null != customer.getDirectorId() && customer.getDirectorId().longValue() == currUser.longValue()) {
			isMySelfClub = true;
		} else if (null != customer.getMinisterId() && customer.getMinisterId().longValue() == currUser.longValue()) {
			isMySelfClub = true;
		} else if (null != customer.getServerId() && customer.getServerId().longValue() == currUser.longValue()) {
			isMySelfClub = true;
		} else if (null != customer.getServerManagerId() && customer.getServerManagerId().longValue() == currUser.longValue()) {
			isMySelfClub = true;
		} else if (null != customer.getContracterId() && customer.getContracterId().longValue() == currUser.longValue()) {
			isMySelfClub = true;
		}
		return isMySelfClub;
	}

	/**
	 * 修改升级流程
	 * 
	 * @param customerId
	 *            客户ID
	 * @param upgradeFlow
	 *            修改的流程状态值
	 * @param currUser
	 *            当前用户ID
	 * @return	customer 用户信息对象
	 */
	public Customer changeUpgradeFlow(Long customerId, Integer upgradeFlow, Long currUser) {
		Customer customer = super.queryById(customerId);
		customer.setUpgraderId(currUser);
		customer.setUpgradeFlow(upgradeFlow);
		customer.setUpdateBy(currUser);
		return super.update(customer);
	}

	/**
	 * 丢弃客户
	 * 
	 * @param ids
	 *            客户ID集合
	 * @param upgradeFlow
	 *            丢弃的流程状态
	 * @param currUser
	 *            当前用户ID
	 * @return	customerList list<客户信息对象>
	 */
	public List<Customer> discardCustomer(List<Long> ids, Integer upgradeFlow, Long currUser) {
		List<Customer> customerList = new ArrayList<Customer>();
		for (Long id : ids) {
			Customer customer = super.queryById(id);
			customer.setUpgradeFlow(upgradeFlow);
			//
			customer.setUpdateBy(currUser);
			customer.setUpdateTime(new Date());
			customer = super.update(customer);
			// 保存丢弃日志
			OperationLogTool.operationLog("丢弃升级客户（ID：" + customer.getId() + "，姓名：" + customer.getName() + "），操作人（ID："
					+ currUser + "，用户姓名：" + super.getUserById(currUser).getAccount() + "）");
			customerList.add(customer);
		}
		return customerList;
	}

	/**
	 * 分页查询
	 * 
	 * @param param 筛选条件
	 * @return	list<分页客户信息对象>
	 */
	public List<Long> selectIdPage(Map<String, Object> param) {
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(param);
		return ((CustomerMapper) mapper).selectIdPage(param);
	}

	/**
	 * 客户生日提醒
	 * 
	 * @param param	筛选条件
	 * @param curr 操作人id
	 * @return	page  Page<>
	 */
	public Page<Map<String, String>> queryBirthday(Map<String, Object> param, Long curr) {
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(param);
		//市场部归属
		setBusiness(param, curr);
		Page<Map<String, String>> page = getPageMap(param);
		page.setRecords(((CustomerMapper) mapper).queryBirthday(page, param));
		return page;
	}

	/**
	 * 【会员表重新统计】修改会员表（当前签单ID、当前签单时间、签单次数、签单金额、是否签单、首次签单时间、旧签单ID）
	 * 
	 * @param id
	 *            会员ID
	 */
	public void recountSignApply(Long id) {
		Customer customer = this.queryById(id);
		List<Long> signApplyIds = signApplyService.queryDealByCustomerId(id);
		SignApply lastSignApply = signApplyService.queryById(signApplyIds.get(0));
		customer.setCurrentApplyId(lastSignApply.getId());
		customer.setApplyTime(lastSignApply.getCreateTime());
		customer.setInsureNum(signApplyIds.size());
		double insureMoney = 0;
		int insureUpgradeNum = 0;
		for (Long signApplyId : signApplyIds) {
			insureMoney += signApplyService.queryById(signApplyId).getAmount();
			if (signApplyService.queryById(signApplyId).getIsUpgrade() == 1) {
				insureUpgradeNum += 1;
			}
		}
		customer.setInsureUpgradeNum(insureUpgradeNum);
		customer.setInsureMoney(insureMoney);
		if (signApplyIds.size() > 1) {
			customer.setFirstApplyedTime(lastSignApply.getCreateTime());
			customer.setOldApplyId((signApplyIds.get(1)));
		} else {
			customer.setFirstApplyedTime(lastSignApply.getCreateTime());
		}
		this.update(customer);
	}
	
	/**
	 * 批量修改延长服务信息
	 * @param param	客户信息对象
	 * @param ids	list<客户id>
	 * @param names	list<客户姓名>
	 * @param currUser	操作人id
	 * @return null
	 */
	public Customer updateMore(Customer param, List<Long> ids, List<String> names,Long currUser) {
		if (ids != null && ids.size() > 0) {
			for (int i = 0 ; i < ids.size() ; i++) {
				Customer customer = new Customer();
				customer.setId(ids.get(i));
				if (StrUtils.isNotNullOrBlank(names)) {
					customer.setName(names.get(i));
				}
				customer.setStartDate(param.getStartDate());
				customer.setEndDate(param.getEndDate());
				customer.setType(5);
				customer.setRemark(param.getRemark());
				customer.setFlowId(param.getFlowId());
				customer.setServerName(super.getUserById(currUser).getAccount());
				customer.setUpdateBy(currUser);
				//循环修改数据
				super.update(customer);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * 短线掘金和投顾的签单成交客户直接转升级待分配
	 * 1、获取签单成功待升级客户信息
	 * 2、获取当前订单信息
	 * 3、将短险掘金和大咖掌舵签单产品分配给升级人员
	 * 	1）升级一部分配  市场一部签单客户
	 * 	2）升级二部分配 市场二部签单客户
	 * 4、分配成功发送钉钉消息通知并保存分配日志
	 * 
	 */
	public void waitAllotUpgrade () {
		//1、获取签单成功待升级客户信息
		List<Long> ids = customerMapper.queryNotUpdateCustomer();
		if (StrUtils.isNotNullOrBlank(ids) && ids.size() > 0) {
			Customer customer = new Customer();
			for (Long id : ids) {
				customer = this.queryById(id);
				SignApply apply = signApplyService.queryById(customer.getCurrentApplyId());//根据当前签单id获取订单信息
				SysUser upgradeUser = null;
				/*应升级部要求，短线掘金 1042965257223069696  ----->升级部	
				 *投顾	1042965446226796544	的签单客户分配给升级二开部
				 **/
				if (apply.getInsuranceId() ==  1042965257223069696L) {
					//2、获取升级人员升级分配次数最少用户
					 upgradeUser =userService.queryById(userService.selectIdByAllotClubNum(88002666).get(0));
					 
					 if (StrUtils.isNotNullOrBlank(upgradeUser)) {
						 //升级部归属
						 setCustomerToUpgrade(customer, upgradeUser.getId());
						 //分配数++
						 upgradeUser.setAllotClubNum(upgradeUser.getAllotClubNum() + 1);
						 upgradeUser.setClubNum(upgradeUser.getClubNum() + 1);//客户数++
						 userService.update(upgradeUser);
						 
						 customer.setUpgradeFlow(10000);
						 //customer.setUpgradeExtraceTime(new Date());//升级抽取时间
						 customer.setAllotUpgradeTime(new Date());//升级分配时间
						 customer.setIsUpgrade(1);//升级客户
						 customer.setIsWaitUpgrade(0);//升级已分配
						 this.update(customer);
						 //发送钉钉消息通知加保人员
						 if (StrUtils.isNotNullOrBlank(upgradeUser.getDingId())) {
							 DingUtil.sendMsg("给你分配了一个原始客户："+customer.getName()+" "+customer.getFuzzyMobile()+"，请及时沟通。签单时间："+DateUtils.DateToStr(customer.getApplyTime(), DateUtils.TIME_FORMAT_CHINESE), upgradeUser.getDingId());
						 }
						 //保存分配日志
						 saveAllotUpgradeLog(customer,upgradeUser.getId(),2L,null,"自动升级成交客户",null);
					 } else {
						 System.out.println("未开启升级分配！");
					 }
				}  
			}
		}
	}
	
	/**
	 * 投顾的签单成交客户直接分配给二开
	 */
	public void allotTwoUpgrade (Long id) {
		Customer customer = this.queryById(id);
		if (StrUtils.isNotNullOrBlank(customer) && customer.getIsTwoUpgrade() == 0) {
			Long upgraderId = null;
			//3、修改客户的升级部归属
			if (customer.getMinisterId() == 10) {//市场一部 刘帆凯10 -->喻寄强 1145587109295951872
				List<Long>  upgradeUserIds = userService.querybyManageId(1145587109295951872L);
				if (upgradeUserIds != null && upgradeUserIds.size() > 0) {
					upgraderId = upgradeUserIds.get(0);
				} else {
					System.out.println("喻寄强组未开启升级分配");
				}
			} else {//市场二部 刘辉1107936925275811840 --->刘黎明 1145584170724626432
				List<Long>  upgradeUserIds = userService.querybyManageId(1145584170724626432L);
				if (upgradeUserIds != null && upgradeUserIds.size() > 0) {
					upgraderId = upgradeUserIds.get(0);
				} else {
					System.out.println("刘黎明组未开启升级分配");
				}
			}
			if (StrUtils.isNotNullOrBlank(upgraderId)) {
				this.setAllUpgradeBusiness(customer,upgraderId);
				//初始化升级流程
				customer.setUpgradeFlow(10000);
				///2.保存分配日志
				saveAllotUpgradeLog(customer,upgraderId,1L,null,"分配升级二开客户",null);
				//7.【DB业务】更新
				customer.setUpgradeExtraceTime(new Date());//二开分配时间
				customer.setIsTwoUpgrade(1);
				customer.setIsUpgrade(1);//升级客户
				customer.setIsWaitUpgrade(0);//升级已分配
				customer.setUpdateBy(1L);
				super.update(customer);
				//发送钉钉消息通知二开人员
				SysUser upgradeUser = userService.queryById(upgraderId);
				if (StrUtils.isNotNullOrBlank(upgradeUser.getDingId())) {
					DingUtil.sendMsg("给你分配了一个二开升级客户："+customer.getName()+" "+customer.getFuzzyMobile()+"，请及时沟通。签单时间："+DateUtils.DateToStr(customer.getApplyTime(), DateUtils.TIME_FORMAT_CHINESE), upgradeUser.getDingId());
				}
				upgradeUser.setAllotClubNum(upgradeUser.getAllotClubNum() + 1);
				upgradeUser.setClubNum(upgradeUser.getClubNum() + 1);//客户数++
				userService.update(upgradeUser);
				System.out.println("成交客户自动分配给二开升级人员");
			}
		}
	}
	
	/**
	 * 执行成交客户自动 均分 给升级人员 
	 * 
	 * 1、获取签单成功待升级客户信息
	 * 2、获取当前订单信息
	 * 3、将短险掘金和大咖掌舵签单产品均分给升级人员
	 * 	
	 * 4、分配成功发送钉钉消息通知并保存分配日志
	 * 
	 */
	public void allotUpgrade1 () {
		
		//1、获取签单成功待升级客户信息
		List<Long> ids = customerMapper.queryNotUpdateCustomer();
		if (StrUtils.isNotNullOrBlank(ids) && ids.size() > 0) {
			Customer customer = new Customer();
			for (Long id : ids) {
				customer = this.queryById(id);
				SignApply apply = signApplyService.queryById(customer.getCurrentApplyId());//根据当前签单id获取订单信息
				//应升级部要求，只有短线掘金和投顾的签单客户分配给升级
				if (apply.getInsuranceId() ==  1042965257223069696L || apply.getInsuranceId() ==  1042965446226796544L) {
					SysUser upgradeUser = new SysUser();
					//2、获取升级人员升级分配次数最少用户
					 upgradeUser =userService.queryById(userService.selectIdByAllotClubNum(88002666).get(0));
					 //升级部归属
					setCustomerToUpgrade(customer, upgradeUser.getId());
					//分配数++
					upgradeUser.setAllotClubNum(upgradeUser.getAllotClubNum() + 1);
					upgradeUser.setClubNum(upgradeUser.getClubNum() + 1);//客户数++
					userService.update(upgradeUser);
					 
					customer.setUpgradeFlow(10000);
					customer.setAllotUpgradeTime(new Date());//升级分配时间
					customer.setUpgradeExtraceTime(new Date());//升级抽取时间
					customer.setIsUpgrade(1);//升级客户
					this.update(customer);
					//发送钉钉消息通知加保人员
					if (StrUtils.isNotNullOrBlank(upgradeUser.getDingId())) {
						DingUtil.sendMsg("给你分配了一个升级原始客户："+customer.getName()+" "+customer.getFuzzyMobile()+"，请及时沟通。签单时间："+DateUtils.DateToStr(customer.getApplyTime(), DateUtils.TIME_FORMAT_CHINESE), upgradeUser.getDingId());
					}
					//保存分配日志
					saveAllotUpgradeLog(customer,upgradeUser.getId(),2L,null,"自动分配升级",null);
				}
			}
		}
	}
	
	
	/**
	 * 修改客户升级部归属
	 * 
	 * @param customer 客户信息对象
	 * @param user		操作人id
	 * 
	 */
	public void setCustomerToUpgrade(Customer customer,Long userId) {
		SysUser user = super.getUserById(userId);
		if (user.getUserGroup() == Constant.USER_GROUP_JB) {
			customer.setUpgraderId(user.getId());
			customer.setUpgraderName(user.getAccount());
			SysUser upgradeManager = userService.queryById(user.getParentId());
			if (null != upgradeManager) {
				customer.setUpgradeManagerId(upgradeManager.getId());
				customer.setUpgradeManagerName(upgradeManager.getAccount());
				SysUser upgradeDirector = userService.queryById(upgradeManager.getParentId());
				if (null != upgradeDirector) {
					customer.setUpgradeDirectorId(upgradeDirector.getId());
					customer.setUpgradeDirectorName(upgradeDirector.getAccount());
					SysUser upgradeMinister = userService.queryById(upgradeDirector.getParentId());
					if (null != upgradeMinister) {
						customer.setUpgradeMinisterId(upgradeMinister.getId());
						customer.setUpgradeMinisterName(upgradeMinister.getAccount());
					}
				}
			}
		} else if (user.getUserGroup() == Constant.USER_GROUP_JBJL) {
			customer.setUpgraderId(0L);
			customer.setUpgraderName("未分配");
			customer.setUpgradeManagerId(user.getId());
			customer.setUpgradeManagerName(user.getAccount());
			SysUser upgradeDirector = userService.queryById(user.getParentId());
			if (null != upgradeDirector) {
				customer.setUpgradeDirectorId(upgradeDirector.getId());
				customer.setUpgradeDirectorName(upgradeDirector.getAccount());
				SysUser upgradeMinister = userService.queryById(upgradeDirector.getParentId());
				if (null != upgradeMinister) {
					customer.setUpgradeMinisterId(upgradeMinister.getId());
					customer.setUpgradeMinisterName(upgradeMinister.getAccount());
				}
			}
		} else if (user.getUserGroup() == Constant.USER_GROUP_JBZJ) {
			customer.setUpgraderId(0L);
			customer.setUpgraderName("未分配");
			customer.setUpgradeManagerId(0L);
			customer.setUpgradeManagerName("未分配");
			customer.setUpgradeDirectorId(user.getId());
			customer.setUpgradeDirectorName(user.getAccount());
			SysUser upgradeMinister = userService.queryById(user.getParentId());
			if (null != upgradeMinister) {
				customer.setUpgradeMinisterId(upgradeMinister.getId());
				customer.setUpgradeMinisterName(upgradeMinister.getAccount());
			}
		} else if (user.getUserGroup() == Constant.USER_GROUP_JBZJL) {
			SysUser upgradeMinister = userService.queryById(user.getParentId());
			if (null != upgradeMinister) {
				customer.setUpgraderId(0L);
				customer.setUpgraderName("未分配");
				customer.setUpgradeManagerId(0L);
				customer.setUpgradeManagerName("未分配");
				customer.setUpgradeDirectorId(0L);
				customer.setUpgradeDirectorName("未分配");
				customer.setUpgradeMinisterId(upgradeMinister.getId());
				customer.setUpgradeMinisterName(upgradeMinister.getAccount());
			}
		} else {
			OperationLogTool.operationLog(Constant.ERROR_LOG, "修改升级归属时发生错误，用户ID："+user.getAccount());
		}
	}

	/**
	 * 成交客户根据 渠道   城市  省份 年龄 统计
	 * 
	 * @param param	筛选条件
	 * @return	list   List<map<String, Object>>
	 */
	public List<Map<String, Object>> queryGroupCustomer(Map<String, Object> param) {
		List<Map<String, Object>> list = new ArrayList<>();
		if (StrUtils.isNullOrBlank(param.get("dimensions"))) {
			list = customerMapper.queryGroupFromInfo(param);
		}else if (param.get("dimensions").toString().equals("fromInfo")) {
			list = customerMapper.queryGroupFromInfo(param);
		}else if (param.get("dimensions").toString().equals("province")) {
			list = customerMapper.queryGroupProvince(param);
		}else if (param.get("dimensions").toString().equals("city")) {
			list = customerMapper.queryGroupCity(param);
		}else if (param.get("dimensions").toString().equals("age")) {
			list = customerMapper.queryGroupAge(param);
		}
		return list;
	}

	/**
	 * 查询所有客户id
	 * @return list<客户信息id>
	 */
	public List<Long> queryAll() {
		return customerMapper.queryAll();
	}

	/**
	 * 校正投顾客户可用资金
	 */
	public void updateAllUserMoney() {
		try {
			List<Long> ids = customerMapper.queryAll();
			for (Long id : ids) {
				Customer cus = this.queryById(id);
				if (StrUtils.isNotNullOrBlank(cus.getTotalMoney())) {
					customerCotService.updateUserMoney(cus);
				}
				if (StrUtils.isNotNullOrBlank(cus.getTotalTeacherMoney())) {
					teacherCotService.updateTeacherUserMoney(cus);
				}
			}
			System.out.println("实虚盘可用资金校正完成！");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("实虚盘可用资金校正异常！");
		}
		
	}
	/**
	 * 升级中心下属查询
	 * @param params
	 */
	public void setUpgradeSubordinate (Map<String, Object> params) {
		if (StrUtils.isNotNullOrBlank(params.get("subordinate"))) {
			Long userId = StrUtils.toLong(params.get("subordinate"));
			if (userId.longValue() == 0) {
				params.put("upgraderId", 0);
			} else {
				Long userGroup = userService.queryById(userId).getUserGroup();
				//防止不设定用户组，越权
				if (userGroup == Constant.USER_GROUP_ZJL) {//总经理
					params.put("ministerId", userId);
				} else if (userGroup == Constant.USER_GROUP_ZJ) {//总监
					params.put("directorId", userId);
				} else if (userGroup == Constant.USER_GROUP_JL) {//经理
					params.put("managerId", userId);
				} else if (userGroup == Constant.USER_GROUP_YWY) {//业务
					params.put("salerId", userId);
				} else if (userGroup == Constant.USER_GROUP_JB) {//升级人员
					params.put("upgraderId", userId);
				} else if (userGroup == Constant.USER_GROUP_JBJL) {//升级经理
					params.put("upgradeManagerId", userId);
				} else if (userGroup == Constant.USER_GROUP_JBZJ) {//升级总监
					params.put("upgradeDirectorId", userId);
				} else if (userGroup == Constant.USER_GROUP_JBZJL) {//升级总经理
					params.put("upgradeMinisterId", userId);
				} else if (userGroup == Constant.USER_GROUP_KF) {//客服
					params.put("serverId", userId);
				} else if (userGroup == Constant.USER_GROUP_KFJL) {//客服经理
					params.put("serverManagerId", userId);
				} else if (userGroup == Constant.USER_GROUP_HG) {//合规客服
					params.put("contracterId", userId);
				}
			}
		}
	}

	public void moveTenDaysCustomer() {
		//1、获取所有投顾客户信息实体
		List<Long>  ids = customerMapper.queryTenDaysCusomer();
		if (StrUtils.isNotNullOrBlank(ids) && ids.size() > 0 ) {
			List<Customer> customerList = customerMapper.selectBatchIds(ids);
			if (StrUtils.isNotNullOrBlank(customerList) && customerList.size() > 0 ) {
				for (Customer customer : customerList) {
					Long upgraderId = null;
					//3、修改客户的升级部归属
					if (customer.getMinisterId() == 10) {//市场一部 明建波10 -->喻寄强 1145587109295951872
						List<Long>  upgradeUserIds = userService.querybyManageId(1145587109295951872L);
						if (upgradeUserIds != null && upgradeUserIds.size() > 0) {
							upgraderId = upgradeUserIds.get(0);
						} else {
							System.out.println("喻寄强组未开启升级分配");
							continue;
						}
					} else {//市场二部 刘辉1107936925275811840 --->刘黎明 1145584170724626432
						List<Long>  upgradeUserIds = userService.querybyManageId(1145584170724626432L);
						if (upgradeUserIds != null && upgradeUserIds.size() > 0) {
							upgraderId = upgradeUserIds.get(0);
						} else {
							System.out.println("刘黎明组未开启升级分配");
							continue;
						}
					}
					this.setAllUpgradeBusiness(customer,upgraderId);
					//6.如果是首次分配，初始化流程
					customer.setUpgradeFlow(10000);
					///2.保存分配日志
					saveAllotUpgradeLog(customer,upgraderId,1L,null,"分配升级二开客户",null);
					//7.【DB业务】更新
					customer.setUpgradeExtraceTime(new Date());//二开分配时间
					customer.setIsTwoUpgrade(1);
					customer.setUpdateBy(1L);
					super.update(customer);
					//发送钉钉消息通知二开人员
					SysUser upgradeUser = userService.queryById(upgraderId);
					if (StrUtils.isNotNullOrBlank(upgradeUser.getDingId())) {
						DingUtil.sendMsg("给你分配了一个二开升级客户："+customer.getName()+" "+customer.getFuzzyMobile()+"，请及时沟通。签单时间："+DateUtils.DateToStr(customer.getApplyTime(), DateUtils.TIME_FORMAT_CHINESE), upgradeUser.getDingId());
					}
					upgradeUser.setAllotClubNum(upgradeUser.getAllotClubNum() + 1);
					upgradeUser.setClubNum(upgradeUser.getClubNum() + 1);//客户数++
					userService.update(upgradeUser);
				}
			}
		}
		
	}
}
