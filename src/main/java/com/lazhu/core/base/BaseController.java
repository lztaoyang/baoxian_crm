/**
 * 
 */
package com.lazhu.core.base;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.baomidou.mybatisplus.plugins.Page;

/**
 * 控制器基类
 * 
 * @author naxj
 * 
 */
public abstract class BaseController<T extends BaseModel> extends AbstractController {
	
	@Autowired
	protected BaseService<T> service;

	public Object query(ModelMap modelMap, Map<String, Object> params) {
		Page<?> list = service.query(params);
		return setSuccessModelMap(modelMap, list);
	}

	public Object queryList(ModelMap modelMap, Map<String, Object> params) {
		List<?> list = service.queryList(params);
		return setSuccessModelMap(modelMap, list);
	}

	public Object get(ModelMap modelMap, T param) {
		T result = service.queryById(param.getId());
		return setSuccessModelMap(modelMap, result);
	}
	
	public Object getNoCode(ModelMap modelMap, T param) {
		T result = service.queryById(param.getId());
		return setSuccessModelMapNoCode(modelMap, result);
	}

	public Object update(ModelMap modelMap, T param) {
		param.setUpdateBy(super.getCurrUser());
		T result = service.update(param);
		return setSuccessModelMap(modelMap,result);
	}

	public Object delete(ModelMap modelMap, T param) {
		service.delete(param.getId());
		return setSuccessModelMap(modelMap);
	}
	public Object del(ModelMap modelMap, T param) {
		service.del(param.getId(),super.getCurrUser());
		return setSuccessModelMap(modelMap);
	}
	public Object del(ModelMap modelMap, List<Long> ids) {
		for(Long id:ids){
			service.del(id,super.getCurrUser());
		}
		return setSuccessModelMap(modelMap);
	}
	public Object delete(ModelMap modelMap, List<Long> ids) {
		for(Long id:ids){
			service.delete(id);
		}
		return setSuccessModelMap(modelMap);
	}
	
}
