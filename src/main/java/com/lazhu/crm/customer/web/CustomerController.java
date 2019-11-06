
package com.lazhu.crm.customer.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.base.BaseController;
import com.lazhu.core.support.Assert;
import com.lazhu.core.util.ComplexMD5Util;
import com.lazhu.core.util.InstanceUtil;
import com.lazhu.core.util.WebUtil;
import com.lazhu.crm.Constant;
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.entity.CustomerDetailsVo;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.customeractualcot.service.CustomerActualCotService;
import com.lazhu.crm.customerlog.entity.CustomerLog;
import com.lazhu.crm.customerlog.service.CustomerLogService;
import com.lazhu.crm.mobile.service.MobileService;
import com.lazhu.crm.resource.entity.Resource;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.crm.teacherdirectivecot.service.TeacherDirectiveCotService;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.service.SysUserService;

/**
 * <p>
 * 成交客户 前端控制器
 * </p>
 *
 * @author hz
 * @since 2017-05-27
 */
@RestController
@Api(value = "", description = "")
@RequestMapping(value = "crm/customer")
public class CustomerController extends BaseController<Customer> {
	
	@Autowired
	protected ResourceService resourceService;
	
	//客户联系方式
	@Autowired
	MobileService mobileService;
	
	//用户信息
	@Autowired
	SysUserService userService;
	
	@Autowired //实盘仓位信息
	CustomerActualCotService customerCotService;

	@Autowired //实盘仓位信息
	TeacherDirectiveCotService teacherCotService;
	
	@Autowired //会员操作日志
	CustomerLogService logService;
	
	@ApiOperation(value = "全局查询")
	@PutMapping(value = "/search/list")
	public Object search(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page<Customer> page = ((CustomerService) service).search(param, super.getCurrUser());
		return setSuccessModelMap(modelMap, page);
	}

	@ApiOperation(value = "查询")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "详情集合")
	@PutMapping(value = "/read/details")
	public Object getFull(ModelMap modelMap, @RequestBody Customer param) {
		Assert.notNull(param.getId(), "未获取当前行ID");
		CustomerDetailsVo vo = ((CustomerService) this.service).findFullEntity(param, super.getCurrUser());
		return super.setSuccessModelMap(modelMap, vo);
	}

	@ApiOperation(value = "商务中心成交客户双击详细")
	@PutMapping(value = "/read/openDetails")
	public Object openDetails(ModelMap modelMap, @RequestBody Customer param) {
		Assert.notNull(param.getId(), "未获取当前行ID");
		CustomerDetailsVo vo = ((CustomerService) this.service).findOpenEntity(param, super.getCurrUser());
		return super.setSuccessModelMap(modelMap, vo);
	}

	@ApiOperation(value = "客服中心成交客户双击详细")
	@PutMapping(value = "/read/serverDetails")
	public Object serverDetails(ModelMap modelMap, @RequestBody Customer param) {
		Assert.notNull(param.getId(), "未获取当前行ID");
		CustomerDetailsVo vo = ((CustomerService) this.service).findServerEntity(param, super.getCurrUser());
		return super.setSuccessModelMap(modelMap, vo);
	}

	@ApiOperation(value = "市场中心(升级中心)退保订单查询双击详细")
	@PutMapping(value = "/read/refundDetails")
	public Object findSignApplyEntity(ModelMap modelMap, @RequestBody Customer param) {
		Assert.notNull(param.getId(), "未获取当前行ID");
		CustomerDetailsVo vo = ((CustomerService) this.service).findSignApplyEntity(param, super.getCurrUser());
		return super.setSuccessModelMap(modelMap, vo);
	}

	@PostMapping
	@ApiOperation(value = "修改")
	public Object update(ModelMap modelMap, @RequestBody Customer param, HttpServletRequest request) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		// 不可新增，只能修改
		Assert.notNull(param.getId(), Constant.GROUPISNULL);
		// 当前操作人
		param.setUpdateBy(super.getCurrUser());
		param.setUpdateTime(new Date());
		//获取当前客户信息
		Customer customer1 = ((CustomerService) service).queryById(param.getId());
		if (StrUtils.isNotNullOrBlank(param) && StrUtils.isNotNullOrBlank(param.getTotalMoney()) ) {
			CustomerLog log = new CustomerLog();
			if (StrUtils.isNullOrBlank(param.getUseMoney())) {//当可用资金为空时，设置可用资金量
				param.setUseMoney(param.getTotalMoney());
				log.setRemark("设置实盘可用资金");
				log.setCreateBy(super.getCurrUser());
				
			} else if (StrUtils.isNotNullOrBlank(param.getUseMoney()) && (param.getTotalMoney() != customer1.getTotalMoney())) {//当总资金量变动时
				//总资金量增加
				if (param.getTotalMoney() >= customer1.getTotalMoney()) {
					double dob = param.getTotalMoney() - customer1.getTotalMoney();
					param.setUseMoney(param.getUseMoney() + dob);
					log.setRemark("增加实盘可用资金");
					log.setCreateBy(super.getCurrUser());
				}
				//总资金量减少时
				if (param.getTotalMoney() < customer1.getTotalMoney()) {
					double changeMoney = customer1.getTotalMoney() - param.getTotalMoney();
					//当总资金量的减少量小于可用资金时
					if (changeMoney < customer1.getUseMoney()) {
						param.setUseMoney(param.getUseMoney() - changeMoney);
					}
					//当总资金量的减少量大于可用资金时
					if (changeMoney >= customer1.getUseMoney()) {
						Assert.notNull(null, "资金量修改有误，请重新输入！");
					}
					log.setRemark("减少实盘可用资金");
					log.setCreateBy(super.getCurrUser());
				}
			}
			logService.update(log);
		}
		if (StrUtils.isNotNullOrBlank(param) && StrUtils.isNotNullOrBlank(param.getTotalTeacherMoney()) ) {
			CustomerLog log = new CustomerLog();
			if (StrUtils.isNullOrBlank(param.getUseTeacherMoney())) {//当可用资金为空时，设置可用资金量
				param.setUseTeacherMoney(param.getTotalTeacherMoney());
				log.setRemark("设置虚盘可用资金");
				log.setCreateBy(super.getCurrUser());
			} else if (StrUtils.isNotNullOrBlank(param.getUseTeacherMoney()) && (param.getTotalTeacherMoney() != customer1.getTotalTeacherMoney())) {//当总资金量变动时
				//总资金量增加
				if (param.getTotalTeacherMoney() >= customer1.getTotalTeacherMoney()) {
					double dob = param.getTotalTeacherMoney() - customer1.getTotalTeacherMoney();
					param.setUseTeacherMoney(param.getUseTeacherMoney() + dob);
					log.setRemark("增加虚盘可用资金");
					log.setCreateBy(super.getCurrUser());
				}
				//总资金量减少时
				if (param.getTotalTeacherMoney() < customer1.getTotalTeacherMoney()) {
					double changeMoney = customer1.getTotalTeacherMoney() - param.getTotalTeacherMoney();
					//当总资金量的减少量小于可用资金时
					if (changeMoney < customer1.getUseTeacherMoney()) {
						param.setUseTeacherMoney(param.getUseTeacherMoney() - changeMoney);
					}
					//当总资金量的减少量大于可用资金时
					if (changeMoney >= customer1.getUseTeacherMoney()) {
						Assert.notNull(null, "资金量修改有误，请重新输入！");
					}
					log.setRemark("减少虚盘可用资金");
					log.setCreateBy(super.getCurrUser());
				}
			}
			logService.update(log);
		}
		Customer customer = ((CustomerService) service).update(param);
		if (StrUtils.isNotNullOrBlank(customer.getTotalMoney())) {
			customerCotService.updatePosition(customer);//更新仓位比
		}
		if (StrUtils.isNotNullOrBlank(customer.getTotalTeacherMoney())) {
			teacherCotService.updateTeacherPosition(customer);//更新仓位比
		}
		
		//重新解密手机号
		if (null != customer) {
			customer.setMobile(mobileService.getMobile(customer.getMd5Mobile()));
			//手机号解密，拨打电话使用
			customer.setMobile(((CustomerService) service).getMobileByMd5Mobile(customer.getMd5Mobile()));
			//权限控制详情页显示的手机号
			/** 当前用户是否可见客户手机号 **/
			Integer isMobileVisible = userService.queryIsMobileVisibleById(super.getCurrUser());
			if (null != isMobileVisible && isMobileVisible == 1) {
				//解密手机号
				customer.setShowMobile(customer.getMobile());
			} else {
				//掩码手机号
				customer.setShowMobile(customer.getFuzzyMobile());
			}
		}
		return setSuccessModelMap(modelMap, customer);
	}

	@PostMapping("/allotCustomer")
	@ApiOperation(value = "会员调配")
	public Object allotCustomer(ModelMap modelMap, @RequestBody Map<String, Object> param, HttpServletRequest request) {
		Assert.notNull(param.get("ids"), "未选中会员");
		Assert.notNull(param.get("userId"), "未指定被分配人");
		List<Long> ids = null;
		if (param.get("ids") instanceof String) {
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.get("ids").toString()));
		} else if (param.get("ids") instanceof JSONArray) {
			ids = ((JSONArray) param.get("ids")).toJavaList(Long.class);
		}
		Long userId = StrUtils.toLong(param.get("userId"));
		String ip = WebUtil.getHost(request);
		List<Customer> customer = ((CustomerService) service).allotCustomer(ids, userId, super.getCurrUser(), ip);
		return setSuccessModelMap(modelMap, customer);
	}

	/** ********************** 市场部 ********************** **/

	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "市场部成交客户")
	@PutMapping(value = "/customerList/list")
	public Object customerList(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page page = ((CustomerService) service).customerList(param, super.getCurrUser());
		return setSuccessModelMap(modelMap, page);
	}

	/** ********************** 商务部 ********************** **/

	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "商务部成交客户")
	@PutMapping(value = "/contracterMore/list")
	public Object contracterMore(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page page = ((CustomerService) service).contracterMore(param, super.getCurrUser());
		return setSuccessModelMap(modelMap, page);
	}

	/** ********************** 客服部 ********************** **/

	@ApiOperation(value = "客服部待分配待接收客户")
	@PutMapping(value = "/allot/list")
	public Object customerAllot(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		@SuppressWarnings("rawtypes")
		Page page = ((CustomerService) service).customerAllot(param, super.getCurrUser());
		return setSuccessModelMap(modelMap, page);
	}

	@ApiOperation(value = "客服部成交客户")
	@PutMapping(value = "/serverMore/list")
	public Object serverMore(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		@SuppressWarnings("rawtypes")
		Page page = ((CustomerService) service).serverMore(param, super.getCurrUser());
		return setSuccessModelMap(modelMap, page);
	}

	@PostMapping("/allotServer")
	@ApiOperation(value = "分配客服")
	public Object allotServer(ModelMap modelMap, @RequestBody Map<String, Object> param, HttpServletRequest request) {
		Assert.notNull(param.get("ids"), "未选中资源");
		Assert.notNull(param.get("userId"), "未指定被分配人");
		List<Long> ids = null;
		if (param.get("ids") instanceof String) {
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.get("ids").toString()));
		} else if (param.get("ids") instanceof JSONArray) {
			ids = ((JSONArray) param.get("ids")).toJavaList(Long.class);
		}
		Long userId = StrUtils.toLong(param.get("userId"));
		// 当前IP
		String ip = WebUtil.getHost(request);
		List<Customer> customer = ((CustomerService) service).allotServer(ids, userId, super.getCurrUser(), ip);
		return setSuccessModelMap(modelMap, customer);
	}

	@PostMapping("/receive")
	@ApiOperation(value = "接收客户")
	public Object receive(ModelMap modelMap, @RequestBody Map<String, Object> param, HttpServletRequest request) {
		Assert.notNull(param.get("ids"), "未选中资源");
		List<Long> ids = null;
		if (param.get("ids") instanceof String) {
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.get("ids").toString()));
		} else if (param.get("ids") instanceof JSONArray) {
			ids = ((JSONArray) param.get("ids")).toJavaList(Long.class);
		}
		// 当前IP
		String ip = WebUtil.getHost(request);
		List<Customer> customer = ((CustomerService) service).receive(ids, super.getCurrUser(), ip);
		return setSuccessModelMap(modelMap, customer);
	}

	/** ********************** 升级中心 ********************** **/

	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "升级部【待分配客户】")
	@PutMapping(value = "/allotUpgrade/list")
	public Object allotUpgradePage(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page page = ((CustomerService)service).allotUpgradePage(param,super.getCurrUser());
		return setSuccessModelMap(modelMap,page);
	}
	
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "升级中心【原始客户】【丢弃客户】【可聊客户】【回访客户】【成交客户】")
	@PutMapping(value = "/upgrade/list")
	public Object upgradePage(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page page = ((CustomerService) service).upgradePage(param, super.getCurrUser());
		return setSuccessModelMap(modelMap, page);
	}
	
	@PostMapping(value = "/allotUpgrade/extract")
	@ApiOperation(value = "升级部【抽取客户】")
	public Object upgradeExtractCustomer(ModelMap modelMap, HttpServletRequest request) {
		String ip = WebUtil.getHost(request);
		List<Customer> customerList = ((CustomerService)service).upgradeExtractCustomer(super.getCurrUser(),ip);
		return setSuccessModelMap(modelMap,customerList);
	}
	
	@PostMapping("/changeUpgradeFlow")
	@ApiOperation(value = "移动升级客户（开发流程）")
	public Object changeUpgradeFlow(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(param.get("id"), "未选中会员");
		Assert.notNull(param.get("upgradeFlow"), "未指定升级流程");
		Customer customer = ((CustomerService) service).changeUpgradeFlow(StrUtils.toLong(param.get("id")),
				StrUtils.toInteger(param.get("upgradeFlow")), super.getCurrUser());
		return setSuccessModelMap(modelMap, customer);
	}

	@PostMapping("/allotUpgrade")
	@ApiOperation(value = "升级调配") // 只能调配给本部门其他人员
	public Object allotUpgrade(ModelMap modelMap, @RequestBody Map<String, Object> param, HttpServletRequest request) {
		Assert.notNull(param.get("ids"), "未选中会员");
		Assert.notNull(param.get("userId"), "未指定被分配人");
		List<Long> ids = null;
		if (param.get("ids") instanceof String) {
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.get("ids").toString()));
		} else if (param.get("ids") instanceof JSONArray) {
			ids = ((JSONArray) param.get("ids")).toJavaList(Long.class);
		}
		Long userId = StrUtils.toLong(param.get("userId"));
		String ip = WebUtil.getHost(request);
		List<Customer> customer = ((CustomerService) service).allotUpgrade(ids, userId, super.getCurrUser(), ip);
		return setSuccessModelMap(modelMap, customer);
	}

	@PostMapping("/discardCustomer")
	@ApiOperation(value = "丢弃升级客户")
	public Object discardCustomer(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(param.get("ids"), "未选中会员");
		Assert.notNull(param.get("upgradeFlow"), "未指定丢弃流程");
		List<Long> ids = null;
		if (param.get("ids") instanceof String) {
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.get("ids").toString()));
		} else if (param.get("ids") instanceof JSONArray) {
			ids = ((JSONArray) param.get("ids")).toJavaList(Long.class);
		}
		Integer upgradeFlow = StrUtils.toInteger(param.get("upgradeFlow"));
		List<Customer> customerList = ((CustomerService) service).discardCustomer(ids, upgradeFlow,
				super.getCurrUser());
		return setSuccessModelMap(modelMap, customerList);
	}

	@PostMapping("/getCustomerByName")
	@ApiOperation(value = "客户姓名查询")
	public Object getClubByName(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(param.get("name"), "客户姓名不能为空");
		List<Long> ids = ((CustomerService) service).selectIdPage(param);
		Customer customer = null;
		if (null != ids && ids.size() > 0) {
			// 由于是模糊查询，结果集是不准确的，等电话被叫需要上线时再完善
			customer = ((CustomerService) service).queryById(ids.get(0));
		}
		return setSuccessModelMap(modelMap, customer);
	}

	@PostMapping("/getCustomerByMobile")
	@ApiOperation(value = "客户电话查询")
	public Object getClubByMobile(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(param.get("mobile"), "客户电话不能为空");
		List<Long> ids = ((CustomerService) service).selectIdPage(param);
		Customer customer = null;
		if (null != ids && ids.size() > 0) {
			customer = ((CustomerService) service).queryById(ids.get(0));
		}
		return setSuccessModelMap(modelMap, customer);
	}

	@ApiOperation(value = "生日提醒")
	@PutMapping(value = "/read/birthday")
	public Object userTotal(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return setSuccessModelMap(modelMap, ((CustomerService) service).queryBirthday(param,super.getCurrUser()));
	}

	@ApiOperation(value = "来电手机号查询")
	@PutMapping(value = "/queryCallInPhone")
	public Object queryCallInPhone(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		String phoneNum = StrUtils.toString(param.get("phoneNum"));
		String md5Mobile = ComplexMD5Util.MD5Encode(phoneNum);
		HashMap<Object, Object> map = InstanceUtil.newHashMap();
		if (StrUtils.isNotNullOrBlank(phoneNum)) {
			Customer customer = ((CustomerService)service).queryByMd5Mobile(md5Mobile);
			if (null != customer) {
				map.put("customerId", customer.getId().toString());
				map.put("type", 1);
				return setSuccessModelMap(modelMap, map);
			} else {
				Resource resource = resourceService.queryByMd5Mobile(md5Mobile);
				if (null != resource) {
					map.put("resourceId", resource.getId().toString());
					map.put("type", 2);
					return setSuccessModelMap(modelMap, map);
				} else {
					map.put("type", 3);
					return setSuccessModelMap(modelMap, map);
				}
			}
		} else {
			OperationLogTool.operationLog(Constant.ERROR_LOG, "来电手机号查询号码为空");
			map.put("type", 3);
			return setSuccessModelMap(modelMap, map);
		}
	}

	
	@PostMapping("/changeServer")
	@ApiOperation(value = "修改服务状态")
	public Object changeServer(ModelMap modelMap, @RequestBody Customer param, HttpServletRequest request) {
		CustomerLog log = new CustomerLog();
		param.setUpdateTime(new Date());
		if (StrUtils.isNotNullOrBlank(param.getIsService())) {
			if (param.getIsService() == 5) {
				log.setType("客服中心暂无服务操作");
			} else if (param.getIsService() == 0) {
				log.setType("升级中心转客服服务操作");
			}
		} else {
			log.setType("修改客户订单服务日期");
		}
		Customer customer = ((CustomerService) service).update(param);
		
		// 当前操作人
		log.setCustomerId(param.getId());
		log.setUpdateBy(super.getCurrUser());
		log.setUpdateTime(new Date());
		log.setCreateBy(super.getCurrUser());
		logService.update(log);
		return setSuccessModelMap(modelMap, customer);
	}
	
	
	@PostMapping("/updateMore")
	@ApiOperation(value = "批量延长服务日期")
	public Object updateMore(ModelMap modelMap, @RequestBody Customer param, HttpServletRequest request) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		// 不可新增，只能修改
		Assert.notNull(param.getIds(), Constant.GROUPISNULL);
		// 当前操作人
		param.setUpdateBy(super.getCurrUser());
		List<Long> ids = null;
		if (param.getIds() instanceof String){
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.getIds().toString()));
		}else if(param.getIds() instanceof JSONArray) {
			ids = ((JSONArray)param.getIds()).toJavaList(Long.class);
		}
		List<String> names = null;
		if (param.getIds() instanceof String){
			if (StrUtils.isNotNullOrBlank(names)) {
				names = new ArrayList<String>();
				names.add(String.valueOf(param.getNames().toString()));
			}
			
		}else if(param.getIds() instanceof JSONArray) {
			if (StrUtils.isNotNullOrBlank(names)) {
				names = ((JSONArray)param.getNames()).toJavaList(String.class);
			}
		}
		Customer customer = ((CustomerService) service).updateMore(param,ids,names,super.getCurrUser());
		return setSuccessModelMap(modelMap, customer);
	}
	
	
	/**
	 * 成交客户根据 渠道   城市  省份 年龄 统计
	 * @param modelMap
	 * @param param
	 * @return
	 */
	@ApiOperation(value = "成交客户渠道区域统计")
	@PutMapping(value = "/search/queryGroupCustomer")
	public Object queryGroupCustomer(ModelMap modelMap, @RequestBody Map<String, Object> param){
		return setSuccessModelMap(modelMap, ((CustomerService) service).queryGroupCustomer(param));
	}
	
	/**
	 * 校正仓位信息
	 */
	@PutMapping("/updateAllStock")
	public void updateAllStock(){
		try {
			List<Long> ids = ((CustomerService) service).queryAll();
			for (Long id : ids) {
				Customer cus = ((CustomerService) service).queryById(id);
				if (StrUtils.isNotNullOrBlank(cus.getTotalMoney())) {
					customerCotService.updatePosition(cus);
				}
				if (StrUtils.isNotNullOrBlank(cus.getTotalTeacherMoney())) {
					teacherCotService.updateTeacherPosition(cus);
				}
			}
			System.out.println("仓位信息校正完成！");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("仓位校正异常！");
		}
		
	}
	/**
	 * 校正实虚盘可用资金
	 */
	@ApiOperation(value = "校正实虚盘可用资金")
	@PutMapping("/updateAllUserMoney")
	public void updateAllUserMoney(){
		((CustomerService) service).updateAllUserMoney();
	}
	
	/**
	 * 升级首次签单超过10天投顾客户  分配给二开刘黎明（市场二部） 喻寄强（市场一部）组
	 * 客服服务时间超过十天的投顾客户 	分配给二开刘黎明（市场二部） 喻寄强（市场一部）经理
	 * 
	 */
	@ApiOperation(value = "校正实虚盘可用资金")
	@PutMapping("/moveTenDaysCustomer")
	public void moveTenDaysCustomer(){
		((CustomerService) service).moveTenDaysCustomer();
	}
}
