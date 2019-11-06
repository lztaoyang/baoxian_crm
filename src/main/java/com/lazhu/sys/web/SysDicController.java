package com.lazhu.sys.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.lazhu.core.Constants;
import com.lazhu.core.base.BaseController;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.model.SysDic;
import com.lazhu.sys.service.SysDicService;

/**
 * 字典管理
 * 
 * @author naxj
 * 
 */
@RestController
@Api(value = "字典管理", description = "字典管理")
@RequestMapping(value = "sys/dic")
public class SysDicController extends BaseController<SysDic> {
	
	@ApiOperation(value = "根据关键字查询字典列表")
	@PutMapping(value = "/read/type")
	public Object getDicByType(ModelMap modelMap, @RequestBody SysDic param) {
		Map<?, ?> result = ((SysDicService) service).queryDicByType(param.getType());
		return setSuccessModelMap(modelMap, result);
	}
	
	@ApiOperation(value = "根据关键字查询字典列表")
	@GetMapping(value = "/read/all")
	public Object getAllDicByType(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
		//获取用户
		//Assert.notNull(super.getCurrUser(),"请退出系统，重新登录");
		if (StrUtils.isNullOrBlank(super.getCurrUser())) {
			response.sendRedirect(Constants.LOGIN_URL);
			return null;
		}
		Map<String, Map<String, String>> allDic = ((SysDicService) service).getAllDic(super.getCurrUser());
		return setSuccessModelMap(modelMap, allDic);
	}
	
	@ApiOperation(value = "查询字典项")
	//@RequiresPermissions("sys.base.dic.read")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		param.put("orderBy", "sort_no");
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "字典项详情")
	//@RequiresPermissions("sys.base.dic.read")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody SysDic param) {
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改字典项")
	//@RequiresPermissions("sys.base.dic.update")
	public Object update(ModelMap modelMap, @RequestBody SysDic param) {
		//新增时，默认为激活的
		if (null == param.getId()) {
			param.setLocked(true);
		}
		return super.update(modelMap, param);
	}

	@DeleteMapping
	@ApiOperation(value = "删除字典项")
	//@RequiresPermissions("sys.base.dic.delete")
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
