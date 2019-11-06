package com.lazhu.crm.customerrefund.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.base.BaseController;
import com.lazhu.core.util.ComplexMD5Util;
import com.lazhu.core.util.DataUtil;
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.customerrefund.entity.CustomerRefund;
import com.lazhu.crm.customerrefund.service.CustomerRefundService;
import com.lazhu.crm.signapply.entity.SignApply;
import com.lazhu.crm.signapply.service.SignApplyService;
import com.lazhu.ecp.utils.StrUtils;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@RestController
@Api(value = "", description = "")
@RequestMapping(value = "crm/customerRefund")
public class CustomerRefundController extends BaseController<CustomerRefund> {

	@Autowired
	private SignApplyService signApplySerivce;
	@Autowired
	private CustomerService customerService;

	@ApiOperation(value = "查询")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		// 判断条件
		Customer customer = null;
		if(param.containsKey("mobile")){
			param.put("md5Mobile", ComplexMD5Util.MD5Encode(MapUtils.getString(param, "mobile")));
			customer = customerService.queryByMd5Mobile(ComplexMD5Util.MD5Encode(MapUtils.getString(param, "mobile")));
			if (null != customer) {
				com.lazhu.core.support.Assert.notNull(customer, "未查询到客户");
				return super.setSuccessModelMap(modelMap);
			}
			param.put("customerId", customer == null ? -1 : customer.getId());
		}

		Page<CustomerRefund> page = service.query(param);
		List<CustomerRefund> list = page.getRecords();
		// 补全数据
		if (DataUtil.isNotEmpty(list)) {
			for (CustomerRefund refund : list) {
				// 客户如果有订单存在
				if (StrUtils.isNotNullOrBlank(refund.getSignApplyId())) {
					SignApply signApply = signApplySerivce.queryById(refund.getSignApplyId());
					if (null != signApply && signApply.getId() > 0) {
						String managerName = signApply.getManagerName();
						String salerName = signApply.getSalerName();
						String directorName = signApply.getDirectorName();
						if (StrUtils.isNullOrBlank(managerName)) {
							managerName = "未分配";
						}
						if (StrUtils.isNullOrBlank(salerName)) {
							salerName = "未分配";
						}
						if (StrUtils.isNullOrBlank(directorName)) {
							directorName = "未分配";
						}
						refund.setBelong(directorName + "-" + managerName + "-" + salerName);
						refund.setSignApply(signApply);
					}
				} else {
					refund.setSignApply(new SignApply());
				}
			}
		}
		return super.setSuccessModelMap(modelMap, page);
	}

	@ApiOperation(value = "市场中心查询")
	@PutMapping(value = "/read/list4sczx")
	public Object list4sczx(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		if (param.containsKey("sczx")) {
			((CustomerRefundService) service).setBusiness(param, super.getCurrUser());
		}
		return setSuccessModelMap(modelMap, ((CustomerRefundService) service).list4zx(param));
	}

	@ApiOperation(value = "升级中心查询")
	@PutMapping(value = "/read/list4jbzx")
	public Object list4jbzx(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		if (param.containsKey("jbzx")) {
			param.put("isUpgrade", 1);
			param.put("upgraderId", super.getCurrUser());
		} else if (param.containsKey("tbzx")) {
			param.put("isUpgrade", 2);
			param.put("upgraderId", super.getCurrUser());
		}
		return setSuccessModelMap(modelMap, ((CustomerRefundService) service).list4zx(param));
	}

	@ApiOperation(value = "查询详细")
	@PutMapping(value = "/read/refund")
	public Object queryRefund(ModelMap modelMap, @RequestBody CustomerRefund param) {

		// 得到退保数据实体
		CustomerRefund refund = ((CustomerRefundService) service).queryById(param.getId());
		// 补全订单数据
		Customer customer = customerService.queryById(refund.getCustomerId());
		SignApply signApply = new SignApply();
		if (StrUtils.isNotNullOrBlank(refund.getSignApplyId())) {
			signApply = signApplySerivce.queryById(refund.getSignApplyId());
		}
		String managerName = customer.getManagerName();
		String salerName = customer.getSalerName();
		String directorName = customer.getDirectorName();
		if (StrUtils.isNullOrBlank(managerName)) {
			managerName = "未分配";
		}
		if (StrUtils.isNullOrBlank(salerName)) {
			salerName = "未分配";
		}
		if (StrUtils.isNullOrBlank(directorName)) {
			directorName = "未分配";
		}
		refund.setBelong(directorName + "-" + managerName + "-" + salerName);
		refund.setSignApply(signApply);
		return super.setSuccessModelMap(modelMap, refund);
	}

	@ApiOperation(value = "详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody CustomerRefund param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改")
	public Object update(ModelMap modelMap, @RequestBody CustomerRefund param) {
		if (param.getId() == null) {
			param.setDealerStatus(0);
		}
		param.setUpdateBy(super.getCurrUser());
		CustomerRefund refund = ((CustomerRefundService)service).update(param);
		//成功退款后修改订单表订单退款状态
		if (StrUtils.isNotNullOrBlank(refund) && refund.getIsSuccess() == 1) {
			SignApply signApply = signApplySerivce.queryById(refund.getSignApplyId());
			signApply.setIsRefund(1);
			signApplySerivce.update(signApply);
		}
		return setSuccessModelMap(modelMap, refund);
	}

	@DeleteMapping
	@ApiOperation(value = "删除")
	public Object delete(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(param.get("ids"), "IDS");
		List<Long> ids = null;
		if (param.get("ids") instanceof String) {
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.get("ids").toString()));
		} else if (param.get("ids") instanceof JSONArray) {
			ids = ((JSONArray) param.get("ids")).toJavaList(Long.class);
		}
		return super.del(modelMap, ids);
	}

	@ApiOperation(value = "退款订单查询")
	@PutMapping(value = "/read/refund1")
	public Object userTotal(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return setSuccessModelMap(modelMap, ((CustomerRefundService) service).queryRefundSignApply(param));
	}
}
