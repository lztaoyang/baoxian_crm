package com.lazhu.sys.web;

import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lazhu.core.base.BaseController;
import com.lazhu.sys.model.SysRole;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 角色管理
 * 
 * @author naxj
 * 
 */
@RestController
@Api(value = "角色管理", description = "角色管理")
@RequestMapping(value = "sys/role")
public class SysRoleController extends BaseController<SysRole> {

	@ApiOperation(value = "查询角色")
	//@RequiresPermissions("sys.base.role.read")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "角色详情")
	//@RequiresPermissions("sys.base.role.read")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody SysRole param) {
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改角色")
	//@RequiresPermissions("sys.base.role.update")
	public Object update(ModelMap modelMap, @RequestBody SysRole param) {
		return super.update(modelMap, param);
	}

	@DeleteMapping
	@ApiOperation(value = "删除角色")
	//@RequiresPermissions("sys.base.role.delete")
	public Object delete(ModelMap modelMap, @RequestBody SysRole param) {
		return super.del(modelMap, param);
	}
}
