package com.lazhu.sys.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lazhu.core.base.AbstractController;
import com.lazhu.core.exception.IllegalParameterException;
import com.lazhu.core.util.WebUtil;
import com.lazhu.sys.model.SysRoleMenu;
import com.lazhu.sys.model.SysUserMenu;
import com.lazhu.sys.model.SysUserRole;
import com.lazhu.sys.service.SysAuthorizeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 权限管理
 * 
 * @author naxj
 * 
 */
@RestController
@Api(value = "权限管理", description = "权限管理")
@RequestMapping(value = "sys")
public class SysAuthorizeController extends AbstractController {
	@Autowired
	private SysAuthorizeService sysAuthorizeService;

	@ApiOperation(value = "获取用户菜单编号")
	@PutMapping(value = "user/read/menu")
	//@RequiresPermissions("sys.permisson.userMenu.read")
	public Object getUserMenu(ModelMap modelMap, @RequestBody SysUserMenu param) {
		List<?> menus = sysAuthorizeService.queryMenuIdsByUserId(param.getUserId());
		return setSuccessModelMap(modelMap, menus);
	}

	@ApiOperation(value = "修改用户菜单")
	@PostMapping(value = "/user/update/menu")
	//@RequiresPermissions("sys.permisson.userMenu.update")
	public Object userMenu(ModelMap modelMap, @RequestBody List<SysUserMenu> list) {
		Long userId = null;
		Long currentUserId = WebUtil.getCurrentUser();
		for (SysUserMenu sysUserMenu : list) {
			if (sysUserMenu.getUserId() != null) {
				if (userId != null && userId != sysUserMenu.getUserId()) {
					throw new IllegalParameterException("参数错误.");
				}
				userId = sysUserMenu.getUserId();
			}
			sysUserMenu.setCreateBy(currentUserId);
			sysUserMenu.setUpdateBy(currentUserId);
		}
		sysAuthorizeService.updateUserMenu(list);
		return setSuccessModelMap(modelMap);
	}

	@ApiOperation(value = "获取用户角色")
	@PutMapping(value = "user/read/role")
	//@RequiresPermissions("sys.permisson.userRole.read")
	public Object getUserRole(ModelMap modelMap, @RequestBody SysUserRole param) {
		List<?> menus = sysAuthorizeService.getRolesByUserId(param.getUserId());
		return setSuccessModelMap(modelMap, menus);
	}

	@ApiOperation(value = "修改用户角色")
	@PostMapping(value = "/user/update/role")
	//@RequiresPermissions("sys.permisson.userRole.update")
	public Object userRole(ModelMap modelMap, @RequestBody List<SysUserRole> list) {
		Long userId = null;
		Long currentUserId = WebUtil.getCurrentUser();
		for (SysUserRole sysUserRole : list) {
			if (sysUserRole.getUserId() != null) {
				if (userId != null && userId.longValue() != sysUserRole.getUserId().longValue()) {
					throw new IllegalParameterException("参数错误.");
				}
				userId = sysUserRole.getUserId();
			}
			sysUserRole.setCreateBy(currentUserId);
			sysUserRole.setUpdateBy(currentUserId);
		}
		sysAuthorizeService.updateUserRole(list);
		return setSuccessModelMap(modelMap);
	}

	@ApiOperation(value = "获取角色菜单编号")
	@PutMapping(value = "role/read/menu")
	//@RequiresPermissions("sys.permisson.roleMenu.read")
	public Object getRoleMenu(ModelMap modelMap, @RequestBody SysRoleMenu param) {
		List<?> menus = sysAuthorizeService.queryMenuIdsByRoleId(param.getRoleId());
		List<HashMap<String,String>> result = new ArrayList<HashMap<String,String>>();
		for(Object menuId:menus){
			HashMap<String,String> temp = new HashMap<String,String>();
			temp.put("menuId", menuId.toString());
			result.add(temp);
		}
		return setSuccessModelMap(modelMap, result);
	}

	@ApiOperation(value = "修改角色菜单")
	@PostMapping(value = "/role/update/menu")
	//@RequiresPermissions("sys.permisson.roleMenu.update")
	public Object roleMenu(ModelMap modelMap, @RequestBody List<SysRoleMenu> list) {
		Long roleId = null;
		Long userId = WebUtil.getCurrentUser();
		for (SysRoleMenu sysRoleMenu : list) {
			if (sysRoleMenu.getRoleId() != null) {
				if (roleId != null && !roleId.equals(sysRoleMenu.getRoleId())) {
					throw new IllegalParameterException("参数错误.");
				}
				roleId = sysRoleMenu.getRoleId();
			}
			sysRoleMenu.setCreateBy(userId);
			sysRoleMenu.setUpdateBy(userId);
		}
		sysAuthorizeService.updateRoleMenu(list);
		return setSuccessModelMap(modelMap);
	}

	@ApiOperation(value = "获取人员操作权限")
	@PutMapping(value = "user/read/permission")
	//@RequiresPermissions("sys.permisson.user.read")
	public Object queryUserPermissions(ModelMap modelMap, @RequestBody SysUserMenu record) {
		List<?> menuIds = sysAuthorizeService.queryUserPermissions(record);
		return setSuccessModelMap(modelMap, menuIds);
	}

	@ApiOperation(value = "修改用户操作权限")
	@PostMapping(value = "/user/update/permission")
	//@RequiresPermissions("sys.permisson.user.update")
	public Object updateUserPermission(ModelMap modelMap, @RequestBody List<SysUserMenu> list) {
		sysAuthorizeService.updateUserPermission(list);
		return setSuccessModelMap(modelMap);
	}

	@ApiOperation(value = "获取角色操作权限")
	@PutMapping(value = "role/read/permission")
	//@RequiresPermissions("sys.permisson.role.read")
	public Object queryRolePermissions(ModelMap modelMap, @RequestBody SysRoleMenu record) {
		List<?> menuIds = sysAuthorizeService.queryRolePermissions(record);
		return setSuccessModelMap(modelMap, menuIds);
	}

	@ApiOperation(value = "修改角色操作权限")
	@PostMapping(value = "/role/update/permission")
	//@RequiresPermissions("sys.permisson.role.update")
	public Object updateRolePermission(ModelMap modelMap, @RequestBody List<SysRoleMenu> list) {
		sysAuthorizeService.updateRolePermission(list);
		return setSuccessModelMap(modelMap);
	}

	@ApiOperation(value = "清理缓存")
	//@RequiresPermissions("sys.cache.update")
	@RequestMapping(value = "/cache/update", method = RequestMethod.POST)
	public Object flush(HttpServletRequest request, ModelMap modelMap) {
		
		return setSuccessModelMap(modelMap);
	}
}
