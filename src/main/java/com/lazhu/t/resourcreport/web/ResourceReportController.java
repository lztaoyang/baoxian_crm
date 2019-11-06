package com.lazhu.t.resourcreport.web;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.t.resourcreport.entity.ResourceReportVO;
import com.lazhu.t.resourcreport.service.ResourceReportService;
import com.lazhu.core.support.HttpCode;
import com.lazhu.core.util.InstanceUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 推广资源报表控制器
 *
 * @author ty
 * @date 2018年11月21日
 * 
 */
@RestController("resourceReportController")
@Api(value = "推广资源报表", description = "推广资源报表")
@RequestMapping(value = "t/resourceReport")
public class ResourceReportController{

	@Autowired
	ResourceReportService resourceReportService;
	
	/**
	 * 通过维度查询相应的推广资源数据
	 * 
	 * @return
	 */
	@ApiOperation(value = "推广资源报表")
	@PutMapping(value = "/list")
	public Object queryResourceByDimension(ModelMap modelMap,@RequestBody Map<String, Object> param){
		param = paramHandle(param);
		
		List<ResourceReportVO> data = null;
		
		if(param.get("dimension").toString().equals("province")){//省份维度
			data = resourceReportService.queryProvinceDimension(param);
		}else if(param.get("dimension").toString().equals("city")){//城市维度
			data = resourceReportService.queryCityDimension(param);
		}else if(param.get("dimension").toString().equals("channel")){//渠道维度
			data = resourceReportService.queryChannelDimension(param);
		}else if(param.get("dimension").toString().equals("plan")){//广告维度
			data = resourceReportService.queryPlanDimension(param);
		}else if(param.get("dimension").toString().equals("timeslot")){//时段维度
			data = resourceReportService.queryTimeslotDimension(param);
		}else{//其他情况 默认省份维度
			data = resourceReportService.queryProvinceDimension(param);
		}
		
		return setModelMap(modelMap,HttpCode.OK,data);
	}
	
	/**
	 * 资源报表参数处理(service层处理的话，使用的地方太分散，不利于统一处理)
	 * 
	 * @param param 前台传过来的参数
	 * @return 处理之后的参数结果
	 */
	protected Map<String, Object> paramHandle(Map<String, Object> param){
		if(param != null ){
			//处理开始日期为yyyy-MM-dd
			if(param.get("startDate") != null && param.get("startDate").toString().length() > 10){
				param.put("startDate", param.get("startDate").toString().substring(0, 10));
			}
			//处理结束日期为yyyy-MM-dd
			if(param.get("endDate") != null && param.get("endDate").toString().length() > 10){
				param.put("endDate", param.get("endDate").toString().substring(0, 10));
			}
			//防止没有维度前端报异常
			if(param.get("dimension") == null || param.get("dimension").toString().length() < 1){
				param.put("dimension", "province");
			}
		}
		return param;
	}
	
	/**
	 * 设置响应代码
	 * 
	 * 该方法引用自com.lazhu.core.base.AbstractController.setModelMap
	 * 引用原因，由于这个类没有继承BaseController(当继承BaseController的时候需要有一一对应的controller、service、mapper、entity)
	 * 设计思路是，每个维度有自己的数据访问类，便于后期维护和修改 所以此处重写setModelMap方法
	 * 
	 *  */
	protected ResponseEntity<ModelMap> setModelMap(ModelMap modelMap, HttpCode code, Object data) {
		Map<String, Object> map = InstanceUtil.newLinkedHashMap();
		map.putAll(modelMap);
		modelMap.clear();
		for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			if (!key.startsWith("org.springframework.validation.BindingResult") && !key.equals("void")) {
				modelMap.put(key, map.get(key));
			}
		}
		if (data != null) {
			if (data instanceof Page) {
				Page<?> page = (Page<?>) data;
				modelMap.put("data", page.getRecords());
				modelMap.put("current", page.getCurrent());
				modelMap.put("size", page.getSize());
				modelMap.put("pages", page.getPages());
				modelMap.put("total", page.getTotal());
				modelMap.put("iTotalRecords", page.getTotal());
				modelMap.put("iTotalDisplayRecords", page.getTotal());
			} else if (data instanceof List<?>) {
				modelMap.put("data", data);
				modelMap.put("total", ((List<?>) data).size());
				modelMap.put("iTotalRecords", ((List<?>) data).size());
				modelMap.put("iTotalDisplayRecords", ((List<?>) data).size());
			} else {
				modelMap.put("data", data);
			}
		}
		modelMap.put("httpCode", code.value());
		modelMap.put("msg", code.msg());
		modelMap.put("timestamp", System.currentTimeMillis());
		return ResponseEntity.ok(modelMap);
	}
}
