/**
 * 
 */
package com.lazhu.sys.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.base.BaseController;
import com.lazhu.core.support.Assert;
import com.lazhu.core.support.HttpCode;
import com.lazhu.core.util.SecurityUtil;
import com.lazhu.core.util.UploadUtil;
import com.lazhu.core.util.WebUtil;
import com.lazhu.crm.Constant;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.model.SysUser;
import com.lazhu.sys.service.SysAuthorizeService;
import com.lazhu.sys.service.SysUserService;

/**
 * 用户管理控制器
 * 
 * @author naxj
 * 
 */
@RestController
@Api(value = "用户管理", description = "用户管理")
@RequestMapping(value = "sys/user")
public class SysUserController extends BaseController<SysUser> {
	@Autowired
	private SysAuthorizeService sysAuthorizeService;
	@Autowired
	private SysUserService userService;

	@PostMapping
	@ApiOperation(value = "修改用户信息")
	//@RequiresPermissions("sys.base.user.update")
	@Transactional
	public Object update(ModelMap modelMap, @RequestBody SysUser param) {
		Assert.isNotBlank(param.getAccount(), "ACCOUNT");
		param.setUserName(param.getAccount());
		Assert.length(param.getAccount(), 2, 15, "用户名长度为2-15位");
		//用户名不可重复
		Long num = userService.queryByAccount(param.getAccount(),param.getId());
		if (num != null && num > 0) {
			Assert.notNull(null,"该用户名已注册");
		}
		SysUser user = null;
		if (param.getId() != null) {
			user = userService.queryById(param.getId());
			Assert.notNull(user, "查询不到当前用户", param.getId());
			if (StringUtils.isNotBlank(param.getPassword())) {
				if (!param.getPassword().equals(user.getPassword())) {
					param.setPassword(SecurityUtil.encryptPassword(param.getPassword()));
				}
			}
		} else {
			//密码加密
			param.setPassword(SecurityUtil.encryptPassword(param.getPassword()));
		}
		Assert.isNotBlank(param.getParentId(), "上级ID不能为空");
		Assert.isNotBlank(param.getUserGroup(), "用户组不能为空");
		SysUser parent = userService.queryById(param.getParentId());
		if (parent == null || parent.getEnable() == false) {
			if (param.getParentId() > 10) {
				Assert.notNull(null, "当前上级:"+param.getParentId()+",不存在或已停用");
			}
		}
		
		//新建客服人员时，设置分配升级客户数为当前客服人员中最低的数量
		if (StrUtils.isNullOrBlank(param.getId())) {
			if (param.getUserGroup() == Constant.USER_GROUP_KF) {
				List<Long> serverIdList = userService.selectIdByAllotClubNumInDept(userService.queryById(param.getParentId()).getParentId(),Constant.USER_GROUP_KF);
				SysUser server = null;
				if (null != serverIdList && serverIdList.size() > 0) {
					server = userService.queryById(serverIdList.get(0));
				}
				if (null != server && StrUtils.isNotNullOrBlank(server.getAllotClubNum())) {
					param.setAllotClubNum(server.getAllotClubNum());
					OperationLogTool.operationLog("修改客服人员时，设置分配客户数为："+server.getAllotClubNum());
				} else {
					param.setAllotClubNum(0);
					OperationLogTool.operationLog(Constant.ERROR_LOG, "新增客服人员时，未获得当前最少分配客户数");
					Assert.notNull(null,"新增客服人员时，未获得当前最少分配客户数");
				}
			}
		//修改用户组为客服人员时
		} else {
			if (param.getUserGroup() == Constant.USER_GROUP_KF) {
				if (StrUtils.isNullOrBlank(param.getAllotClubNum()) || param.getAllotClubNum() <= 0) {
					List<Long> serverIdList = userService.selectIdByAllotClubNumInDept(userService.queryById(param.getParentId()).getParentId(),Constant.USER_GROUP_KF);
					SysUser server = null;
					if (null != serverIdList && serverIdList.size() > 0) {
						server = userService.queryById(serverIdList.get(0));
					}
					if (null != server && StrUtils.isNotNullOrBlank(server.getAllotClubNum())) {
						param.setAllotClubNum(server.getAllotClubNum());
						OperationLogTool.operationLog("修改客服人员时，设置分配客户数为："+server.getAllotClubNum());
					} else {
						param.setAllotClubNum(0);
						OperationLogTool.operationLog(Constant.ERROR_LOG, "修改客服人员时，未获得当前最少分配客户数");
						Assert.notNull(null,"修改用户为客服人员时，未获得当前最少分配客户数");
					}
				}
			}
		}
		
		return super.update(modelMap, param);
	}
	
	@PostMapping("/isExist")
	@ApiOperation(value = "注册时，校验用户名是否可用")
	public Object isExist(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.isNotBlank(param.get("account"), "用户名不能为空");
		Assert.length(param.get("account").toString(), 2, 15, "用户名长度为2-15位");
		Long num = userService.queryByAccount(param.get("account").toString(),StrUtils.toLong(param.get("id")));
		if (num != null && num > 0) {
			Assert.notNull(null,"该用户名已注册");
			modelMap.put("msg", "该用户名已注册");
			modelMap.put("isexist", 1);
		} else {
			modelMap.put("msg", "该用户名可用");
		}
		return setSuccessModelMap(modelMap);
	}

	// 查询用户
	@ApiOperation(value = "查询用户")
	//@RequiresPermissions("sys.base.user.read")
	@PutMapping(value = "/read/list")
	public Object queryL(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page page = ((SysUserService)service).readList(param);
		return setSuccessModelMap(modelMap,page);
	}

	// 用户详细信息
	@ApiOperation(value = "用户详细信息")
	//@RequiresPermissions("sys.base.user.read")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody SysUser param) {
		return super.get(modelMap, param);
	}

	// 用户详细信息
	@ApiOperation(value = "删除用户")
	//@RequiresPermissions("sys.base.user.delete")
	@DeleteMapping
	public Object delete(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(param.get("ids"), "IDS");
		List<Long> ids = null;
		if (param.get("ids") instanceof String){
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.get("ids").toString()));
		}else if(param.get("ids") instanceof JSONArray) {
			ids = ((JSONArray)param.get("ids")).toJavaList(Long.class);
		}
		return super.del(modelMap, ids);
	}

	// 当前用户
	@ApiOperation(value = "当前用户信息")
	@PutMapping(value = "/read/promission")
	public Object promission(ModelMap modelMap) {
		Long id = getCurrUser();
		/** 获取当前用户信息 **/
		SysUser sysUser = userService.queryById(id);
		modelMap.put("user", sysUser);
		/** 获取当前用户对应的菜单 **/
		List<?> menus = sysAuthorizeService.queryAuthorizeByUserId(id);
		modelMap.put("menus", menus);
		return setSuccessModelMap(modelMap);
	}

	// 当前用户
	@ApiOperation(value = "当前用户信息")
	@PutMapping(value = "/read/current")
	public Object current(ModelMap modelMap) {
		SysUser param = new SysUser();
		param.setId(getCurrUser());
		return super.get(modelMap, param);
	}
	
	// 当前用户及功能权限
	@ApiOperation(value = "当前用户及功能权限")
	@GetMapping(value = "/read/currentAndGroup")
	public Object currentAndGroup(ModelMap modelMap) {
		SysUser param = new SysUser();
		param.setId(getCurrUser());
		return super.get(modelMap, param);
	}

	@ApiOperation(value = "修改个人信息")
	@PostMapping(value = "/update/person")
	public Object updatePerson(ModelMap modelMap, @RequestBody SysUser param) {
		param.setId(WebUtil.getCurrentUser());
		param.setPassword(null);
		Assert.isNotBlank(param.getAccount(), "ACCOUNT");
		Assert.length(param.getAccount(), 3, 15, "ACCOUNT");
		return super.update(modelMap, param);
	}

	@ApiOperation(value = "修改用户头像")
	@PostMapping(value = "/update/avatar")
	public Object updateAvatar(HttpServletRequest request, ModelMap modelMap) {
		List<String> fileNames = UploadUtil.uploadImage(request);
		if (fileNames.size() > 0) {
			SysUser param = new SysUser();
			param.setId(WebUtil.getCurrentUser());
			String filePath = UploadUtil.getUploadDir(request) + fileNames.get(0);
			// String avatar = UploadUtil.remove2DFS("sysUser", "user" +
			// sysUser.getId(), filePath).getRemotePath();
			// String avatar = UploadUtil.remove2Sftp(filePath, "user" +
			// sysUser.getId());
			param.setAvatar(filePath);
			return super.update(modelMap, param);
		} else {
			setModelMap(modelMap, HttpCode.BAD_REQUEST);
			modelMap.put("msg", "请选择要上传的文件！");
			return modelMap;
		}
	}

	// 修改密码和推广手机号
	@ApiOperation(value = "修改密码")
	@PostMapping(value = "/update/password")
	public Object updatePassword(ModelMap modelMap, @RequestBody SysUser param) {
		Assert.notNull(param.getId(), "无法识别用户，请重新登录");
		/*if (StrUtils.isNotNullOrBlank(param.getUserGroup()) && param.getUserGroup() == Constant.user_group_ywy) {
			Assert.isNotBlank(param.getDingId(), "钉钉ID不能为空");
		}*/
		/*if (StrUtils.isNotNullOrBlank(param.getUserGroup()) && param.getUserGroup() == Constant.user_group_ywy) {
			Assert.isNotBlank(param.getPhone(), "推广手机号不能为空");
		}*/
		if (StrUtils.isNotNullOrBlank(param.getDingId())) {
			param.setDingId(param.getDingId().trim().replaceAll("\\s*|\t|\r|\n", ""));
			if (!(StrUtils.isNumeric(param.getDingId()))) {
				Assert.notNull(null, "钉钉号格式不正确");
			}
		} else {
			param.setDingId(null);
		}
		if (StrUtils.isNotNullOrBlank(param.getPassword())) {
			Long userId = WebUtil.getCurrentUser();
			if (!param.getId().equals(userId)) {
				SysUser user = userService.queryById(userId);
				if (user.getUserType() == 1) {
					throw new UnauthorizedException("您没有权限修改用户密码.");
				}
			}
			//加密新密码
			String password = SecurityUtil.encryptPassword(param.getPassword());
			param.setPassword(password);
		}
		param.setUpdateBy(WebUtil.getCurrentUser());
		return super.update(modelMap, param);
	}
	
	// 查询一个部门下的用户
	@ApiOperation(value = "查询用户")
	@PutMapping(value = "/read/user")
	public Object queryByDeptId(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page<SysUser>page = userService.queryUser(modelMap, param);
		return super.setSuccessModelMap(modelMap, page);
	}

	// 查询分配资源用户
	@ApiOperation(value = "查询分配资源用户")
	@PutMapping(value = "/read/allotResource/list")
	public Object queryAllotResource(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Page<SysUser>page = userService.queryAllotResource(modelMap, param,super.getCurrUser());
		return super.setSuccessModelMap(modelMap, page);
	}
	
	// 修改分配资源用户信息
	@ApiOperation(value = "修改用户信息")
	@PostMapping(value = "/updateInfo")
	public Object queryAllotResource(ModelMap modelMap, @RequestBody SysUser param) {
		SysUser user = userService.update(param);
		if (StrUtils.isNotNullOrBlank(user.getParentId())) {
			if (user.getUserGroup() == Constant.USER_GROUP_YWY) {
				SysUser manager = userService.queryById(user.getParentId());
				if (null != manager) {
					user.setManagerName(manager.getAccount());
					SysUser director = userService.queryById(manager.getParentId());
					if (null != director) {
						user.setDirectorName(director.getAccount());
					}
				}
			} else {
				user.setManagerName(user.getAccount());
				user.setAccount(null);
				SysUser director = userService.queryById(user.getParentId());
				if (null != director) {
					user.setDirectorName(director.getAccount());
				}
			}
		}
		return super.setSuccessModelMap(modelMap, user);
	}

	// 停止我部门推广分配
	@ApiOperation(value = "停止我部门推广分配")
	@PostMapping(value = "/allotResource/stopAll")
	public Object allotResourceStop(ModelMap modelMap) {
		List<SysUser> page = userService.allotResourceStop(super.getCurrUser());
		return super.setSuccessModelMap(modelMap, page);
	}	
	
	// 停止我的推广分配
	@ApiOperation(value = "停止我的推广分配")
	@PostMapping(value = "/allotResource/stopMyself")
	public Object allotResourceStopMyself(ModelMap modelMap) {
		SysUser user = userService.allotResourceStopMyself(super.getCurrUser());
		return super.setSuccessModelMap(modelMap, user);
	}	
	
	// 修改我部门最大分配数
	@ApiOperation(value = "修改我部门最大分配数")
	@PostMapping(value = "/changeAllotResourceMax")
	public Object changeAllotResourceMax(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(param.get("allotResourceMaxValue"), "分配数不可为空");
		int allotResourceMax = StrUtils.toInt(param.get("allotResourceMaxValue"));
		userService.changeAllotResourceMax(allotResourceMax, super.getCurrUser(),"手动修改最大分配数为"+allotResourceMax);
		return super.setSuccessModelMap(modelMap);
	}
	
	// 获取用户级联数据
	@ApiOperation(value = "获取用户级联数据")
	@PostMapping(value = "/queryCascader")
	public Object queryCascader(ModelMap modelMap) {
		return super.setSuccessModelMap(modelMap, userService.queryCascader());
	}
	
	// 获取登陆用户级联数据
	@ApiOperation(value = "获取登陆用户级联数据")
	@PostMapping(value = "/queryAuthCascader")
	public Object queryAuthCascader(ModelMap modelMap) {
		return super.setSuccessModelMap(modelMap, userService.queryAuthCascader(super.getCurrUser()));
	}
	
	@ApiOperation(value = "修改今日奖励数")
	@PostMapping(value = "/updateReward")
	public Object updateReward(ModelMap modelMap, @RequestBody SysUser param) {
		Assert.notNull(param.getRewardResourceNum(), "今日奖励资源数不可为空");
		SysUser user = userService.queryById(param.getId());
		if (user.getOverRewardResourceNum() == 0) {
			user.setRewardResourceNum(param.getRewardResourceNum());
			user.setOverRewardResourceNum(param.getRewardResourceNum());
			userService.update(user);
		} else {
			Assert.notNull(null,"剩余奖励资源数为0时才可修改");
		}
		return super.update(modelMap, user);
	}

	// 查询当前修改最大分配数的说明
	@ApiOperation(value = "查询当前修改最大分配数的说明")
	@PutMapping(value = "/queryCarm")
	public Object queryCarm(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		String carm = userService.queryCarm();
		System.out.println(carm);
		return super.setSuccessModelMap(modelMap, carm);
	}
	
	// 查询一个部门下的用户
		@ApiOperation(value = "查询用户")
		@PutMapping(value = "/read/JLUser")
		public Object queryByDeptIdUser(ModelMap modelMap, @RequestBody Map<String, Object> param) {
			Assert.notNull(super.getCurrUser(),"未获取当前用户ID");
			param.put("parentId", super.getCurrUser());
			Page<SysUser>page = userService.queryUser(modelMap, param);
			return super.setSuccessModelMap(modelMap, page);
		}
}
