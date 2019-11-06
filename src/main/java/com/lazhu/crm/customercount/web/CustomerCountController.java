package com.lazhu.crm.customercount.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONArray;
import com.lazhu.core.base.BaseController;
import com.lazhu.crm.customercount.entity.CustomerCount;
import com.lazhu.crm.customercount.service.CustomerCountService;

/**
 *  会员统计表前端控制器
 *
 * @author ty
 * @date 2018年11月21日
 */
@RestController
@Api(value = "会员统计表", description = "会员统计表")
@RequestMapping(value = "crm/customercount")
public class CustomerCountController extends BaseController<CustomerCount> {
	
	@Autowired
	CustomerCountService customerCountService;
	
	@ApiOperation(value = "查询")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return this.setSuccessModelMap(modelMap, customerCountService.queryByDimension(param));
	}

	@ApiOperation(value = "详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody CustomerCount param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改")
	public Object update(ModelMap modelMap, @RequestBody CustomerCount param) {
		return super.update(modelMap, param);
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
	
	@ApiOperation(value = "同步会员统计表数据")
	@GetMapping(value = "/synHistorySignApply")
	public Object synHistorySignApply(ModelMap modelMap,String key){
		customerCountService.synHistorySignApply();
		return setSuccessModelMap(modelMap);
	}
}
