package com.lazhu.sys.service;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.base.BaseService;
import com.lazhu.core.support.Assert;
import com.lazhu.core.util.CacheUtil;
import com.lazhu.core.util.InstanceUtil;
import com.lazhu.crm.Constant;
import com.lazhu.ecp.utils.DateUtils;
import com.lazhu.ecp.utils.DingUtil;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.mapper.SysUserMapper;
import com.lazhu.sys.mapper.SysUserMenuMapper;
import com.lazhu.sys.model.SysRole;
import com.lazhu.sys.model.SysUser;

/**
 * SysUser服务实现类
 * 
 * @author naxj
 * 
 */
@Service
@CacheConfig(cacheNames = "SysUser")
public class SysUserService extends BaseService<SysUser>{
	@Autowired
	private SysDicService sysDicService;
	@Autowired
	private SysDeptService sysDeptService;
	@Autowired
	private SysUserMenuMapper sysUserMenuMapper;
	@Autowired
	private SysUserRoleService userRoleService;
	@Autowired
	private SysRoleService roleService;

	@Autowired
	private SysUserMapper mapper;
	
	//当前修改的最大分配数（默认值/初始值）
	public Integer currentAllotResourceMax = 3;
		
	//当前修改的最大分配数的说明
	public String CARM = "";
	
	@Transactional
	public SysUser queryByIdUnRedis(Long id) {
		try {
			return mapper.selectById(id);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	

	/**
	 * 查询用户（支持多用户名搜索）
	 * @param param
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Page readList(Map<String, Object> param) {
		List<String> userNameList = null;
		String userName = StrUtils.toString(param.get("userName"));
		userName = userName.trim().replaceAll("\\s*|\t|\r|\n", "");
		userName = userName.replace("/(^\\s*)|(\\s*$)/g", "").replace("/\\?/g","");
		if(StrUtils.isNotNullOrBlank(userName)) {
			userName = userName.replace("，", ",");
			userName = userName.replace("。", ",");
			userName = userName.replace("、", ",");
			userName = userName.replace(".", ",");
			//多用户名搜索
			if (userName.indexOf(",") > 0) {
				String[] userNames = userName.split(",");
				userNameList =  java.util.Arrays.asList(userNames);
				param.put("userNameList", userNameList);
				//删除用户名参数
				param.remove("userName");
			}
		}
		Page<Long> page = getPage(param);
		page.setRecords(mapper.selectIdPage(page,param));
		return getPage(page);
	}

	public Page<SysUser> query(Map<String, Object> params) {
		Map<String, String> userTypeMap = sysDicService.queryDicByType("USERTYPE");
		Page<SysUser> pageInfo = super.query(params);
		for (SysUser userBean : pageInfo.getRecords()) {
			if (userBean.getUserType() != null) {
				userBean.setUserTypeText(userTypeMap.get(userBean.getUserType().toString()));
			}
			if (userBean.getDeptId() != null) {
				userBean.setDeptName(sysDeptService.queryById(userBean.getDeptId()).getDeptName());
			}
			List<String> permissions = sysUserMenuMapper.queryPermission(userBean.getId());
			for (String permission : permissions) {
				if (StringUtils.isBlank(userBean.getPermission())) {
					userBean.setPermission(permission);
				} else {
					userBean.setPermission(userBean.getPermission() + ";" + permission);
				}
			}
		}
		return pageInfo;
	}

	public void init() {
		List<Long> list = ((SysUserMapper) mapper).selectIdPage(Collections.<String, Object>emptyMap());
		for (Long id : list) {
			CacheUtil.getCache().set(getCacheKey(id), mapper.selectById(id));
		}
	}

	/**
	 * 通过用户名查询
	 * @param account
	 * @return
	 */
	public Long queryByAccount(String account,Long id) {
		return ((SysUserMapper) mapper).queryByAccount(account,id);
	}

	/**
	 * 查询当前用户的所有下级
	 * @param userId
	 * @return
	 */
	public List<Long> querySUBById(Long userId) {
		return ((SysUserMapper) mapper).querySUBById(userId);
	}
	
	/** 查询升级客户数最少的升级人员（） **/
	public List<Long> selectIdByClubNum () {
		return ((SysUserMapper) mapper).selectIdByClubNum();
	}
	
	/** 查询分配升级客户数最少的升级人员（不可删除，目前自动分配客服使用） **/
	public List<Long> selectIdByAllotClubNum (Integer userGroup) {
		return ((SysUserMapper) mapper).selectIdByAllotClubNum(userGroup);
	}
	
	/** 
	 * @author ty
	 * 查询分配升级客户数最少的升级人员 
	 * @return List
	 * **/
	public List<Long> queryByGroupAndDept (Integer userGroup,List<Long> deptId) {
		return ((SysUserMapper) mapper).queryByGroupAndDept(userGroup,deptId);
	}
	
	/** 查询一个部门下的所有员工*/
	public Page<SysUser>queryUser(ModelMap modelMap,Map<String, Object> param){
		Page<SysUser> page = super.query(param);
		return page;
	}
	
	/** 查询某个（升级）总监部门下分配升级客户数最少的（某个用户组）人员 **/
	public List<Long> selectIdByAllotClubNumInDept (Long userId,Integer userGroup) {
		return ((SysUserMapper) mapper).selectIdByAllotClubNumInDept(userId,userGroup);
	}
	
	/** 查询某个（升级）部门下分配升级客户数最少的经理 **/
	public List<Long> selectUpgradeManagerIdByAllotClubNum () {
		return ((SysUserMapper) mapper).selectUpgradeManagerIdByAllotClubNum();
	}
	
	public List<Long> queryAllDirector() {
		return ((SysUserMapper) mapper).queryAllDirector();
	}

	public List<Long> queryAllManager() {
		return ((SysUserMapper) mapper).queryAllManager();
	}
	
	public List<Long> queryAllMan() {
		return ((SysUserMapper) mapper).queryAllMan();
	}

	public List<Long> queryAllUpgradeDirector() {
		return ((SysUserMapper) mapper).queryAllUpgradeDirector();
	}

	public List<Long> queryAllUpgradeManager() {
		return ((SysUserMapper) mapper).queryAllUpgradeManager();
	}

	public List<Long> queryAllUpgradeMan() {
		return ((SysUserMapper) mapper).queryAllUpgradeMan();
	}
	
	/** 查询分配升级阶梯数最少的升级人员  **/
	public List<Long> selectIdByAllotStairNum () {
		return ((SysUserMapper) mapper).selectIdByAllotStairNum();
	}

	/**
	 * 查询分配资源用户
	 * @param modelMap
	 * @param param
	 * @param currUser 
	 * @return
	 */
	public Page<SysUser> queryAllotResource(ModelMap modelMap,Map<String, Object> param, Long currUser) {
		Long userGroup = super.queryById(currUser).getUserGroup();
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		//经理看到自己的下属
		if (userGroup == Constant.USER_GROUP_JL) {
			param.put("parentId", currUser);
		}
		//升级经理
		if (userGroup == Constant.USER_GROUP_JBJL) {
			param.put("parentId", currUser);
		}
		//总监
		if (userGroup == Constant.USER_GROUP_ZJ) {
			//查询总监下所有经理
			List<Long> uIds = this.queryDirectorMang(currUser);
			param.put("parentIds", uIds);
		}
		//升级总监
		if (userGroup == Constant.USER_GROUP_JBZJ) {
			List<Long> uIds = this.queryDirectorMang(currUser);
			param.put("parentIds", uIds);
		}
		Page<SysUser> userPage = new Page<SysUser>();
		List<SysUser> list = InstanceUtil.newArrayList();
		userPage = super.query(param);
		list = userPage.getRecords();
		for(SysUser item : list){
			if (StrUtils.isNotNullOrBlank(item.getParentId())) {
				if (item.getUserGroup() == Constant.USER_GROUP_YWY || item.getUserGroup() == Constant.USER_GROUP_JB) {
					SysUser manager = this.queryById(item.getParentId());
					if (null != manager) {
						item.setManagerName(manager.getAccount());
						SysUser director = this.queryById(manager.getParentId());
						if (null != director) {
							item.setDirectorName(director.getAccount());
						}
					}
				} else {
					item.setManagerName(item.getAccount());
					item.setAccount(null);
					SysUser director = this.queryById(item.getParentId());
					if (null != director) {
						item.setDirectorName(director.getAccount());
					}
				}
			}
		}
		//根据上级ID排序
		if (null != list && list.size() >0) {
			//按创建时间降序排序
			Collections.sort(list,new Comparator<SysUser>() {
				@Override
				public int compare(SysUser o1,SysUser o2) {
					if (null != o1 && null != o2 && null != o1.getParentId() && null != o2.getParentId()) {
						if (o1.getParentId().longValue() < o2.getParentId().longValue()) {
							return 1;
						}else if (o1.getParentId().longValue() == o2.getParentId().longValue()) {
							return 0;
						} else {
							return -1;
						}
					}
					return -1;
				}
			});
		}
		userPage.setRecords(list);
		return userPage;
	}

	private List<Long> queryDirectorMang(Long currUser) {
		return mapper.queryDirectorMang(currUser);
	}


	/**
	 * 查询分配次数最少的用户（经纪人）
	 * @return
	 */
	public List<Long> querySalerAllotResourceMin() {
		return mapper.querySalerAllotResourceMin();
	}
	
	/**
	 * 定时每天重置分配资源数0，最大分配数，每天抽取次数5
	 * @param num
	 */
	public void resetUserAllotNum(int num) {
		mapper.resetUserAllotNum(num);
		
		currentAllotResourceMax = 0;
		CARM = "重置最大分配数为"+num;
		//清理缓存
		CacheUtil.getCache().delAll("*:SysUser:*");
		OperationLogTool.operationLog(Constant.AUTO_LOG, "重置保险经纪人和经理分配资源为为：0，最大分配数为："+num+"，抽取次数为：5");
	}
	public void stopUserAllot() {
		//所有用户
		mapper.stopUserAllot();
		// 清理缓存
		CacheUtil.getCache().delAll("*:SysUser:*");
	}
	
	/** 执行每天8点-23点用户奖励资源数--、控制分配数--的消费 **/
	public void recycleUserRewardCommit(int num) {
		mapper.recycleUserRewardCommit(num);
		// 清理缓存
		CacheUtil.getCache().delAll("*:SysUser:*");
		OperationLogTool.operationLog(Constant.AUTO_LOG, "每天8点-23点用户奖励资源数--、控制分配数--的消费");
	}

	/** 每周六早上重置今日奖励资源数、今日剩余奖励资源数为零 **/
	public void resetUserRewardNum() {
		mapper.resetUserRewardNum();
		// 清理缓存
		CacheUtil.getCache().delAll("*:SysUser:*");
		OperationLogTool.operationLog(Constant.AUTO_LOG, "每周六早上重置今日奖励资源数、今日剩余奖励资源数为0");
	}

	/**
	 * 查询经理及所有下属
	 * @param currUser
	 * @return
	 */
	public List<Long> queryManagerSub(Long currUser) {
		return mapper.queryManagerSub(currUser);
	}
	
	/**
	 * 查询总监及所有下属
	 * @param currUser
	 * @return
	 */
	public List<Long> queryDirectorSub(Long currUser) {
		return mapper.queryDirectorSub(currUser);
	}
	
	/**
	 * 查询总经理及所有下属
	 * @param currUser
	 * @return
	 */
	public List<Long> queryMinisterSub(Long currUser) {
		return mapper.queryMinisterSub(currUser);
	}

	/**
	 * 查询市场部所有人（启用、激活）
	 * @return
	 */
	public List<Long> queryAllNormalBusiness() {
		return mapper.queryAllNormalBusiness();
	}

	/**
	 * 停止我部门推广分配
	 * @param currUser
	 * @return
	 */
	public List<SysUser> allotResourceStop(Long currUser) {
		SysUser curr = this.queryById(currUser);
		List<Long> ids = null;
		List<SysUser> userList = null;
		if (curr.getUserGroup() == Constant.USER_GROUP_JL) {
			ids = mapper.queryManagerSub(currUser);
		} else if (curr.getUserGroup() == Constant.USER_GROUP_ZJ) {
			ids = mapper.queryDirectorSub(currUser);
		} else if (curr.getUserGroup() == Constant.USER_GROUP_ZJL) {
			ids = mapper.queryMinisterSub(currUser);
		} else if (curr.getUserGroup() == Constant.ADMIN) {
			ids = mapper.queryAllAllotResource();
		} else {
			Assert.notNull(null, "你没有权限");
		}
		if (null != ids) {
			userList = InstanceUtil.newArrayList();
			SysUser user = null;
			for (Long id : ids) {
				user = this.queryById(id);
				user.setIsAllotResource(0);
				this.update(user);
				userList.add(user);
			}
		}
		return userList;
	}

	/**
	 * 停止我的推广分配
	 * @param currUser
	 * @return
	 */
	public SysUser allotResourceStopMyself(Long currUser) {
		SysUser user = this.queryById(currUser);
		user.setIsAllotResource(0);
		this.update(user);
		return user;
	}

	/**
	 * 修改我部门最大分配数
	 * 
	 * @param num 最大分配数
	 * @param currUser 用户ID
	 * @param carmString	修改说明
	 */
	public void changeAllotResourceMax(int allotResourceMax, Long currUser,String carmString) {
		SysUser curr = this.queryById(currUser);
		List<Long> ids = null;
		if (curr.getUserGroup() == Constant.USER_GROUP_JL) {
			ids = mapper.queryManagerSub(currUser);
		} else if (curr.getUserGroup() == Constant.USER_GROUP_ZJ) {
			ids = mapper.queryDirectorSub(currUser);
		} else if (curr.getUserGroup() == Constant.USER_GROUP_ZJL) {
			ids = mapper.queryMinisterSub(currUser);
		} else if (curr.getUserGroup() == Constant.ADMIN) {
			ids = mapper.queryAllAllotResource();
		} else {
			Assert.notNull(null, "你没有权限");
		}
		if (null != ids && ids.size() > 0) {
			SysUser user = null;
			for (Long id : ids) {
				user = this.queryById(id);
				user.setAllotResourceMax(allotResourceMax);
				this.update(user);
			}
		}
		//记录当前最大分配数
		currentAllotResourceMax = allotResourceMax;
		//修改说明
		CARM = carmString;
	}
	
	/**
	 * 初始化级联数据到redis
	 */
	public JSONArray queryCascader() {
		
//		Object o = CacheUtil.getCache().get(getCacheKey("_initCascader"));
//		if (o != null) {
//			return (JSONArray) o;
//		}
		// 总经理
		JSONArray ministers = new JSONArray();
		
		Map<Long, JSONObject> ministersMap = InstanceUtil.newHashMap();// 总经理
		Map<Long, JSONObject> directorMap = InstanceUtil.newHashMap();// 总监 
		Map<Long, JSONObject> managerMap = InstanceUtil.newHashMap();// 经理 
		List<JSONObject> salers = InstanceUtil.newArrayList();// 业务员
		// 查询所有正常用户
		Map<String, Object> params = InstanceUtil.newHashMap();
		params.put("locked", 0);
		List<SysUser> users = queryList(params);
		for (SysUser u : users) {
			JSONObject userJson = new JSONObject();
			userJson.put("value", u.getId().toString());
			userJson.put("label", u.getUserName());
			if (u.getUserGroup() == Constant.USER_GROUP_ZJL) {// 总经理
				userJson.put("children", new JSONArray());
				ministersMap.put(u.getId(), userJson);
			} else if (u.getUserGroup() == Constant.USER_GROUP_ZJ) {// 总监
				userJson.put("parentId", u.getParentId());
				userJson.put("children", new JSONArray());
				directorMap.put(u.getId(), userJson);
			} else if (u.getUserGroup() == Constant.USER_GROUP_JL) {// 经理
				userJson.put("parentId", u.getParentId());
				userJson.put("children", new JSONArray());
				managerMap.put(u.getId(), userJson);
			} else {
				userJson.put("parentId", u.getParentId());
				salers.add(userJson);
			}
		}
		
		for (JSONObject u : salers) {
			if (managerMap.containsKey(u.getLong("parentId"))) {
				managerMap.get(u.getLong("parentId")).getJSONArray("children").add(u);
				u.remove("parentId");
			}
		}
		
		for (JSONObject u : managerMap.values()) {
			if (directorMap.containsKey(u.getLong("parentId"))) {
				directorMap.get(u.getLong("parentId")).getJSONArray("children").add(u);
				u.remove("parentId");
			}
		}
		
		for (JSONObject u : directorMap.values()) {
			if (ministersMap.containsKey(u.getLong("parentId"))) {
				ministersMap.get(u.getLong("parentId")).getJSONArray("children").add(u);
				u.remove("parentId");
			}
		}
		
		for (JSONObject u : ministersMap.values()) {
			ministers.add(u);
		}
		
//		CacheUtil.getCache().set(getCacheKey("_initCascader"), ministers);
		
		return ministers;
	}
	
	/**
	 * 初始化级联数据到redis（按当前登录用户权限）
	 */
	public JSONArray queryAuthCascader(Long currUser) {
		// 查询当前用户组
		SysUser u = this.queryById(currUser);
		
		JSONArray ministers = new JSONArray();
		
		JSONObject superJson = new JSONObject();
		superJson.put("value", u.getId().toString());
		superJson.put("label", u.getUserName());
		if (u.getUserGroup() == Constant.USER_GROUP_ZJL) {// 总经理
			JSONArray children = new JSONArray();
			// 总监
			List<SysUser> directorList = getList(this.querySUBById(u.getId()));
			for (SysUser director : directorList) {
				JSONObject directorJson = new JSONObject();
				directorJson.put("value", director.getId().toString());
				directorJson.put("label", director.getUserName());
				JSONArray directorJa = new JSONArray();
				// 经理
				List<SysUser> managerList = getList(this.querySUBById(director.getId()));
				for (SysUser manager : managerList) {
					JSONObject managerJson = new JSONObject();
					managerJson.put("value", manager.getId().toString());
					managerJson.put("label", manager.getUserName());
					JSONArray managerJa = new JSONArray();
					// 业务员
					List<SysUser> salerList = getList(this.querySUBById(manager.getId()));
					for (SysUser saler : salerList) {
						JSONObject salerJson = new JSONObject();
						salerJson.put("value", saler.getId().toString());
						salerJson.put("label", saler.getUserName());
						managerJa.add(salerJson);
					}
					managerJson.put("children", managerJa);
					directorJa.add(managerJson);
				}
				directorJson.put("children", directorJa);
				children.add(directorJson);
			}
			superJson.put("children", children);
			ministers.add(superJson);
		} else if (u.getUserGroup() == Constant.USER_GROUP_ZJ) {// 总监
			JSONArray children = new JSONArray();
			// 经理
			List<SysUser> managerList = getList(this.querySUBById(u.getId()));
			for (SysUser manager : managerList) {
				JSONObject managerJson = new JSONObject();
				managerJson.put("value", manager.getId().toString());
				managerJson.put("label", manager.getUserName().toString());
				JSONArray managerJa = new JSONArray();
				// 业务员
				List<SysUser> salerList = getList(this.querySUBById(manager.getId()));
				for (SysUser saler : salerList) {
					JSONObject salerJson = new JSONObject();
					salerJson.put("value", saler.getId().toString());
					salerJson.put("label", saler.getUserName());
					managerJa.add(salerJson);
				}
				managerJson.put("children", managerJa);
				children.add(managerJson);
			}
			superJson.put("children", children);
			// 补上总经理
			JSONArray children1 = new JSONArray();
			children1.add(superJson);
			SysUser ministersUser = queryById(u.getParentId());
			JSONObject ministerJson = new JSONObject();
			ministerJson.put("value", ministersUser.getId().toString());
			ministerJson.put("label", ministersUser.getUserName());
			ministerJson.put("children", children1);
			ministers.add(ministerJson);
		} else if (u.getUserGroup() == Constant.USER_GROUP_JL) {// 经理
			JSONArray children = new JSONArray();
			// 业务员
			List<SysUser> salerList = getList(this.querySUBById(u.getId()));
			for (SysUser saler : salerList) {
				JSONObject salerJson = new JSONObject();
				salerJson.put("value", saler.getId().toString());
				salerJson.put("label", saler.getUserName());
				children.add(salerJson);
			}
			superJson.put("children", children);
			// 补总监
			SysUser director = queryById(u.getParentId());
			JSONArray children1 = new JSONArray();
			children1.add(superJson);
			JSONObject directorJson = new JSONObject();
			directorJson.put("value", director.getId().toString());
			directorJson.put("label", director.getUserName());
			directorJson.put("children", children1);
			// 补总经理
			SysUser ministersUser = queryById(director.getParentId());
			JSONArray children2 = new JSONArray();
			children2.add(directorJson);
			JSONObject ministersJson = new JSONObject();
			ministersJson.put("value", ministersUser.getId().toString());
			ministersJson.put("label", ministersUser.getUserName());
			ministersJson.put("children", children2);
			ministers.add(ministersJson);
		} else {
			// 补经理
			SysUser manager = queryById(u.getParentId());
			JSONObject managerJson = new JSONObject();
			JSONArray children = new JSONArray();
			children.add(superJson);
			managerJson.put("value", manager.getId().toString());
			managerJson.put("label", manager.getUserName());
			managerJson.put("children", children);
			// 补总监
			SysUser director = queryById(manager.getParentId());
			JSONArray children1 = new JSONArray();
			children1.add(managerJson);
			JSONObject directorJson = new JSONObject();
			directorJson.put("value", director.getId().toString());
			directorJson.put("label", director.getUserName());
			directorJson.put("children", children1);
			// 补总经理
			SysUser ministersUser = queryById(director.getParentId());
			JSONArray children2 = new JSONArray();
			children2.add(directorJson);
			JSONObject ministersJson = new JSONObject();
			ministersJson.put("value", ministersUser.getId().toString());
			ministersJson.put("label", ministersUser.getUserName());
			ministersJson.put("children", children2);
			ministers.add(ministersJson);
		}
		
		return ministers;
	}

	/**
	 * 分机号查询用户
	 * @param agentNo
	 * @return
	 */
	public SysUser queryAgentNo(Integer agentNo) {
		List<Long> ids = mapper.queryAgentNo(agentNo);
		if (null == ids) {
			OperationLogTool.operationLog(Constant.ERROR_LOG, "分机号："+agentNo+"，查询用户为空");
		} else if (ids.size() == 1) {
			return this.queryById(ids.get(0));
		} else if (ids.size() > 1) {
			OperationLogTool.operationLog(Constant.ERROR_LOG, "分机号："+agentNo+"，查询到多个用户");
		}
		return null;
	}

	/**
	 * 当前用户是否可见客户手机号
	 * @param userId
	 * @return
	 */
	public Integer queryIsMobileVisibleById(Long userId) {
		List<Long> roleIds = userRoleService.queryByUserId(userId);
		
		if (null != roleIds && roleIds.size() > 0) {
			SysRole role = null;
			for (Long roleId : roleIds) {
				role = roleService.queryById(roleId);
				if (null != role && role.getIsMobileVisible() == 1) {
					return 1;
				}
			}
		}
		return null;
	}

	public List<Long> queryAllUser() {
		return mapper.queryAllUser();
	}

	/**通知保险经纪人上班后开启推广分配**/
	public void msgSalerToOpenAllotResource() {
		List<Long> ids = mapper.queryAllMan();
		if (null != ids && ids.size() > 0) {
			SysUser user = null;
			for (Long id : ids) {
				user = this.queryById(id);
				if (null != user && StrUtils.isNotNullOrBlank(user.getDingId())) {
					DingUtil.sendMsg("请各位到公司之后登录呼叫系统，打开自己的推广资源分配，系统将开始为您分配推广资源！", user.getDingId());
				}
			}
		}
	}

	/**
	 * 每10分钟查询经纪人开启推广分配比例，统一修改最大分配数
	 */
	public void syncChangeTResourceMax(int firstWeekNum, int secondWeekNum ,int num) {
		List<Long> ids = this.queryAllMan();
		if (null != ids && ids.size() > 0) {
			SysUser user = null;
			Date toDay = new Date();
			int week = DateUtils.getWeekOfDate(toDay);
			if (week >= Calendar.MONDAY && week <= Calendar.THURSDAY || week == Calendar.SUNDAY) {
				for (Long id : ids) {
					user = this.queryById(id);
					if (StrUtils.isNotNullOrBlank(user.getCreateTime())) {
						int day = DateUtils.getDistanceDays(toDay,user.getCreateTime());
						if (day <= 6) {
							//入职第一周
							user.setAllotResourceMax(firstWeekNum);
						} else if (day > 6 && day <= 13) {
							//入职第二周
							user.setAllotResourceMax(secondWeekNum);
						} else if (day > 13 && day <= 20) {
							//入职第三周
							user.setAllotResourceMax(num);
						} else if (day > 20 && day <= 27) {
							//入职第四周
							user.setAllotResourceMax(num);
						} else if (day > 27) {
							//入职五周及以上
							user.setAllotResourceMax(num);
						}
					} else {
						System.out.println(user.getAccount()+"，创建时间为空");
					}
					this.update(user);
				}
			} else if (week == Calendar.FRIDAY ) {
				for (Long id : ids) {
					user = this.queryById(id);
					if (StrUtils.isNotNullOrBlank(user.getCreateTime())) {
						int day = DateUtils.getDistanceDays(toDay,user.getCreateTime());
						if (day <= 7) {
							//入职第一周
							user.setAllotResourceMax(3);
						} else if (day > 7) {
							//入职第二周
							user.setAllotResourceMax(5);
						} 
					} else {
						System.out.println(user.getAccount()+"，创建时间为空");
					}
					this.update(user);
				}
			}
			System.out.println("星期" + week+"自动调节最大分配数");
		}
	}

	public String queryCarm() {
		return CARM;
	}

	/**
	 * 批量更新用户分配记录
	 * @param userList
	 */
	public void updateResourceAllot(List<Long> userList) {
		mapper.updateResourceAllot(userList);
	}
	
	/**
	 * 定时每天3点重置升级分配数
	 * @author ty
	 */
	public void resetUserAllotClubNum() {
		mapper.resetUserAllotClubNum();
		//清理缓存
		CacheUtil.getCache().delAll("*:SysUser:*");
		OperationLogTool.operationLog(Constant.AUTO_LOG, "重置升级人员每天资源分配数");
	}

	/**
	 * 根据升级经理id查下属分配数最少的升级人员
	 * @param manageId
	 * @return
	 */
	public List<Long> querybyManageId(long manageId) {
		return ((SysUserMapper) mapper).querybyManageId(manageId);
	}
	
	public List<Long> queryByDIds(String did) {
		List<Long> userIds = ((SysUserMapper) mapper).queryByDIds(did);
		return userIds;
	}


	/**
	 * 根据升级人员id集合查询分配数最少的升级人员
	 * @param ids
	 * @return
	 */
	public List<Long> queryUpgradeUserByIds(List<Long> ids) {
		return  ((SysUserMapper) mapper).queryUpgradeUserByIds(ids);
	}
}
