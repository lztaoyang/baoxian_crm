package com.lazhu.crm.developdaycall.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.lazhu.core.base.BaseController;
import com.lazhu.crm.developdaycall.entity.DevelopDayCall;
import com.lazhu.crm.developdaycall.service.DevelopDayCallService;

/**
 * <p>
 * 经纪人日通话统计表 前端控制器
 * </p>
 *
 * @author hz
 * @since 2018-01-09
 */
@RestController
@Api(value = "经纪人日通话统计表", description = "经纪人日通话统计表")
@RequestMapping(value = "crm/developDayCall")
public class DevelopDayCallController extends BaseController<DevelopDayCall> {
	
	@ApiOperation(value = "查询经纪人日通话统计表")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "经纪人日通话统计表详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody DevelopDayCall param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改经纪人日通话统计表")
	public Object update(ModelMap modelMap, @RequestBody DevelopDayCall param) {
		return super.update(modelMap, param);
	}

	@DeleteMapping
	@ApiOperation(value = "删除经纪人日通话统计表")
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

	@ApiOperation(value = "市场部网销开发详情视图")
	@PutMapping(value = "/read/developList")
	public Object developVo(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		List<Map<String, Object>> developVoList = ((DevelopDayCallService)service).developVo(param,super.getCurrUser());
		return setSuccessModelMap(modelMap,developVoList);
	}
	
	@ApiOperation(value = "市场部电销开发详情视图")
	@PutMapping(value = "/read/developTelemarketingList")
	public Object developTelemarketingVo(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		List<Map<String, Object>> developVoList = ((DevelopDayCallService)service).developTelemarketingVo(param,super.getCurrUser());
		return setSuccessModelMap(modelMap,developVoList);
	}
	
	@ApiOperation(value = "升级部开发详情视图")
	@PutMapping(value = "/read/upgradeDevelopList")
	public Object upgradeDevelopVo(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		List<Map<String, Object>> upgradeDevelopVoList = ((DevelopDayCallService)service).upgradeDevelopVo(param,super.getCurrUser());
		return setSuccessModelMap(modelMap,upgradeDevelopVoList);
	}
}
