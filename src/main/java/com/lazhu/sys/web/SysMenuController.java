package com.lazhu.sys.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.lazhu.core.base.BaseController;
import com.lazhu.sys.model.SysMenu;
import com.lazhu.sys.service.SysMenuService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 菜单管理
 * 
 * @author naxj
 * 
 */
@RestController
@Api(value = "菜单管理", description = "菜单管理")
@RequestMapping(value = "sys/menu")
public class SysMenuController extends BaseController<SysMenu> {

	@ApiOperation(value = "查询菜单")
	@PutMapping(value = "/read/page")
	//@RequiresPermissions("sys.base.menu.read")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "菜单列表")
	@PutMapping(value = "/read/list")
	//@RequiresPermissions("sys.base.menu.read")
	public Object get(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.queryList(modelMap, param);
	}

	@ApiOperation(value = "菜单详情")
	@PutMapping(value = "/read/detail")
	//@RequiresPermissions("sys.base.menu.read")
	public Object get(ModelMap modelMap, @RequestBody SysMenu param) {
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改菜单")
	//@RequiresPermissions("sys.base.menu.update")
	public Object update(ModelMap modelMap, @RequestBody SysMenu param) {
		return super.update(modelMap, param);
	}


	@DeleteMapping
	@ApiOperation(value = "删除菜单")
	//@RequiresPermissions("sys.base.menu.delete")
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

	@ApiOperation(value = "获取所有权限")
	//@RequiresPermissions("sys.base.menu.read")
	@RequestMapping(value = "/read/permission")
	public Object getPermissions(ModelMap modelMap) {
		List<?> list = ((SysMenuService)service).getPermissions();
		return setSuccessModelMap(modelMap, list);
	}
}
