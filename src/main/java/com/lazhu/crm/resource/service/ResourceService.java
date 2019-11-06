package com.lazhu.crm.resource.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.support.Assert;
import com.lazhu.core.util.CacheUtil;
import com.lazhu.core.util.ChineseCalendar;
import com.lazhu.core.util.ComplexMD5Util;
import com.lazhu.core.util.InstanceUtil;
import com.lazhu.core.util.WxCollectUtil;
import com.lazhu.crm.Constant;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.mapper.CustomerMapper;
import com.lazhu.crm.customerlog.entity.CustomerLog;
import com.lazhu.crm.mobile.entity.Mobile;
import com.lazhu.crm.resource.entity.Resource;
import com.lazhu.crm.resource.entity.ResourceDetailsVo;
import com.lazhu.crm.resource.mapper.ResourceMapper;
import com.lazhu.crm.resourcechange.entity.ResourceChange;
import com.lazhu.crm.resourceleave.entity.ResourceLeave;
import com.lazhu.crm.resourceleave.mapper.ResourceLeaveMapper;
import com.lazhu.crm.resourceleave.service.ResourceLeaveService;
import com.lazhu.crm.salerrecord.entity.SalerRecord;
import com.lazhu.crm.salerrecord.service.SalerRecordService;
import com.lazhu.crm.signapply.entity.SignApply;
import com.lazhu.crm.signapply.service.SignApplyService;
import com.lazhu.ecp.utils.CollectionUtil;
import com.lazhu.ecp.utils.ConstantUtils;
import com.lazhu.ecp.utils.DateUtils;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.httpApi.AreaUrl;
import com.lazhu.httpApi.MobileAreaUrl;
import com.lazhu.sys.model.SysUser;
import com.lazhu.t.resourceallot.entity.ResourceAllot;
import com.lazhu.t.resourceallot.service.ResourceAllotService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@Service
@CacheConfig(cacheNames = "resource")
public class ResourceService extends BusinessBaseService<Resource> {
	
	@Autowired//资源
	ResourceMapper resourceMapper;
	
	@Autowired//闲置资源
	ResourceLeaveService resourceLeaveService;
	
	@Autowired//闲置资源
	ResourceLeaveMapper resourceLeaveMapper;
	
	@Autowired//会员
	CustomerMapper customerMapper;
	
	@Autowired//市场部通话记录
	SalerRecordService salerRecordService;
	
	@Autowired//分配资源记录表
	ResourceAllotService resourceAllotService;
	
	@Autowired//分配资源记录表
	SignApplyService applyService;
	
	/**
	 * 手机号精准搜索客户
	 * @param md5Mobile
	 * @return
	 */
	public Resource queryByMd5Mobile(String md5Mobile) {
		Resource resource = null;
		List<Long> ids = resourceMapper.queryIdByMd5Mobile(md5Mobile);
		if (null != ids && ids.size() > 0) {
			if (ids.size() == 1) {
				resource = super.queryById(ids.get(0));
			} else {
				OperationLogTool.operationLog(Constant.ERROR_LOG, "【开发资源】手机号精准搜索发现重复手机号客户："+md5Mobile+"，个数："+ids.size());
			}
		} else {
			OperationLogTool.operationLog(Constant.ERROR_LOG, "【开发资源】手机号精准搜索失败："+md5Mobile);
		}
		return resource;
	}
	
	/**
	 * 未成交资源列表
	 * @param params
	 * @param userId
	 * @return
	 */
	public Map<String, Object> queryResourceAndList(Map<String, Object> params,Long userId) {
		//设置市场部权限（用户组）
		super.setBusiness(params, userId);
		//手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		//下属查询
		if (StrUtils.isNotNullOrBlank(params.get("subordinate"))) {
			super.setBusiness(params, StrUtils.toLong(params.get("subordinate")));
		}
		//删除下属key
		params.remove("subordinate");
		//
		int pageSize = StrUtils.toInt(params.get("pageSize"));
		int pageNum = StrUtils.toInt(params.get("pageNum"));
		params.put("pageSize", "100000");
		params.put("pageNum", "1");
		Page<Long> page = getPage(params);
		List<Long> idList = resourceMapper.selectIdPage(page, params);
		List<String> idStringList = InstanceUtil.newArrayList();
		if (null != idList && idList.size() > 0) {
			for (Long l : idList) {
				idStringList.add(l.toString());
			}
		}
		List<Long> ids = InstanceUtil.newArrayList();
		//模拟分页
		ids = CollectionUtil.getPageList(idList,pageNum,pageSize);
		page.setRecords(ids);
		Page<Resource> resourcePage = super.getPage(page);
		Map<String, Object> map = InstanceUtil.newHashMap();
		//
		Map<String,Object> resourceMap = InstanceUtil.newHashMap();
		resourceMap.put("data", resourcePage.getRecords());
		resourceMap.put("current", pageNum);
		resourceMap.put("size", pageSize);
		resourceMap.put("pages", resourcePage.getPages());
		resourceMap.put("total", resourcePage.getTotal());
		resourceMap.put("iTotalRecords", resourcePage.getTotal());
		resourceMap.put("iTotalDisplayRecords", resourcePage.getTotal());
		
		//分页数据
		map.put("resourcePage", resourceMap);
		//所有ID集合
		map.put("idList", idStringList);
		//今日拨打客户数
		Long todayCallNum = resourceMapper.todayCallResourceNum(params);
		map.put("todayCallNum", todayCallNum);
		return map;
	}
	
	/**
	 * 新增资源
	 * @param param
	 */
	@Transactional
	public Resource addResource(Resource param,String ip) {
		String mobile = StrUtils.toString(param.getMobile());
		Assert.notNull(mobile, "手机号不能为空");
		param.setMd5Mobile(ComplexMD5Util.MD5Encode(mobile));
		param.setFuzzyMobile(StrUtils.hideMobile(mobile));
		//2.【数据验证】手机号格式验证
		//checkNumFormat(param);
		//3.【业务逻辑：资源唯一原子性】新增时查询资源表是否已经存在该MD5手机号
		resourceAddIsExist(param);
		//4.【业务逻辑：会员唯一原子性】查询会员表是否已经存在该MD5手机号
		customerIsExist(param);
		//5.【业务逻辑：延期（5天），保护期（2天、15天）】查询资源表是否已经存在该号码
		resourceDevelopIsRetain(param.getMd5Mobile());
		//6.录入资源填写手机号时，同时将省市信息通过手机号录入进去
		addAreaByMobile(param);
		//7.【数据扩展】如果是新增，需设置业务，经理，总监，总经理
		setAllBusiness(param,param.getUpdateBy());
		//8.添加到期时间（忽略周六周日）
		setRetainTime(param);
		param.setEnterTime(new Date());
		//10.【新增日志】保存新增（或流程）日志
		saveLog(param,ip);
		//9.【DB业务】保存或修改
		param = super.update(param);
		if (StrUtils.isNotNullOrBlank(mobile)) {
			/////生成客户信息记录表数据（资源ID，掩码手机号，MD5手机号，RSA加密手机号）
			Mobile mobileEntity = new Mobile();
			mobileEntity.setCustomerId(param.getId());
			mobileEntity.setMobile(mobile);
			mobileService.addMobile(mobileEntity,ip);
		}
		//录入资源时，增加一条推广资源分配记录
		ResourceAllot resourceAllot = resourceAllotService.addResource(param);
		//
		param.setAllotId(resourceAllot.getId());
		//更新资源的分配ID
		super.update(param);
		return param;
	}

	/**
	 * 添加抽取资源到期时间（抽取资源保护期1天）
	 * @param param
	 */
	public void setRetainTime(Resource param) {
		Integer retainDay1 = StrUtils.toInteger(ConstantUtils.getLoadConstant("resource.retain.day1"));
		param.setRetainTime(ChineseCalendar.exactWeekdays(new Date(),retainDay1));
	}
	/**
	 * 添加退广资源到期时间（推广保护期3天）
	 * @param param
	 */
	public void setRetainTimeTG(Resource param) {
		Integer retainDay0 = StrUtils.toInteger(ConstantUtils.getLoadConstant("resource.retain.day0"));
		param.setRetainTime(ChineseCalendar.exactWeekdays(new Date(),retainDay0));
	}
	
	/**
	 * 修改资源
	 * @param param
	 * @param curr 
	 */
	@Transactional
	public Resource updateResource(Resource param,Long curr, String ip) {
		Long userGroup = super.getUserGroup(curr);
		//如果是业务员编辑，则自动校正为最新归属
		if (userGroup.longValue() == Constant.USER_GROUP_YWY) {
			setAllBusiness(param, curr);
		}
		//记录最后修改备注时间
		lastUpdateRemark(param);
		//7.【DB业务】保存或修改
		Resource resource = super.update(param);
		if (null != resource) {
			//手机号解密，拨打电话使用
			resource.setMobile(super.getMobileByMd5Mobile(resource.getMd5Mobile()));
			//权限控制详情页显示的手机号
			/** 当前用户是否可见客户手机号 **/
			Integer isMobileVisible = userService.queryIsMobileVisibleById(curr);
			if (null != isMobileVisible && isMobileVisible == 1) {
				//解密手机号
				resource.setShowMobile(resource.getMobile());
			} else {
				//掩码手机号
				resource.setShowMobile(resource.getFuzzyMobile());
			}
		}
		return resource;
	}
	
	/**
	 * 记录最后修改备注时间
	 */
	private void lastUpdateRemark(Resource param) {
		Resource resource = this.queryById(param.getId());
		try {
			//首次添加备注
			if (StrUtils.isNullOrBlank(param.getRemark()) && StrUtils.isNotNullOrBlank(resource.getRemark())) {
				param.setLastCallTime(new Date());
			}
			//修改备注
			if (StrUtils.isNotNullOrBlank(param.getRemark()) && StrUtils.isNotNullOrBlank(resource.getRemark())) {
				if (resource.getRemark().equals(param.getRemark())) {
					param.setLastCallTime(new Date());
				}
			}
			//清空备注
			
		} catch (Exception e) {
			System.out.println("###修改备注时出错###");
			System.out.println("原备注："+resource.getRemark());
			System.out.println("新备注："+param.getRemark());
			e.printStackTrace();
		}
	}
	
	/**
	 * 微信号码，手机号码验证
	 * @param param
	 */
	private void checkNumFormat(Resource param) {
		if (StrUtils.isNotNullOrBlank(param.getMobile())) {
			String mobile = param.getMobile().trim().replaceAll("\\s*|\t|\r|\n", "");
			mobile = StrUtils.filterUnNumber(mobile);
			Assert.length(mobile, 10, 12, "电话号码错误（必须为11位）");
			param.setMobile(mobile);
		}
	}

	/**
	 * 通过手机号添加区域信息
	 * @param mobile 需要判断的手机号
	 * @param param 需要添加的资源
	 * @return
	 */
	public void addAreaByMobile(Resource param) {
		//手机号
		String mobile = param.getMobile();
		if (StrUtils.isNotNullOrBlank(mobile) && mobile.length() == 11) {
			//接口1查询
			String[] s = MobileAreaUrl.getMobileArea(mobile);
			if (null != s) {
				if(StrUtils.isNotNullOrBlank(s[0])){
					if(s[0].equals("北京")||s[0].equals("上海")||s[0].equals("天津")||s[0].equals("重庆")){
						param.setProvince(s[0]+"市");
					}else{
						param.setProvince(s[0]+"省");
					}
				}
				if(StrUtils.isNotNullOrBlank(s[1])){
					param.setCity(s[1]+"市");
				}
			}
		} else {
			//打印错误日志
			OperationLogTool.operationLog(Constant.ERROR_LOG, param.getName()+"，手机号错误："+mobile);
		}
		//crm_mobile查询不到归属，则用接口2查询
		if (StrUtils.isNullOrBlank(param.getProvince())) {
			//截取手机号前7位数字
			String mobilePrefix = mobile.substring(0, 7);
			//获取省份信息
			String province = resourceMapper.queryProvinceByMobile(mobilePrefix);
			//获取城市信息
			String city = resourceMapper.queryCityByMobile(mobilePrefix);
			if(StrUtils.isNotNullOrBlank(province)){
				if(province.equals("北京")||province.equals("上海")||province.equals("天津")||province.equals("重庆")){
					param.setProvince(province+"市");
				}else{
					param.setProvince(province+"省");
				}
			}
			if(StrUtils.isNotNullOrBlank(city)){
				if(city.equals("北京")||city.equals("上海")||city.equals("天津")||city.equals("重庆")){
					param.setCity(null);
				}else{
					param.setCity(city+"市");
				}
			}
		}
		//则用接口2查询
		if (StrUtils.isNullOrBlank(param.getProvince())) {
			param.setProvince(AreaUrl.getMobileArea(mobile));
		}
		if (StrUtils.isNullOrBlank(param.getProvince())) {
			//打印错误日志
			OperationLogTool.operationLog(Constant.ERROR_LOG, param.getName()+"，手机号归属地未知，手机号："+mobile);
		}
	}

	private void resourceAddIsExist(Resource param) {
		Long resourceNum = null;
		resourceNum = resourceMapper.isExist(param);
		if (resourceNum != null && resourceNum > 0) {
			/**
			 * 已添加微信资源，如果客户再次咨询，
			 * 如果在原有业务员手上超过30天，删除原有资源，重新进行分配
			 */
			Resource resource = this.queryByMd5Mobile(param.getMd5Mobile());
			if (StrUtils.isNotNullOrBlank(resource) && StrUtils.isNotNullOrBlank(resource.getCreateTime()) 
					&& StrUtils.isNotNullOrBlank(resource.getFlowId()) && resource.getFlowId() == Constant.FLOW_YX ) {
				Integer timeLength = DateUtils.getDistanceDays(new Date() ,resource.getCreateTime());//
				if (timeLength > 30 ) {//已添加微信
					super.delete(resource.getId());//删除资源表
					resourceLeaveService.delete(resource.getId());//删除闲置表
				}else{
					Assert.notNull(null, "该号码已经存在！");
				}
			}else{
				Assert.notNull(null, "该号码已经存在！");
			}
			
		}
	}
	
	private void customerIsExist(Resource param) {
		Long clubNum = null;
		//判断会员表里面是否已经存在
		Customer customer = new Customer();
		customer.setMobile(param.getMobile());
		//同时验证微信，手机号
		clubNum = customerMapper.isExist(customer);
		if (clubNum != null && clubNum > 0) {
			Assert.notNull(null, "该号码已经成交！");
		}
	}

	/**
	 * 资源调配
	 * @param ids		List<资源ID>
	 * @param userId	用户ID
	 * @param currUser	操作者ID
	 * @param ip		操作值IP
	 * @return
	 */
	@Transactional
	public List<Resource> allot(List<Long> ids, Long userId, Long currUser, String ip) {
		//1.当前操作值，权限
		Long curr = super.getUserById(currUser).getUserGroup();
		if (curr == Constant.USER_GROUP_YWY) {
			Assert.notNull(null, Constant.GROUPISNULL);
		}
		
		//2.查询所有实体
		List<Resource> resourceList = resourceMapper.selectBatchIds(ids);
		for (Resource resource : resourceList) {
			//3.保存分配日志
			CustomerLog log = new CustomerLog();
			log.setCustomerId(resource.getId());
			log.setCustomerName(resource.getName());
			Long oldUserId = null;
			if (resource.getSalerId() != null && resource.getSalerId() > 0) {
				oldUserId = resource.getSalerId();
			} else if (resource.getManagerId() != null && resource.getManagerId() > 0) {
				oldUserId = resource.getManagerId();
			} else if (resource.getDirectorId() != null && resource.getDirectorId() > 0) {
				oldUserId = resource.getDirectorId();
			}
			log.setOldUser(oldUserId);
			log.setNewUser(userId);
			String type = "资源调配";
			if (resource.getFlowId()==Constant.FLOW_GXC) {
				type = "共享池资源调配";
			} else if (resource.getFlowId()==Constant.FLOW_BLACK) {
				type = "黑名单资源调配";
			} else if (resource.getFlowId()==Constant.FLOW_LJ) {
				type = "垃圾池资源调配";
			}
			log.setType(type);
			log.setIp(ip);
			log.setUpdateBy(currUser);
			super.saveCustomerLog(log);
			
			//4.根据被分配者的用户组，设置市场部信息
			setAllBusiness(resource,userId);
			//5.【DB业务】更新
			resource.setUpdateBy(currUser);
			super.update(resource);
		}
		return resourceList;
	}
	
	/**
	 * 资源调配
	 * @param ids		List<资源ID>
	 * @param userId	用户ID
	 * @param currUser	操作者ID
	 * @param ip		操作值IP
	 * @return
	 */
	@Transactional
	public List<Resource> allotTestResource(List<Long> ids, Long userId, Long currUser, String ip) {
		//1.当前操作值，权限
		Long curr = super.getUserById(currUser).getUserGroup();
		if (curr == Constant.USER_GROUP_YWY) {
			Assert.notNull(null, Constant.GROUPISNULL);
		}
		
		//2.查询所有实体
		List<Resource> resourceList = resourceMapper.selectBatchIds(ids);
		for (Resource resource : resourceList) {
			//3.保存分配日志
			CustomerLog log = new CustomerLog();
			log.setCustomerId(resource.getId());
			log.setCustomerName(resource.getName());
			Long oldUserId = null;
			if (resource.getSalerId() != null && resource.getSalerId() > 0) {
				oldUserId = resource.getSalerId();
			} else if (resource.getManagerId() != null && resource.getManagerId() > 0) {
				oldUserId = resource.getManagerId();
			} else if (resource.getDirectorId() != null && resource.getDirectorId() > 0) {
				oldUserId = resource.getDirectorId();
			}
			log.setOldUser(oldUserId);
			log.setNewUser(userId);
			String type = "资源调配";
			if (resource.getFlowId()==Constant.FLOW_GXC) {
				type = "共享池资源调配";
			} else if (resource.getFlowId()==Constant.FLOW_BLACK) {
				type = "黑名单资源调配";
			} else if (resource.getFlowId()==Constant.FLOW_LJ) {
				type = "垃圾池资源调配";
			}
			log.setType(type);
			log.setIp(ip);
			log.setUpdateBy(currUser);
			super.saveCustomerLog(log);
			
			//4.根据被分配者的用户组，设置市场部信息
			setAllBusiness(resource,userId);
			//5.【DB业务】更新
			resource.setUpdateBy(currUser);
			
			resource.setIsAdd(0);
			super.update(resource);
		}
		return resourceList;
	}
	
	
	/**
	 * 设置业务，经理，总监，总经理
	 * @param param
	 * @param userId
	 */
	public void setAllBusiness(Resource param, Long userId) {
		SysUser manager = null;
		SysUser director = null;
		SysUser minister = null;
		//业务员
		SysUser user = super.getUserById(userId);
		if (user.getUserGroup() == Constant.USER_GROUP_YWY) {
			param.setSalerId(userId);
			param.setSalerName(user.getUserName());
			//经理
			manager = super.getUserById(user.getParentId());
			param.setManagerId(manager.getId());
			param.setManagerName(manager.getUserName());
			//总监
			director = super.getUserById(manager.getParentId());
			param.setDirectorId(director.getId());
			param.setDirectorName(director.getUserName());
			//总经理
			minister = super.getUserById(director.getParentId());
			param.setMinisterId(minister.getId());
			param.setMinisterName(minister.getUserName());
		} else if (user.getUserGroup() == Constant.USER_GROUP_JL) {
			//经纪人
			param.setSalerId(0L);
			param.setSalerName("未分配");
			//经理
			manager = super.getUserById(userId);
			param.setManagerId(manager.getId());
			param.setManagerName(manager.getUserName());
			//总监
			director = super.getUserById(manager.getParentId());
			param.setDirectorId(director.getId());
			param.setDirectorName(director.getUserName());
			//总经理
			minister = super.getUserById(director.getParentId());
			param.setMinisterId(minister.getId());
			param.setMinisterName(minister.getUserName());
		}else if (user.getUserGroup() == Constant.USER_GROUP_ZJ) {
			//经纪人
			param.setSalerId(0L);
			param.setSalerName("未分配");
			//经理
			param.setManagerId(0L);
			param.setManagerName("未分配");
			//总监
			director = super.getUserById(userId);
			param.setDirectorId(director.getId());
			param.setDirectorName(director.getUserName());
			//总经理
			minister = super.getUserById(director.getParentId());
			param.setMinisterId(minister.getId());
			param.setMinisterName(minister.getUserName());
		}else if (user.getUserGroup() == Constant.USER_GROUP_ZJL) {
			//经纪人
			param.setSalerId(0L);
			param.setSalerName("未分配");
			//经理
			param.setManagerId(0L);
			param.setManagerName("未分配");
			//总监
			param.setDirectorId(0L);
			param.setDirectorName("未分配");
			//总经理
			minister = super.getUserById(userId);
			param.setMinisterId(minister.getId());
			param.setMinisterName(minister.getUserName());
		}
		manager = null;
		director = null;
		minister = null;
	}


	/**
	 * 保存流程日志
	 * @param resource	资源
	 * @param userId	用户ID
	 * @param ip		用户IP
	 */
	public void saveLog (Resource resource,String ip) {
		CustomerLog log = new CustomerLog();
		log.setCustomerId(resource.getId());
		log.setCustomerName(resource.getName());
		log.setNewUser(resource.getUpdateBy());
		log.setType("录入资源");
		log.setIp(ip);
		log.setUpdateBy(resource.getUpdateBy());
		super.saveCustomerLog(log);
	}
	
	/**
	 * 保存修改日志
	 * @param param		资源
	 * @param userId	用户ID
	 * @param ip		用户IP
	 */
	public void saveChangeLog (Resource param,String ip) {
		Resource old = super.queryById(param.getId());
		if (old != null && old.getId() > 0) {
			boolean wx = true;
			boolean qq = true;
			boolean mobile = true;
			if (StrUtils.isNotNullOrBlank(param.getWechatNo())) {
				wx = param.getWechatNo().equals(old.getWechatNo());
			} else if (StrUtils.isNotNullOrBlank(old.getWechatNo())) {
				wx = old.getWechatNo().equals(param.getWechatNo());
			}
			if (StrUtils.isNotNullOrBlank(param.getQq())) {
				qq = param.getQq().equals(old.getQq());
			} else if (StrUtils.isNotNullOrBlank(old.getQq())) {
				qq = old.getQq().equals(param.getQq());
			}
			if (StrUtils.isNotNullOrBlank(param.getMd5Mobile())) {
				mobile = param.getMd5Mobile().equals(old.getMd5Mobile());
			} else if (StrUtils.isNotNullOrBlank(old.getMd5Mobile())) {
				mobile = old.getMd5Mobile().equals(param.getMd5Mobile());
			}
			
			if (!wx || !qq || !mobile) {
				ResourceChange change = new ResourceChange();
				change.setCustomerId(old.getId());
				change.setCustomerName(old.getName());
				if (!wx) {
					change.setOldWebchat(old.getWechatNo());
					change.setNewWebchat(param.getWechatNo());
				}
				if (!qq) {
					change.setOldQq(old.getQq());
					change.setNewQq(param.getQq());
				}
				if (!mobile) {
					change.setOldMobile(old.getMd5Mobile());
					change.setNewMobile(param.getMd5Mobile());
				}
				change.setIp(ip);
				change.setUpdateBy(param.getUpdateBy());
				super.saveResourceChange(change);
			}
		}
	}

	/**通过手机号查找客户所在省份**/
	public String queryProvinceByMobile(String mobile) {
		return resourceMapper.queryProvinceByMobile(mobile);
	}

	/**通过手机号查找客户所在城市**/
	public String queryCityByMobile(String mobile) {
		return resourceMapper.queryCityByMobile(mobile);
	}

	/**
	 * 包装详情类
	 * @param param
	 * @param currUser
	 * @return
	 */
	public ResourceDetailsVo findFullEntity(Resource param, Long currUser) {
		ResourceDetailsVo vo = null;
		Long userGroup = super.getUserById(currUser).getUserGroup();
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		Resource resource = super.queryById(param.getId());
		if (null != resource) {
			//手机号解密，拨打电话使用
			resource.setMobile(super.getMobileByMd5Mobile(resource.getMd5Mobile()));
			//权限控制详情页显示的手机号
			/** 当前用户是否可见客户手机号 **/
			Integer isMobileVisible = userService.queryIsMobileVisibleById(currUser);
			if (null != isMobileVisible && isMobileVisible == 1) {
				//解密手机号
				resource.setShowMobile(resource.getMobile());
			} else {
				//掩码手机号
				resource.setShowMobile(resource.getFuzzyMobile());
			}
			/** 市场部电话服务记录 **/
			List<Long> salerRecordIds = null;
			List<SalerRecord> salerRecord = null;
			salerRecordIds = salerRecordService.queryByCustomerId(resource.getId());
			if (salerRecordIds != null && salerRecordIds.size() > 0) {
				salerRecord = salerRecordService.getList(salerRecordIds);
			}
			//排序
			resourceDetailsSort(salerRecord);
			/** 市场部电话服务记录-30秒内的成功通话 **/
			List<Long> salerRecord1Ids = null;
			List<SalerRecord> salerRecord1 = null;
			salerRecord1Ids = salerRecordService.queryRealByCustomerId(resource.getId());
			if (salerRecord1Ids != null && salerRecord1Ids.size() > 0) {
				salerRecord1 = salerRecordService.getList(salerRecord1Ids);
			}
			//排序
			resourceDetailsSort(salerRecord1);
			//封装到新的实体
			vo = new ResourceDetailsVo(resource,salerRecord,salerRecord1);
		} else {
			return null;
		}
		return vo;
	}


	private void resourceDetailsSort(List<SalerRecord> salerRecord) {
		if (null != salerRecord && salerRecord.size() >0) {
			//按创建时间降序排序
			Collections.sort(salerRecord,new Comparator<SalerRecord>() {
				@Override
				public int compare(SalerRecord o1,SalerRecord o2) {
					if (null != o1 && null != o2 && null != o1.getCreateTime() && null != o2.getCreateTime()) {
						if (o1.getCreateTime().getTime() < o2.getCreateTime().getTime()) {
							return 1;
						}else if (o1.getCreateTime().getTime() == o2.getCreateTime().getTime()) {
							return 0;
						} else {
							return -1;
						}
					}
					return -1;
				}
			});
		}
	}

	/**
	 * 是否属于自己的会员
	 * （市场部），（升级部），（客服部），（商务部）
	 * @param customer	客户
	 * @param currUser	用户ID
	 * @return
	 */
	public boolean isMySelfClub (Resource resource,Long currUser) {
		boolean isMySelfClub = false;
		if (null != resource.getSalerId() && resource.getSalerId().longValue() == currUser.longValue()) {
			isMySelfClub = true;
		} else if (null != resource.getManagerId() && resource.getManagerId().longValue() == currUser.longValue()) {
			isMySelfClub = true;
		} else if (null != resource.getDirectorId() && resource.getDirectorId().longValue() == currUser.longValue()) {
			isMySelfClub = true;
		} else if (null != resource.getMinisterId() && resource.getMinisterId().longValue() == currUser.longValue()) {
			isMySelfClub = true;
		} else if (currUser <= 10) {//管理员
			isMySelfClub = true;
		}
		return isMySelfClub;
	}
	
	/**
	 * 录入资源，判断手机号是否在资源表已存在（当前判断为是否在保护期）
	 * @param md5Mobile
	 * @return
	 */
	public void resourceDevelopIsRetain(String md5Mobile) {
		long retainDay1 = StrUtils.toLong(ConstantUtils.getLoadConstant("resource.retain.day1"));
		long retainDay2 = StrUtils.toLong(ConstantUtils.getLoadConstant("resource.retain.day2"));
		List<Long> resourceIds = resourceMapper.queryIdByMd5Mobile(md5Mobile);
		
		if (null != resourceIds && resourceIds.size() > 0) {
			for (Long id : resourceIds) {
				Resource resource = this.queryById(id);
				if (resource.getFlowId() == 101 || resource.getFlowId() == 201 || resource.getFlowId() == 601) {
					//超过待开发保护期
					boolean flag = DateUtils.timeOutDay(resource.getCreateTime(), retainDay1);
					if (!flag) {
						Assert.notNull(null, "待开发保护期："+retainDay1+"天，不可录入");
					}
				} else if (resource.getFlowId() == 901) {//共享池资源
					Assert.notNull(null, "共享池资源，不可录入");
				}  else if (resource.getFlowId() == 100) {//个人录入微信资源
					Assert.notNull(null, "个人录入微信资源，不可重复录入");
				} else {
					//超过流程开发保护期
					boolean flag = DateUtils.timeOutDay(resource.getCreateTime(), retainDay2);
					if (!flag) {
						Assert.notNull(null, "开发保护期："+retainDay2+"天，不可录入");
					}
				}
			}
		}
		
		List<Long> resourceLeaveIds = resourceLeaveMapper.queryIdByMd5Mobile(md5Mobile);
		if (null != resourceLeaveIds && resourceLeaveIds.size() > 0) {
			for (Long id : resourceLeaveIds) {
				ResourceLeave resourceLeave = resourceLeaveService.queryById(id);
				if (resourceLeave.getFlowId() == 901) {//共享池资源
					Assert.notNull(null, "共享池资源，不可录入");
				}  else if (resourceLeave.getFlowId() == 401) {
					Assert.notNull(null, "垃圾资源，不可录入");
				} else if (resourceLeave.getFlowId() == 402) {
					Assert.notNull(null, "黑名单资源，不可录入");
				}
				Assert.notNull(null, "闲置资源，不可录入");
			}
		}
	}
	
	/**
	 * 更新资源，判断手机号是否在资源表已存在（当前判断为是否在保护期）
	 * @param md5Mobile
	 * @return
	 */
	public void resourceUpdateDevelopIsRetain(String md5Mobile,Long currUser) {
		long retainDay1 = StrUtils.toLong(ConstantUtils.getLoadConstant("resource.retain.day1"));
		long retainDay2 = StrUtils.toLong(ConstantUtils.getLoadConstant("resource.retain.day2"));
		List<Long> ids = resourceMapper.queryIdByMd5Mobile(md5Mobile);
		
		if (null != ids && ids.size() > 0) {
			for (Long id : ids) {
				Resource resource = this.queryById(id);
				if (!isMySelfClub(resource, currUser)) {
					if (resource.getFlowId() == 101 || resource.getFlowId() == 201 || resource.getFlowId() == 601) {
						//超过待开发保护期
						boolean flag = DateUtils.timeOutDay(resource.getCreateTime(), retainDay1);
						if (!flag) {
							Assert.notNull(null, "待开发保护期："+retainDay1+"天，不可修改");
						}
					} else if (resource.getFlowId() == 901) {//共享池资源
						Assert.notNull(null, "共享池资源，不可修改");
					} else {
						//超过流程开发保护期
						boolean flag = DateUtils.timeOutDay(resource.getCreateTime(), retainDay2);
						if (!flag) {
							Assert.notNull(null, "开发保护期："+retainDay2+"天，不可修改");
						}
					}
				}
			}
		}
	}

	/**
	 * 分配推广资源，判断手机号是否在资源表已存在（当前判断为是否在保护期）
	 * @param md5Mobile
	 * @return
	 */
	public Integer resourceAllotIsRetain(String md5Mobile , List<CustomerLog> customerLogList) {
		int flowId = 0;
		List<Long> ids = resourceMapper.queryIdByMd5Mobile(md5Mobile);
		if (null != ids && ids.size() > 0) {
			/**
			 * 已添加微信资源，如果客户再次咨询，
			 * 如果在原有业务员手上超过30天，删除原有资源，重新进行分配
			 */
			Resource resource = this.queryByMd5Mobile(md5Mobile);
			if (StrUtils.isNotNullOrBlank(resource) && StrUtils.isNotNullOrBlank(resource.getCreateTime()) 
					&& StrUtils.isNotNullOrBlank(resource.getFlowId()) && flowId == Constant.FLOW_YX ) {
				Integer timeLength = DateUtils.getDistanceDays(new Date() ,resource.getCreateTime());//
				if (timeLength > 30) {//已添加微信
					/** 
					 * 如果共享池中有同一MD5，则回收资源并生成记录 
					 ***/
					CustomerLog customerLog = new CustomerLog();
					customerLog.setCustomerId(resource.getId());
					customerLog.setCustomerName(resource.getName());
					customerLog.setOldUser(resource.getSalerId());
					customerLog.setOldFlow(resource.getFlowId());
					customerLog.setType("已添加微信超过30天未签单，自动回收资源");
					customerLog.setCreateTime(new Date());
					customerLog.setUpdateTime(new Date());
					customerLogList.add(customerLog);
					
					super.delete(resource.getId());//删除资源表
					System.out.println("删除了原始资源"+resource.getName()+"/"+resource.getMd5Mobile()+"/"+resource.getFuzzyMobile()+"对应的共享池资源！");
					resourceLeaveService.delete(resource.getId());
				}else{
					flowId = this.queryById(ids.get(0)).getFlowId();
				}
			}else{
				flowId = this.queryById(ids.get(0)).getFlowId();
			}
			
		}
		return flowId;
	}
	
	@Transactional
	public void syncRecycleSharedPool() {
		Integer num = resourceMapper.syncRecycleSharedPool();
		//清理缓存
		CacheUtil.getCache().delAll("*:resource:*");
		OperationLogTool.operationLog(Constant.AUTO_LOG,"回收超过保护期资源："+num+"条");
		//删除共享池资源
		try {
			resourceMapper.deleteSharedPool();
		} catch (Exception e) {
			System.out.println("自动回收过期资源，删除共享池资源时，失败！");
		}
	}
	
	/**
	 * 通过分配ID查询资源ID
	 * @param allotId
	 * @return
	 */
	public Long queryByAllotId(Long allotId) {
		List<Long> ids = resourceMapper.queryByAllotId(allotId);
		if (null != ids && ids.size() > 0) {
			if (ids.size() > 1) {
				OperationLogTool.operationLog(Constant.ERROR_LOG, "资源表出现重复分配ID："+allotId);
			}
			return ids.get(0);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Page search(Map<String, Object> params, Long currUser) {
		//记录是否有无限搜索，防止资源泄露
		if (StrUtils.isNullOrBlank(params.get("id")) && StrUtils.isNullOrBlank(params.get("name")) && StrUtils.isNullOrBlank(params.get("wechatNo")) && StrUtils.isNullOrBlank(params.get("mobile"))) {
			return null;
		}
		OperationLogTool.operationLog(Constant.SEARCH_LOG,super.getUserById(currUser).getAccount()+"，【全局搜索资源】姓名："+params.get("name")+"，微信："+params.get("wechatNo")+"，手机号："+params.get("mobile"));
		Page<Long> page = getPage(params);
		//手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(params);
		page.setRecords(resourceMapper.selectIdPage(page,params));
		//资源表无数据，则查询闲置表
		if (page.getRecords().size() <= 0) {
			page.setRecords(resourceLeaveMapper.selectIdPage(page,params));
			return resourceLeaveService.getPage(page);
		}
		return super.getPage(page);
	}

	/**
	 * 今日未添加微信资源数
	 * @param userId
	 * @return
	 */
	public Integer queryToDayTochatNum(Long userId) {
		return resourceMapper.queryToDayTochatNum(userId);
	}

	/**
	 * 今日已添加微信资源数
	 * @param userId
	 * @return
	 */
	public Integer queryToDayDisposedNum(Long userId) {
		return resourceMapper.queryToDayDisposedNum(userId);
	}

	/**
	 * 今日推广资源数
	 * @param userId
	 * @return
	 */
	public Integer queryToDayPromoteResourceNum(Long userId) {
		return resourceMapper.queryToDayPromoteResourceNum(userId);
	}

	/**
	 * 今日抽取资源数
	 * @param userId
	 * @return
	 */
	public Integer queryToDayExtractNum(Long userId) {
		return resourceMapper.queryToDayExtractNum(userId);
	}

	public List<Map<String, Object>> queryBySql(String sql) {
		return resourceMapper.queryBySql(sql);
	}

	/**
	 * 查询当前经纪人有几条推广资源
	 * @param currUser
	 */
	public Integer resourceInitNum(Long currUser) {
		return resourceMapper.resourceInitNum(currUser);
	}
	
	
	/**
	 * 商务中心-首次签单待合规客户-双击详细数据
	 * 
	 * @param param
	 * @param currUser
	 * @return
	 */
	public ResourceDetailsVo hgEntity(Resource param, Long currUser) {
		Long userGroup = super.getUserById(currUser).getUserGroup();
		Assert.notNull(userGroup, Constant.GROUPISNULL);
		Resource resource = super.queryById(param.getId());
		SignApply apply = applyService.queryById(applyService.queryByCustomerId(param.getId()).get(0));
		resource.setSubmitCode(WxCollectUtil.getUrl(apply.getSubmitCode()));
		// 手机号解密，拨打电话使用
		String mobile = super.getMobileByMd5Mobile(resource.getMd5Mobile());
		resource.setMobile(mobile);
		/** 详情页显示的手机号 **/
		detailsShowMobile(resource,mobile,currUser);
		
		List<SalerRecord> salerRecord = null;
		/** 市场部电话服务记录 （已接通） **/
		List<Long> salerRecordIds = salerRecordService.queryRealByCustomerId1(resource.getId());
		if (salerRecordIds != null && salerRecordIds.size() > 0) {
			salerRecord = salerRecordService.getList(salerRecordIds);
		}
		
		/** 市场部所有电话服务记录 **/
		List<Long> salerRecord1Ids = null;
		List<SalerRecord> salerRecord1 = null;
		if (userGroup == Constant.USER_GROUP_JB || userGroup == Constant.USER_GROUP_JBJL
				|| userGroup == Constant.USER_GROUP_ZJ || userGroup == Constant.ADMIN) {
		}
		salerRecord1Ids = salerRecordService.queryByCustomerId(resource.getId());
		if (salerRecord1Ids != null && salerRecord1Ids.size() > 0) {
			salerRecord1 = salerRecordService.getList(salerRecord1Ids);
		}
		// 封装到新的实体
		ResourceDetailsVo vo = new ResourceDetailsVo(resource, salerRecord, salerRecord1);
		return vo;
	}
	
	/**
	 * 根据用户角色是否显示手机号，判断显示完整手机号、还是掩码手机号
	 * 
	 * @param customer
	 * @param mobile
	 * @param currUser
	 * @return
	 */
	private void detailsShowMobile(Resource resource,String mobile,Long currUser) {
		Integer isMobileVisible = userService.queryIsMobileVisibleById(currUser);
		if (null != isMobileVisible && isMobileVisible == 1) {
			//解密手机号
			resource.setShowMobile(mobile);
		} else {
			//掩码手机号
			resource.setShowMobile(resource.getFuzzyMobile());
		}
	}
	
	
	/**
	 * 新增资源
	 * @param param
	 */
	@Transactional
	public Resource addTestResource(Resource param,String ip) {
		//1.【数据意义】手机号码不能同时为空
		String mobile = StrUtils.toString(param.getMobile());
		Assert.notNull(mobile, "手机号不能为空");
		param.setMd5Mobile(ComplexMD5Util.MD5Encode(mobile));
		param.setFuzzyMobile(StrUtils.hideMobile(mobile));
		//2.【数据验证】手机号格式验证
		checkNumFormat(param);
		//3.【业务逻辑：资源唯一原子性】新增时查询资源表是否已经存在该MD5手机号
		resourceAddIsExist(param);
		//4.【业务逻辑：会员唯一原子性】查询会员表是否已经存在该MD5手机号
		customerIsExist(param);
		//5.【业务逻辑：延期（5天），保护期（2天、15天）】查询资源表是否已经存在该号码
		resourceDevelopIsRetain(param.getMd5Mobile());
		//6.录入资源填写手机号时，同时将省市信息通过手机号录入进去
		addAreaByMobile(param);
		//7.【数据扩展】如果是新增，需设置业务，经理，总监，总经理
		setAllBusiness(param,param.getSalerId());
		//8.添加到期时间（忽略周六周日）
		setRetainTime(param);
		param.setEnterTime(new Date());
		//10.【新增日志】保存新增（或流程）日志
		saveLog(param,ip);
		//9.【DB业务】保存或修改
		param = super.update(param);
		if (StrUtils.isNotNullOrBlank(mobile)) {
			//生成客户信息记录表数据（资源ID，掩码手机号，MD5手机号，RSA加密手机号）
			Mobile mobileEntity = new Mobile();
			mobileEntity.setCustomerId(param.getId());
			mobileEntity.setMobile(mobile);
			mobileService.addMobile(mobileEntity,ip);
		}
		//t_resource_allot表中插入测试资源
		this.addTestResourceAllot(param,ip);
		
		//发送钉钉消息
		// 8.【可分配时】消息通知
		/*try {
			sendMessage(param, param.getSalerId(), mobile);
			sendMessageManager(param, param.getSalerName(), param.getManagerId(), mobile);// 经理
		} catch (Exception e) {
			System.out.println("钉钉消息，推广分配资源消息通知，发送失败!");
		}*/
		return param;
	}
	
	/**
	 * 录入测试资源时，增加一条推广资源分配记录
	 * @param resource
	 * @return
	 */
	public ResourceAllot addTestResourceAllot(Resource resource,String ip) {
		ResourceAllot resourceAllot = new ResourceAllot();
		
		resourceAllot.setId(resource.getAllotId());//
		resourceAllot.setResourceId(resource.getId());//resource表ID即customer表ID
		
		resourceAllot.setName(resource.getName());
		resourceAllot.setFuzzyMobile(resource.getFuzzyMobile());// 掩码手机号
		resourceAllot.setMd5Mobile(resource.getMd5Mobile());// MD5手机号
		resourceAllot.setChannel(resource.getFromInfo());// 推广渠道
		resourceAllot.setContent(resource.getDemand());// 内容
		//设置区域
		resourceAllot.setProvince(resource.getProvince());
		resourceAllot.setCity(resource.getCity());
		// 设置业务，经理，总监，总经理
		resourceAllot.setSalerId(resource.getSalerId());
		resourceAllot.setSalerName(resource.getSalerName());
		resourceAllot.setManagerId(resource.getManagerId());
		resourceAllot.setManagerName(resource.getManagerName());
		resourceAllot.setDirectorId(resource.getDirectorId());
		resourceAllot.setDirectorName(resource.getDirectorName());
		resourceAllot.setMinisterId(resource.getMinisterId());
		resourceAllot.setMinisterName(resource.getMinisterName());
		resourceAllot.setRemark(resource.getRemark());
		resourceAllot.setIp(ip);
		
		resourceAllot.setCreaterTime(resource.getEnterTime());
		resourceAllot.setAllotTime(resource.getCreateTime());
		
		resourceAllot.setType(9);
		return resourceAllotService.update(resourceAllot);
	}

	/**
	 * 批量插入资源
	 * @param resourceList
	 */
	public void insertBatchResource(List<Resource> resourceList) {
		resourceMapper.insertBatchResource(resourceList);
	}

	
}