package com.lazhu.crm.mobilelastlog.web;

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
import com.lazhu.crm.mobilelastlog.entity.MobileLastLog;

/**
 * <p>
 * 最近拨打记录 前端控制器
 * </p>
 *
 * @author hz
 * @since 2018-01-07
 */
@RestController
@Api(value = "最近拨打记录", description = "最近拨打记录")
@RequestMapping(value = "crm/mobileLastLog")
public class MobileLastLogController extends BaseController<MobileLastLog> {
	
	@ApiOperation(value = "查询最近拨打记录")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "最近拨打记录详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody MobileLastLog param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改最近拨打记录")
	public Object update(ModelMap modelMap, @RequestBody MobileLastLog param) {
		return super.update(modelMap, param);
	}

	@DeleteMapping
	@ApiOperation(value = "删除最近拨打记录")
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
