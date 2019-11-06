package com.lazhu.t.resourceallot.web;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.business.excel.service.ExcelService;
import com.lazhu.core.base.BaseController;
import com.lazhu.core.util.WebUtil;
import com.lazhu.crm.resource.entity.Resource;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.t.resourceallot.entity.ResourceAllot;
import com.lazhu.t.resourceallot.service.ResourceAllotService;

/**
 * <p>
 * 推广资源分配表 前端控制器
 * </p>
 *
 * @author hz
 * @since 2017-10-27
 */
@RestController
@Api(value = "推广资源分配表", description = "推广资源分配表")
@RequestMapping(value = "t/resourceAllot")
public class ResourceAllotController extends BaseController<ResourceAllot> {
	
	@Autowired
	ExcelService excelService;
	
	@Autowired
	// 开发资源
	private ResourceService resourceService;
	
	
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "查询推广资源分配表")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page page = ((ResourceAllotService)service).readList(param);
		List<ResourceAllot> list = page.getRecords();
		for (int i = 0; i < list.size(); i++) {
			ResourceAllot resourceAllot = list.get(i);
			Long id = resourceService.queryByAllotId(resourceAllot.getId());
			if (StrUtils.isNotNullOrBlank(id)) {
				Resource resource = resourceService.queryById(id);
				if (StrUtils.isNotNullOrBlank(resource)) {
					String remark =  resource.getRemark();
					list.get(i).setRemark(remark);
				}
			}
		}
		return setSuccessModelMap(modelMap,page);
	}
	
	@ApiOperation(value = "查询用户群最大分配数、当前分配数、待分配数")
	@PutMapping(value = "/read/userAllotNum")
	public Object queryUserAllotNum(ModelMap modelMap) {
		int userAllotMan = StrUtils.toInt(((ResourceAllotService)service).queryUserAllotMan());
		int userWaitNum = StrUtils.toInt(((ResourceAllotService)service).queryUserWaitNum());
		int resourceAllotNum = StrUtils.toInt(((ResourceAllotService)service).queryResourceAllotNum());
		return setSuccessModelMap(modelMap, "当前正在分配人数："+userAllotMan+"人，当前已接收分配数："+resourceAllotNum+"，当前等待分配数："+userWaitNum);
	}

	@ApiOperation(value = "推广资源分配表详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody ResourceAllot param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改推广资源分配表")
	public Object update(ModelMap modelMap, @RequestBody ResourceAllot param) {
		return super.update(modelMap, param);
	}
	
	@PostMapping("/allot")
	@ApiOperation(value = "资源调配")
	public Object resourceAllot(ModelMap modelMap, @RequestBody Map<String, Object> param,HttpServletRequest request) {
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
		int num = ((ResourceAllotService)service).resourceAllot(ids,userId,super.getCurrUser(),ip);
		return setSuccessModelMap(modelMap,num);
	}
	
	@ApiOperation(value = "导出excel")
	@GetMapping(value = "/exportMd5Excel")
	public void exportMd5Excel(ModelMap modelMap, 
			@RequestParam Map<String, Object> param,
			HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		List<Map<String,Object>> list = ((ResourceAllotService)service).queryMd5Mobile(param);
		try {
			excelService.exportExcel(list,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@ApiOperation(value = "导出推广明细excel")
	@GetMapping(value = "/exportAllotDetail")
	public void exportAllotDetail(ModelMap modelMap, 
			@RequestParam Map<String, Object> param,
			HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		List<Map<String,Object>> list = ((ResourceAllotService)service).queryAllotDetail(param);
		 String[] headers = new String[list.get(0).size() + 1];
         headers[0] = "序号";
         headers[1] = "执行人"; 
         headers[2] = "客户提交时间"; 
         headers[3] = "CRM分配时间"; 
         headers[4] = "姓名"; 
         headers[5] = "手机号"; 
         headers[6] = "区域"; 
         headers[7] = "搜索关键词";
         headers[8] = "IP地址"; 
         headers[9] = "内容"; 
         headers[10] = "专题名"; 
         headers[11] = "计划名";
         headers[12] = "来源地址"; 
         headers[13] = "备注"; 
         headers[14] = "UserAgent浏览器";  
         headers[15] = "总经理";
         headers[16] = "总监";
         headers[17] = "经理";
         headers[18] = "业务员"; 
         headers[19] = "开发流程"; 
         headers[20] = "渠道";  
         headers[21] = "号码状态";
         headers[22] = "分配状态";
		try {
			excelService.exportExcelName(list,response,"推广明细",headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}