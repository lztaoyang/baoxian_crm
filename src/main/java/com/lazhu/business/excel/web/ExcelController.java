package com.lazhu.business.excel.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.UnsupportedEncodingException;
import java.util.Date;
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

import com.lazhu.business.excel.service.ExcelService;
import com.lazhu.core.support.Assert;
import com.lazhu.ecp.utils.DateUtils;
import com.lazhu.ecp.utils.DingUtil;
import com.lazhu.ecp.utils.StrUtils;

/**
 * Date:     2018年5月9日 上午11:19:07
 * @author   hz	 
 */
@RestController
@Api(value = "", description = "")
@RequestMapping(value = "business/excel")
public class ExcelController {
	
	@Autowired
	ExcelService excelService;

	@ApiOperation(value = "导出excel")
	@GetMapping(value = "/exportExcel")
	public void exportExcel(ModelMap modelMap, 
			String sql,
			HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {

		Assert.notNull(sql, "SQL不能为空");
		Assert.isTrue(checkSql(sql), "SQL不合法");
		List<Map<String,Object>> list = excelService.queryList(sql);
		try {
			excelService.exportExcel(list,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean checkSql(String sql) {
		// 判断黑名单
		String[] inj_stra = { "script", "mid", "master", "truncate", "insert", "delete", "update", "declare",
				"iframe","onreadystatechange", "alert", "atestu", "xss", "svg", "confirm", "prompt", "onload", "onmouseover", "onfocus", "onerror" };
		sql = sql.toLowerCase(); // sql不区分大小写
		for (int i = 0; i < inj_stra.length; i++) {
			if (sql.indexOf(inj_stra[i]) >= 0) {
				return false;
			}
		}
		return true;
	}
	
	@ApiOperation(value = "API发送工作通知")
	@PostMapping("/apiSendMsg")
	public Object apiSendMsg(ModelMap modelMap, @RequestBody Map<String, Object> param)  {
		Assert.notNull(param.get("msg"), "msg不能为空");
		Assert.notNull(param.get("dingId"), "dingId不能为空");
		//发送工作通知
		return DingUtil.sendMsg(StrUtils.toString(param.get("msg"))+"，"+DateUtils.DateToStr(new Date(), DateUtils.TIME_FORMAT_CHINESE_SECOND), StrUtils.toString(param.get("dingId")));
	}
	
}

