package com.lazhu.crm.auditmobile.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONArray;
import com.lazhu.core.base.BaseController;
import com.lazhu.crm.Constant;
import com.lazhu.crm.auditmobile.entity.AuditMobile;
import com.lazhu.crm.auditmobile.service.AuditMobileService;
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.resource.entity.Resource;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.ecp.utils.DateUtils;
import com.lazhu.ecp.utils.DingUtil;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.model.SysUser;
import com.lazhu.sys.service.SysUserService;

/**
 * <p>
 * 手机号明文申请表 前端控制器
 * </p>
 *
 * @author taoyang
 * @since 2019-07-30
 */
@RestController
@Api(value = "手机号明文申请表", description = "手机号明文申请表")
@RequestMapping(value = "crm/auditMobile")
public class AuditMobileController extends BaseController<AuditMobile> {
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AuditMobileService auditMobileService;
	
	@Autowired
	private SysUserService userService;
	
	@Autowired
	private ResourceService resourceService;
	
	@ApiOperation(value = "查询手机号明文申请表")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		SysUser user = userService.queryById(super.getCurrUser());
		Assert.notNull(user.getUserGroup(), Constant.GROUPISNULL);
		Long userGroup = user.getUserGroup();
		if (userGroup == Constant.ADMIN) {//管理员
			
		} else if (userGroup == Constant.USER_GROUP_ZJL) {//总经理
			param.put("auditId", super.getCurrUser());
		} else {
			param.put("applicantId", super.getCurrUser());
		}
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "手机号明文申请表详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody AuditMobile param) {
		Assert.notNull(param.getId(), "ID");
		param = auditMobileService.queryById(param.getId());
		if (StrUtils.isNotNullOrBlank(param.getStatus()) && param.getStatus() == 1) {
			String mobile = auditMobileService.getMobileByMd5Mobile(param.getMd5Mobile());
			param.setShowMobile(mobile);
		}
		return setSuccessModelMap(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改手机号明文申请表")
	public Object update(ModelMap modelMap, @RequestBody AuditMobile param) {
		Assert.notNull(param.getId(), "未获取当前行id");
		param.setAuditId(super.getCurrUser());
		param.setAuditName(userService.queryById(super.getCurrUser()).getAccount());
		param.setAuditTime(new Date());
		param = service.update(param);
		if (StrUtils.isNotNullOrBlank(param.getStatus()) && param.getStatus() == 1) {
			DingUtil.sendMsg("【"+param.getCustomerName()+"】的手机号查看申请已通过，有效时间24小时,请及时查看。通过时间："+DateUtils.DateToStr(new Date(), DateUtils.TIME_FORMAT_CHINESE), userService.queryById(param.getApplicantId()).getDingId());
		}
		return setSuccessModelMap(modelMap, param);
	}
	
	@PostMapping(value = "/applyAudit")
	@ApiOperation(value = "提交手机号明文申请")
	public Object applyAudit(ModelMap modelMap, @RequestBody AuditMobile param) {
		Assert.notNull(param.getCustomerId(), "未获取客户id");
		Customer customer = customerService.queryById(param.getCustomerId());
		if (StrUtils.isNotNullOrBlank(customer)) {
			
			param.setCustomerId(customer.getId());
			param.setCustomerName(customer.getName());
			param.setMd5Mobile(customer.getMd5Mobile());
			param.setFuzzyMobile(customer.getFuzzyMobile());
			param.setAuditId(customer.getMinisterId());
			param.setAuditName(customer.getMinisterName());
			param.setApplicantId(super.getCurrUser());
			param.setCreateBy(super.getCurrUser());
			param.setApplicantName(userService.queryById(super.getCurrUser()).getAccount());
		}else{
			Resource resource = resourceService.queryById(param.getCustomerId());
			param.setCustomerId(resource.getId());
			param.setCustomerName(resource.getName());
			param.setMd5Mobile(resource.getMd5Mobile());
			param.setFuzzyMobile(resource.getFuzzyMobile());
			param.setAuditId(resource.getMinisterId());
			param.setAuditName(resource.getMinisterName());
			param.setApplicantId(super.getCurrUser());
			param.setCreateBy(super.getCurrUser());
			param.setApplicantName(userService.queryById(super.getCurrUser()).getAccount());
		}
		param = auditMobileService.update(param);
		DingUtil.sendMsg("你有一个待处理事项，请在客户管理系统审核中心及时处理。申请时间："+DateUtils.DateToStr(new Date(), DateUtils.TIME_FORMAT_CHINESE), userService.queryById(param.getAuditId()).getDingId());
		return setSuccessModelMap(modelMap, param);
	}

	@DeleteMapping
	@ApiOperation(value = "删除手机号明文申请表")
	public Object delete(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(param.get("ids"), "IDS");
		List<Long> ids = null;
		if (param.get("ids") instanceof String){
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.get("ids").toString()));
		}else if(param.get("ids") instanceof JSONArray) {
			ids = ((JSONArray)param.get("ids")).toJavaList(Long.class);
		}
		return super.del(modelMap, ids);
	}
}
