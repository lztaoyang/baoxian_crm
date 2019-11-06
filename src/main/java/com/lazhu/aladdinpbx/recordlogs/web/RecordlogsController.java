package com.lazhu.aladdinpbx.recordlogs.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.aladdinpbx.recordlogs.entity.Recordlogs;
import com.lazhu.aladdinpbx.recordlogs.service.RecordlogsService;
import com.lazhu.core.base.BaseController;
import com.lazhu.core.support.Assert;
import com.lazhu.crm.Constant;
import com.lazhu.sys.service.SysUserService;

/**
 * <p>
 * 电话录音记录表 前端控制器
 * </p>
 *
 * @author hz
 * @since 2017-12-02
 */
@RestController
@Api(value = "电话录音记录表", description = "电话录音记录表")
@RequestMapping(value = "aladdinpbx/recordlogs")
public class RecordlogsController extends BaseController<Recordlogs> {
	
	@Autowired
	RecordlogsService recordlogsService;
	
	@Autowired
	SysUserService userService;
	
	@ApiOperation(value = "查询已接电话录音记录表")
	@PutMapping(value = "/read/answerList")
	public Object answerList(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(super.getCurrUser(), "请重新登录系统");
		Long userGroup = userService.queryById(super.getCurrUser()).getUserGroup();
		//防止不设定用户组，越权
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		if (userGroup == Constant.USER_GROUP_HG) {//合规客服
			param.put("recAgentid", super.getCurrUser());
		}
		Page<Recordlogs> recordlogsList = recordlogsService.answerList(param);
		return setSuccessModelMap(modelMap,recordlogsList);
	}

	@ApiOperation(value = "电话录音记录表详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody Recordlogs param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改电话录音记录表")
	public Object update(ModelMap modelMap, @RequestBody Recordlogs param) {
		return super.update(modelMap, param);
	}

	@DeleteMapping
	@ApiOperation(value = "删除电话录音记录表")
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
