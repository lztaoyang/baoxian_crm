package com.lazhu.crm.customeractualcot.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.base.BaseController;
import com.lazhu.crm.Constant;
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.customeractualcot.entity.CustomerActualCot;
import com.lazhu.crm.customeractualcot.service.CustomerActualCotService;
import com.lazhu.crm.teacherdirectivecot.service.TeacherDirectiveCotService;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.service.SysUserService;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author taoyang
 * @since 2018-11-16
 */
@RestController
@Api(value = "", description = "")
@RequestMapping(value = "crm/customerActualCot")
public class CustomerActualCotController extends BaseController<CustomerActualCot> {
	
	@Autowired
    CustomerService customerService;
	
	@Autowired
    CustomerActualCotService customerCotService;
    
    @Autowired
    TeacherDirectiveCotService teacherCotService;

    @Autowired //用户组
	SysUserService userService;
    
    @SuppressWarnings("rawtypes")
	@ApiOperation(value = "根据股票维度查询")
	@PutMapping(value = "/search/list")
	public Object queryGroupStock(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page page = customerCotService.queryGroupStock(param, super.getCurrUser());
		return setSuccessModelMap(modelMap, page);
	}

	@ApiOperation(value = "查询")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Long userGroup = userService.queryById(super.getCurrUser()).getUserGroup();
		//防止不设定用户组，越权
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		if (userGroup == Constant.USER_GROUP_KF) {//客服
			param.put("servicerId", super.getCurrUser());
		}
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody CustomerActualCot param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}
	
	/**
	 * 建仓/买入/卖出
	 */
	@PostMapping
	@ApiOperation(value = "修改")
	public Object update(ModelMap modelMap, @RequestBody CustomerActualCot param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		// 当前操作人
		CustomerActualCot actualCot = new CustomerActualCot();
		param.setUpdateBy(super.getCurrUser());
		if (param.getDirectionType() == 0) {//建仓
			actualCot = ((CustomerActualCotService) service).addStock(param);
		} else if (param.getDirectionType() == 1) {//买入
			actualCot = ((CustomerActualCotService) service).buyStock(param);
		} else if (param.getDirectionType() == 2) {//卖出
			actualCot = ((CustomerActualCotService) service).sellStock(param);
		}
		//3、更新持仓表
		Customer cus = customerService.queryById(actualCot.getCustomerId());
		if (StrUtils.isNotNullOrBlank(cus)) {
			((CustomerActualCotService) service).updatePosition(cus);
		}
		return setSuccessModelMap(modelMap, actualCot);
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
		//删除股票信息时，重新计算可用资金
		((CustomerActualCotService) service).delStock(ids,super.getCurrUser());
		return super.del(modelMap, ids);
	}
	
	/**
	 * 查询当前价格
	 * @param stockCode
	 * @return
	 */
	
	@ApiOperation(value = "查询股票价格")
	@PutMapping(value = "/readPrice")
	public Object queryCurrentPrice (ModelMap modelMap, @RequestBody CustomerActualCot param) {
		String currentPrice = ((CustomerActualCotService) service).qureyPriceByStock(param.getStockCode()).get(0);
		String stockName = ((CustomerActualCotService) service).qureyPriceByStock(param.getStockCode()).get(1);
		param.setCurrentPrice(Double.valueOf(currentPrice.toString()));
		param.setStockName(stockName);
		return setSuccessModelMap(modelMap, param);
	}
	
	/**
	 * 批量建仓
	 */
	@PostMapping(value = "/addMoreStock")
	@ApiOperation(value = "批量建仓")
	public Object addMoreStock(ModelMap modelMap, @RequestBody CustomerActualCot param , HttpServletRequest request) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		// 当前操作人
		param.setUpdateBy(super.getCurrUser());
		List<Long> ids = null;
		if (param.getIds() instanceof String){
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.getIds().toString()));
		}else if(param.getIds() instanceof JSONArray) {
			ids = ((JSONArray)param.getIds()).toJavaList(Long.class);
		}
		List<String> names = null;
		if (param.getIds() instanceof String){
			if (StrUtils.isNotNullOrBlank(param.getNames())) {
				names = new ArrayList<String>();
				names.add(String.valueOf(param.getNames().toString()));
			}
			
		}else if(param.getIds() instanceof JSONArray) {
			if (StrUtils.isNotNullOrBlank(param.getNames())) {
				names = ((JSONArray)param.getNames()).toJavaList(String.class);
			}
		}
		List<String> useMoneys = null;
		if (param.getIds() instanceof String){
			if (StrUtils.isNotNullOrBlank(param.getUseMoneys())) {
				useMoneys = new ArrayList<String>();
				useMoneys.add(String.valueOf(param.getUseMoneys().toString()));
			}
			
		}else if(param.getIds() instanceof JSONArray) {
			if (StrUtils.isNotNullOrBlank(param.getUseMoneys())) {
				useMoneys = ((JSONArray)param.getUseMoneys()).toJavaList(String.class);
			}
		}
		Map<String , Object> map = customerCotService.addMoreStock(param,ids,names,useMoneys,super.getCurrUser());
		return setSuccessModelMap(modelMap, map);
	}
	
}
