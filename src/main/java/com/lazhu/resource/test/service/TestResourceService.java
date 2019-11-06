package com.lazhu.resource.test.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.core.util.ChineseCalendar;
import com.lazhu.core.util.ComplexMD5Util;
import com.lazhu.crm.Constant;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.mapper.CustomerMapper;
import com.lazhu.crm.customerlog.entity.CustomerLog;
import com.lazhu.crm.mobile.entity.Mobile;
import com.lazhu.crm.mobile.service.MobileService;
import com.lazhu.crm.resource.entity.Resource;
import com.lazhu.crm.resource.mapper.ResourceMapper;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.httpApi.AreaUrl;
import com.lazhu.httpApi.MobileAreaUrl;
import com.lazhu.resource.test.entity.TestResource;
import com.lazhu.resource.test.mapper.TestResourceMapper;
import com.lazhu.t.resourceallot.entity.ResourceAllot;
import com.lazhu.t.resourceallot.service.ResourceAllotService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hz
 * @since 2018-10-11
 */
@Service
@CacheConfig(cacheNames = "testResource")
public class TestResourceService extends BusinessBaseService<TestResource> {
	
	@Autowired
	ResourceService resourceService;
	
	@Autowired
	ResourceMapper resourceMapper;
	
	@Autowired
	CustomerMapper customerMapper;
	
	@Autowired
	ResourceAllotService resourceAllotService;
	
	@Autowired
	MobileService mobileService;
	
	@Autowired
	TestResourceMapper testResourceMapper;

	public void addResourceMobieTest() {
		List<Long> ids = testResourceMapper.queryEnable();
		if (null != ids && ids.size() > 0) {
			TestResource testResource = null;
			Resource resource = null;
			for (int i = 0; i < ids.size(); i++) {
				testResource = this.queryById(ids.get(i));
				resource = new Resource();
				resource.setName("导入");
				resource.setDemand("[{\"key\":\"录入需求\",\"value\":\"导入\"}]");
				resource.setMobile(testResource.getMobile());
				resource.setDirectorId(testResource.getDirectorId());
				resource.setIsAdd(1);
				
				//插入资源表
				addResource(resource,null);
				
				if (i%50 == 0) {
					System.out.println("已完成："+i);
				}
			}
		}
	}
	
	public Resource addResource(Resource param,String ip) {
		String mobile = StrUtils.toString(param.getMobile());
		if (StrUtils.isNullOrBlank(mobile)) {
			System.out.println("手机号不能为空");
			return null;
		}
		param.setMd5Mobile(ComplexMD5Util.MD5Encode(mobile));
		param.setFuzzyMobile(StrUtils.hideMobile(mobile));
		//2.【数据验证】手机号格式验证
		//checkNumFormat(param);
		//3.【业务逻辑：资源唯一原子性】新增时查询资源表是否已经存在该MD5手机号
		Long resourceNum = resourceMapper.isExist(param);
		if (resourceNum != null && resourceNum > 0) {
			System.out.println("手机号是资源："+mobile);
			return null;
		}
		//4.【业务逻辑：会员唯一原子性】查询会员表是否已经存在该MD5手机号
		Long clubNum = null;
		//判断会员表里面是否已经存在
		Customer customer = new Customer();
		customer.setMobile(param.getMobile());
		//同时验证微信，手机号
		clubNum = customerMapper.isExist(customer);
		if (clubNum != null && clubNum > 0) {
			System.out.println("手机号是会员："+mobile);
			return null;
		}
		//6.录入资源填写手机号时，同时将省市信息通过手机号录入进去
		addAreaByMobile(param);
		//8.添加到期时间（忽略周六周日）
		param.setRetainTime(ChineseCalendar.exactWeekdays(new Date(),60));
		param.setEnterTime(new Date());
		//10.【新增日志】保存新增（或流程）日志
		saveLog(param,ip);
		//9.【DB业务】保存或修改
		param = resourceService.update(param);
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
		resourceService.update(param);
		return param;
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
	
}
