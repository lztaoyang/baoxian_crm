package com.lazhu.crm.worktrailabnormal.web;

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
import com.lazhu.crm.worktrailabnormal.entity.WorkTrailAbnormal;
import com.lazhu.crm.worktrailabnormal.service.WorkTrailAbnormalService;

/**
 * <p>
 * 市场部异常工作轨迹表 前端控制器
 * </p>
 *
 * @author LAPTOP-HDRAA393
 * @since 2018-02-01
 */
@RestController
@Api(value = "市场部异常工作轨迹表", description = "市场部异常工作轨迹表")
@RequestMapping(value = "crm/workTrailAbnormal")
public class WorkTrailAbnormalController extends BaseController<WorkTrailAbnormal> {
	
	@ApiOperation(value = "查询市场部异常工作轨迹表")
	@PutMapping(value = "/read/list")
	public Object queryL(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "市场部异常工作轨迹表详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody WorkTrailAbnormal param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}
	
	@ApiOperation(value = "市场部正常工作轨迹统计查询")
	@PutMapping(value = "/read/total")
	public Object get(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		((WorkTrailAbnormalService)service).setBusiness(param, getCurrUser());
		return setSuccessModelMap(modelMap, ((WorkTrailAbnormalService)service).queryTotal(param));
	}

	@PostMapping
	@ApiOperation(value = "修改市场部异常工作轨迹表")
	public Object update(ModelMap modelMap, @RequestBody WorkTrailAbnormal param) {
		return super.update(modelMap, param);
	}

	@DeleteMapping
	@ApiOperation(value = "删除市场部异常工作轨迹表")
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
