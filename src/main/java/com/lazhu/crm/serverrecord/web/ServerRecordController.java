package com.lazhu.crm.serverrecord.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.lazhu.core.base.BaseController;
import com.lazhu.crm.serverrecord.entity.ServerRecord;
import com.lazhu.crm.serverrecord.service.ServerRecordService;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@RestController
@Api(value = "", description = "")
@RequestMapping(value = "crm/serverRecord")
public class ServerRecordController extends BaseController<ServerRecord> {
	
	@ApiOperation(value = "查询")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody ServerRecord param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改")
	public Object update(ModelMap modelMap, @RequestBody ServerRecord param) {
		if (param != null) {
			param.setUpdateBy(getCurrUser());
			if (param.getId() == null) {
				Assert.notNull(param.getIds(), "未获取会员ID");
				List<Long> ids = null;
				if (param.getIds() instanceof String){
					ids = new ArrayList<Long>();
					ids.add(Long.valueOf(param.getIds().toString()));
				}else if(param.getIds() instanceof JSONArray) {
					ids = ((JSONArray)param.getIds()).toJavaList(Long.class);
				}
				List<String> names = null;
				if (param.getIds() instanceof String){
					names = new ArrayList<String>();
					names.add(String.valueOf(param.getNames().toString()));
				}else if(param.getIds() instanceof JSONArray) {
					names = ((JSONArray)param.getNames()).toJavaList(String.class);
				}
				((ServerRecordService)service).add(param,ids,names,super.getCurrUser());
			} else {
				super.update(modelMap, param);
			}
		}
		return super.setSuccessModelMap(modelMap);
	}

	@DeleteMapping
	@ApiOperation(value = "删除")
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
