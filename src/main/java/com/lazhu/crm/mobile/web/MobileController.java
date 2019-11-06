package com.lazhu.crm.mobile.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.lazhu.core.util.WebUtil;
import com.lazhu.crm.mobile.entity.Mobile;
import com.lazhu.crm.mobile.service.MobileService;

/**
 * <p>
 * 客户联系方式表 前端控制器
 * </p>
 *
 * @author hz
 * @since 2018-02-01
 */
@RestController
@Api(value = "客户联系方式表", description = "客户联系方式表")
@RequestMapping(value = "crm/mobile")
public class MobileController extends BaseController<Mobile> {
	
	@ApiOperation(value = "查询客户联系方式表")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page<Mobile> mobilePage = ((MobileService)service).queryMobile(modelMap, param);
		return setSuccessModelMap(modelMap,mobilePage);
	}

	@ApiOperation(value = "客户联系方式表详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody Mobile param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改客户联系方式表")
	public Object update(ModelMap modelMap, @RequestBody Mobile param,HttpServletRequest request) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		Assert.notNull(param.getMobile(), "手机号不能为空");
		//当前IP
		String ip = WebUtil.getHost(request);
		//当前操作人
		param.setUpdateBy(super.getCurrUser());
		param.setUpdateTime(new Date());
		Mobile  mobile = null;
		if (null == param.getId()) {
			mobile = ((MobileService)service).addMobile(param,ip);
		} else {
			mobile = ((MobileService)service).updateMobile(param,ip);
		}
		return setSuccessModelMap(modelMap,mobile);
	}

	@DeleteMapping
	@ApiOperation(value = "删除客户联系方式表")
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
