package com.lazhu.ad.channelcost.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lazhu.ad.channelcost.entity.ChannelCost;
import com.lazhu.ad.channelcost.service.ChannelCostService;
import com.lazhu.ad.cost.entity.Cost;
import com.lazhu.core.base.BaseController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author phong
 * @since 2017-11-02
 */
@RestController
@Api(value = "", description = "")
@RequestMapping(value = "ad/channelCost")
public class ChannelCostController extends BaseController<ChannelCost> {
	
	@ApiOperation(value = "查询")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody ChannelCost param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改")
	public Object update(ModelMap modelMap, @RequestBody ChannelCost param) {
		param.setUpdateBy(super.getCurrUser());
		Cost result = ((ChannelCostService)service).updateChannel(param);
		return setSuccessModelMap(modelMap, result);
	}

	@DeleteMapping
	@ApiOperation(value = "删除")
	public Object delete(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(param.get("id"), "ID");
		Cost result = ((ChannelCostService)service).delChannel(Long.valueOf(param.get("id").toString()));
		return setSuccessModelMap(modelMap, result);
	}
}
