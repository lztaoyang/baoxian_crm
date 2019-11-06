/**
 * 
 */
package com.lazhu.sys.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lazhu.core.base.BaseController;
import com.lazhu.core.support.Assert;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.model.SysUser;
import com.lazhu.sys.service.SysCacheService;

/**
 * 
 * @author hz
 * @date 2018年1月2日  
 * @version 1.0.0
 */
@RestController
@Api(value = "缓存管理", description = "redis管理")
@RequestMapping(value = "sys/cache")
public class SysCacheController extends BaseController<SysUser> {
	
	@Autowired
	private SysCacheService sysCacheService;

	// 删除redis
	@ApiOperation(value = "删除指定表的redis")
	@DeleteMapping
	public Object delete(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(param.get("cacheNames"), "cacheNames不可为空");
		String cacheNames = StrUtils.toString(param.get("cacheNames"));
		String id = StrUtils.toString(param.get("id"));
		if (StrUtils.isNotNullOrBlank(id)) {
			sysCacheService.flush(cacheNames,id);
		} else {
			sysCacheService.flush(cacheNames);
		}
		return setSuccessModelMap(modelMap);
	}

	
}
