package com.lazhu.crm.signapply.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.dingtalk.chatbot.message.TextMessage;
import com.lazhu.core.base.BaseController;
import com.lazhu.core.util.WebUtil;
import com.lazhu.core.util.WxCollectUtil;
import com.lazhu.crm.signapply.entity.SignApply;
import com.lazhu.crm.signapply.service.SignApplyService;
import com.lazhu.ecp.utils.DingUtil;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.t.resourceallot.service.ResourceAllotService;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@RestController
@Api(value = "", description = "")
@RequestMapping(value = "crm/signApply")
public class SignApplyController extends BaseController<SignApply> {
	@Autowired
	ResourceAllotService resourceAllotService;

	@ApiOperation(value = "查询")
	@PutMapping(value = "/read/list")
	public Object queryL(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		Page<SignApply> page = service.query(param);
		List<SignApply> list = page.getRecords();
		for (SignApply apply : list) {
			apply.setNameAndAmount(
					apply.getInsuranceName() + "(" + apply.getAmount() + "元)");
		}
		page.setRecords(list);
		return super.setSuccessModelMap(modelMap, page);
	}

	@ApiOperation(value = "详情")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody SignApply param) {
		Assert.notNull(param.getId(), "未获取选中行ID");
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改")
	public Object update(ModelMap modelMap, @RequestBody SignApply param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		// 当前操作人
		param.setUpdateBy(super.getCurrUser());
		SignApply signApply = null;
		if (param.getId() == null) {
			// signApply = ((SignApplyService)service).add(param);
		} else {
			signApply = ((SignApplyService) service).mazyUpdate(param);
		}
		return setSuccessModelMap(modelMap, signApply);
	}

	@PostMapping("/businessUpdate")
	@ApiOperation(value = "市场部修改")
	public Object updateByBusiness(ModelMap modelMap, @RequestBody SignApply param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		Assert.notNull(param.getId(), "无法获取订单ID");
		// 当前操作人
		param.setUpdateBy(super.getCurrUser());
		SignApply signApply = ((SignApplyService) service).updateByBusiness(param, super.getCurrUser());
		return setSuccessModelMap(modelMap, signApply);
	}

	@PostMapping("/audit")
	@ApiOperation(value = "审核")
	public Object audit(ModelMap modelMap, @RequestBody Map<String, Object> param, HttpServletRequest request) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		Assert.notNull(param.get("ids"), "未选中资源");
		Assert.notNull(param.get("userId"), "未指定被分配人");
		List<Long> ids = null;
		if (param.get("ids") instanceof String) {
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.get("ids").toString()));
		} else if (param.get("ids") instanceof JSONArray) {
			ids = ((JSONArray) param.get("ids")).toJavaList(Long.class);
		}
		Long userId = StrUtils.toLong(param.get("userId"));
		// 当前IP
		String ip = WebUtil.getHost(request);
		List<SignApply> signApply = ((SignApplyService) service).audit(ids, userId, super.getCurrUser(), ip);
		return setSuccessModelMap(modelMap, signApply);
	}

	@PostMapping("/reject")
	@ApiOperation(value = "驳回")
	public Object reject(ModelMap modelMap, @RequestBody SignApply param, HttpServletRequest request) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		Assert.notNull(param.getId(), "未选中");
		// 当前IP
		String ip = WebUtil.getHost(request);
		SignApply signApply = ((SignApplyService) service).reject(param, super.getCurrUser(), ip);
		return setSuccessModelMap(modelMap, signApply);
	}

	@PostMapping("/hg")
	@ApiOperation(value = "合规通过")
	public Object hg(ModelMap modelMap, @RequestBody Map<String, Object> param, HttpServletRequest request) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		Assert.notNull(param.get("ids"), "未选中资源");
		List<Long> ids = null;
		if (param.get("ids") instanceof String) {
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.get("ids").toString()));
		} else if (param.get("ids") instanceof JSONArray) {
			ids = ((JSONArray) param.get("ids")).toJavaList(Long.class);
		}
		Integer isHg = StrUtils.toInteger(param.get("isHg"));
		// 当前IP
		String ip = WebUtil.getHost(request);
		List<SignApply> signApply = ((SignApplyService) service).hg(ids, isHg, super.getCurrUser(), ip);
		return setSuccessModelMap(modelMap, signApply);
	}

	@PostMapping("/hgbh")
	@ApiOperation(value = "合规不通过")
	public Object hg(ModelMap modelMap, @RequestBody SignApply param, HttpServletRequest request) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		Assert.notNull(param.getId(), "未选中");
		// 当前IP
		String ip = WebUtil.getHost(request);
		SignApply signApply = ((SignApplyService) service).hgReject(param, super.getCurrUser(), ip);
		return setSuccessModelMap(modelMap, signApply);
	}

	@ApiOperation(value = "商务部订单管理/订单查询")
	@PutMapping(value = "/contracter/list")
	public Object contracterList(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		Page page = ((SignApplyService) service).contracterList(param, super.getCurrUser());
		return setSuccessModelMap(modelMap, page);
	}

	@ApiOperation(value = "市场部订单管理/订单查询")
	@PutMapping(value = "/market/list")
	public Object marketList(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		Page page = ((SignApplyService) service).marketList(param, super.getCurrUser());
		return setSuccessModelMap(modelMap, page);
	}

	@ApiOperation(value = "客户经理部订单管理/订单查询")
	@PutMapping(value = "/upgrade/list")
	public Object upgradeList(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		Page page = ((SignApplyService) service).upgradeList(param, super.getCurrUser());
		return setSuccessModelMap(modelMap, page);
	}

	////////////////////////////////////// 【不分页】
	////////////////////////////////////// ////////////////////////////////////////

	@ApiOperation(value = "商务部待审核")
	@PutMapping(value = "/checkPending/audit/contracter/list/nonsort")
	public Object contracterAuditData(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		List<SignApply> signApplyList = ((SignApplyService) service).contracterAuditData(param, super.getCurrUser());
		return setSuccessModelMap(modelMap, signApplyList);
	}

	@ApiOperation(value = "商务部待合规")
	@PutMapping(value = "/checkPending/hg/contracter/list/nonsort")
	public Object contracterHgData(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		//List<SignApply> signApplyList = ((SignApplyService) service).contracterHgData(param, super.getCurrUser());
		Page page = ((SignApplyService) service).contracterHgData(param, super.getCurrUser());
		return setSuccessModelMap(modelMap, page);
	}

	@ApiOperation(value = "市场部待审核/待合规")
	@PutMapping(value = "/checkPending/market/list/nonsort")
	public Object checkPendingMarketListNonsort(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		List<SignApply> signApplyList = ((SignApplyService) service).checkPendingMarketListNonsort(param,
				super.getCurrUser());
		return setSuccessModelMap(modelMap, signApplyList);
	}

	@ApiOperation(value = "客户经理待审核/待合规")
	@PutMapping(value = "/checkPending/upgrade/list/nonsort")
	public Object checkPendingUpgradeListNonsort(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		List<SignApply> signApplyList = ((SignApplyService) service).checkPendingUpgradeListNonsort(param,
				super.getCurrUser());
		return setSuccessModelMap(modelMap, signApplyList);
	}


	@PostMapping("/modelSignApply")
	@ApiOperation(value = "模板申请签单")
	public Object modelSignApply(ModelMap modelMap, @RequestBody SignApply param, HttpServletRequest request) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		// 申请签单必须指定会员ID
		Assert.notNull(param.getCustomerId(), "未找到会员，请重新申请");
		// 当前操作人
		param.setUpdateBy(super.getCurrUser());
		// 当前IP
		String ip = WebUtil.getHost(request);
		SignApply signApply = null;
		if (param.getType() == 1) {
			signApply = ((SignApplyService) service).modelSignApplyResource(param, super.getCurrUser(), ip);
		} else if (param.getType() == 2) {
			signApply = ((SignApplyService) service).modelSignApplyClub(param, super.getCurrUser(), ip);
		} else if (param.getType() == 3 || param.getType() == 4) {
			signApply = ((SignApplyService) service).modelSignApplyUpgrade(param, super.getCurrUser(), ip);
		} else {
			Assert.notNull(null, "签单类型错误！");
		}
		if (null != signApply) {
			//到单通知
			((SignApplyService) service).msgToGroup(signApply);
		}
		return setSuccessModelMap(modelMap);
	}

	@PutMapping("/check/submitCode")
	@ApiOperation(value = "校验签单消息提交码")
	public Object checkSubmitCode(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(param.get("submitCode"), "签单消息提交码不能为空");
		boolean verify = WxCollectUtil.verify(StrUtils.toString(param.get("submitCode")));
		return setSuccessModelMap(modelMap, verify);
	}

	@PutMapping(value = "/recommend/list")
	@ApiOperation(value = "市场部/成交客户聊天观摩")
	public Object recommendList(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		Page page = ((SignApplyService) service).recommendList(param, super.getCurrUser());
		return setSuccessModelMap(modelMap, page);
	}

	@ApiOperation(value = "商务部订单管理/获取修改删除订单口令")
	@PutMapping(value = "/contracter/word")
	public Object contracterWord(ModelMap modelMap) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		//群：https://oapi.dingtalk.com/robot/send?access_token=dc29029a9d24257da1fdb8357c556a7d153aacc587287c88681ad0166ab9d233
		TextMessage message = new TextMessage("订单修改口令（1小时内有效期）：" + StrUtils.hourWordPass());
		DingUtil.sendGroupMesg("dd.group.hg.url", message);
		//DingUtil.sendMsg("订单修改口令（1小时内有效期）：" + StrUtils.hourWordPass(),"132465153926309620,132525055528590399,080768155633073413");
		return setSuccessModelMap(modelMap);
	}

	@ApiOperation(value = "商务部订单管理/删除订单")
	@PostMapping(value = "/contracter/del")
	public Object contracterDelete(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		Assert.notNull(param.get("ids"), "请选择订单");
		List<Long> ids = null;
		if (param.get("ids") instanceof String) {
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.get("ids").toString()));
		} else if (param.get("ids") instanceof JSONArray) {
			ids = ((JSONArray) param.get("ids")).toJavaList(Long.class);
		}
		if (ids.size() == 1) {
			((SignApplyService) service).contracterDelete(ids.get(0), super.getCurrUser());
		} else {
			Assert.notNull(null, "一次只能操作一条订单");
		}
		return setSuccessModelMap(modelMap);
	}

	@ApiOperation(value = "合规排名统计")
	@PutMapping("/business/list")
	public Object businessRanking(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.setSuccessModelMap(modelMap, ((SignApplyService)service).businessRanking(param));
	}


	@PostMapping("/changeServer")
	@ApiOperation(value = "修改")
	public Object changeServer(ModelMap modelMap, @RequestBody SignApply param) {
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		Assert.notNull(param.getId(), "未获取订单ID");
		Assert.notNull(param.getCustomerId(), "未获取客户ID");
		Assert.notNull(super.getCurrUser(), "登录超时，请重新登录");
		// 当前操作人
		param.setUpdateBy(super.getCurrUser());
		SignApply signApply = null;
		if (param.getId() != null && param.getCustomerId() != null) {
			signApply = ((SignApplyService) service).changeServer(param , super.getCurrUser());
		}
		
		return setSuccessModelMap(modelMap, signApply);
	}
	
	@ApiOperation(value = "合规新单统计")
	@PutMapping("/open/toDayList")
	public Object openToDayCount(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.setSuccessModelMap(modelMap, ((SignApplyService)service).openToDayCount(param));
	}
}
