package com.lazhu.aladdinpbx.agentevent.web;

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
import com.lazhu.aladdinpbx.agentevent.entity.AgentEvent;
import com.lazhu.core.base.BaseController;
import com.lazhu.ecp.utils.DateUtils;
import com.lazhu.ecp.utils.StrUtils;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hz
 * @since 2018-06-12
 */
@RestController
@Api(value = "", description = "")
@RequestMapping(value = "aladdinpbx/agentEvent")
public class AgentEventController extends BaseController<AgentEvent> {
	
	@ApiOperation(value = "查询")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody AgentEvent param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改")
	public Object update(ModelMap modelMap, @RequestBody AgentEvent param) {
		return super.update(modelMap, param);
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
	
	@ApiOperation(value = "接收分机事件")
	@PostMapping(value = "/sendMsg")
	public void sendMsg(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		System.out.println("sendMsg接口接收到请求："+param.toString());
		//Assert.notNull(param.get("Code"), "Code不能为空");
		AgentEvent agentEvent = new AgentEvent();
		agentEvent.setCode(StrUtils.toInteger(param.get("Code")));
		agentEvent.setAgentExt(StrUtils.toInteger(param.get("Ext")));
		agentEvent.setAgentFrom(StrUtils.toInteger(param.get("From")));
		agentEvent.setAgentTo(StrUtils.toInteger(param.get("To")));
		agentEvent.setBody(StrUtils.toString(param.get("Body")));
		agentEvent.setP1(StrUtils.toString(param.get("P1")));
		agentEvent.setP2(StrUtils.toString(param.get("P2")));
		agentEvent.setP3(StrUtils.toString(param.get("P3")));
		agentEvent.setP4(StrUtils.toString(param.get("P4")));
		agentEvent.setP5(StrUtils.toString(param.get("P5")));
		agentEvent.setP6(StrUtils.toString(param.get("P6")));
		agentEvent.setP7(StrUtils.toString(param.get("P7")));
		try {
			agentEvent.setDt(DateUtils.convertDate(StrUtils.toString(param.get("DT")), DateUtils.TIME_FORMAT));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("DT时间处理错误");
		}
		agentEvent.setRemark(param.toString());
		super.update(modelMap, agentEvent);
	}
	
}
