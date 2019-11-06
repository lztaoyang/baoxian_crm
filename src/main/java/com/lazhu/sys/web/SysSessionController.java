package com.lazhu.sys.web;

import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lazhu.core.base.BaseController;
import com.lazhu.core.listener.SessionListener;
import com.lazhu.sys.model.SysSession;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 用户会话管理
 * 
 * @author naxj
 * 
 */
@RestController
@Api(value = "会话管理", description = "会话管理")
@RequestMapping(value = "sys/session")
public class SysSessionController extends BaseController<SysSession> {

	// 查询会话
	@ApiOperation(value = "查询会话")
	@PutMapping(value = "/read/list")
	//@RequiresPermissions("sys.base.session.read")
	public Object get(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Integer number = SessionListener.getAllUserNumber();
		modelMap.put("userNumber", number); // 用户数大于会话数,有用户没有登录
		return super.query(modelMap, param);
	}

	@DeleteMapping
	@ApiOperation(value = "删除会话")
	//@RequiresPermissions("sys.base.session.delete")
	public Object delete(ModelMap modelMap, @RequestBody SysSession param) {
		return super.del(modelMap, param);
	}
}
