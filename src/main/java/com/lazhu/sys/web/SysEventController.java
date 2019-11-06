package com.lazhu.sys.web;

import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lazhu.core.base.BaseController;
import com.lazhu.sys.model.SysEvent;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 系统日志控制类
 * 
 * @author naxj
 * 
 */
@RestController
@Api(value = "系统日志", description = "系统日志")
@RequestMapping(value = "sys/event")
public class SysEventController extends BaseController<SysEvent> {

	@ApiOperation(value = "查询日志")
	//@RequiresPermissions("public.news.read")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}
}
