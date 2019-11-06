package com.lazhu.crm.serverrecordmobile.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.salercallinrecord.service.SalerCallinRecordService;
import com.lazhu.crm.serverrecordmobile.entity.ServerRecordMobile;
import com.lazhu.crm.serverrecordmobile.service.ServerRecordMobileService;
import com.lazhu.ecp.utils.StrUtils;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@RestController
@Api(value = "", description = "")
@RequestMapping(value = "crm/serverRecordMobile")
public class ServerRecordMobileController extends BaseController<ServerRecordMobile> {
	
	@Autowired
	private SalerCallinRecordService salerCallinRecordService;
	
	@Autowired
	private CustomerService customerService;
	
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "查询")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page page = ((ServerRecordMobileService)service).queryRecord(param,super.getCurrUser());
		return setSuccessModelMap(modelMap,page);
	}
	@ApiOperation(value = "客户统计记录查询")
	@PutMapping(value = "/read/list/customer")
	public Object queryByCustomerId(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		List<ServerRecordMobile>  serverRecordMobile= ((ServerRecordMobileService)service).queryRecordByCustomerId(param);
		return setSuccessModelMap(modelMap,serverRecordMobile);
	}

	@ApiOperation(value = "详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody ServerRecordMobile param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改")
	public Object update(ModelMap modelMap, @RequestBody ServerRecordMobile param) {
		if (null == param.getId()) {
			param = ((ServerRecordMobileService)service).add(param,super.getCurrUser());
		} else {
			param = ((ServerRecordMobileService)service).update(param);
		}
		//更新市场部来电记录表的客服通话内容
		salerCallinRecordService.updateServerRecordByCallFile(param.getCallFile(), param.getServerRecord());
		//更新会员表客服通话记录和内容
		if (StrUtils.isNotNullOrBlank(param.getCustomerId())) {
			Customer cus = customerService.queryById(param.getCustomerId());
			if (StrUtils.isNotNullOrBlank(cus)) {
				cus.setLastCallTime(param.getCreateTime());
				cus.setLastCallRecord(param.getServerRecord());
				customerService.update(cus);
			}
		}
		return super.setSuccessModelMap(modelMap,param);
	}

	@DeleteMapping
	@ApiOperation(value = "删除")
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
	
	@PutMapping("/automation")
	@ApiOperation(value = "自动保存通话记录")
	@Transactional
	public Object automation(ModelMap modelMap, @RequestBody ServerRecordMobile param) {
		if (null == param.getId()) {
			param.setType("1");
			param = ((ServerRecordMobileService)service).add(param,super.getCurrUser());
		} else {
			param = ((ServerRecordMobileService)service).save(param);
		}
		return super.setSuccessModelMap(modelMap,param);
	}
	
	@PutMapping("/toCustomer")
	@ApiOperation(value = "初始化客服通话记录到会员表")
	public Object toCustomer(ModelMap modelMap) {
		((ServerRecordMobileService)service).toCustomer();
		return super.setSuccessModelMap(modelMap);
	}
}
