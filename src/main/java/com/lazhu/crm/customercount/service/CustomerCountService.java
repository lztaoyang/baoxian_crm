package com.lazhu.crm.customercount.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.customercount.entity.CustomerCount;
import com.lazhu.crm.customercount.mapper.CustomerCountMapper;
import com.lazhu.crm.signapply.entity.SignApply;
import com.lazhu.crm.signapply.service.SignApplyService;
import com.lazhu.t.resourceallot.entity.ResourceAllot;
import com.lazhu.t.resourceallot.service.ResourceAllotService;
import com.lazhu.crm.Constant;
import com.lazhu.core.base.BaseService;
import com.lazhu.core.util.CacheUtil;
import com.lazhu.core.util.DateUtil;
import com.lazhu.core.util.InstanceUtil;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;

/**
 *  会员统计表服务实现类
 *
  * @author ty
 * @date 2018年11月21日
 */
@Service
@CacheConfig(cacheNames = "customerCount")
public class CustomerCountService extends BaseService<CustomerCount> {
	
	@Autowired
	CustomerCountMapper customerCountMapper;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	SignApplyService signApplyService;
	
	@Autowired
	ResourceAllotService resourceAllotService;
	
	@Autowired
	CustomerCountService countService;
	
	/**
	 * 客户签单统计
	 * 
	 * @param customerId 客户id
	 * @param isUpgrade 是否升级
	 */
	public void applyCustomerCount(Long signApplyId,int isUpgrade){
		//获取订单数据
		SignApply signApply = signApplyService.queryById(signApplyId);
		List<Long> customerCountIds = customerCountMapper.selectCountByCustomerId(signApply.getCustomerId());
		Long customerId = signApply == null ? 0  : signApply.getCustomerId();
		
		if(null == customerCountIds || customerCountIds.size() == 0){//		1.为新签单客户
			//获取推广分配数据
			ResourceAllot resourceAllot = resourceAllotService.queryByResourceId(customerId);
			
			CustomerCount customerCount = new CustomerCount();
			customerCount.setCustomerId(customerId);
			customerCount.setApplyNum(1);
			if(signApply != null){
				customerCount.setApplyMoney(signApply.getAmount());
				customerCount.setInsureMoney(signApply.getAmount());
				customerCount.setValidMoney(signApply.getAmount());
				customerCount.setProvince(signApply.getProvince());
				customerCount.setCity(signApply.getCity());
				customerCount.setName(signApply.getCustomerName());
				customerCount.setSalerId(signApply.getSalerId());
			}
			
			if(resourceAllot != null){
				customerCount.setAllotId(resourceAllot.getId());//推广资源分配表获取
				customerCount.setChannel(resourceAllot.getChannel());
				customerCount.setExecuter(resourceAllot.getExecuter());
				customerCount.setPlanCode(resourceAllot.getPlanCode());
				customerCount.setEnterTime(resourceAllot.getCreaterTime());
				customerCount.setEnterTimeSolt(DateUtil.format(resourceAllot.getCreaterTime(),"HH"));
			}
			
			this.update(customerCount);
		}else{//		2.已签单，即为升级客户
			if (isUpgrade == 0) {//普通再次签单
				customerCountMapper.updateCustomerApply(signApply.getAmount(),customerId,0);
			} else {//升级签单
				upgradeCustomerCount(signApply.getAmount(),customerId,0);
			}
			//清理缓存
		    CacheUtil.getCache().delAll("*:customerCount:*");
		}
	}
	
	/**
	 * 客户升级处理
	 * 
	 * @param upgradeAmount 升级签单金额
	 * @param customerId	客户id
	 * @param isDel	是否是删除操作
	 */
	public void upgradeCustomerCount(Double upgradeAmount,Long customerId,int isDel){
		customerCountMapper.updateCustomerUpgrade(upgradeAmount, customerId,isDel);
	}
	
	/**
	 * 客户退保处理
	 * 
	 * @param signApplyId 订单id
	 * @param isWaver 是否犹豫期 	0犹豫期内	 	1犹豫期外
	 */
	/*public void refundCustomerCount(Long signApplyId,int isWaver){
		//获取订单数据
		SignApply signApply = signApplyService.queryById(signApplyId);
		if(signApply.getIsRefund() != null && signApply.getIsRefund() == 0){//一个订单只能退保一次
			if(0 == isWaver){//犹豫期内	//有效金额=总金额-犹豫期内退保金额
				customerCountMapper.updateCustomerRefund(signApply.getAmount(),signApply.getCustomerId(),isWaver);
			}else{//犹豫期外 		退保金额其实也是有效金额
				customerCountMapper.updateCustomerRefund(signApply.getAmount(),signApply.getCustomerId(),isWaver);
			}
			//清理缓存
		    CacheUtil.getCache().delAll("*:customerCount:*");
		}
		
	}*/
	
	/**
	 * 删除订单
	 */
	public void delCustomerCount(Long signApplyId,int isUpgrade){
		//获取订单数据
		SignApply signApply = signApplyService.queryById(signApplyId);
		CustomerCount customerCount = queryByCustomerId(signApply.getCustomerId());
		if(customerCount != null){
			Long customerId = signApply == null ? 0  : signApply.getCustomerId();
			if(customerCount.getApplyNum() < 2 && customerCount.getUpgradeNum() < 1){//签单次数为1，并且升级次数为0，就删除
				this.delete(customerCount.getId());
			}else{
				if (isUpgrade == 0) {//多次签单
					customerCountMapper.updateCustomerApply(signApply.getAmount(),customerId,1);
				}else{//升级过的
					upgradeCustomerCount(signApply.getAmount(),customerId,1);
				}
				//清理缓存
			    CacheUtil.getCache().delAll("*:customerCount:*");
			}
		}
	}
	
	/**
	 * 查询维度数据
	 * 
	 * @param param
	 * @return
	 */
	public List<Map<String,Object>> queryByDimension(Map<String, Object> param){
		List<Map<String,Object>> customerCountList = customerCountMapper.queryByDimension(param);
		for (Map<String, Object> map : customerCountList) {
			if(map != null && map.get("dimension") == null){
				customerCountList.remove(map);
				break;
			}
		}
		return customerCountList;
	}

	public CustomerCount queryByCustomerId(Long customerId) {
		List<Long> ids = customerCountMapper.selectCountByCustomerId(customerId);
		if (null != ids && ids.size() > 0) {
			if (ids.size() > 1) {
				OperationLogTool.operationLog(Constant.ERROR_LOG, "查询到多条客户统计表信息，客户ID："+customerId);
			}
			return this.queryById(ids.get(0));
		} else {
			OperationLogTool.operationLog(Constant.ERROR_LOG, "未查询到客户统计表信息，客户ID："+customerId);
			return null;
		}
	}
	
	/**
	 * 客户统计报表历史数据处理
	 * 
	 * @param param
	 * @return
	 */
	public  void synHistorySignApply() {
		Map<String,Object> map = InstanceUtil.newHashMap();
		List<SignApply> signApplys = signApplyService.queryList(map);
		for (SignApply signApply : signApplys) {
			//获取订单数据
			Customer customer = customerService.queryById(signApply.getCustomerId());
			List<Long> customerCountIds = customerCountMapper.selectCountByCustomerId(signApply.getCustomerId());
			
			if(null == customerCountIds || customerCountIds.size() == 0){//		1.为新签单客户
				//获取推广分配数据
				ResourceAllot resourceAllot = resourceAllotService.queryByResourceId(signApply.getCustomerId());
				CustomerCount customerCount = new CustomerCount();
				customerCount.setCustomerId(signApply.getCustomerId());
				customerCount.setApplyNum(1);
				customerCount.setApplyMoney(signApply.getAmount());
				customerCount.setInsureMoney(signApply.getAmount());
				customerCount.setValidMoney(signApply.getAmount());
				customerCount.setProvince(signApply.getProvince());
				customerCount.setCity(signApply.getCity());
				customerCount.setName(signApply.getCustomerName());
				customerCount.setSalerId(signApply.getSalerId());
				
				if(resourceAllot != null){
					customerCount.setAllotId(resourceAllot.getId());//推广资源分配表获取
					customerCount.setChannel(resourceAllot.getChannel());
					customerCount.setExecuter(resourceAllot.getExecuter());
					customerCount.setPlanCode(resourceAllot.getPlanCode());
					customerCount.setEnterTime(resourceAllot.getCreaterTime());
					customerCount.setEnterTimeSolt(DateUtil.format(resourceAllot.getCreaterTime(),"HH"));
				}
				
				this.update(customerCount);
			}else if (StrUtils.isNotNullOrBlank(customer)) {//		2.已签单，即为升级客户
				if (customer.getIsUpgrade() == 0) {//普通再次签单
					customerCountMapper.updateCustomerApply(signApply.getAmount(),signApply.getCustomerId(),0);
				} else {//升级签单
					upgradeCustomerCount(signApply.getAmount(),signApply.getCustomerId(),0);
				}
				//清理缓存
			    CacheUtil.getCache().delAll("*:customerCount:*");
			}
		}
	}
	
}
