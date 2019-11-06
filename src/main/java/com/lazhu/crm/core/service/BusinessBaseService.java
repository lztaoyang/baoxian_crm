package com.lazhu.crm.core.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;

import com.lazhu.core.base.BaseModel;
import com.lazhu.core.base.BaseService;
import com.lazhu.core.support.Assert;
import com.lazhu.core.util.ComplexMD5Util;
import com.lazhu.crm.Constant;
import com.lazhu.crm.customerlog.entity.CustomerLog;
import com.lazhu.crm.customerlog.service.CustomerLogService;
import com.lazhu.crm.mobile.service.MobileService;
import com.lazhu.crm.resourcechange.entity.ResourceChange;
import com.lazhu.crm.resourcechange.service.ResourceChangeService;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.model.SysUser;
import com.lazhu.sys.service.SysUserService;

/**
 * Date:     2018年4月16日 下午3:26:59
 * @author   hz
 */
@CacheConfig(cacheNames="businessBaseService")
public abstract class BusinessBaseService<T extends BaseModel> extends BaseService<T> {

	@Autowired//用户
	protected SysUserService userService;
	
	@Autowired//客户联系方式
	protected MobileService mobileService;
	
	@Autowired//资源流程日志
	protected CustomerLogService customerLogService;
	
	@Autowired//资源修改日志
	protected ResourceChangeService resourceChangeService;
	
	/**
	 * 获取用户信息
	 * @param userId	用户ID
	 * @return
	 */
	public SysUser getUserById(Long userId) {
		return userService.queryById(userId);
	}

	
	/**保存流程日志**/
	public void saveCustomerLog (CustomerLog log) {
		customerLogService.update(log);
	}
	public void saveCustomerLog(Long customerId, String customerName,String type, Long curr, String ip) {
		CustomerLog log = new CustomerLog();
		log.setCustomerId(customerId);
		log.setCustomerName(customerName);
		log.setType(type);
		log.setUpdateBy(curr);
		log.setIp(ip);
		customerLogService.update(log);
	}
	
	/**保存修改日志**/
	public void saveResourceChange (ResourceChange change) {
		resourceChangeService.update(change);
	}
	
	/**
	 * 设置市场部权限
	 * @param params
	 * @param userId
	 */
	public void setBusiness (Map<String, Object> params,Long userId) {
		//防止不设定用户组，越权
		Assert.notNull(userId, Constant.GROUPISNULL);
		if (userId.longValue() == 0) {
			params.put("salerId", 0);
		} else {
			Long userGroup = userService.queryById(userId).getUserGroup();
			//防止不设定用户组，越权
			Assert.notNull(userGroup, Constant.GROUPISNULL);
			if (userGroup == Constant.USER_GROUP_ZJL) {//总经理
				params.put("ministerId", userId);
			} else if (userGroup == Constant.USER_GROUP_ZJ) {//总监
				params.put("directorId", userId);
			} else if (userGroup == Constant.USER_GROUP_JL) {//经理
				params.put("managerId", userId);
			} else if (userGroup == Constant.USER_GROUP_YWY) {//业务
				params.put("salerId", userId);
			}
		}
	}
	
	/**
	 * 返回用户组ID
	 * @param userId
	 * @return
	 */
	public Long getUserGroup(Long userId) {
		return userService.queryById(userId).getUserGroup();
	}
	
	/**
	 * 设置商务部权限
	 * @param params
	 * @param userId
	 */
	public void setContracter (Map<String, Object> params,Long userId) {
		Long userGroup = userService.queryById(userId).getUserGroup();
		//防止不设定用户组，越权
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		if (userGroup == Constant.USER_GROUP_HG) {//合规客服
			params.put("contracterId", userId);
		}
	}
	
	/**
	 * 设置客服部权限
	 * @param params
	 * @param userId
	 */
	public void setServer (Map<String, Object> params,Long userId) {
		Long userGroup = userService.queryById(userId).getUserGroup();
		//防止不设定用户组，越权
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		if (userGroup == Constant.USER_GROUP_KF) {//客服
			params.put("serverId", userId);
		} else if (userGroup == Constant.USER_GROUP_KFJL) {//客服经理
			params.put("serverManagerId", userId);
		}
	}
	
	/**
	 * 设置市场升级部部权限（用户组）
	 * @param params
	 * @param currUser
	 */
	public void setUpgradeBusiness(Map<String, Object> params,Long userId) {
		SysUser user = userService.queryById(userId);
		Long userGroup = user.getUserGroup();
		//防止不设定用户组，越权
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		if (userGroup == Constant.USER_GROUP_JB) {//升级人员
			params.put("upgraderId", userId);
		} else if (userGroup == Constant.USER_GROUP_JBJL) {//升级经理
			params.put("upgradeManagerId", userId);
		} else if (userGroup == Constant.USER_GROUP_JBZJ) {//升级总监
			params.put("upgradeDirectorId", userId);
		} else if (userGroup == Constant.USER_GROUP_JBZJL) {//升级总经理
			params.put("upgradeMinisterId", userId);
		}
	}
	
	/**
	 * 设置市场升级部是否升级签单
	 * @param params
	 * @param currUser
	 */
	public void setIsUpgrade (Map<String, Object> params, Long userId) {
		Long userGroup = userService.queryById(userId).getUserGroup();
		//防止不设定用户组，越权
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		if (userGroup == Constant.USER_GROUP_ZJL) {//总经理
			params.put("isUpgrade", 0);
		} else if (userGroup == Constant.USER_GROUP_ZJ) {//总监
			params.put("isUpgrade", 0);
		} else if (userGroup == Constant.USER_GROUP_JL) {//经理
			params.put("isUpgrade", 0);
		} else if (userGroup == Constant.USER_GROUP_YWY) {//业务
			params.put("isUpgrade", 0);
		} else if (userGroup == Constant.USER_GROUP_JB) {//升级人员
			params.put("isUpgrade", 1);
		} else if (userGroup == Constant.USER_GROUP_JBJL) {//升级经理
			params.put("isUpgrade", 1);
		} else if (userGroup == Constant.USER_GROUP_JBZJ) {//升级总监
			params.put("isUpgrade", 1);
		} else if (userGroup == Constant.USER_GROUP_JBZJL) {//升级总经理
			params.put("isUpgrade", 1);
		}
	}

	/**
	 * 下属查询
	 * @param params
	 */
	public void setSubordinate (Map<String, Object> params) {
		if (StrUtils.isNotNullOrBlank(params.get("subordinate"))) {
			Long userId = StrUtils.toLong(params.get("subordinate"));
			if (userId.longValue() == 0) {
				params.put("salerId", 0);
			} else {
				Long userGroup = userService.queryById(userId).getUserGroup();
				//防止不设定用户组，越权
				if (userGroup == Constant.USER_GROUP_ZJL) {//总经理
					params.put("ministerId", userId);
				} else if (userGroup == Constant.USER_GROUP_ZJ) {//总监
					params.put("directorId", userId);
				} else if (userGroup == Constant.USER_GROUP_JL) {//经理
					params.put("managerId", userId);
				} else if (userGroup == Constant.USER_GROUP_YWY) {//业务
					params.put("salerId", userId);
				} else if (userGroup == Constant.USER_GROUP_JB) {//升级人员
					params.put("upgraderId", userId);
				} else if (userGroup == Constant.USER_GROUP_JBJL) {//升级经理
					params.put("upgradeManagerId", userId);
				} else if (userGroup == Constant.USER_GROUP_JBZJ) {//升级总监
					params.put("upgradeDirectorId", userId);
				} else if (userGroup == Constant.USER_GROUP_JBZJL) {//升级总经理
					params.put("upgradeMinisterId", userId);
				} else if (userGroup == Constant.USER_GROUP_KF) {//客服
					params.put("serverId", userId);
				} else if (userGroup == Constant.USER_GROUP_KFJL) {//客服经理
					params.put("serverManagerId", userId);
				} else if (userGroup == Constant.USER_GROUP_HG) {//合规客服
					params.put("contracterId", userId);
				}
			}
		}
	}
	
	/**
	 * 用户组分类
	 * @param userGroup
	 * @return
	 */
	public int getUserGroupType(Long userGroup) {
		if (userGroup.longValue() <= Constant.USER_GROUP_ZJL || userGroup.longValue() == Constant.USER_GROUP_JB) {
			return 1;
		} else if (userGroup.longValue() == Constant.USER_GROUP_HG || userGroup.longValue() == Constant.USER_GROUP_HGJL) {
			return 2;
		} else if (userGroup.longValue() == Constant.USER_GROUP_KF || userGroup.longValue() == Constant.USER_GROUP_KFJL || userGroup.longValue() == Constant.USER_GROUP_KFZJ) {
			return 3;
		}
		return 0;
	}

	/**
	 * MD5手机号生成（需先加密成MD5手机号，然后查询MD5手机号）
	 * @param params
	 */
	public void setMd5MobileWhenMobile(Map<String, Object> params) {
		if (params.containsKey("mobile") && StrUtils.isNotNullOrBlank(params.get("mobile"))) {
			String mobile = params.get("mobile").toString().trim().replaceAll("\\s*", "");
			if (mobile.indexOf("****") >= 0) {
				params.put("fuzzyMobile", mobile);
				params.remove("mobile");
			} else {
				params.put("md5Mobile", ComplexMD5Util.MD5Encode(StrUtils.toString(params.get("mobile"))));
			}
		}
	}
	
	/**
	 * 通过MD5手机号查询手机号
	 * @param md5Mobile
	 * @return
	 */
	public String getMobileByMd5Mobile(String md5Mobile) {
		return mobileService.getMobile(md5Mobile);
	}
	
	/**
	 * 设置市场升级部转市场部部权限（用户组）
	 * @param params
	 * @param currUser
	 */
	public void setUpgradeToBusiness(Map<String, Object> params,Long userId) {
		SysUser user = userService.queryById(userId);
		Long userGroup = user.getUserGroup();
		//防止不设定用户组，越权
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		if (userGroup == Constant.USER_GROUP_JB) {//升级人员
			params.put("salerId", userId);
		} else if (userGroup == Constant.USER_GROUP_JBJL) {//升级经理
			params.put("managerId", userId);
		} else if (userGroup == Constant.USER_GROUP_JBZJ) {//升级总监
			params.put("directorId", userId);
		} else if (userGroup == Constant.USER_GROUP_JBZJL) {//升级总经理
			params.put("ministerId", userId);
		}
	}
	
	/**
	 * 设置市场客服部转市场部部权限（用户组）
	 * @param params
	 * @param currUser
	 */
	public void setServerToBusiness(Map<String, Object> params,Long userId) {
		SysUser user = userService.queryById(userId);
		Long userGroup = user.getUserGroup();
		//防止不设定用户组，越权
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		if (userGroup == Constant.USER_GROUP_KF) {//客服
			params.put("salerId", userId);
		} else if (userGroup == Constant.USER_GROUP_KFJL) {//客服经理
			params.put("managerId", userId);
		} else if (userGroup == Constant.USER_GROUP_KFZJ) {//客服总监
			params.put("directorId", userId);
		}
	}
	
}