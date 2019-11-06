package com.lazhu.aladdinpbx.noanswercall.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.lazhu.aladdinpbx.noanswercall.entity.Noanswercall;
import com.lazhu.aladdinpbx.noanswercall.service.NoanswercallService;
import com.lazhu.core.base.BaseController;

/**
 * <p>
 * 未接电话记录表 前端控制器
 * </p>
 *
 * @author hz
 * @since 2017-12-18
 */
@RestController
@Api(value = "未接电话记录表", description = "未接电话记录表")
@RequestMapping(value = "aladdinpbx/noanswercall")
public class NoanswercallController extends BaseController<Noanswercall> {
	
	@Autowired
	private NoanswercallService noanswercallService;
	
	@ApiOperation(value = "查询未接电话记录及客户相关")
	@PutMapping(value = "/read/list/customer")
	public Object queryList(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(super.getCurrUser(), "请重新登录系统");
		if (super.getCurrUser() > 10) {
			param.put("nasAgentid", super.getCurrUser());
		}
		Page<Noanswercall> page = noanswercallService.queryListCustomer(param);
		return setSuccessModelMap(modelMap, page);
	}

	@ApiOperation(value = "未接电话记录表详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody Noanswercall param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改未接电话记录表")
	public Object update(ModelMap modelMap, @RequestBody Noanswercall param) {
		return super.update(modelMap, param);
	}

	@DeleteMapping
	@ApiOperation(value = "删除未接电话记录表")
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
