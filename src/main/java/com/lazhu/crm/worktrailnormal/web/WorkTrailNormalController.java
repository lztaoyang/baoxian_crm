package com.lazhu.crm.worktrailnormal.web;

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
import com.lazhu.crm.worktrailnormal.entity.WorkTrailNormal;
import com.lazhu.crm.worktrailnormal.service.WorkTrailNormalService;

/**
 * <p>
 * 市场部正常工作轨迹 前端控制器
 * </p>
 *
 * @author LAPTOP-HDRAA393
 * @since 2018-02-01
 */
@RestController
@Api(value = "市场部正常工作轨迹", description = "市场部正常工作轨迹")
@RequestMapping(value = "crm/workTrailNormal")
public class WorkTrailNormalController extends BaseController<WorkTrailNormal> {
	
	@ApiOperation(value = "查询市场部正常工作轨迹")
	@PutMapping(value = "/read/list")
	public Object queryL(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "市场部正常工作轨迹详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody WorkTrailNormal param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}
	
	@ApiOperation(value = "市场部正常工作轨迹统计查询")
	@PutMapping(value = "/read/total")
	public Object get(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		((WorkTrailNormalService)service).setBusiness(param, getCurrUser());
		return setSuccessModelMap(modelMap, ((WorkTrailNormalService)service).queryTotal(param));
	}

	@PostMapping
	@ApiOperation(value = "修改市场部正常工作轨迹")
	public Object update(ModelMap modelMap, @RequestBody WorkTrailNormal param) {
		return super.update(modelMap, param);
	}

	@DeleteMapping
	@ApiOperation(value = "删除市场部正常工作轨迹")
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
