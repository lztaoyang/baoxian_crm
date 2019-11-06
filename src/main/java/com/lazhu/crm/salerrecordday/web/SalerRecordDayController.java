package com.lazhu.crm.salerrecordday.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lazhu.core.base.BaseController;
import com.lazhu.crm.salerrecordday.entity.SalerRecordDay;

/**
 * <p>
 * 市场部通话记录今天表 前端控制器
 * </p>
 *
 * @author hz
 * @since 2018-04-13
 */
@RestController
@Api(value = "市场部通话记录今天表", description = "市场部通话记录今天表")
@RequestMapping(value = "crm/salerRecordDay")
public class SalerRecordDayController extends BaseController<SalerRecordDay> {
	
	@ApiOperation(value = "查询市场部通话记录今天表")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "市场部通话记录今天表详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody SalerRecordDay param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改市场部通话记录今天表")
	public Object update(ModelMap modelMap, @RequestBody SalerRecordDay param) {
		return super.update(modelMap, param);
	}

}
