package com.lazhu.product.insuranceproduct.web;

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
import com.lazhu.product.insuranceproduct.entity.InsuranceProduct;
import com.lazhu.product.insuranceproduct.service.InsuranceProductService;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hz
 * @since 2017-06-20
 */
@RestController
@Api(value = "", description = "")
@RequestMapping(value = "bd/insuranceProduct")
public class InsuranceProductController extends BaseController<InsuranceProduct> {
	
	@ApiOperation(value = "查询")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		List<InsuranceProduct> productList = ((InsuranceProductService)service).queryList(param);
		return setSuccessModelMap(modelMap,productList);
	}

	@ApiOperation(value = "详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody InsuranceProduct param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改")
	public Object update(ModelMap modelMap, @RequestBody InsuranceProduct param) {
		param.setUpdateBy(getCurrUser());
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
	
	@PostMapping("/product")
	@ApiOperation(value = "立即重新统计订单")
	public Object promote(ModelMap modelMap) {
		((InsuranceProductService)this.service).product();
		return super.setSuccessModelMap(modelMap);
	}
}
