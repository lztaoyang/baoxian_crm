package com.lazhu.sys.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.core.util.InstanceUtil;
import com.lazhu.crm.Constant;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.product.insuranceproduct.entity.InsuranceProduct;
import com.lazhu.product.insuranceproduct.mapper.InsuranceProductMapper;
import com.lazhu.sys.mapper.SysDicMapper;
import com.lazhu.sys.mapper.SysUserMapper;
import com.lazhu.sys.model.SysDic;
import com.lazhu.sys.model.SysUser;

/**
 * @author naxj
 * 
 */
@Service
@CacheConfig(cacheNames = "SysDic")
public class SysDicService extends BusinessBaseService<SysDic> {
	
	@Autowired//用户
	private SysUserMapper userMapper;
	
	@Autowired//保险产品
	private InsuranceProductMapper productMapper;
	
	/**
	 * 全局字典
	 * @param userId
	 * @return
	 */
	public Map<String, Map<String, String>> getAllDic(Long userId) {
		//获取用户
		SysUser user = super.getUserById(userId);
		
		Map<String, Object> params = InstanceUtil.newHashMap();
		params.put("orderBy", "sort_no");
		//只查询启用的
		params.put("locked", 1);
		List<SysDic> list = queryList(params);
		Map<String, Map<String, String>> resultMap = InstanceUtil.newHashMap();
		for (SysDic sysDic : list) {
			String key = sysDic.getType();
			if (resultMap.get(key) == null) {
				Map<String, String> dicMap = InstanceUtil.newHashMap();
				resultMap.put(key, dicMap);
			}
			resultMap.get(key).put(sysDic.getCode(), sysDic.getCodeText());
		}
		
		//经理级别（获取直接下属）字典
		if (user.getPosition() != null && user.getPosition() == 1 ) {
			if (user.getUserGroup() == Constant.ADMIN || user.getUserGroup() == Constant.USER_GROUP_GLY) {
				//插入所有经理姓名，ID
				List<Long> subordinates = userMapper.queryAllManager();
				if (subordinates != null && subordinates.size() > 0) {
					List<SysUser> userList = userMapper.selectBatchIds(subordinates);
					if (userList != null && userList.size() > 0) {
						Map<String,String> userMap = InstanceUtil.newHashMap();
						for (SysUser sysUser : userList) {
							userMap.put(StrUtils.toString(sysUser.getId()),sysUser.getUserName());
						}
						userMap.put("0","未分配");
						resultMap.put("SUBORDINATES", userMap);
					}
				}
			} else {
				//插入直接下级姓名，ID
				List<Long> subordinates = userMapper.querySUBById(userId);
				if (subordinates != null && subordinates.size() > 0) {
					List<SysUser> userList = userMapper.selectBatchIds(subordinates);
					if (userList != null && userList.size() > 0) {
						Map<String,String> userMap = InstanceUtil.newHashMap();
						for (SysUser sysUser : userList) {
							userMap.put(StrUtils.toString(sysUser.getId()),sysUser.getUserName());
						}
						userMap.put("0","未分配");
						resultMap.put("SUBORDINATES", userMap);
					}
				}
			}
		} else {
			//自己
			Map<String,String> userMap = InstanceUtil.newHashMap();
			userMap.put(StrUtils.toString(userId),user.getUserName());
			resultMap.put("SUBORDINATES", userMap);
		}
		
		//超级管理员或系统管理员（获取所有经理及以上级别的用户）字典
		if (user.getUserGroup() != null && ( user.getUserGroup() == Constant.ADMIN || user.getUserGroup() == Constant.USER_GROUP_GLY)) {
			List<Long> senior = userMapper.queryAllSenior();
			if (senior != null && senior.size() > 0) {
				List<SysUser> seniorList = userMapper.selectBatchIds(senior);
				if (seniorList != null && seniorList.size() > 0) {
					Map<String,String> userMap = InstanceUtil.newHashMap();
					for (SysUser sysUser : seniorList) {
						userMap.put(StrUtils.toString(sysUser.getId()),sysUser.getUserName());
					}
					resultMap.put("SENIOR", userMap);
				}
			}
		}
		
		//从保险产品表查询名称集合，插入到字典
		List<Long> allProductIds = productMapper.queryAll();
		if (allProductIds != null && allProductIds.size() > 0) {
			List<InsuranceProduct> productList = productMapper.selectBatchIds(allProductIds);
			if (productList != null && productList.size() > 0) {
				Map<String,String> productMap = InstanceUtil.newHashMap();
				for (InsuranceProduct product : productList) {
					productMap.put(StrUtils.toString(product.getId()),product.getName());
				}
				resultMap.put("ALLPRODUCT", productMap);
			}
		}
		
		//从保险产品表查询名称集合，插入到字典
		List<Long> productIds = productMapper.queryByIsPutaway();
		if (productIds != null && productIds.size() > 0) {
			List<InsuranceProduct> productList = productMapper.selectBatchIds(productIds);
			if (productList != null && productList.size() > 0) {
				Map<String,String> productMap = InstanceUtil.newHashMap();
				for (InsuranceProduct product : productList) {
					productMap.put(StrUtils.toString(product.getId()),product.getName());
				}
				resultMap.put("PRODUCT", productMap);
			}
		}
		//从保险产品表查询长险的名称集合，插入到字典
		List<Long> productLongTermIds = productMapper.queryLongTermByIsPutaway();
		if (productLongTermIds != null && productLongTermIds.size() > 0) {
			List<InsuranceProduct> productList = productMapper.selectBatchIds(productLongTermIds);
			if (productList != null && productList.size() > 0) {
				Map<String,String> productMap = InstanceUtil.newHashMap();
				for (InsuranceProduct product : productList) {
					productMap.put(StrUtils.toString(product.getId()),product.getName());
				}
				resultMap.put("PRODUCTLONG", productMap);
			}
		}
		//加保人员姓名，ID
		List<Long> upgraderIds = userMapper.queryAllUpgradeMan();
		if (upgraderIds != null && upgraderIds.size() > 0) {
			List<SysUser> upgraders = userMapper.selectBatchIds(upgraderIds);
			if (upgraders != null && upgraders.size() > 0) {
				Map<String,String> userMap = InstanceUtil.newHashMap();
				for (SysUser upgrader : upgraders) {
					userMap.put(StrUtils.toString(upgrader.getId()),upgrader.getUserName());
				}
				resultMap.put("UPGRADER", userMap);
			}
		}
		//所有升级经理姓名，ID
		List<Long> upgradeManagerIds = userMapper.queryAllUpgradeManager();
		if (upgradeManagerIds != null && upgradeManagerIds.size() > 0) {
			List<SysUser> userList = userMapper.selectBatchIds(upgradeManagerIds);
			if (userList != null && userList.size() > 0) {
				Map<String,String> userMap = InstanceUtil.newHashMap();
				for (SysUser sysUser : userList) {
					userMap.put(StrUtils.toString(sysUser.getId()),sysUser.getUserName());
				}
				resultMap.put("UPGRADEMANAGER", userMap);
			}
		}
		//所有经理姓名，ID
		List<Long> managerIds = userMapper.queryAllManager();
		if (managerIds != null && managerIds.size() > 0) {
			List<SysUser> userList = userMapper.selectBatchIds(managerIds);
			if (userList != null && userList.size() > 0) {
				Map<String,String> userMap = InstanceUtil.newHashMap();
				for (SysUser sysUser : userList) {
					userMap.put(StrUtils.toString(sysUser.getId()),sysUser.getUserName());
				}
				resultMap.put("MANAGER", userMap);
			}
		}
		//所有总监姓名，ID
		List<Long> directorIds = userMapper.queryAllDirector();
		if (directorIds != null && directorIds.size() > 0) {
			List<SysUser> userList = userMapper.selectBatchIds(directorIds);
			if (userList != null && userList.size() > 0) {
				Map<String,String> userMap = InstanceUtil.newHashMap();
				for (SysUser sysUser : userList) {
					userMap.put(StrUtils.toString(sysUser.getId()),sysUser.getUserName());
				}
				resultMap.put("DIRECTOR", userMap);
			}
		}
		
		
		return resultMap;
	}
	

	/**
	 * 获取字典集合
	 * @return
	 */
	public Map<String, Map<String, String>> getAllDic() {
		Map<String, Object> params = InstanceUtil.newHashMap();
		params.put("orderBy", "sort_no");
		List<SysDic> list = queryList(params);
		Map<String, Map<String, String>> resultMap = InstanceUtil.newHashMap();
		for (SysDic sysDic : list) {
			String key = sysDic.getType();
			if (resultMap.get(key) == null) {
				Map<String, String> dicMap = InstanceUtil.newHashMap();
				resultMap.put(key, dicMap);
			}
			resultMap.get(key).put(sysDic.getCode(), sysDic.getCodeText());
		}
		return resultMap;
	}

	/**
	 * key查询字典集合
	 * @param key
	 * @return
	 */
	//@Cacheable(value = "sysDicMap")
	public Map<String, String> queryDicByType(String key) {
		return InstanceUtil.getBean(SysDicService.class).getAllDic().get(key);
	}
	
	/**
	 * 暂时无用
	 * @param key
	 * @return
	 */
	public List<Map<String, String>> queryDicByType2(String key) {
		List<Map<String, String>> result = InstanceUtil.newArrayList();
		Map<String,String> temp =  InstanceUtil.getBean(SysDicService.class).getAllDic().get(key);
		
		for (Iterator<String> iterator = temp.keySet().iterator(); iterator.hasNext();) {
			Map<String, String> tempMap = InstanceUtil.newHashMap();
			String dkey = iterator.next();
			tempMap.put("code", dkey);
			tempMap.put("txt", temp.get(dkey));
			result.add(tempMap);
		}
		return result;
	}

	/**
	 * 暂时无用
	 * @param key
	 * @return
	 */
	public List<SysDic> queryByType(String type) {
		List<Long> ids = ((SysDicMapper)mapper).queryByType(type);
		return super.getList(ids);
	}
	
	/**
	 * 通过type、code查询codeText
	 * @param key
	 * @return
	 */
	public String queryCodeTextByTypeCode(String type,String code) {
		List<String> list = ((SysDicMapper)mapper).queryCodeTextByTypeCode(type,code);
		if (null != list && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
}