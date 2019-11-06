package com.lazhu.crm.resource.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.base.BaseController;
import com.lazhu.core.support.Assert;
import com.lazhu.core.util.ChineseCalendar;
import com.lazhu.core.util.WebUtil;
import com.lazhu.crm.Constant;
import com.lazhu.crm.customerlog.entity.CustomerLog;
import com.lazhu.crm.mobile.service.MobileService;
import com.lazhu.crm.resource.entity.Resource;
import com.lazhu.crm.resource.entity.ResourceDetailsVo;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.crm.worktrailabnormal.entity.WorkTrailAbnormal;
import com.lazhu.crm.worktrailabnormal.service.WorkTrailAbnormalService;
import com.lazhu.crm.worktrailnormal.entity.WorkTrailNormal;
import com.lazhu.crm.worktrailnormal.service.WorkTrailNormalService;
import com.lazhu.ecp.utils.ConstantUtils;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.service.SysUserService;
import com.lazhu.t.resourceallot.service.ResourceAllotService;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@RestController
@Api(value = "", description = "")
@RequestMapping(value = "crm/resource")
public class ResourceController extends BaseController<Resource> {
	
	@Autowired
	SysUserService userService;
	// 正常工作轨迹
	@Autowired
	WorkTrailNormalService workTrailNormalService;
	// 异常工作轨迹
	@Autowired
	WorkTrailAbnormalService workTrailAbnormalService;
	//客户联系方式
	@Autowired
	protected MobileService mobileService;
	//分配表
	@Autowired
	protected ResourceAllotService resourceAllotService;
	
	@ApiOperation(value = "全局查询")
	@PutMapping(value = "/search/list")
	public Object search(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		@SuppressWarnings("rawtypes")
		Page page = ((ResourceService)service).search(param,super.getCurrUser());
		return setSuccessModelMap(modelMap,page);
	}
	
	@ApiOperation(value = "查询")
	@PutMapping(value = "/read/list")
	public Object queryListL(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		Map<String, Object> map = ((ResourceService)service).queryResourceAndList(param,super.getCurrUser());
		return setSuccessModelMap(modelMap,map);
	}

	@ApiOperation(value = "详情集合")
	@PutMapping(value = "/read/details")
	public Object getFull(ModelMap modelMap, @RequestBody Resource param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		Assert.notNull(param.getId(), "未获取当前行ID");
		ResourceDetailsVo vo = ((ResourceService)this.service).findFullEntity(param,super.getCurrUser());
		return super.setSuccessModelMap(modelMap, vo);
	}

	@PostMapping
	@ApiOperation(value = "新建或修改")
	public Object update(ModelMap modelMap, @RequestBody Resource param,HttpServletRequest request) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		//当前IP
		String ip = WebUtil.getHost(request);
		//当前操作人
		param.setUpdateBy(super.getCurrUser());
		param.setUpdateTime(new Date());
		if (StrUtils.isNotNullOrBlank(param.getIsAddChat()) && param.getIsAddChat() == 1) {
			param.setAddChatTime(new Date());
		}
		Resource  resource = null;
		if (null == param.getId()) {
			resource = ((ResourceService)service).addResource(param,ip);
		} else {
			resource = ((ResourceService)service).updateResource(param,super.getCurrUser(),ip);
		}
		return setSuccessModelMap(modelMap,resource);
	}
	
	@PostMapping("/allot")
	@ApiOperation(value = "资源调配")
	public Object allot(ModelMap modelMap, @RequestBody Map<String, Object> param,HttpServletRequest request) {
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
		List<Resource>  resource = ((ResourceService)service).allot(ids,userId,super.getCurrUser(),ip);
		return setSuccessModelMap(modelMap,resource);
	}
	
	@PostMapping("/allotTestResource")
	@ApiOperation(value = "资源调配")
	public Object allotTestResource(ModelMap modelMap, @RequestBody Map<String, Object> param,HttpServletRequest request) {
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
		List<Resource>  resource = ((ResourceService)service).allotTestResource(ids,userId,super.getCurrUser(),ip);
		return setSuccessModelMap(modelMap,resource);
	}
	
	@PostMapping("/moveResource")
	@ApiOperation(value = "移动开发流程")
	@Transactional
	public Object moveResource(ModelMap modelMap, @RequestBody Resource param,HttpServletRequest request) {
		Assert.notNull(param.getId(), "未选中资源");
		Assert.notNull(param.getFlowId(), "未指定流程");
		Resource resource = ((ResourceService)service).queryById(param.getId());
		if (null == resource) {
			Assert.notNull(null, "当前资源已丢弃，请从共享池捞取");
		}
		Integer retainDay0 = StrUtils.toInteger(ConstantUtils.getLoadConstant("resource.retain.day0"));
		Integer retainDay1 = StrUtils.toInteger(ConstantUtils.getLoadConstant("resource.retain.day1"));
		Integer retainDay2 = StrUtils.toInteger(ConstantUtils.getLoadConstant("resource.retain.day2"));
		Integer retainDay3 = StrUtils.toInteger(ConstantUtils.getLoadConstant("resource.retain.day3"));//意向30天
		Integer retainDay4 = StrUtils.toInteger(ConstantUtils.getLoadConstant("resource.retain.day4"));//可聊15天
		//添加修改到期时间
		if (param.getFlowId() == Constant.FLOW_CQ){//抽取资源（保护期1天）
			param.setRetainTime(ChineseCalendar.exactWeekdays(resource.getCreateTime(),retainDay1));
		} else if (param.getFlowId() == Constant.FLOW_KF) {//推广资源（保护期3天）
			param.setRetainTime(ChineseCalendar.exactWeekdays(resource.getCreateTime(),retainDay0));
		} else if (param.getFlowId() == Constant.FLOW_WJ) {
			if (resource.getFlowId() != Constant.FLOW_WJ && param.getFlowId() == Constant.FLOW_WJ) {
				param.setRetainTime(ChineseCalendar.exactWeekdays(resource.getCreateTime(),retainDay2));
			}
		} else if (param.getFlowId() == Constant.FLOW_YX) {
			param.setRetainTime(ChineseCalendar.exactWeekdays(resource.getCreateTime(),StrUtils.toInteger(retainDay3)));
			if (resource.getFlowId() != Constant.FLOW_YX && param.getFlowId() == Constant.FLOW_YX) {
				param.setIsTalk(1);
				param.setTalkTime(new Date());
			}
		} else if (param.getFlowId() == Constant.FLOW_KL) {//可聊资源有效期15天
			param.setRetainTime(ChineseCalendar.exactWeekdays(resource.getCreateTime(),retainDay4));
			//param.setRetainTime(DateUtils.ResourceRetainTime(resource.getCreateTime(),retainDay4,false));
			if (resource.getFlowId() != Constant.FLOW_KL && param.getFlowId() == Constant.FLOW_KL) {
				param.setIsTalk(1);
				param.setTalkTime(new Date());
			}
		} else if (param.getFlowId() == Constant.FLOW_GXC) {
			param.setFlowId(Constant.FLOW_GXC);							//共享池流程
			param.setIsLose(1);								//丢弃资源
			param.setLoseNum(resource.getLoseNum() + 1);	//丢弃次数+1
			param.setLossTime(new Date());					//丢失时间
		} else {
			OperationLogTool.operationLog(Constant.ERROR_LOG , "未知流程："+param.getFlowId());
		}
		//保存流程日志
		if (param.getFlowId().intValue() != resource.getFlowId().intValue()) {
			CustomerLog log = new CustomerLog();
			log.setCustomerId(resource.getId());
			log.setCustomerName(resource.getName());
			log.setOldFlow(resource.getFlowId());
			log.setNewFlow(param.getFlowId());
			String type = "资源流程移动";
			if (param.getFlowId()==Constant.FLOW_GXC) {
				type = "丢弃为共享池资源";
			} else if (param.getFlowId()==Constant.FLOW_BLACK) {
				type = "丢弃为黑名单资源";
			} else if (param.getFlowId()==Constant.FLOW_LJ) {
				type = "丢弃为垃圾池资源";
			}
			log.setType(type);
			log.setIp(WebUtil.getHost(request));
			log.setUpdateBy(resource.getUpdateBy());
			((ResourceService)service).saveCustomerLog(log);
		}
		
		// 更新 正常工作轨迹 或 加入异常工作轨迹 （workTrailNormalId 存在 则是正常轨迹移动 否则 异常）
		if (param.getWorkTrailNormalId() != null) {
			WorkTrailNormal bean = workTrailNormalService.queryById(param.getWorkTrailNormalId());
			bean.setAfterFlowId(param.getFlowId());// 移动后流程
			bean.setUpdateBy(getCurrUser());
			workTrailNormalService.update(bean);
		} else {
			Long userGroup = ((ResourceService)service).getUserGroup(getCurrUser());
			WorkTrailAbnormal bean = new WorkTrailAbnormal();
			bean.setBeforeFlowId(resource.getFlowId());// 上一次流程
			bean.setAfterFlowId(param.getFlowId());
			bean.setCustomerId(param.getId());// 客户ID
			bean.setDirectorId(resource.getDirectorId());
			bean.setManagerId(resource.getManagerId());
			bean.setMinisterId(resource.getMinisterId());
			bean.setSalerId(userGroup == Constant.USER_GROUP_YWY ? resource.getSalerId() : -1L);
			bean.setSalerRecordId(param.getId());
			bean.setEnterTime(resource.getEnterTime());
			bean.setUpdateBy(getCurrUser());
			workTrailAbnormalService.update(bean);
		}
		
		resource = ((ResourceService)service).update(param);
		//回写分配表资源开发流程
		resourceAllotService.updateFlowId(resource.getId(),resource.getFlowId());
		//如果是丢弃到共享池、垃圾池、黑名单，则删除资源
		if (resource.getFlowId() == Constant.FLOW_GXC 
				|| resource.getFlowId() == Constant.FLOW_LJ 
				|| resource.getFlowId() == Constant.FLOW_BLACK) {
			((ResourceService)service).delete(resource.getId());
			return setSuccessModelMap(modelMap);
		}
		//重新解密手机号
		resource.setMobile(mobileService.getMobile(resource.getMd5Mobile()));
		return setSuccessModelMap(modelMap,resource);
	}
	
	@PostMapping("/moveBlacklist")
	@ApiOperation(value = "移动到黑名单")
	@Transactional
	public Object moveBlacklist(ModelMap modelMap, @RequestBody Resource param,HttpServletRequest request) {
		Assert.notNull(param.getId(), "未选中资源");
		Assert.notNull(param.getFlowId(), "未指定流程");
		Assert.notNull(param.getBlackRemark(), "未填写黑名单备注");
		param.setUpdateBy(super.getCurrUser());
		@SuppressWarnings("unused")
		Integer oldFlowId = ((ResourceService)service).queryById(param.getId()).getFlowId();
		Resource resource = ((ResourceService)service).update(param);
		//更新资源会员数
		//businessNumService.updateDevelopDataNumByBusinessIds(oldFlowId,param.getFlowId(),1,businessNumService.ListAllBusinessUserId(resource),false);
		//回写分配表资源开发流程
		resourceAllotService.updateFlowId(resource.getId(),Constant.FLOW_BLACK);
		//删除资源
		((ResourceService)service).delete(param.getId());
		return setSuccessModelMap(modelMap,resource);
	}

	@ApiOperation(value = "查询当前经纪人有几条推广资源")
	@PutMapping(value = "/resourceInitNum")
	public Object resourceInitNum(ModelMap modelMap) {
		Integer resourceInitNum = ((ResourceService)service).resourceInitNum(super.getCurrUser());
		return setSuccessModelMap(modelMap,resourceInitNum);
	}
	
	@ApiOperation(value = "商务中心首次成交待合规客户双击详细")
	@PutMapping(value = "/read/resourceDetails")
	public Object resourceDetails(ModelMap modelMap, @RequestBody Resource param) {
		Assert.notNull(param.getId(), "未获取当前行ID");
		ResourceDetailsVo vo = ((ResourceService) this.service).hgEntity(param, super.getCurrUser());
		return super.setSuccessModelMap(modelMap, vo);
	}
	
	@PostMapping("/addTestResource")
	@ApiOperation(value = "新建测试资源")
	public Object addTestResource(ModelMap modelMap, @RequestBody Resource param,HttpServletRequest request) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		//当前IP
		String ip = WebUtil.getHost(request);
		//当前操作人
		param.setUpdateBy(super.getCurrUser());
		Resource  resource = null;
		if (null == param.getId()) {
			resource = ((ResourceService)service).addTestResource(param,ip);
		}
		return setSuccessModelMap(modelMap,resource);
	}
	
}