package com.lazhu.crm.signapply.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.dingtalk.chatbot.message.MarkdownMessage;
import com.lazhu.core.support.Assert;
import com.lazhu.core.util.WxCollectUtil;
import com.lazhu.crm.Constant;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.mapper.CustomerMapper;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.customercount.service.CustomerCountService;
import com.lazhu.crm.customerlog.entity.CustomerLog;
import com.lazhu.crm.resource.entity.Resource;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.crm.serverrecord.entity.ServerRecord;
import com.lazhu.crm.serverrecord.service.ServerRecordService;
import com.lazhu.crm.signapply.entity.SignApply;
import com.lazhu.crm.signapply.mapper.SignApplyMapper;
import com.lazhu.ecp.utils.DateUtils;
import com.lazhu.ecp.utils.DingUtil;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.product.insuranceproduct.entity.InsuranceProduct;
import com.lazhu.product.insuranceproduct.service.InsuranceProductService;
import com.lazhu.sys.model.SysUser;
import com.lazhu.sys.service.SysDicService;
import com.lazhu.t.resourceallot.entity.ResourceAllot;
import com.lazhu.t.resourceallot.service.ResourceAllotService;
import com.lazhu.task.scheduler.CoreTaskService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@Service
@CacheConfig(cacheNames = "signApply")
public class SignApplyService extends BusinessBaseService<SignApply> {

	@Autowired
	ServerRecordService recordServer;//服务操作记录
	
	@Autowired
	SignApplyMapper signApplyMapper;
	
	@Autowired // 资源
	ResourceService resourceService;

	@Autowired // 会员
	CustomerMapper customerMapper;// 必须insert

	@Autowired // 会员
	CustomerService customerService;

	@Autowired // 产品
	InsuranceProductService productService;
	
	@Autowired // 字典
	SysDicService sysDicService;
	
	@Autowired
	ResourceAllotService resourceAllotService;
	
	@Autowired  //客户统计
	CustomerCountService customerCountService;
	
	@Autowired //成交客户升级
	protected CoreTaskService coreTaskService;

	String[] dingEmoji = {"[赞][赞][赞]","[出差][出差][出差]","[跳舞][跳舞][跳舞]","[加油][加油][加油]","[你强][你强][你强]","[广播][广播][广播]","[老板][老板][老板]","[算账][算账][算账]","[爱心][爱心][爱心]","[干杯][干杯][干杯]"};

	public List<Long> queryDealByCustomerId(Long id) {
		return signApplyMapper.queryDealByCustomerId(id);
	}

	/**
	 * 市场部、客户经理修改订单
	 * 
	 * @param param
	 *            订单实体类
	 * @param currUser 
	 * 			操作人id
	 * @return	SignApply 订单信息实体
	 */
	public SignApply updateByBusiness(SignApply param, Long currUser) {
		SysUser user = super.getUserById(param.getUpdateBy());
		SignApply oldSignApply = super.queryById(param.getId());
		// 修改消息提交码
		changeSubmitCode(param, oldSignApply, user.getUserGroup());
		if (StrUtils.isNotNullOrBlank(param.getSubmitCode())) {
			OperationLogTool.operationLog(
					"用户：" + user.getAccount() + "，修改订单ID:" + param.getId() + "的消息提交码：" + param.getSubmitCode());
		}
		// 点赞praise

		return super.update(param);
	}

	/**
	 * 商务部修改订单（审核前） 
	 * 【默认】 
	 * 		0.用户组权限控制 
	 * 【选用修改操作】
	 * 		1.修改产品时，同步修改是否长险 
	 * 		2.手动设置审核时间
	 * 		3.修改消息提交码
	 * 
	 * @param param
	 *            订单实体类
	 */
	public SignApply mazyUpdate(SignApply param) {
		// 0.用户组权限控制
		Long userGroup = super.getUserById(param.getUpdateBy()).getUserGroup();
		if (userGroup != Constant.ADMIN && userGroup != Constant.USER_GROUP_HGJL
				&& userGroup != Constant.USER_GROUP_CW) {
			Assert.notNull(null, Constant.GROUPISNULL);
			return null;
		}

		/** 编辑、修改 **/

		// 获取订单当前实体
		SignApply oldSignApply = super.queryById(param.getId());
		// 1.修改产品时，同步修改是否长险
		if (StrUtils.isNotNullOrBlank(param.getInsuranceId())) {
			if (oldSignApply.getInsuranceId().longValue() != param.getInsuranceId().longValue()) {
				// 保存修改产品日志
				OperationLogTool
						.operationLog("订单（ID：" + oldSignApply.getId() + "，客户姓名：" + oldSignApply.getCustomerName()
								+ "），原产品（ID：" + oldSignApply.getInsuranceId() + "，名称：" + oldSignApply.getInsuranceName()
								+ "），新产品（ID：" + param.getInsuranceId() + "，名称：" + param.getInsuranceName() + "）");
			}
		}
		// 2.手动设置审核时间
		if (StrUtils.isNotNullOrBlank(param.getAuditTime())) {
			if (StrUtils.isNullOrBlank(oldSignApply.getAuditTime())
					|| (oldSignApply.getAuditTime().getTime() != param.getAuditTime().getTime())) {
				// 保存修改审核时间日志
				OperationLogTool
						.operationLog("【商务部】修改订单审核时间，客户：" + param.getCustomerName() + "，审核时间：" + param.getAuditTime());
			}
		}
		// 3.添加或修改升级人员、是否升级签单
		// addUpgrader(param);
		// 添加或修改保险经纪人
		addSaler(param);
		// 4.修改消息提交码
		changeSubmitCode(param, oldSignApply, userGroup);
		return super.update(param);
	}

	/**
	 * 添加或修改业务归属
	 * 
	 * @param param 订单信息实体
	 */
	private void addSaler(SignApply param) {
		if (StrUtils.isNotNullOrBlank(param.getSalerId()) && param.getSalerId() > 0) {
			SysUser saler = super.getUserById(param.getSalerId());
			param.setSalerName(saler.getAccount());
			if (StrUtils.isNotNullOrBlank(saler) && StrUtils.isNotNullOrBlank(saler.getParentId())) {
				SysUser manager = super.getUserById(saler.getParentId());
				if (StrUtils.isNotNullOrBlank(manager)) {
					param.setManagerId(manager.getId());
					param.setManagerName(manager.getAccount());
					SysUser director = super.getUserById(manager.getParentId());
					if (StrUtils.isNotNullOrBlank(director)) {
						param.setDirectorId(director.getId());
						param.setDirectorName(director.getAccount());
						SysUser minister = super.getUserById(director.getParentId());
						if (StrUtils.isNotNullOrBlank(minister)) {
							param.setMinisterId(minister.getId());
							param.setMinisterName(minister.getAccount());
						}
					}
					param.setBelong(director.getAccount() + "-" + manager.getAccount() + "-" + saler.getAccount());
				}
			}
		}
	}
	/**
	 * 添加或修改升级人员、是否升级签单
	 * 1、添加客户经理归属信息
	 * 2、添加升级签单次数（当全部第几次签单）
	 * 3、修改为升级签单
	 * 
	 * @param param 订单信息实体
	 */
		
	@SuppressWarnings("unused")
	private void addUpgrader(SignApply param) { 
		if (StrUtils.isNotNullOrBlank(param.getUpgraderId())) { 
			//添加客户经理归属信息
			setUpgradeBusinessInfo(param); 
			//添加升级签单次数（当前为第几次签单）
			setUpgradeNum(param); 
			//修改为升级签单 param.setIsUpgrade(1);
			OperationLogTool.operationLog("【商务部】修改订单为升级签单，客户："+param.getCustomerName()+"，升级人员："+userService.queryById(param.getUpgraderId()).getAccount()); 
		}
	}
		

	/**
	 * 添加客户经理归属信息
	 * 
	 * @param param	订单信息实体
	 */
		
	private void setUpgradeBusinessInfo(SignApply param) {
		SysUser upgrader = super.getUserById(param.getUpgraderId());
		param.setUpgraderName(upgrader.getAccount());
		if (StrUtils.isNotNullOrBlank(upgrader) && StrUtils.isNotNullOrBlank(upgrader.getParentId())) {
			SysUser upgradeManager = super.getUserById(upgrader.getParentId());
			if (StrUtils.isNotNullOrBlank(upgradeManager)) {
				param.setUpgradeManagerId(upgradeManager.getId());
				param.setUpgradeManagerName(upgradeManager.getAccount());
				SysUser upgradeDirector = super.getUserById(upgradeManager.getParentId());
				if (StrUtils.isNotNullOrBlank(upgradeDirector)) {
					param.setUpgradeDirectorId(upgradeDirector.getId());
					param.setUpgradeDirectorName(upgradeDirector.getAccount());
					SysUser upgradeMinister = super.getUserById(upgradeDirector.getParentId());
					if (StrUtils.isNotNullOrBlank(upgradeMinister)) {
						param.setUpgradeMinisterId(upgradeMinister.getId());
						param.setUpgradeMinisterName(upgradeMinister.getAccount());
					}
				}
			}
		}
	}
		

	/**
	 * 修改消息提交码
	 * 
	 * 规则1：如果当前订单已经有了消息提交码，则不可修改。
	 * 规则2：如果当前订单是客户的最后一次订单，则同步修改客户的消息提交码，否则只修改当前订单的消息提交码。
	 * 
	 * @param param	当前订单信息实体
	 * @param signApply	 订单信息实体
	 * @param userGroup	用户所属角色id
	 */
	private void changeSubmitCode(SignApply param, SignApply signApply, Long userGroup) {
		if (StrUtils.isNotNullOrBlank(param.getSubmitCode())) {
			if (StrUtils.isNotNullOrBlank(signApply.getSubmitCode()) && userGroup == Constant.USER_GROUP_YWY) {
				param.setSubmitCode(signApply.getSubmitCode());
				OperationLogTool.operationLog(Constant.WARN_LOG, "当前订单已有消息提交码，修改失败");
			} else {
				
			}
		}
	}

	/**
	 * 审核通过
	 * 
	 * 1、用户组权限控制
	 * 2、区分不同的签单情况，查询该客户历史审核通过的次数
	 * 		首次签单   1）、查看资源表ID是否已经存在于会员表
	 * 				2）、判断当前签单微信和电话号码是否已经签单
	 * 		多次签单
	 * 3、保存修改签单
	 * 4、分配合规人员
	 * 5、保存流程日志
	 * 6、生成/更新客户统计表
	 * 
	 * @param ids List<订单信息实体id>
	 * @param userId	分配合规人员id
	 * @param currUser	操作人id
	 * @param ip		操作人ip
	 * @return	applyList List<订单信息实体>
	 */
	@Transactional
	public synchronized List<SignApply> audit(List<Long> ids, Long userId, Long currUser, String ip) {
		SysUser user = super.getUserById(currUser);
		Long userGroup = user.getUserGroup();
		// 1.权限判断
		if (userGroup != Constant.USER_GROUP_HGJL && userGroup != Constant.ADMIN) {
			Assert.notNull(null, Constant.GROUPISNULL);
		}
		List<SignApply> applyList = signApplyMapper.selectBatchIds(ids);
		for (SignApply item : applyList) {
			// 系统变量，是否验证保单号
			Resource resource = null;
			Customer customer = null;
			// 3.区分不同的签单情况，查询该客户历史审核通过的次数
			Long auditNum = signApplyMapper.queryAuditNum(item.getCustomerId());
			// 3.1资源签单（首次）
			if (auditNum == null || auditNum == 0) {
				resource = resourceService.queryById(item.getCustomerId());
				// 3.1.1查看资源表ID是否已经存在于会员表
				customer = customerService.queryById(resource.getId());
				if (customer != null && customer.getId() > 0) {
					// 该会员ID已经存在
					Assert.notNull(null, "审核异常，非首次签单");
				} else {
					// 判断当前签单微信和电话号码是否已经签单
					customer = new Customer();
					customer.setWechatNo(item.getWechatNo());
					customer.setMd5Mobile(item.getMd5Mobile());
					if (null != customerService.isExist(customer) && customerService.isExist(customer) > 0) {
						Assert.notNull(null, "当前号码已签单，请核查！");
					}
				}
				// 3.2会员签单（多次）
			} else if (auditNum != null && auditNum > 0) {

			}

			// 4.【DB操作】保存修改签单
			Integer status = item.getSignStatus();
			item.setAuditId(user.getId());
			item.setAuditName(user.getUserName());
			item.setSignStatus(Constant.STATUS_HG);
			if (StrUtils.isNullOrBlank(item.getAuditTime())) {
				item.setAuditTime(new Date());
			}
			item.setUpdateBy(currUser);
			// 5.分配合规人员
			item.setComplianceId(userId);
			item.setComplianceName(super.getUserById(userId).getAccount());
			SignApply signApply = super.update(item);
			// 6.保存流程日志
			CustomerLog log = new CustomerLog();
			log.setCustomerId(signApply.getCustomerId());
			log.setCustomerName(signApply.getCustomerName());
			log.setOldFlow(status);
			log.setNewFlow(signApply.getSignStatus());
			log.setType("审核通过");
			log.setUpdateBy(signApply.getUpdateBy());
			log.setIp(ip);
			super.saveCustomerLog(log);
			
			//生成或者更新客户统计表
			customerCountService.applyCustomerCount(item.getId(), item.getIsUpgrade());
		}
		return applyList;
	}

	/**
	 * 审核驳回
	 * 
	 * @param param		订单信息实体
	 * @param currUser	操作人id
	 * @param ip		操作人ip
	 * @return	signApply	订单信息实体
	 */
	@Transactional
	public synchronized SignApply reject(SignApply param, Long currUser, String ip) {
		SysUser user = super.getUserById(currUser);
		Long userGroup = user.getUserGroup();
		// 1.权限判断
		if (userGroup != Constant.USER_GROUP_HGJL && userGroup != Constant.ADMIN) {
			Assert.notNull(null, Constant.GROUPISNULL);
		}
		if (param.getSignStatus() != null && param.getSignStatus() != Constant.STATUS_DSH) {
			Assert.notNull(null, "非待审核签单，不可驳回");
		}
		// 2.区分不同的签单情况，查询该客户历史审核通过的次数
		Long auditNum = signApplyMapper.queryAuditNum(param.getCustomerId());
		// 2.1资源签单（首次）
		if (auditNum == null || auditNum == 0) {
			Resource resource = resourceService.queryById(param.getCustomerId());
			// 2.1.1修改资源表（是否签单的状态为否）
			resource.setFlowId(resource.getOldFlowId());
			resourceService.update(resource);

			// 2.2会员签单（多次）
		} else if (auditNum != null && auditNum > 0) {
			Customer customer = customerService.queryById(param.getCustomerId());
			// 2.2.1修改会员表
			customer.setFlowId(Constant.CLUB_ZC);// 当前流程
			customerService.update(customer);
		} else {
			Assert.notNull(null, "未知的错误");
		}
		// 3.【DB操作】保存修改签单
		Integer status = param.getSignStatus();
		param.setAuditId(user.getId());
		param.setAuditName(user.getUserName());
		param.setSignStatus(Constant.STATUS_SHBH);
		param.setAuditTime(new Date());
		param.setUpdateBy(currUser);
		SignApply signApply = super.update(param);
		// 4.保存日志
		CustomerLog log = new CustomerLog();
		log.setCustomerId(signApply.getCustomerId());
		log.setCustomerName(signApply.getCustomerName());
		log.setOldFlow(status);
		log.setNewFlow(signApply.getSignStatus());
		log.setType("审核驳回");
		log.setUpdateBy(signApply.getUpdateBy());
		log.setIp(ip);
		super.saveCustomerLog(log);
		return signApply;
	}

	/**
	 * 处理合规
	 * 1、用户组权限控制
	 * 2、订单流程状态判断
	 * 3、区分不同的签单情况，查询该客户历史审核通过的次数
	 * 	首次签单	1）、查询资源id是否已存在于客户会员表中
	 * 		2）、生成会员表并拷贝资源属性到会员中
	 * 		3）、修改资源表，回写分配表资源开发流程
	 *  多次签单	1）、修改会员表，签单次数+1
	 *  		2）、更新服务结束日期
	 *  4、保存修改签单
	 *  5、保存流程日志
	 *  6、短险掘金/大咖掌舵产品订单客户分配升级
	 *  7、推广资源签单成功（市场部、非升级部），发送钉钉通知
	 * 
	 * @param ids
	 *            List<订单信息实体id>
	 * @param isHg
	 *            合规（0待合规，1合规通过，5合规未通过）
	 * @param currUser
	 *            操作人ID
	 * @param ip	操作人ip
	 * @return	List<订单信息实体>
	 */
	@Transactional
	public synchronized List<SignApply> hg(List<Long> ids, Integer isHg, Long currUser, String ip) {
		Date date = new Date();
		SysUser user = super.getUserById(currUser);
		Long userGroup = user.getUserGroup();
		// 1.权限判断
		if (userGroup != Constant.USER_GROUP_HG && userGroup != Constant.USER_GROUP_HGJL
				&& userGroup != Constant.ADMIN) {
			Assert.notNull(null, Constant.GROUPISNULL);
		}
		Resource resource = null;
		Customer customer = null;
		for (Long id : ids) {
			SignApply item = super.queryById(id);
			// 2.流程状态
			if (item.getSignStatus() != Constant.STATUS_HG) {
				Assert.notNull(null, "非待合规签单，不可合规");
			}
			resource = null;
			customer = null;
			// 4.区分不同的签单情况，查询该客户历史审核通过的次数
			Long auditNum = signApplyMapper.queryAuditNum(item.getCustomerId());
			// 4.1资源签单（首次）
			if (auditNum == null || auditNum == 0) {
				resource = resourceService.queryById(item.getCustomerId());
				// 4.1.1查看资源表ID是否已经存在于会员表
				customer = customerService.queryById(resource.getId());
				if (customer != null && customer.getId() > 0) {
					// 该会员ID已经存在
					Assert.notNull(null, "当前签单已合规，不可重复合规");
				} else {
					// 4.1.2生成会员表
					customer = new Customer();
					// 4.1.3拷贝资源属性到会员中
					setFieldByResource(customer, resource);

					customer.setName(item.getCustomerName()); // 市场部乱填名字或代理投保，已签单表为准
					customer.setFlowId(Constant.CLUB_ZC);
					customer.setInsureNum(1); // 签单次数为1
					customer.setType(1);
					if (item.getAmount() != null) {
						customer.setInsureMoney(item.getAmount()); // 签单金额
					}
					customer.setCurrentApplyId(item.getId()); // 当前签单ID
					customer.setIsService(0); // 是否服务
					customer.setContracterId(currUser);
					customer.setContracterName(super.getUserById(currUser).getUserName());
					// 记录首次签单时间
					customer.setFirstApplyedTime(item.getCreateTime());
					// 为了排序
					customer.setApplyTime(item.getCreateTime());
					// 4.1.4插入到会员表
					customer.setUpdateTime(date);
					// 审核时间
					customer.setAuditTime(item.getAuditTime());
					//如果是升级部签单，升级流程赋值为成交
					//如果是新单的升级签单（这种情况比较少）
					if (StrUtils.isNotNullOrBlank(item.getIsUpgrade()) && item.getIsUpgrade() == 1) {
						customer.setUpgraderId(item.getUpgraderId());
						customer.setUpgradeManagerId(item.getUpgradeManagerId());
						customer.setUpgradeDirectorId(item.getUpgradeDirectorId());
						customer.setUpgraderName(item.getUpgraderName());
						customer.setUpgradeManagerName(item.getUpgradeManagerName());
						customer.setUpgradeDirectorName(item.getUpgradeDirectorName());
						customer.setAllotUpgradeTime(date);
						customer.setUpgradeFlow(99999);
						//添加升级签单次数（当前为第几次签单）
						setUpgradeNum(item);
						//如果只有升级部归属信息（市场部经理ID为空），则标记为不是新客
					}
					//服务期
					customer.setStartDate(item.getStartDate());
					customer.setEndDate(item.getEndDate());
					if (item.getIsInterest() == 1) {
						customer.setIsInterest(1);
					}
					customer.setCreateTime(new Date());
					customer.setUpdateTime(new Date());
					customerMapper.insert(customer);
					// 4.1.5修改资源表
					resource.setUpdateBy(currUser);
					resource.setFlowId(Constant.FLOW_CJ);
					resourceService.update(resource);
					//回写分配表资源开发流程
					resourceAllotService.updateFlowId(resource.getId(),Constant.FLOW_CJ);
				}
				// 4.2会员签单（多次）
			} else if (auditNum != null && auditNum > 0) {
				customer = customerService.queryById(item.getCustomerId());
				// 4.2.1修改会员表
				customer.setOldApplyId(customer.getCurrentApplyId());
				customer.setOldIsService(customer.getIsService());
				customer.setOldFlowId(customer.getFlowId());
				customer.setOldServerId(customer.getServerId());
				customer.setOldServerName(customer.getServerName());

				customer.setFlowId(Constant.CLUB_ZC);
				customer.setInsureNum(customer.getInsureNum() + 1); // 签单次数加1
				customer.setType(2);
				if (item.getIsUpgrade() == 1) {
					customer.setInsureUpgradeNum(customer.getInsureUpgradeNum() + 1);	//加保签单次数加1
				}
				if (customer.getInsureMoney() != null && item.getAmount() != null) {
					customer.setInsureMoney(customer.getInsureMoney() + item.getAmount()); // 签单金额
				}
				customer.setCurrentApplyId(item.getId()); // 当前签单ID
				customer.setIsService(0); // 是否服务
				customer.setContracterId(currUser);
				customer.setContracterName(super.getUserById(currUser).getUserName());
				customer.setUpdateBy(currUser);
				customer.setUpdateTime(date);
				// 为了排序
				customer.setApplyTime(item.getCreateTime());
				customer.setName(item.getCustomerName()); // 市场部乱填名字或代理投保，已签单表为准
				// 审核时间
				customer.setAuditTime(item.getAuditTime());
				// 如果是客户经理签单，升级流程赋值为成交
				if (StrUtils.isNotNullOrBlank(item.getIsUpgrade()) && item.getIsUpgrade() == 1) {
					customer.setUpgradeFlow(99999);
				}
				// 再次签单客户自动接收并开始服务（已有客服）
				customer.setAllotTime(date);
				// 客户添加消息提交码或升级消息提交码
				//服务期结束日期更新
				if (item.getEndDate().getTime() > customer.getEndDate().getTime()) {
					customer.setEndDate(item.getEndDate());
				}
				//老客户自动分配，钉钉通知客服
				SysUser server = userService.queryById(customer.getServerId());
				if (null != server) {
					String dingId = server.getDingId();
					if (StrUtils.isNotNullOrBlank(dingId)) {
						DingUtil.sendMsg("（成交客户 ）有分配新客户啦！请及时查收！姓名："+customer.getName()+"，签单金额："+item.getAmount()+" 元。",dingId);
					}
					//重新接收
					customer.setIsService(0);
				}
				if (item.getIsInterest() == 1) {
					customer.setIsInterest(1);
				}
				customerService.update(customer);
			} else {
				Assert.notNull(null, "未知的错误");
			}
			
			// 5.【DB操作】保存修改签单
			Integer status = item.getSignStatus();
			item.setComplianceId(user.getId());
			item.setComplianceName(user.getUserName());
			item.setSignStatus(Constant.STATUS_SUCCESS);
			item.setComplianceTime(date);
			item.setUpdateBy(currUser);
			SignApply signApply = super.update(item);

			// 7.保存流程日志
			CustomerLog log = new CustomerLog();
			log.setCustomerId(signApply.getCustomerId());
			log.setCustomerName(signApply.getCustomerName());
			log.setOldFlow(status);
			log.setNewFlow(signApply.getSignStatus());
			log.setType("合规通过");
			log.setUpdateBy(signApply.getUpdateBy());
			log.setIp(ip);
			super.saveCustomerLog(log);
			
			//短线掘金的签单客户分配给升级部
			if (signApply.getInsuranceId() ==  1042965257223069696L ){
				coreTaskService.customerToUpgrade();
				
			}
			//投顾的签单客户直接分配给二开升级部
			if (signApply.getInsuranceId() ==  1042965446226796544L){
				customerService.allotTwoUpgrade(customer.getId());
				
			}
			//推广资源签单成功（市场部、非升级部），发送钉钉通知
			if (StrUtils.isNotNullOrBlank(item.getFromInfo()) && !"personal".equals(item.getFromInfo())) {
				//非升级部
				if (null != item.getIsUpgrade() && item.getIsUpgrade() == 0) {
					tResourceSignSendMessage(item);
				}
			}
		}
		return signApplyMapper.selectBatchIds(ids);
	}

	/**
	 * 合规不通过
	 * 1、用户组权限控制
	 * 2、修改状态为合规不通过，保存修改签单
	 * 3、区分不同的签单情况，查询该客户历史审核通过的次数
	 * 		首次签单		修改资源表（是否签单的状态为否）
	 * 		多次签单		修改会员表
	 * 4、保存流程日志
	 * 
	 * @param param		订单信息实体	
	 * @param currUser	操作人id	
	 * @param ip		操作人ip
	 * @return	signApply 订单信息实体
	 */
	@Transactional
	public synchronized SignApply hgReject(SignApply param, Long currUser, String ip) {
		SysUser user = super.getUserById(currUser);
		if (user.getUserGroup() != Constant.ADMIN && user.getUserGroup() != Constant.USER_GROUP_HGJL
				&& user.getUserGroup() != Constant.USER_GROUP_HG) {
			Assert.notNull(null, Constant.GROUPISNULL);
			return null;
		}
		// 1.修改状态为合规不通过
		// 5.【DB操作】保存修改签单
		Integer status = param.getSignStatus();
		param.setComplianceId(user.getId());
		param.setComplianceName(user.getUserName());
		param.setSignStatus(Constant.STATUS_HGBH);
		param.setComplianceTime(new Date());
		param.setUpdateBy(currUser);

		SignApply signApply = super.update(param);
		// 2.区分不同的签单情况，查询该客户历史审核通过的次数
		Long auditNum = signApplyMapper.queryAuditNum(param.getCustomerId());
		// 2.1资源签单（首次）
		if (auditNum == null || auditNum == 0) {
			Resource resource = resourceService.queryById(param.getCustomerId());
			// 2.1.1修改资源表（是否签单的状态为否）
			resource.setFlowId(resource.getOldFlowId());
			resourceService.update(resource);

			// 2.2会员签单（多次）
		} else if (auditNum != null && auditNum > 0) {
			Customer customer = customerService.queryById(param.getCustomerId());
			// 2.2.1修改会员表
			if (StrUtils.isNotNullOrBlank(customer.getOldFlowId())) {
				customer.setFlowId(customer.getOldFlowId());// 当前流程
			} else {
				customer.setFlowId(Constant.CLUB_ZC);// 当前流程
			}
			customerService.update(customer);
		} else {
			Assert.notNull(null, "未知的错误");
		}
		// 3.保存流程日志
		CustomerLog log = new CustomerLog();
		log.setCustomerId(signApply.getCustomerId());
		log.setCustomerName(signApply.getCustomerName());
		log.setOldFlow(status);
		log.setNewFlow(signApply.getSignStatus());
		log.setType("合规不通过");
		log.setUpdateBy(signApply.getUpdateBy());
		log.setIp(ip);
		super.saveCustomerLog(log);
		return signApply;
	}

	/////////////////////////////////////////////////////////////////

	/**
	 * 商务部订单管理/订单查询（待审核，审核驳回，待合规，合规不通过）
	 * 1、用户组权限控制
	 * 2、订单分页信息转换
	 * 
	 * @param param		筛选条件
	 * @param currUser	操作人id
	 * @return	Page 封装分页
	 */
	@SuppressWarnings("rawtypes")
	public Page contracterList(Map<String, Object> params, Long currUser) {
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		// 测试
		// testService.testSignApply(params);
		String name = StrUtils.toString(params.get("customerName"));
		if ("对比订单".equals(name)) {
			params.remove("customerName");
		}
		// 商务部归属
		super.setContracter(params, currUser);
		// 下属查询
		super.setSubordinate(params);
		Page<Long> page = getPage(params);
		page.setRecords(signApplyMapper.selectIdPage(page, params));
		// 订单信息转换
		return signApplyInfoTransform(page);
	}

	/**
	 * 市场部订单管理/订单查询（待审核，审核驳回，待合规，合规不通过）
	 * 1、市场部用户组权限控制
	 * 2、订单分页信息转换
	 * 
	 * @param param		筛选条件
	 * @param currUser	操作人id
	 * @return	Page 封装分页
	 */
	@SuppressWarnings("rawtypes")
	public Page marketList(Map<String, Object> params, Long currUser) {
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		// 市场部归属
		super.setBusiness(params, currUser);
		// 下属查询
		super.setSubordinate(params);
		Page<Long> page = getPage(params);
		page.setRecords(signApplyMapper.selectIdPage(page, params));
		// 订单信息转换
		return signApplyInfoTransform(page);
	}

	/**
	 * 客户经理订单管理/订单查询（待审核，审核驳回，待合规，合规不通过）
	 * 
	 * 1、用户组权限控制
	 * 2、订单分页信息转换
	 * 
	 * @param param		筛选条件
	 * @param currUser	操作人id
	 * @return	Page 封装分页
	 */
	@SuppressWarnings("rawtypes")
	public Page upgradeList(Map<String, Object> params, Long currUser) {
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		// 市场部归属
		super.setBusiness(params, currUser);
		// 客户经理归属
		SysUser user = userService.queryById(currUser);
		Long userGroup = user.getUserGroup();
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		if (userGroup == Constant.USER_GROUP_ZJ) {//升级总监
			params.put("upgradeDirectorId", currUser);
		} else if (userGroup == Constant.USER_GROUP_ZJL) {//升级总经理
			params.put("upgradeMinisterId", currUser);
		} else {
			super.setUpgradeBusiness(params, currUser);
		}
		// 设置市场客户经理是否升级签单
		params.put("isUpgrade", 1);
		// 下属查询
		super.setSubordinate(params);
		Page<Long> page = getPage(params);
		page.setRecords(signApplyMapper.selectIdPage(page, params));
		// 订单信息转换
		return signApplyInfoTransform(page);
	}

	////////////////////////////////////// 【不分页】
	////////////////////////////////////// ////////////////////////////////////////

	/**
	 * 商务部待审核
	 * 
	 * 1、用户组权限控制
	 * 2、订单分页信息转换
	 * 
	 * @param param		筛选条件
	 * @param currUser	操作人id
	 * @return	List<订单信息实体>
	 */
	public List<SignApply> contracterAuditData(Map<String, Object> params, Long currUser) {
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		// 设置商务部权限（用户组）
		super.setContracter(params, currUser);
		params.put("signStatus", Constant.STATUS_DSH);
		List<SignApply> signApplyList = super.queryList(params);
		// 订单信息转换
		return signApplyInfoTransform(signApplyList,currUser);
	}

	/**
	 * 商务部待合规列表
	 * 1、用户组权限控制
	 * 2、订单信息转换
	 * 
	 * @param param		筛选条件
	 * @param currUser	操作人id
	 * @return Page 封装分页
	 */
	@SuppressWarnings({ "rawtypes"})
	public Page contracterHgData(Map<String, Object> params, Long currUser) {
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		// 设置商务部权限（用户组）
		super.setContracter(params, currUser);
		// 下属查询complianceId
		if (StrUtils.isNotNullOrBlank(params.get("subordinate"))) {
			Long userId = StrUtils.toLong(params.get("subordinate"));
			if (userId.longValue() == 0) {
				params.put("salerId", 0);
			} else {
				Long group = userService.queryById(userId).getUserGroup();
				//防止不设定用户组，越权
				if (group == Constant.USER_GROUP_ZJL) {//总经理
					params.put("ministerId", userId);
				} else if (group == Constant.USER_GROUP_ZJ) {//总监
					params.put("directorId", userId);
				} else if (group == Constant.USER_GROUP_JL) {//经理
					params.put("managerId", userId);
				} else if (group == Constant.USER_GROUP_YWY) {//业务
					params.put("salerId", userId);
				} else if (group == Constant.USER_GROUP_JB) {//升级人员
					params.put("upgraderId", userId);
				} else if (group == Constant.USER_GROUP_JBJL) {//升级经理
					params.put("upgradeManagerId", userId);
				} else if (group == Constant.USER_GROUP_JBZJ) {//升级总监
					params.put("upgradeDirectorId", userId);
				} else if (group == Constant.USER_GROUP_JBZJL) {//升级总经理
					params.put("upgradeMinisterId", userId);
				} else if (group == Constant.USER_GROUP_KF) {//客服
					params.put("serverId", userId);
				} else if (group == Constant.USER_GROUP_KFJL) {//客服经理
					params.put("serverManagerId", userId);
				} else if (group == Constant.USER_GROUP_HG) {//合规客服
					params.put("complianceId", userId);
				}
			}
		}
		//设置合规权限
		Long userGroup = userService.queryById(currUser).getUserGroup();
		//防止不设定用户组，越权
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		if (userGroup == Constant.USER_GROUP_HG) {//合规客服
			params.put("complianceId", currUser);
		}
		
		params.put("signStatus", Constant.STATUS_HG);
		// 下属查询
		super.setSubordinate(params);
		Page<Long> page = getPage(params);
		page.setRecords(signApplyMapper.selectIdPage(page, params));
		//List<SignApply> signApplyList = super.queryList(params);
		// 订单信息转换
		return signApplyInfoTransformHg(page,currUser);
	}

	/**
	 * 市场部待审核（只能看到自己申请的签单）
	 * 	1、用户组权限设置
	 * ·2、订单信息转换
	 * @param param		筛选条件
	 * @param currUser	操作人id
	 * @return	List<订单信息实体>
	 */
	public List<SignApply> checkPendingMarketListNonsort(Map<String, Object> params, Long currUser) {
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		// 设置市场部归属
		super.setBusiness(params, currUser);
		// 待审核、待合规
		params.put("isUpgrade", 0);
		params.put("signStatusWait", 1);
		List<SignApply> signApplyList = super.queryList(params);
		// 订单信息转换
		return signApplyInfoTransform(signApplyList,currUser);
	}

	/**
	 * 客户经理待审核（只能看到本部门申请的签单）
	 * 
	 * @param param	筛选条件
	 * @param currUser	操作人id
	 * @return	List<订单对象实体>
	 */
	public List<SignApply> checkPendingUpgradeListNonsort(Map<String, Object> params, Long currUser) {
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		// 设置客户经理归属
		SysUser user = userService.queryById(currUser);
		Long userGroup = user.getUserGroup();
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		if (userGroup == Constant.USER_GROUP_ZJ) {//升级总监
			params.put("upgradeDirectorId", currUser);
		} else if (userGroup == Constant.USER_GROUP_ZJL) {//升级总经理
			params.put("upgradeMinisterId", currUser);
		} else {
			super.setUpgradeBusiness(params, currUser);
		}
		// 设置市场客户经理是否升级签单
		// super.setIsUpgrade(params, currUser);
		params.put("signStatusWait", 1);
		List<SignApply> signApplyList = super.queryList(params);
		// 订单信息转换
		return signApplyInfoTransform(signApplyList,currUser);
	}

	/**
	 * 插入资源表客户信息，业务信息到签单表【申请签单时】
	 * 
	 * @param param	订单信息实体
	 * @param resource	资源信息实体
	 */
	private void setResourceInfo(Resource resource,SignApply param) {
		// 插入客户信息
		param.setCustomerName(resource.getName());
		param.setWechatNo(resource.getWechatNo());
		param.setFuzzyMobile(resource.getFuzzyMobile());
		param.setMd5Mobile(resource.getMd5Mobile());
		
		param.setProvince(resource.getProvince());
		param.setCity(resource.getCity());
		
		// 插入业务信息
		param.setSalerId(resource.getSalerId());
		param.setManagerId(resource.getManagerId());
		param.setDirectorId(resource.getDirectorId());
		param.setMinisterId(resource.getMinisterId());
		param.setSalerName(resource.getSalerName());
		param.setManagerName(resource.getManagerName());
		param.setDirectorName(resource.getDirectorName());
		param.setMinisterName(resource.getMinisterName());
		// 市场部归属
		String DirectorName = resource.getDirectorName() == null ? "未分配" : resource.getDirectorName();
		String ManagerName = resource.getManagerName() == null ? "未分配" : resource.getManagerName();
		String SalerName = resource.getSalerName() == null ? "未分配" : resource.getSalerName();
		param.setBelong(DirectorName + "-" + ManagerName + "-" + SalerName);
		//
		param.setFromInfo(resource.getFromInfo());
	}

	/**
	 * 插入会员表客户信息，业务信息到签单表【再次申请签单时】
	 * 
	 * @param param	订单信息实体
	 * @param customer	客户会员信息实体
	 */
	private void setCustomerInfo(Customer customer,SignApply param) {
		// 插入客户信息
		param.setCustomerName(customer.getName());
		param.setWechatNo(customer.getWechatNo());
		param.setFuzzyMobile(customer.getFuzzyMobile());
		param.setMd5Mobile(customer.getMd5Mobile());
		
		param.setProvince(customer.getProvince());
		param.setCity(customer.getCity());
		
		//插入市场部信息
		param.setSalerId(customer.getSalerId());
		param.setManagerId(customer.getManagerId());
		param.setDirectorId(customer.getDirectorId());
		param.setMinisterId(customer.getMinisterId());
		param.setSalerName(customer.getSalerName());
		param.setManagerName(customer.getManagerName());
		param.setDirectorName(customer.getDirectorName());
		param.setMinisterName(customer.getMinisterName());
		// 市场部归属
		String DirectorName = customer.getDirectorName() == null ? "未分配" : customer.getDirectorName();
		String ManagerName = customer.getManagerName() == null ? "未分配" : customer.getManagerName();
		String SalerName = customer.getSalerName() == null ? "未分配" : customer.getSalerName();
		if (customer.getIsUpgrade() != null && customer.getIsUpgrade() == 1) {
			String upgradeManagerName = customer.getUpgradeManagerName() == null ? "未分配" : customer.getUpgradeManagerName();
			String upgraderName = customer.getUpgraderName() == null ? "未分配" : customer.getUpgraderName();
			param.setBelong(DirectorName + "-" + ManagerName + "-" + SalerName +'+' + upgradeManagerName + "-" + upgraderName);
		} else {
			param.setBelong(DirectorName + "-" + ManagerName + "-" + SalerName);
		}
		
		param.setFromInfo(customer.getFromInfo());
		param.setInsureNum(customer.getInsureNum());
		//
	}

	/**
	 * 插入会员表客户信息，业务信息到签单表【升级申请签单时】
	 * 
	 * @param param
	 * @param resource
	 */
	private void setCustomerUpgradeInfo(Customer customer,SignApply param) {
		//插入客户信息
		param.setCustomerName(customer.getName());
		param.setWechatNo(customer.getWechatNo());
		param.setFuzzyMobile(customer.getFuzzyMobile());
		param.setMd5Mobile(customer.getMd5Mobile());
		//插入市场部信息
		param.setSalerId(customer.getSalerId());
		param.setManagerId(customer.getManagerId());
		param.setDirectorId(customer.getDirectorId());
		param.setMinisterId(customer.getMinisterId());
		param.setSalerName(customer.getSalerName());
		param.setManagerName(customer.getManagerName());
		param.setDirectorName(customer.getDirectorName());
		param.setMinisterName(customer.getMinisterName());
		
		//插入升级部信息
		param.setUpgraderId(customer.getUpgraderId());
		param.setUpgradeManagerId(customer.getUpgradeManagerId());
		param.setUpgradeDirectorId(customer.getUpgradeDirectorId());
		param.setUpgradeMinisterId(customer.getUpgradeMinisterId());
		param.setUpgraderName(customer.getUpgraderName());
		param.setUpgradeManagerName(customer.getUpgradeManagerName());
		param.setUpgradeDirectorName(customer.getUpgradeDirectorName());
		param.setUpgradeMinisterName(customer.getUpgradeMinisterName());
		
		//升级部归属
		String upgradeManagerName	= param.getUpgradeManagerName()==null?"未分配升级":param.getUpgradeManagerName();
		String upgraderName	= param.getUpgraderName()==null?"未分配升级":param.getUpgraderName();
		//param.setBelong(upgradeDirectorName+"-"+upgradeManagerName+"-"+upgraderName);
		//市场部总监--市场部经理--业务--升级经理--升级
		param.setBelong(customer.getDirectorName()+"-"+customer.getManagerName()+"-"+customer.getSalerName()+"+"+ upgradeManagerName+'-'+upgraderName);
		param.setFromInfo(customer.getFromInfo());
		param.setInsureNum(customer.getInsureNum());
		//
	}

	/**
	 * 拷贝资源的属性到会员
	 * 
	 * @param customer
	 * @param resource
	 */
	private void setFieldByResource(Customer customer, Resource resource) {
		customer.setId(resource.getId());
		customer.setName(resource.getName());
		customer.setFromInfo(resource.getFromInfo());
		customer.setEnterTime(resource.getEnterTime());
		customer.setWechatNo(resource.getWechatNo());
		customer.setQq(resource.getQq());
		customer.setFuzzyMobile(resource.getFuzzyMobile());
	    customer.setMd5Mobile(resource.getMd5Mobile());
	    customer.setPhone(resource.getPhone());
	    customer.setAge(resource.getAge());//应该取订单的年龄
	    customer.setSex(resource.getSex());//应该取订单的性别
	    customer.setBirthday(resource.getBirthday());//应该取订单的生日
	    customer.setDuty(resource.getDuty());//职业
	    customer.setProvince(resource.getProvince());
	    customer.setCity(resource.getCity());
	    //保存客户手机归属地信息
	    customer.setSalerId(resource.getSalerId());
	    customer.setManagerId(resource.getManagerId());
	    customer.setDirectorId(resource.getDirectorId());
	    customer.setMinisterId(resource.getMinisterId());
	    customer.setSalerName(resource.getSalerName());
	    customer.setManagerName(resource.getManagerName());
	    customer.setDirectorName(resource.getDirectorName());
	    customer.setMinisterName(resource.getMinisterName());
	    customer.setRemark(resource.getRemark());
	}

	/**
	 * 统计某天录入的资源成功总签单数
	 * 
	 * @param someday
	 *            日期时间
	 * @return
	 */
	public Long applyNumByDate(Date someday) {
		return signApplyMapper.applyNumByDate(someday);
	}

	/**
	 * 统计某天总签单金额
	 * 
	 * @param promotionDate
	 *            日期
	 * @return
	 */
	public Double sumAmountByDate(Date someday) {
		return signApplyMapper.sumAmountByDate(someday);
	}

	/**
	 * 通过会员ID查询所有的签单记录
	 * 
	 * @param customerId
	 *            会员ID
	 * @return
	 */
	public List<Long> queryByCustomerId(Long customerId) {
		return signApplyMapper.queryByCustomerId(customerId);
	}

	/**
	 * 统计市场部的资源成交数
	 * 
	 * @param fromInfo
	 *            来源渠道
	 * @param params
	 *            查询条件（区间开始时间，区间结束时间，用户ID）
	 * @return
	 */
	public Long enterNumByFromInfoAndBusiness(List<Long> fromInfoList, Map<String, Object> params) {
		return signApplyMapper.enterNumByFromInfoAndBusiness(fromInfoList, params);
	}

	/**
	 * 统计市场部的资源成交数
	 * 
	 * @param fromInfo
	 *            来源渠道
	 * @param params
	 *            查询条件（区间开始时间，区间结束时间，用户ID）
	 * @return
	 */
	public Long applyNumByFromInfoAndBusiness(List<Long> fromInfoList, Map<String, Object> params) {
		return signApplyMapper.applyNumByFromInfoAndBusiness(fromInfoList, params);
	}

	/**
	 * 统计市场部的资源金额
	 * 
	 * @param fromInfo
	 *            来源渠道
	 * @param params
	 *            查询条件（区间开始时间，区间结束时间，用户ID）
	 * @return
	 */
	public Double insureMoneyByEnterAndBusiness(List<Long> fromInfoList, Map<String, Object> params) {
		return signApplyMapper.insureMoneyByEnterAndBusiness(fromInfoList, params);
	}

	public List<Long> selectFirstByCustomerId(Long customerId) {
		return signApplyMapper.selectFirstByCustomerId(customerId);
	}

	/** 推广每日新客统计 **/
	// @Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
	public Long effectiveNumFromInfo(List<Long> fromInfoList, Date promotionDate) {
		return signApplyMapper.effectiveNumFromInfo(fromInfoList, promotionDate);
	}

	// @Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
	public Long signNumFromInfo(List<Long> fromInfoList, Date promotionDate) {
		return signApplyMapper.signNumFromInfo(fromInfoList, promotionDate);
	}

	// @Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
	public Double sumAmountFromInfo(List<Long> fromInfoList, Date promotionDate) {
		return signApplyMapper.sumAmountFromInfo(fromInfoList, promotionDate);
	}

	// @Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
	public Long daySignNumFromInfo(List<Long> fromInfoList, Date promotionDate) {
		return signApplyMapper.daySignNumFromInfo(fromInfoList, promotionDate);
	}

	// @Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
	public Double daySignPremiumFromInfo(List<Long> fromInfoList, Date promotionDate) {
		return signApplyMapper.daySignPremiumFromInfo(fromInfoList, promotionDate);
	}

	public Double isLongTermCommission(Integer isLongTerm, Date promotionDate) {
		return signApplyMapper.isLongTermCommission(isLongTerm, promotionDate);
	}

	// @Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
	public Long customerNumByProductId(Long productId) {
		return signApplyMapper.customerNumByProductId(productId);
	}

	// @Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
	public Long signNumByProductId(Long productId) {
		return signApplyMapper.signNumByProductId(productId);
	}

	// @Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
	public Double sumAmountByProductId(Long productId) {
		return signApplyMapper.sumAmountByProductId(productId);
	}



	/***************************************** 业务拆分，优化方法体 ****************************************/


	/**
	 * 【回写资源状态】，修改当前资源状态为已成交、签单时间
	 */
	private void writeBackResource(Resource resource) {
		Integer flowId = resource.getFlowId();
		resource.setOldFlowId(flowId);
		resource.setFlowId(Constant.FLOW_DSH);
		resourceService.update(resource);
	}

	/**
	 * 根据当前用户来校正资源归属信息
	 * 
	 * @param userGroup
	 *            用户组
	 */
	private void updateResourceBusinessInfo(Resource resource,SysUser user) {
		resourceService.setAllBusiness(resource, user.getId());
		resourceService.update(resource);
		// 如果是业务签单
		if (user.getUserGroup() == Constant.USER_GROUP_YWY) {
		} else {
			// 待扩展
		}
	}

	/**
	 * 根据当前用户来校正会员归属信息
	 * 
	 * @param userGroup
	 *            用户组
	 */
	private void updateClubBusinessInfo(Customer customer,SysUser user) {
		customerService.setAllBusiness(customer, user.getId());
		customerService.update(customer);
		// 如果是业务签单
		if (user.getUserGroup() == Constant.USER_GROUP_YWY) {
		} else {
			// 待扩展
		}
	}

	/**
	 * 根据当前用户来校正会员客户经理归属信息
	 * @param userGroup
	 *            用户组
	 */
	private void updateUpgradeBusinessInfo(Customer customer,SysUser user) {
		customerService.setAllUpgradeBusiness(customer, user.getId());
		customerService.update(customer);
		//如果是业务签单
		if (user.getUserGroup() == Constant.USER_GROUP_JB) {
		} else {
			//待扩展
		}
	}


	/**
	 * 检查当前客户是否有待审核，待合规的签单。且签单间隔时间需大于1分钟
	 * 
	 * @param customerId
	 */
	private void customerSignApplyCheck(Long customerId) {
		List<Long> ids = signApplyMapper.queryByCustomerId(customerId);
		if (ids != null && ids.size() > 0) {
			List<SignApply> applyList = signApplyMapper.selectBatchIds(ids);
			if (applyList != null && applyList.size() > 0) {
				for (SignApply apply : applyList) {
					if (apply.getSignStatus() != null && apply.getSignStatus() == Constant.STATUS_DSH) {
						Assert.notNull(null, "该客户有签单正在审核，请稍后再签单");
					}
					if (apply.getSignStatus() != null && apply.getSignStatus() == Constant.STATUS_HG) {
						Assert.notNull(null, "该客户有签单正在合规，请稍后再签单");
					}
					// 4.距离上次申请签单时间间隔是否大于允许值（1分钟）
					if (DateUtils.timeOut(apply.getCreateTime(), 1L)) {
						Assert.notNull(null, "1分钟内只能申请一次签单，请稍后");
					}
				}
			}
		}
	}

	/**
	 * 检查微信号、手机号是否已成交
	 * 
	 * @param wechatNo
	 * @param mobile
	 */
	private void numberCheck(String wechatNo, String mobile) {
		Customer customer = new Customer();
		customer.setWechatNo(wechatNo);
		customer.setMobile(mobile);
		Long isExist = customerService.isExist(customer);
		if (null != isExist && isExist > 0) {
			Assert.notNull(null, "当前号码已签单，请核查！");
		}
	}

	/**
	 * 记录当前签单次数（历史成功签单次数加1）
	 * 
	 * @param param
	 */
	private void setInsureNum(SignApply param) {
		List<Long> ids = signApplyMapper.queryDealByCustomerId(param.getCustomerId());
		param.setInsureNum(ids.size() + 1);
	}

	/**
	 * 记录当前升级签单次数（历史成功升级签单次数加1）
	 * 
	 * @param param
	 */
	private void setUpgradeNum(SignApply param) {
		Integer upgradeNum = signApplyMapper.countUpgradeNum(param.getCustomerId());
		param.setUpgradeNum(upgradeNum + 1);
	}

	/**
	 * 修改当会员流程为正在签单，并记录旧的流程
	 * 
	 * @param curr
	 */
	private void writeBackClub(Customer customer,Long curr) {
		customer.setOldFlowId(Constant.CLUB_ZC);
		customer.setFlowId(Constant.CLUB_QD);
		customerService.update(customer);
	}

	/***************************** 模板签单 **************************/

	/**
	 * 模板签单（待开发资源）
	 * 
	 * 0.权限控制 
	 * 1.检查该资源是否已签单 
	 * 2.检查当前签单微信和电话号码是否已经签单 
	 * 3.检查保单号是否正在签单或已签单
	 * 4.检查当前客户是否有待审核，待合规的签单。且签单间隔时间需大于1分钟 
	 * 5.根据当前用户来校正资源归属信息 
	 * 6.插入客户信息，市场部信息到订单
	 * 7.插入产品名称、是否长险、保险公司ID 计算产品折标费用 
	 * 8.【提交订单】 
	 * 9.【回写资源状态】，修改当前资源状态为已成交
	 * 10.【保存流程日志】
	 * 
	 * @param param
	 *            提交的订单
	 * @param curr
	 *            用户ID
	 * @param ip
	 *            用户IP
	 * @return 生成的签单
	 */
	@Transactional
	public SignApply modelSignApplyResource(SignApply param, Long curr, String ip) {
		SysUser user = super.getUserById(curr);
		// 0.权限控制
		if (user.getUserGroup() != Constant.USER_GROUP_YWY && user.getUserGroup() != Constant.USER_GROUP_JL
				&& user.getUserGroup() != Constant.USER_GROUP_ZJ && user.getUserGroup() != Constant.ADMIN) {
			Assert.notNull(null, "只有经纪人，经理，总监才能签单");
		}
		// 获取资源信息
		Resource resource = resourceService.queryById(param.getCustomerId());
		// 2.检查当前签单微信和电话号码是否已经签单
		numberCheck(param.getWechatNo(), param.getMobile());
		// 4.检查当前客户是否有待审核，待合规的签单。且签单间隔时间需大于1分钟
		customerSignApplyCheck(param.getCustomerId());
		// 5.根据当前用户来校正资源归属信息
		updateResourceBusinessInfo(resource,user);
		// 6.插入客户信息，业务信息到订单
		setResourceInfo(resource,param);
		// 6.插入产品信息到订单
		addProductInfo(param);
		// 10.【提交订单】
		SignApply signApply = super.update(param);
		// 11.【回写资源状态】，修改当前资源状态为已成交、签单时间
		writeBackResource(resource);
		// 12.【保存流程日志】
		super.saveCustomerLog(signApply.getCustomerId(), signApply.getCustomerName(), "首次申请签单", curr, ip);
		return signApply;
	}

	/**
	 * 添加产品信息到订单
	 * @param param
	 */
	private void addProductInfo(SignApply param) {
		if (StrUtils.isNotNullOrBlank(param.getInsuranceId())) {
			InsuranceProduct product = productService.queryById(param.getInsuranceId());
			if (null != product) {
				param.setInsuranceName(product.getName());
				param.setIsInterest(product.getType());
			}
		}
	}

	/**
	 * 模板签单（会员签单）
	 * 
	 * 0.权限控制 1.检查保单号是否正在签单或已签单 2.该会员当前流程是否允许申请签单
	 * 3.检查当前客户是否有待审核，待合规的签单。且签单间隔时间需大于1分钟 4.根据当前用户来校正资源归属信息 5.插入客户信息，市场部信息到订单
	 * 6.插入产品名称、是否长险、保险公司ID 7.记录当前签单次数（历史成功签单次数加1） 计算产品折标费用 8.【提交订单】
	 * 9.【回写会员状态】，修改当会员流程为正在签单，并记录旧的流程 10.【保存流程日志】
	 * 
	 * @param param
	 *            提交的订单
	 * @param curr
	 *            用户ID
	 * @param ip
	 *            用户IP
	 * @return 生成的签单
	 */
	@Transactional
	public SignApply modelSignApplyClub(SignApply param, Long curr, String ip) {
		SysUser user = super.getUserById(curr);
		if (user.getUserGroup() != Constant.USER_GROUP_YWY && user.getUserGroup() != Constant.USER_GROUP_JL
				&& user.getUserGroup() != Constant.USER_GROUP_ZJ && user.getUserGroup() != Constant.ADMIN) {
			Assert.notNull(null, "只有经纪人，经理，总监才能签单");
		}
		// 2.该会员当前流程是否允许申请签单
		Customer customer = customerService.queryById(param.getCustomerId());
		if (customer.getFlowId() == Constant.CLUB_QD) {
			Assert.notNull(null, "该会员正在签单，不可重复签单");
		}
		// 3.检查当前客户是否有待审核，待合规的签单。且签单间隔时间需大于1分钟
		customerSignApplyCheck(param.getCustomerId());
		// 4.根据当前用户来校正会员归属信息
		updateClubBusinessInfo(customer,user);
		// 5.插入客户信息，市场部信息到订单
		setCustomerInfo(customer,param);
		// 6.插入产品信息到订单
		addProductInfo(param);
		// 7.记录当前签单次数（历史成功签单次数加1）
		setInsureNum(param);
		// 8.【提交订单】
		SignApply signApply = super.update(param);
		// 9.修改当会员流程为正在签单，并记录旧的流程
		writeBackClub(customer,curr);
		// 10.【保存流程日志】
		super.saveCustomerLog(signApply.getCustomerId(), signApply.getCustomerName(), "再次申请签单", curr, ip);
		return signApply;
	}

	/**
	 * 模板签单（升级签单）
	 * 
	 * 0.权限控制 1.检查保单号是否正在签单或已签单 2.该会员当前流程是否允许申请签单
	 * 3.检查当前客户是否有待审核，待合规的签单。且签单间隔时间需大于1分钟 4.根据当前用户来校正升级归属信息
	 * 5.插入客户信息，市场部、客户经理归属信息到订单 6.插入产品名称、是否长险、保险公司ID 7.记录当前签单次数（历史成功签单次数加1）
	 * 8.记录当前升级签单次数（历史成功升级签单次数加1） 计算产品折标费用 9.【提交订单】 10.修改当会员流程为正在签单，并记录旧的流程
	 * 加保人员签单，记录加保人员ID（不共享原则） 11.【保存流程日志】
	 * 
	 * @param param
	 *            提交的订单
	 * @param curr
	 *            用户ID
	 * @param ip
	 *            用户IP
	 * @return 生成的签单
	 */
	@Transactional
	public SignApply modelSignApplyUpgrade(SignApply param, Long curr, String ip) {
		SysUser user = super.getUserById(curr);
		if (user.getUserGroup() != Constant.USER_GROUP_JB && user.getUserGroup() != Constant.ADMIN) {
			Assert.notNull(null, "只有客户经理才能签单");
		}
		// 2.该会员当前流程是否允许申请签单
		Customer customer = customerService.queryById(param.getCustomerId());
		if (customer.getFlowId() == Constant.CLUB_QD) {
			Assert.notNull(null, "该会员正在签单，不可重复签单");
		}
		// 3.检查当前客户是否有待审核，待合规的签单。且签单间隔时间需大于1分钟
		customerSignApplyCheck(param.getCustomerId());
		// 4.根据当前用户来校正升级归属信息
		updateUpgradeBusinessInfo(customer,user);
		// 5.插入客户信息，市场部、客户经理归属信息到订单
		setCustomerUpgradeInfo(customer,param);
		// 6.插入产品信息到订单
		addProductInfo(param);
		// 7.记录当前签单次数（历史成功签单次数加1）
		setInsureNum(param);
		// 8.记录当前升级签单次数（历史成功升级签单次数加1）
		setUpgradeNum(param);
		// 9.【提交订单】
		SignApply signApply = super.update(param);
		// 10.修改当会员流程为正在签单，并记录旧的流程
		writeBackClub(customer,curr);
		// 11.【保存流程日志】
		String type = param.getUpgradeNum() == 1 ? "客户经理首次签单" : "客户经理再次签单";
		super.saveCustomerLog(signApply.getCustomerId(), signApply.getCustomerName(), type, curr, ip);
		return signApply;
	}


	/**
	 * 订单信息转换处理
	 * 
	 * @param signApplyPage
	 * @return
	 */
	private Page<SignApply> signApplyInfoTransform(Page<Long> page) {
		Page<SignApply> signApplyPage = super.getPage(page);
		if (signApplyPage != null && signApplyPage.getRecords() != null && signApplyPage.getRecords().size() > 0) {
			List<SignApply> newList = new ArrayList<SignApply>();
			for (SignApply item : signApplyPage.getRecords()) {
				// EZ-TOKEN转码WTS_TOKEN
				if (null != item && StrUtils.isNotNullOrBlank(item.getSubmitCode())) {
					String oldCode = item.getSubmitCode();
					item.setSubmitCodeOld(WxCollectUtil.getOldUrl(oldCode));
					item.setSubmitCode(WxCollectUtil.getUrl(item.getSubmitCode()));
				} else {
					item.setSubmitCode("javascript:void(0);");
				}
				newList.add(item);
			}
			// 暂时不需要重新排序
			signApplyPage.setRecords(newList);
		}
		return signApplyPage;
	}
	/**
	 * 订单信息转换处理
	 * 
	 * @param signApplyPage
	 * @return
	 */
	private Page<SignApply> signApplyInfoTransformHg(Page<Long> page,Long currUser) {
		Page<SignApply> signApplyPage = super.getPage(page);
		if (signApplyPage != null && signApplyPage.getRecords() != null && signApplyPage.getRecords().size() > 0) {
			List<SignApply> newList = new ArrayList<SignApply>();
			for (SignApply item : signApplyPage.getRecords()) {
				//列表显示手机号
				Integer isMobileVisible = userService.queryIsMobileVisibleById(currUser);
				if (null != isMobileVisible && isMobileVisible == 1) {
					String mobile = super.getMobileByMd5Mobile(item.getMd5Mobile());
					//解密手机号
					item.setMobile(mobile);
				} else {
					//掩码手机号
					item.setMobile(item.getFuzzyMobile());
				}
				// EZ-TOKEN转码WTS_TOKEN
				if (null != item && StrUtils.isNotNullOrBlank(item.getSubmitCode())) {
					String oldCode = item.getSubmitCode();
					item.setSubmitCodeOld(WxCollectUtil.getOldUrl(oldCode));
					item.setSubmitCode(WxCollectUtil.getUrl(item.getSubmitCode()));
				} else {
					item.setSubmitCode("javascript:void(0);");
				}
				newList.add(item);
			}
			// 暂时不需要重新排序
			signApplyPage.setRecords(newList);
		}
		return signApplyPage;
	}

	/**
	 * 订单信息转换处理
	 * 
	 * @param signApplyList
	 * @return
	 */
	private List<SignApply> signApplyInfoTransform(List<SignApply> signApplyList,Long currUser) {
		if (signApplyList != null && signApplyList.size() > 0) {
			for (SignApply item : signApplyList) {
				//列表显示手机号
				Integer isMobileVisible = userService.queryIsMobileVisibleById(currUser);
				if (null != isMobileVisible && isMobileVisible == 1) {
					String mobile = super.getMobileByMd5Mobile(item.getMd5Mobile());
					//解密手机号
					item.setMobile(mobile);
				} else {
					//掩码手机号
					item.setMobile(item.getFuzzyMobile());
				}
				
				// EZ-TOKEN转码WTS_TOKEN
				if (null != item && StrUtils.isNotNullOrBlank(item.getSubmitCode())) {
					String oldCode = item.getSubmitCode();
					item.setSubmitCodeOld(WxCollectUtil.getOldUrl(oldCode));
					item.setSubmitCode(WxCollectUtil.getUrl(item.getSubmitCode()));
				} else {
					item.setSubmitCode("javascript:void(0);");
				}
			}
		}
		return signApplyList;
	}
	
	/**
	 * 订单信息转换处理
	 * 
	 * @param signApply
	 * @return
	 */
	public SignApply signApplyInfoTransform(SignApply signApply) {
		// EZ-TOKEN转码WTS_TOKEN
		if (null != signApply && StrUtils.isNotNullOrBlank(signApply.getSubmitCode())) {
			String oldCode = signApply.getSubmitCode();
			signApply.setSubmitCodeOld(WxCollectUtil.getOldUrl(oldCode));
			signApply.setSubmitCode(WxCollectUtil.getUrl(signApply.getSubmitCode()));
		} else {
			signApply.setSubmitCode("javascript:void(0);");
		}
		return signApply;
	}

	/**
	 * 市场部/成交客户聊天观摩
	 * 
	 * @param param
	 * @param currUser
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Page recommendList(Map<String, Object> params, Long currUser) {
		// 手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		// 聊天记录不为空
		params.put("submitCodeIsNotNull", 1);
		Page<SignApply> signApplyPage = super.query(params);
		if (signApplyPage != null && signApplyPage.getRecords() != null && signApplyPage.getRecords().size() > 0) {
			List<SignApply> newList = new ArrayList<SignApply>();
			for (SignApply item : signApplyPage.getRecords()) {
				// EZ-TOKEN转码WTS_TOKEN
				signApplyInfoTransform(item);
				newList.add(item);
			}
			// 暂时不需要重新排序
			signApplyPage.setRecords(newList);
		}
		return signApplyPage;
	}

	/**
	 * 今日成交资源数（保险经纪人）
	 * 
	 * @param userId
	 * @return
	 */
	public Integer queryToDaySignNumBySalerId(Long userId) {
		return signApplyMapper.queryToDaySignNumByUpgraderId(userId);
	}

	/**
	 * 今日成交资源数（加保人员）
	 * 
	 * @param userId
	 * @return
	 */
	public Integer queryToDaySignNumByUpgraderId(Long userId) {
		return signApplyMapper.queryToDaySignNumByUpgraderId(userId);
	}

	/**
	 * 商务部订单管理（删除订单） 0.删除订单、删除返佣记录表 1.1 如果是首次签单，删除会员表；修改资源流程和是否签单； 1.2
	 * 如果是多次签单，修改会员表（当前签单ID、当前签单时间、签单次数、签单金额、是否签单、首次签单时间、旧签单ID）
	 * 
	 * @param id
	 * @param curr
	 */
	@Transactional
	public void contracterDelete(Long id, Long curr) {
		SysUser user = super.getUserById(curr);
		if (user.getUserGroup() != Constant.USER_GROUP_GLY && user.getUserGroup() != Constant.ADMIN) {
			Assert.notNull(null, Constant.GROUPISNULL);
		}
		SignApply signApply = this.queryById(id);
		// 删除订单
		this.del(id, curr);
		// 是否首次签单
		List<Long> dealIds = signApplyMapper.queryDealByCustomerId(signApply.getCustomerId());
		if (null == dealIds || dealIds.size() == 0) {
			// 删除会员表
			customerService.delete(signApply.getCustomerId());
			// 修改资源表流程和是否签单
			Resource resource = resourceService.queryById(signApply.getCustomerId());
			resource.setFlowId(resource.getOldFlowId());
			resourceService.update(resource);
		} else {
			// 【会员表重新统计】修改会员表（签单次数、签单金额、首次签单时间、当前签单时间、是否签单、当前签单ID、旧签单ID）
			customerService.recountSignApply(signApply.getCustomerId());
		}

	}

	/**
	 * 商务部订单管理/修改订单 1.修改订单 2.修改会员表（当前签单ID、当前签单时间、签单次数、签单金额、是否签单、首次签单时间、旧签单ID)
	 * 3.删除返佣记录表，重新生成返佣记录表
	 * 
	 * @param param
	 * @return
	 */
	@Transactional
	public SignApply contracterUpdate(SignApply param) {
		// 0.用户组权限控制
		Long userGroup = super.getUserById(param.getUpdateBy()).getUserGroup();
		if (userGroup != Constant.ADMIN && userGroup != Constant.USER_GROUP_HGJL
				&& userGroup != Constant.USER_GROUP_CW) {
			Assert.notNull(null, Constant.GROUPISNULL);
			return null;
		}

		/** 编辑、修改 **/

		// 获取订单当前实体
		SignApply oldSignApply = super.queryById(param.getId());
		if (oldSignApply.getSignStatus() != Constant.STATUS_SUCCESS) {
			Assert.notNull(null, "只可修改已签单完成订单");
		}
		// 1.修改产品时，同步修改是否长险
		if (StrUtils.isNotNullOrBlank(param.getInsuranceId())) {
			if (oldSignApply.getInsuranceId().longValue() != param.getInsuranceId().longValue()) {
				// 保存修改产品日志
				OperationLogTool
						.operationLog("订单（ID：" + oldSignApply.getId() + "，客户姓名：" + oldSignApply.getCustomerName()
								+ "），原产品（ID：" + oldSignApply.getInsuranceId() + "，名称：" + oldSignApply.getInsuranceName()
								+ "），新产品（ID：" + param.getInsuranceId() + "，名称：" + param.getInsuranceName() + "）");
			}
		}
		// 2.手动设置审核时间
		if (StrUtils.isNotNullOrBlank(param.getAuditTime())) {
			if (StrUtils.isNullOrBlank(oldSignApply.getAuditTime())
					|| (oldSignApply.getAuditTime().getTime() != param.getAuditTime().getTime())) {
				// 保存修改审核时间日志
				OperationLogTool
						.operationLog("【商务部】修改订单审核时间，客户：" + param.getCustomerName() + "，审核时间：" + param.getAuditTime());
			}
		}
		// 3.添加或修改升级人员、是否升级签单
		// addUpgrader(param);
		// 添加或修改保险经纪人
		addSaler(param);
		// 4.修改消息提交码
		changeSubmitCode(param, oldSignApply, userGroup);

		// 【会员表重新统计】修改会员表（签单次数、签单金额、首次签单时间、当前签单时间、是否签单、当前签单ID、旧签单ID）
		customerService.recountSignApply(oldSignApply.getCustomerId());
		return super.update(param);
	}

	public List<Long> selectAllSuccess() {
		return signApplyMapper.selectAllSuccess();
	}
	
	/**
	 * 到单通知
	 * @param signApply
	 * @param type
	 */
	public void msgToGroup(SignApply signApply) {
		MarkdownMessage message = new MarkdownMessage();
		message.setTitle("签单提醒");
		//市场部、升级部
		String belong = "";
		String type = "";
		if (signApply.getIsUpgrade() == 0) {
			type = "市场部";
			belong = signApply.getDirectorName()+"-"+signApply.getManagerName()+"-"+signApply.getSalerName();
		} else {
			type = "升级部";
			//belong = signApply.getUpgradeDirectorName()+"-"+signApply.getUpgradeManagerName()+"-"+signApply.getUpgraderName();
			belong = signApply.getDirectorName()+"-"+signApply.getManagerName()+"-"+signApply.getSalerName()+"-"+signApply.getUpgraderName();
			//市场部总监--市场部经理--业务--升级
		}
		message.add("恭喜"+type);
		message.add(MarkdownMessage.getBoldText(belong));
		message.add(" 到单"+signApply.getAmount() + "元");
		message.add("\n");
		java.util.Random random=new java.util.Random();
		int index=random.nextInt(10);
		message.add(dingEmoji[index]);
		DingUtil.sendGroupMesg("dd.group.sign.apply", message);
	}
	
	/**
	 * 报表中心-合规业绩排名 （经理排名、总监排名、总经理排名、总单量、总保费、退保、退保费
	 * @param managerList
	 */
	public Map<String, Object> businessRanking(Map<String, Object> map) {
		// 经理排名
		List<Map<String, Object>> manager = signApplyMapper.queryAmountByManager(map);
		// 总监排名
		List<Map<String, Object>> director = signApplyMapper.queryAmountByDirector(map);
		// 总经理排名
		List<Map<String, Object>> minister = signApplyMapper.queryAmountByMinister(map);
		// 总保费
		Map<String, Object> all = signApplyMapper.queryAmount(map);
		// 数据返回
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("manager", manager);
		returnMap.put("director", director);
		returnMap.put("minister", minister);
		returnMap.put("amount", String.format("%.2f", MapUtils.getDoubleValue(all, "amount")));
		returnMap.put("nums", MapUtils.getIntValue(all, "nums"));
		return returnMap;
	}
	
	/**
	 * 推广资源签单成功，发送钉钉通知
	 * @param item
	 */
	private void tResourceSignSendMessage(SignApply item) {
		ResourceAllot resourceAllot = resourceAllotService.queryByResourceId(item.getCustomerId());
		MarkdownMessage message = new MarkdownMessage();
		message.setTitle("推广资源成交！");
		message.add(MarkdownMessage.getHeaderText(5, "推广资源成交！"));
		
		message.add(MarkdownMessage.getBoldText("签单时间："));
		message.add(DateUtils.DateToStr(item.getCreateTime(), DateUtils.TIME_FORMAT_CHINESE_SECOND));
		message.add("\n");
		message.add(MarkdownMessage.getBoldText("产品名称："));
		message.add(item.getInsuranceName());
		message.add("\n");
		message.add(MarkdownMessage.getBoldText("缴费金额："));
		message.add(StrUtils.toString(item.getAmount()));
		message.add("\n");
		
		if (null != resourceAllot) {
			message.add(MarkdownMessage.getBoldText("成交客户区域："));
			message.add(resourceAllot.getProvince() + resourceAllot.getCity());
			message.add("\n");
			message.add(MarkdownMessage.getBoldText("入库时间："));
			message.add(DateUtils.DateToStr(resourceAllot.getCreaterTime(), DateUtils.TIME_FORMAT_CHINESE_SECOND));
			message.add("\n");
			message.add(MarkdownMessage.getBoldText("执行人："));
			message.add(sysDicService.queryCodeTextByTypeCode("RESOURCE_OP", StrUtils.toString(resourceAllot.getExecuter())));
			message.add("\n");
			message.add(MarkdownMessage.getBoldText("来源渠道："));
			message.add(sysDicService.queryCodeTextByTypeCode("RESOURCE_CHANNEL", StrUtils.toString(resourceAllot.getChannel())));
			message.add("\n");
			message.add(MarkdownMessage.getBoldText("计划名："));
			message.add(resourceAllot.getPlanName());
			message.add("\n");
			message.add(MarkdownMessage.getBoldText("专题名："));
			message.add(resourceAllot.getPlanCode());
			message.add("\n");
			if (StrUtils.isNotNullOrBlank(resourceAllot.getKeywords())) {
				message.add(MarkdownMessage.getBoldText("搜索词："));
				message.add(resourceAllot.getKeywords());
				message.add("\n");
			}
		}
		
		message.add(MarkdownMessage.getBoldText("当前时间："));
		message.add(DateUtils.DateToStr(new Date(), DateUtils.TIME_FORMAT_CHINESE_SECOND));
		message.add("\n");
		
		DingUtil.sendGroupMesg("tg.resource.sign", message);
	}
	
	/**
	 * 修改服务日期
	 * @param param
	 * @return
	 */
	public SignApply changeServer(SignApply param , Long currUser) {
		//获取订单列表
		List<Long> ids = signApplyMapper.queryDealByCustomerId(param.getCustomerId());
		List<SignApply> signApplyList = this.getList(ids);
		ServerRecord record = new ServerRecord();
		if(StrUtils.isNotNullOrBlank(ids) && ids.size() > 0){
			Customer customer = customerService.queryById(param.getCustomerId());
			if(ids.size() == 1){
				customer.setStartDate(param.getStartDate());
				customer.setEndDate(param.getEndDate());
				record.setServerRecord("修改服务时间:" + DateUtils.DateToStr(param.getStartDate(), DateUtils.DATE_FORMAT) + "----" + DateUtils.DateToStr(param.getEndDate(), DateUtils.DATE_FORMAT));
				customerService.update(customer);
			} else {  
				Date minDate = customer.getStartDate();
				Date maxDate = customer.getEndDate();
				for (SignApply signApply : signApplyList) {
					if(minDate.getTime() > signApply.getStartDate().getTime()){
						minDate = signApply.getStartDate();
					}
					if(maxDate.getTime() < signApply.getEndDate().getTime()){
						maxDate = signApply.getEndDate();
					}
				}
				
				customer.setStartDate(minDate);
				customer.setEndDate(maxDate);
				record.setServerRecord("修改服务时间:" + DateUtils.DateToStr(minDate, DateUtils.DATE_FORMAT) + "----" + DateUtils.DateToStr(maxDate, DateUtils.DATE_FORMAT));
				
				customerService.update(customer);
			}
			
			this.update(param);
			
			record.setCustomerId(customer.getId());
			record.setCustomerName(customer.getName());
			record.setCreateBy(currUser);
			record.setCreateTime(new Date());
			record.setType("4");
			record.setServerName(userService.queryById(currUser).getAccount());
			System.out.println(record.getServerRecord());
			recordServer.update(record);
			
		}
		return param;
	}
	
	
	/**
	 * 当日新单数量统计 （业务、经理、总监、总经理）
	 * 
	 * @param map
	 * @return
	 */
	public Map<String, Object> openToDayCount(Map<String, Object> map) {
		// 业务
		List<Map<String, Object>> saler = signApplyMapper.queryToDaySignNumGroupSalerId(map);
		// 经理
		List<Map<String, Object>> manager = signApplyMapper.queryToDaySignNumGroupManagerId(map);
		// 总监
		List<Map<String, Object>> director = signApplyMapper.queryToDaySignNumGroupDirectorId(map);
		// 总经理
		List<Map<String, Object>> minister = signApplyMapper.queryToDaySignNumGroupMinisterId(map);
		// 总缴费
		Map<String, Object> all = signApplyMapper.queryToDayAmount(map);
		// 业务
		List<Map<String, Object>> salerTotal = signApplyMapper.queryToDaySignNumTotalSalerId(map);
		// 经理
		List<Map<String, Object>> managerTotal = signApplyMapper.queryToDaySignNumTotalManagerId(map);
		// 总监
		List<Map<String, Object>> directorTotal = signApplyMapper.queryToDaySignNumTotalDirectorId(map);
		// 总经理
		List<Map<String, Object>> ministerTotal = signApplyMapper.queryToDaySignNumTotalMinisterId(map);
		// 总缴费
		Map<String, Object> allTotal = signApplyMapper.queryToDayTotalAmount(map);
		// 新单数
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("saler", saler);
		returnMap.put("manager", manager);
		returnMap.put("director", director);
		returnMap.put("minister", minister);
		returnMap.put("amount", String.format("%.2f", MapUtils.getDoubleValue(all, "amount")));
		returnMap.put("nums", MapUtils.getIntValue(all, "nums"));
		//签单数
		returnMap.put("salerTotal", salerTotal);
		returnMap.put("managerTotal", managerTotal);
		returnMap.put("directorTotal", directorTotal);
		returnMap.put("ministerTotal", ministerTotal);
		returnMap.put("amountTotal", String.format("%.2f", MapUtils.getDoubleValue(allTotal, "amountTotal")));
		returnMap.put("numsTotal", MapUtils.getIntValue(allTotal, "numsTotal"));
		return returnMap;
	}
	
}