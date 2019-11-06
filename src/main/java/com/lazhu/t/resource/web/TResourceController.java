package com.lazhu.t.resource.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.base.BaseController;
import com.lazhu.t.resource.entity.TResource;
import com.lazhu.t.resource.service.TResourceService;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hz
 * @since 2017-10-27
 */
@RestController
@Api(value = "", description = "")
@RequestMapping(value = "t/resource")
public class TResourceController extends BaseController<TResource> {
	
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "查询")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page page = ((TResourceService)service).queryResource(param);
		return setSuccessModelMap(modelMap,page);
	}

	@ApiOperation(value = "详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody TResource param) {
		Assert.notNull(param.getId(), "ID");
		return super.get(modelMap, param);
	}

}
