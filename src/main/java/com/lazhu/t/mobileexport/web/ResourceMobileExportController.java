package com.lazhu.t.mobileexport.web;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lazhu.t.mobileexport.entity.ResourceMobileExport;
import com.lazhu.t.mobileexport.service.ResourceMobileExportService;
import com.lazhu.business.excel.service.ExcelService;
import com.lazhu.core.base.BaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 推广资源手机号信息导出控制器
 * 
 * @author ty
 * @date 2018年11月21日
 */
@RestController
@Api(value = "推广资源手机号信息导出", description = "推广资源手机号信息导出")
@RequestMapping(value = "t/resourceMobileExport")
public class ResourceMobileExportController extends BaseController<ResourceMobileExport>{

	@Autowired
	ResourceMobileExportService resourceMobileExportService;
	
	@Autowired
	ExcelService excelService;
	
	@ApiOperation(value = "导出excel")
	@GetMapping(value = "/exportExcel")
	public void exportExcel(ModelMap modelMap,HttpServletRequest request,HttpServletResponse response,String channel,String flowId,String startDate,String endDate) throws UnsupportedEncodingException {
		Map<String, Object> param = new HashMap<String, Object>();	
		param.put("channel", channel);
		param.put("flowId", flowId);
		param.put("startDate", startDate);
		param.put("endDate", endDate);
		List<Map<String,Object>> list = resourceMobileExportService.queryMobileList(param);
		if(list != null && list.size() > 0){
//			excelService.exportExcel(list, response);
			String[] headers = new String[2];
			headers[1] = "sha手机号码";
			excelService.exportExcelName(list, response, "手机号码Sha ", headers);
		}
	}
	
	@ApiOperation(value = "查询数据条数")
	@PostMapping(value = "/queryMobileNum")
	public Object exportExcel(ModelMap modelMap, @RequestBody Map<String, Object> param){
		if(param != null){
			List<Map<String,Object>> list = resourceMobileExportService.queryMobileList(param);
			if(list != null && list.size() > 0){
				return this.setSuccessModelMap(modelMap, list.size());
			}
		}
		return this.setSuccessModelMap(modelMap, 0);
	}
	
}
