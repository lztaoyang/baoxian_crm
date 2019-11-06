package com.lazhu.ad.cost.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.lazhu.ad.cost.entity.Cost;
import com.lazhu.ad.cost.service.CostService;
import com.lazhu.business.excel.service.ExcelService;
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
@RequestMapping(value = "ad/cost")
public class CostController extends BaseController<Cost> {
	
	@Autowired
	ExcelService excelService;
	
	@ApiOperation(value = "查询")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody Cost param) {
		Assert.notNull(param.getId(), "ID");
		Cost result = service.queryById(param.getId());
		return setSuccessModelMap(modelMap, result);
	}
	
	@ApiOperation(value = "日统计报表")
	@PutMapping(value = "/read/reportDay")
	public Object get(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return setSuccessModelMap(modelMap, ((CostService)service).queryReortDay(param));
	}
	
	@ApiOperation(value = "推广客户明细表")
	@PutMapping(value = "/read/reportCustomerDetail")
	public Object queryReportCustomerDetail(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return setSuccessModelMap(modelMap, ((CostService)service).queryReportCustomerDetail(param));
	}
	
	@ApiOperation(value = "渠道统计报表")
	@PutMapping(value = "/read/channelTotal")
	public Object channelTotal(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return setSuccessModelMap(modelMap, ((CostService)service).queryChannelTotal(param));
	}
	
	@ApiOperation(value = "执行人统计报表")
	@PutMapping(value = "/read/executerTotal")
	public Object executerTotal(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return setSuccessModelMap(modelMap, ((CostService)service).queryExecuterTotal(param));
	}
	
	@ApiOperation(value = "市场人员统计报表")
	@PutMapping(value = "/read/userTotal")
	public Object userTotal(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return setSuccessModelMap(modelMap, ((CostService)service).queryUserTotal(param));
	}

	@PostMapping
	@ApiOperation(value = "修改")
	public Object update(ModelMap modelMap, @RequestBody Cost param) {
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
	
	/**
	 * 签单明细
	 * @param modelMap
	 * @param param
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@ApiOperation(value = "导出签单明细excel")
	@GetMapping(value = "/exportApplyDetail")
	public void exportApplyDetail(ModelMap modelMap, @RequestParam Map<String, Object> param,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		List<Map<String,Object>> list = ((CostService)service).exportApplyDetail(param);
		String[] headers = new String[list.get(0).size() + 1];
		headers[0] = "序号";
		headers[1] = "提交时间"; 
		headers[2] = "分配时间"; 
		headers[3] = "签单时间"; 
		headers[4] = "执行人";
		headers[5] = "姓名"; 
		headers[6] = "手机号"; 
		headers[7] = "签单次数"; 
		headers[8] = "签单总金额"; 
		headers[9] = "内容"; 
		headers[10] = "专题名"; 
		headers[11] = "计划名";
		headers[12] = "区域"; 
		headers[13] = "来源地址"; 
		headers[14] = "UserAgent浏览器"; 
		headers[15] = "渠道";  
		headers[16] = "号码状态";
		try {
			excelService.exportExcelName(list,response,"签单明细",headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
