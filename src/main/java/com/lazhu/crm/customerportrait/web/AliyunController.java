package com.lazhu.crm.customerportrait.web;

import java.util.Map;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lazhu.core.base.AbstractController;
import com.lazhu.core.base.BaseController;
import com.lazhu.core.base.BaseModel;
import com.lazhu.ecp.utils.AliyunAccessTokenUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 获取阿里云accessToken
 * @author 庞红
 *
 */
@RestController
@Api(value = "阿里云token", description = "阿里云AccessToken")
@RequestMapping(value = "crm/customerportrait/aliyun")
public class AliyunController extends AbstractController{
	
	@ApiOperation(value = "获取accessToken")
	@PutMapping(value = "/token")
	public Object queryPolicy(ModelMap modelMap) {
		String token = AliyunAccessTokenUtil.getAccessToken();
		return super.setSuccessModelMap(modelMap, token);
	}
}
