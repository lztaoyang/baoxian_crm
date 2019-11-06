package com.lazhu.crm.resourceleave.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.base.BaseController;
import com.lazhu.core.util.WebUtil;
import com.lazhu.crm.resourceleave.entity.ResourceLeave;
import com.lazhu.crm.resourceleave.service.ResourceLeaveService;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.model.SysUser;

/**
 * <p>
 * 资源闲置表 前端控制器
 * </p>
 *
 * @author hz
 * @since 2018-04-13
 */
@RestController
@Api(value = "资源闲置表", description = "资源闲置表")
@RequestMapping(value = "crm/resourceLeave")
public class ResourceLeaveController extends BaseController<ResourceLeave> {
	
	@ApiOperation(value = "查询资源闲置表")
	@PutMapping(value = "/read/list")
	public Object queryL(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page page = ((ResourceLeaveService) service).resourceLeaveList(param, super.getCurrUser());
		return setSuccessModelMap(modelMap, page);
	}
	

	@ApiOperation(value = "查询剩余可抽取资源数")
	@PutMapping(value = "/read/remainExtractNum")
	public Object remainExtractNum(ModelMap modelMap) {
		int num = StrUtils.toInt(((ResourceLeaveService)service).remainExtractNum());
		return setSuccessModelMap(modelMap,num);
	}

	@ApiOperation(value = "资源闲置表详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody ResourceLeave param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改资源闲置表")
	public Object update(ModelMap modelMap, @RequestBody ResourceLeave param) {
		return super.update(modelMap, param);
	}
	
	
	@PostMapping("/extractResource")
	@ApiOperation(value = "抽取共享池资源")
	public Object extractResource(ModelMap modelMap) {
		//查询抽取次数
		SysUser user = ((ResourceLeaveService)service).getUserById(super.getCurrUser());
		int num = 0;
		if (null != user && user.getExtractNum() > 0) {
			num = ((ResourceLeaveService)service).getSharedPoolResource(super.getCurrUser(),0,true);
		}
		return setSuccessModelMap(modelMap,num);
	}
	
	@PostMapping("/allot")
	@ApiOperation(value = "资源调配")
	public Object allot(ModelMap modelMap, @RequestBody Map<String, Object> param,HttpServletRequest request) {
		Assert.notNull(param.get("ids"), "未选中资源");
		Assert.notNull(param.get("userId"), "未指定被分配人");
		List<Long> ids = null;
		if (param.get("ids") instanceof String){
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.get("ids").toString()));
		}else if(param.get("ids") instanceof JSONArray) {
			ids = ((JSONArray)param.get("ids")).toJavaList(Long.class);
		}
		Long userId = StrUtils.toLong(param.get("userId"));
		String ip = WebUtil.getHost(request);
		List<ResourceLeave>  resourceLeave = ((ResourceLeaveService)service).allot(ids,userId,super.getCurrUser(),ip);
		return setSuccessModelMap(modelMap,resourceLeave);
	}

}
