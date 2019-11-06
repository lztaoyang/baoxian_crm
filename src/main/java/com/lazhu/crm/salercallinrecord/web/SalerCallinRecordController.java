package com.lazhu.crm.salercallinrecord.web;

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
import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.base.BaseController;
import com.lazhu.crm.salercallinrecord.entity.SalerCallinRecord;
import com.lazhu.crm.salercallinrecord.service.SalerCallinRecordService;

/**
 * <p>
 * 市场部来电记录表 前端控制器
 * </p>
 *
 * @author hz
 * @since 2018-01-18
 */
@RestController
@Api(value = "市场部来电记录表", description = "市场部来电记录表")
@RequestMapping(value = "crm/salerCallinRecord")
public class SalerCallinRecordController extends BaseController<SalerCallinRecord> {
	
	@ApiOperation(value = "查询市场部来电记录表")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page page = ((SalerCallinRecordService)service).queryList(param,super.getCurrUser());
		return setSuccessModelMap(modelMap,page);
	}

	@ApiOperation(value = "市场部来电记录表详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody SalerCallinRecord param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改市场部来电记录表")
	public Object update(ModelMap modelMap, @RequestBody SalerCallinRecord param) {
		return super.update(modelMap, param);
	}

	@DeleteMapping
	@ApiOperation(value = "删除市场部来电记录表")
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
