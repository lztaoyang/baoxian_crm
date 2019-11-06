package com.lazhu.crm.salerrecord.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.Date;
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
import com.lazhu.crm.salerrecord.entity.SalerRecord;
import com.lazhu.crm.salerrecord.service.SalerRecordService;
import com.lazhu.crm.worktrailnormal.entity.WorkTrailNormal;
import com.lazhu.crm.worktrailnormal.service.WorkTrailNormalService;
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
@RequestMapping(value = "crm/salerRecord")
public class SalerRecordController extends BaseController<SalerRecord> {
	
	@Autowired
	private WorkTrailNormalService workTrailNormalService;
	
	@ApiOperation(value = "查询")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page page = ((SalerRecordService)service).queryRecord(param,super.getCurrUser());
		return setSuccessModelMap(modelMap,page);
	}
	@ApiOperation(value = "客户通话记录")
	@PutMapping(value = "/read/list/customer")
	public Object queryByCustomerId(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		List<SalerRecord>  salerRecord= ((SalerRecordService)service).queryRecordByCustomerId(param);
		return setSuccessModelMap(modelMap,salerRecord);
	}
	@ApiOperation(value = "客户接通通话记录")
	@PutMapping(value = "/read/list/customer/real")
	public Object queryRealByCustomerId(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		List<SalerRecord>  salerRecord= ((SalerRecordService)service).queryRealRecordByCustomerId(param);
		return setSuccessModelMap(modelMap,salerRecord);
	}
	@ApiOperation(value = "2018客户接通通话记录")
	@PutMapping(value = "/read/list/customer/real/newyear")
	public Object queryNewYearRealByCustomerId(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		List<SalerRecord>  salerRecord= ((SalerRecordService)service).queryNewYearRealByCustomerId(param);
		return setSuccessModelMap(modelMap,salerRecord);
	}

	@ApiOperation(value = "详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody SalerRecord param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改")
	@Transactional
	public Object update(ModelMap modelMap, @RequestBody SalerRecord param) {
		//Assert.notNull(param.getCustomerId(), "无法获取当前客户信息");
		if (null == param.getId()) {
			param = ((SalerRecordService)service).add(param,super.getCurrUser());
		} else {
			param = ((SalerRecordService)service).save(param);
		}
		return super.setSuccessModelMap(modelMap,param);
	}
	@PutMapping("/automation")
	@ApiOperation(value = "自动保存通话记录")
	@Transactional
	public Object automation(ModelMap modelMap, @RequestBody SalerRecord param) {
		if (null == param.getId()) {
			param.setType("1");
			param = ((SalerRecordService)service).add(param,super.getCurrUser());
		} else {
			param = ((SalerRecordService)service).save(param);
		}
		return super.setSuccessModelMap(modelMap,param);
	}
	
	@PostMapping("/adminUpdate")
	@ApiOperation(value = "管理员修改通话记录")
	@Transactional
	public Object adminUpdate(ModelMap modelMap, @RequestBody SalerRecord param) {
		if (null == param.getId()) {
			
		} else {
			param = ((SalerRecordService)service).update(param);
			/** 同时更新正常开发轨迹的通话时长 **/
			WorkTrailNormal workTrailNormal = workTrailNormalService.queryBySalerRecordId(param.getId());
			if (null != workTrailNormal) {
				workTrailNormal.setTimeLength(StrUtils.toLong(param.getTestTimeLength()));
				workTrailNormalService.update(workTrailNormal);
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

	
}
