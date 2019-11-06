package com.lazhu.sys.web;

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
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.model.Properties;
import com.lazhu.sys.service.PropertiesService;

/**
 * <p>
 * 配置表 前端控制器
 * </p>
 *
 * @author hz
 * @since 2018-01-07
 */
@RestController
@Api(value = "配置表", description = "配置表")
@RequestMapping(value = "sys/properties")
public class PropertiesController extends BaseController<Properties> {
	
	@ApiOperation(value = "查询配置表")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "配置表详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody Properties param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改配置表")
	public Object update(ModelMap modelMap, @RequestBody Properties param) {
		return super.update(modelMap, param);
	}

	@DeleteMapping
	@ApiOperation(value = "删除配置表")
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
	
	@ApiOperation(value = "查询配置value")
	@PutMapping(value = "/queryByKeyString")
	public Object queryByKeyString(ModelMap modelMap,@RequestBody Map<String, Object> param) {
		String keyString = StrUtils.toString(param.get("keyString"));
		Assert.notNull(keyString,"KEY不可为空");
		Properties p = ((PropertiesService)service).queryByKeyString(keyString);
		return setSuccessModelMap(modelMap, p);
	}
}
