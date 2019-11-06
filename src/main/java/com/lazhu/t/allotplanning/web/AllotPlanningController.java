package com.lazhu.t.allotplanning.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.lazhu.t.allotplanning.entity.AllotPlanning;

/**
 * <p>
 * 推广分配计划表 前端控制器
 * </p>
 *
 * @author hz
 * @since 2018-05-10
 */
@RestController
@Api(value = "推广分配计划表", description = "推广分配计划表")
@RequestMapping(value = "t/allotPlanning")
public class AllotPlanningController extends BaseController<AllotPlanning> {
	
	@ApiOperation(value = "查询推广分配计划表")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "推广分配计划表详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody AllotPlanning param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改推广分配计划表")
	public Object update(ModelMap modelMap, @RequestBody AllotPlanning param) {
		return super.update(modelMap, param);
	}

	@DeleteMapping
	@ApiOperation(value = "删除推广分配计划表")
	public Object delete(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(param.get("ids"), "IDS");
		List<Long> ids = null;
		if (param.get("ids") instanceof String){
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.get("ids").toString()));
		}else if(param.get("ids") instanceof JSONArray) {
			ids = ((JSONArray)param.get("ids")).toJavaList(Long.class);
		}
		return super.delete(modelMap, ids);
	}
}
