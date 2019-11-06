package com.lazhu.crm.alarm.web;

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
import com.lazhu.core.base.BaseController;
import com.lazhu.crm.alarm.entity.Alarm;
import com.lazhu.crm.alarm.service.AlarmService;
import com.lazhu.sys.model.SysUser;
import com.lazhu.sys.service.SysUserService;

/**
 * <p>
 * 钉钉闹钟 前端控制器
 * </p>
 *
 * @author hz
 * @since 2017-11-16
 */
@RestController
@Api(value = "钉钉闹钟", description = "钉钉闹钟")
@RequestMapping(value = "crm/alarm")
public class AlarmController extends BaseController<Alarm> {
	
	@Autowired
	private SysUserService userService;
	
	@ApiOperation(value = "查询钉钉闹钟")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}
	
	@ApiOperation(value = "当前用户查询当前客户钉钉闹钟列表")
	@PutMapping(value = "/read/queryList")
	public Object queryList(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		List<Alarm> alarmList = ((AlarmService)service).queryValid(param);
		return setSuccessModelMap(modelMap, alarmList);
	}
	
	@ApiOperation(value = "查询钉钉闹钟列表")
	@PutMapping(value = "/read/allList")
	public Object allList(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page<Alarm> alarmList = ((AlarmService)service).allList(param,super.getCurrUser());
		return setSuccessModelMap(modelMap, alarmList);
	}

	@ApiOperation(value = "钉钉闹钟详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody Alarm param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改钉钉闹钟")
	public Object update(ModelMap modelMap, @RequestBody Alarm param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		//当前操作人
		Long userId = super.getCurrUser();
		param.setUpdateBy(userId);
		if (null == param.getId()) {
			SysUser user = userService.queryById(userId);
			param.setUserId(userId);
			param.setUserName(user.getAccount());
			param.setDingId(user.getDingId());
			param.setUserGroup(user.getUserGroup());
			//归属
			SysUser manager = userService.queryById(user.getParentId());
			if (null != manager && manager.getId() > 0) {
				param.setManagerId(manager.getId());
				SysUser director = userService.queryById(manager.getParentId());
				if (null != director && director.getId() > 0) {
					param.setDirectorId(director.getId());
				}
			}
			param = ((AlarmService)service).update(param);
		} else {
			param = ((AlarmService)service).update(param);
		}
		return setSuccessModelMap(modelMap,param);
	}

	@DeleteMapping
	@ApiOperation(value = "删除钉钉闹钟")
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
