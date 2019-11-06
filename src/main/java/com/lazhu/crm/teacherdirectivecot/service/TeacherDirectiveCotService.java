package com.lazhu.crm.teacherdirectivecot.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.base.BaseService;
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.customerlog.entity.CustomerLog;
import com.lazhu.crm.customerlog.service.CustomerLogService;
import com.lazhu.crm.teacherdirectivecot.entity.TeacherDirectiveCot;
import com.lazhu.crm.teacherdirectivecot.mapper.TeacherDirectiveCotMapper;
import com.lazhu.crm.teacherdirectiveoperationlog.entity.TeacherDirectiveOperationLog;
import com.lazhu.crm.teacherdirectiveoperationlog.service.TeacherDirectiveOperationLogService;
import com.lazhu.ecp.utils.CalculatorUtil;
import com.lazhu.ecp.utils.StrUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author taoyang
 * @since 2018-11-16
 */
@Service
@CacheConfig(cacheNames = "teacherDirectiveCot")
public class TeacherDirectiveCotService extends BaseService<TeacherDirectiveCot> {
	
	@Autowired
	TeacherDirectiveCotMapper teacherDirectiveCotMapper;
	
	/**
	 * 仓位操作记录
	 */
	@Autowired
	TeacherDirectiveOperationLogService teacherDirectiveOperationLogService;
	
	/**
	 * 查询客户信息
	 */
	@Autowired
	CustomerService customerService;
	
	@Autowired
	CustomerLogService logService;
	
	/**
	 * 建仓
	 * 
	 * @param param
	 * @return
	 */
	@Transactional
	public TeacherDirectiveCot addStock(TeacherDirectiveCot param) {
		if (StrUtils.isNullOrBlank(param.getUseMoney())) {
			Assert.notNull(null ,"资金未设定，建仓失败");
		}
		double money = param.getUseMoney();
		if (money <= 0) {
			Assert.notNull(null ,"资金不足(或未设定)，建仓失败");
		}
		//查询是否持有该股票
		List<Long> list = teacherDirectiveCotMapper.queryByStockCode(param.getStockCode() ,param.getCustomerId());
		if (StrUtils.isNotNullOrBlank(list) && list.size() > 0) {
			Assert.notNull(null , "建仓失败，该客户已持有此种股票，请找到相应股票，执行买入或卖出操作");
		}
		Customer customer = customerService.queryById(param.getCustomerId());
		//总资金
		double totalMoney = customer.getTotalTeacherMoney();
		//交易价格
		double cotPrice = param.getCotPrice();
		//交 易金额
		double cotMoney = param.getCotMoney();
		//交易数量
		Integer cotNum = param.getCotNum().intValue();
		//当前价格
		double currentPrice = param.getCurrentPrice();
		//可用资金
		double useMoney = param.getUseMoney();
		if (useMoney < cotMoney) {
			Assert.notNull(null , "可用资金不足，建仓失败！");
		}
		if (cotPrice <=0 || cotMoney <= 0 || cotNum <= 0 || currentPrice < 0 ) {
			Assert.notNull(null , "参数错误，请重新填写");
		}
		//当前市值
		double marketMoney = currentPrice * cotNum;
		//市值盈利金额
		double lossMoney = marketMoney - cotMoney;
		//1、更新会员表可用资金
		useMoney = useMoney - cotMoney;
		customer.setUseTeacherMoney(useMoney);
		//建仓增加一条持股类型数
		customer.setTeacherStockNum(customer.getTeacherStockNum() + 1);
		//更新总仓位
		if (StrUtils.isNotNullOrBlank(customer.getTeacherPosition())) {
			customer.setTeacherPosition(customer.getTeacherPosition() + CalculatorUtil.percent(cotMoney ,totalMoney));
		} else {
			customer.setTeacherPosition(CalculatorUtil.percent(cotMoney ,totalMoney));
		}
		customerService.update(customer);
		
		param.setUseMoney(useMoney);//可用资金
		param.setLossMoney(lossMoney);//盈亏金额
		//2、计算所有股票仓位
		param.setPosition(CalculatorUtil.percent(cotMoney,totalMoney));
		//盈亏比      （市场现价 - 买入价格）/买入价格   *100%
		///double lossPatio = (currentPrice - cotPrice)/cotPrice * 100;
		double lossPatio = CalculatorUtil.percent(currentPrice - cotPrice, cotPrice);
		param.setLossPatio(lossPatio);
		param.setLossMoney(lossMoney);
		//3、建立或更新持仓表
		super.update(param);
		
		//4、保存交易操作记录
		saveCustomerLog(param, null);//0--建仓
		//所属服务客服
		param.setServicerId(customer.getServerId());
		param.setServicerName(customer.getServerName());
		return super.update(param);
	}
	
	/**
	 * 批量建仓
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> addMoreStock( TeacherDirectiveCot param, List<Long> ids, List<String> names,List<String> useMoneys, Long currUser) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Long> idList = new ArrayList<Long>();
		List<String> nameList = new ArrayList<String>();
		Integer num ;
		if (ids != null && ids.size() > 0) {
			for (int i = 0 ; i < ids.size() ; i++) {
				TeacherDirectiveCot teacherCot = new TeacherDirectiveCot();
				List<Long> list = teacherDirectiveCotMapper.queryByStockCode(param.getStockCode() ,ids.get(i));
				Customer customer = customerService.queryById(ids.get(i));
				teacherCot.setCustomerId(ids.get(i));
				if (StrUtils.isNotNullOrBlank(names)) {
					teacherCot.setCustomerName(names.get(i));
				}
				if (StrUtils.isNotNullOrBlank(useMoneys)) {
					if (StrUtils.isNotNullOrBlank(useMoneys.get(i))) {
						teacherCot.setUseMoney(Double.valueOf(useMoneys.get(i)));
						num =(int)Math.floor(param.getPosition() * customer.getTotalTeacherMoney() / param.getCotPrice());//交易数量
						param.setCotMoney(param.getCotPrice() * StrUtils.toDouble(num));//交易金额
						param.setCurrentMoney(StrUtils.toDouble(num) * param.getCurrentPrice());//市值
						
						if (param.getCotMoney() > Double.valueOf(useMoneys.get(i))) {//可用资金不足
							idList.add(ids.get(i));
							nameList.add(names.get(i));
							System.out.println("可用资金不足，建仓失败！ 建仓资金==" +param.getCotMoney() + "，**可用资金：=="+ useMoneys.get(i));
							continue;
						}
					} else {//可用资金为空
						idList.add(ids.get(i));
						nameList.add(names.get(i));
						System.out.println("可用资金为空，批量建仓失败！");
						continue;
					}
				} else {
					idList.add(ids.get(i));
					nameList.add(names.get(i));
					System.out.println("未获取可用资金，批量建仓失败！");
					continue;
				}
				//客户已持有此种股票
				if (StrUtils.isNotNullOrBlank(list) && list.size() > 0) {
					idList.add(ids.get(i));
					nameList.add(names.get(i));
					System.out.println("建仓失败，该客户已持有此种股票，请找到相应股票，执行买入或卖出操作");
					continue;
					//Assert.notNull(null , "建仓失败，该客户已持有此种股票，请找到相应股票，执行买入或卖出操作");
				}
				
				teacherCot.setDirectionType(0);
				teacherCot.setStockCode(param.getStockCode());
				teacherCot.setStockName(param.getStockName());
				teacherCot.setCotPrice(param.getCotPrice());
				teacherCot.setCotNum(StrUtils.toLong(num));
				teacherCot.setCotMoney(param.getCotMoney());
				teacherCot.setCreateTime(new Date());
				teacherCot.setCurrentPrice(param.getCurrentPrice());
				teacherCot.setCurrentMoney(param.getCurrentMoney());
				teacherCot.setUpdateBy(currUser);
				//循环修改数据
				this.addStock(teacherCot);
				//3、更新持仓表
				Customer cus = customerService.queryById(ids.get(i));
				if (StrUtils.isNotNullOrBlank(cus)) {
					this.updateTeacherPosition(cus);
				}
			}
		}
		map.put("ids", idList);
		map.put("names", nameList);
		return map;
	}
	
	/**
	 * 买入
	 * @param param
	 * @return
	 */
	public TeacherDirectiveCot buyStock(TeacherDirectiveCot param) {
		if (StrUtils.isNullOrBlank(param.getUseMoney())) {
			Assert.notNull(null ,"资金未设定，建仓失败");
		}
		double money = param.getUseMoney();
		if (money <= 0) {
			Assert.notNull(null ,"资金不足(或未设定)，建仓失败");
		}
		Customer customer = customerService.queryById(param.getCustomerId());
		//查询当前买入股票信息
		List<Long> list = teacherDirectiveCotMapper.queryByStockCode(param.getStockCode() ,param.getCustomerId());
		TeacherDirectiveCot actualCot = null;
		if (StrUtils.isNotNullOrBlank(list) && list.size() > 0) {
			actualCot  = this.queryById(list.get(0));
		}
		//交易价格
		double cotPrice = param.getCotPrice();
		//交易金额
		double cotMoney = param.getCotMoney() ;
		//交易数量
		Integer cotNum = param.getCotNum().intValue();
		//当前价格
		double currentPrice = param.getCurrentPrice();
		//可用资金
		double useMoney = param.getUseMoney();
		if (useMoney < cotMoney) {
			Assert.notNull(null , "可用资金不足，建仓失败！");
		}
		if (cotPrice <=0 || cotMoney <= 0 || cotNum <= 0 || currentPrice < 0 ) {
			Assert.notNull(null , "参数错误，请重新填写");
		}
		//当前市值
		double marketMoney = currentPrice * cotNum;
		//市值盈利金额
		double lossMoney = marketMoney - cotMoney;
		//1、保存交易操作记录
		param.setId(actualCot.getId());
		//盈亏金额
		param.setLossMoney(lossMoney);
		saveCustomerLog(param ,null);//1--买入
		
		//2、更新资金表可用资金
		useMoney = useMoney - param.getCotMoney();
		customer.setUseTeacherMoney(useMoney);
		if (actualCot.getCotNum().intValue() == 0) {
			customer.setTeacherStockNum(customer.getTeacherStockNum() + 1);
		}
		//更新总仓位
		if (StrUtils.isNotNullOrBlank(customer.getTeacherPosition())) {
			customer.setTeacherPosition(customer.getTeacherPosition() + CalculatorUtil.percent(cotMoney ,customer.getTotalTeacherMoney()));
		} else {
			customer.setTeacherPosition(CalculatorUtil.percent(cotMoney , customer.getTotalTeacherMoney()));
		}
		customerService.update(customer);
		
		param.setUseMoney(useMoney);//可用资金
		//当前总持股数
		param.setCotNum(StrUtils.toLong(cotNum + actualCot.getCotNum().intValue()));
		//交易总金额及成本总金额
		param.setCotMoney(cotMoney + actualCot.getCotMoney());
		//成本价
		double costPrice = CalculatorUtil.divisionKeepDecimal(cotMoney + actualCot.getCotMoney(),StrUtils.toDouble(cotNum + actualCot.getCotNum().intValue()),2);
		//四舍五入保留两位小数点
		param.setCotPrice(costPrice);
		//盈亏金额
		param.setLossMoney(param.getCurrentPrice() * param.getCotNum() - param.getCotMoney());
		//仓位比
		double position = CalculatorUtil.percent(param.getCotMoney() , customer.getTotalTeacherMoney());
		param.setPosition(position);
		//盈亏比      （市场现价 - 买入价格）/买入价格   *100%
		double lossPatio = CalculatorUtil.percent(currentPrice - cotPrice,cotPrice) ;
		param.setLossPatio(lossPatio);
		param.setCurrentMoney(currentPrice * Double.valueOf(param.getCotNum().intValue())); 
		return super.update(param);
		
	}
	
	/**
	 * 批量加仓/批量买入
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> batchAddStock( TeacherDirectiveCot param, List<Long> ids, List<String> names,List<String> useMoneys, Long currUser) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Long> idList = new ArrayList<Long>();
		List<String> nameList = new ArrayList<String>();
		Integer num ;
		if (ids != null && ids.size() > 0) {
			for (int i = 0 ; i < ids.size() ; i++) {
				TeacherDirectiveCot teacherCot = new TeacherDirectiveCot();
				/**
				 * 1、查询客户是否持有该股票
				 * 	持有----执行买入加仓操作
				 * 	未持有---中断操作，提示加仓失败
				 */
				
				List<Long> list = teacherDirectiveCotMapper.queryByStockCode(param.getStockCode() ,ids.get(i));
				Customer customer = customerService.queryById(ids.get(i));
				teacherCot.setCustomerId(ids.get(i));
				if (StrUtils.isNotNullOrBlank(names)) {
					teacherCot.setCustomerName(names.get(i));
				}
				//客户未持有此种股票
				if (StrUtils.isNullOrBlank(list) || list.size() == 0) {
					idList.add(ids.get(i));
					nameList.add(names.get(i));
					System.out.println("加仓失败，该客户未持有此种股票，请先执行建仓操作");
					continue;
				} else {
					//客户已持有此种股票，执行加仓买入操作
					if (StrUtils.isNotNullOrBlank(useMoneys)) {
						if (StrUtils.isNotNullOrBlank(useMoneys.get(i))) {
							teacherCot.setUseMoney(Double.valueOf(useMoneys.get(i)));
							num =(int)Math.floor(param.getPosition() * customer.getTotalTeacherMoney() / param.getCotPrice());//交易数量
							param.setCotMoney(param.getCotPrice() * StrUtils.toDouble(num));//交易金额
							param.setCurrentMoney(StrUtils.toDouble(num) * param.getCurrentPrice());//市值
							
							if (param.getCotMoney() > Double.valueOf(useMoneys.get(i))) {//可用资金不足
								idList.add(ids.get(i));
								nameList.add(names.get(i));
								System.out.println("可用资金不足，批量加仓失败！ 建仓资金==" +param.getCotMoney() + "，**可用资金：=="+ useMoneys.get(i));
								continue;
							}
						} else {//可用资金为空
							idList.add(ids.get(i));
							nameList.add(names.get(i));
							System.out.println("可用资金为空，批量加仓失败！");
							continue;
						}
					} else {
						idList.add(ids.get(i));
						nameList.add(names.get(i));
						System.out.println("未获取可用资金，批量加仓失败！");
						continue;
					}
					
				}
				
				teacherCot.setDirectionType(5);//加仓
				teacherCot.setStockCode(param.getStockCode());
				teacherCot.setStockName(param.getStockName());
				teacherCot.setCotPrice(param.getCotPrice());
				teacherCot.setCotNum(StrUtils.toLong(num));
				teacherCot.setCotMoney(param.getCotMoney());
				teacherCot.setCreateTime(new Date());
				teacherCot.setCurrentPrice(param.getCurrentPrice());
				teacherCot.setCurrentMoney(param.getCurrentMoney());
				teacherCot.setUpdateBy(currUser);
				//循环修改数据
				teacherCot = this.buyStock(teacherCot);
				//3、更新持仓表
				Customer cus = customerService.queryById(ids.get(i));
				if (StrUtils.isNotNullOrBlank(cus)) {
					this.updateTeacherPosition(cus);
				}
			}
		}
		map.put("ids", idList);
		map.put("names", nameList);
		return map;
	}
	
	/**
	 * 
	 * 卖出
	 * @param param
	 * @return
	 */
	public TeacherDirectiveCot sellStock(TeacherDirectiveCot param) {
		
		Customer customer = customerService.queryById(param.getCustomerId());
		TeacherDirectiveCot actualCot = null;
		//查询所持有该股票
		List<Long> list = teacherDirectiveCotMapper.queryByStockCode(param.getStockCode() ,param.getCustomerId());
		if (StrUtils.isNotNullOrBlank(list) && list.size() > 0) {
			actualCot  = this.queryById(list.get(0));//list.get(0);
		}
		//交易价格
		double cotPrice = param.getCotPrice();
		//交 易金额
		double cotMoney = param.getCotMoney();
		//交易数量
		Integer cotNum = param.getCotNum().intValue();
		//当前价格
		double currentPrice = param.getCurrentPrice();
		//盈利金额
		double lossMoney = cotMoney - Double.valueOf(actualCot.getCotPrice() * cotNum);
		
		//可用资金
		double useMoney = param.getUseMoney();
		if (cotPrice <=0 || cotMoney <= 0 || cotNum <= 0 || currentPrice < 0 ) {
			Assert.notNull(null , "参数错误，请重新填写");
		}
		if ( (actualCot.getCotNum().intValue() - cotNum) > 0) {//卖出
			//1、保存交易操作记录
			param.setId(actualCot.getId());
			param.setLossMoney(lossMoney);//盈亏金额
			saveCustomerLog(param, actualCot);//2--卖出
			
			param.setUseMoney(useMoney + cotMoney);//可用资金
			
			//当前持股数
			param.setCotNum(StrUtils.toLong(actualCot.getCotNum().intValue() - cotNum));
			//交易总金额 及 剩余总成本
			param.setCotMoney(actualCot.getCotMoney() - cotMoney);
			//成本价
			double costPrice = CalculatorUtil.divisionKeepDecimal(actualCot.getCotMoney() - cotMoney , StrUtils.toDouble(actualCot.getCotNum().intValue() - cotNum) , 2);
			//四舍五入保留两位小数点
			param.setCotPrice(costPrice);
			//盈亏金额
			param.setLossMoney(param.getCurrentPrice() * Double.valueOf(cotNum) - param.getCotMoney());
			//仓位比
			param.setPosition( CalculatorUtil.percent(param.getCotMoney() , customer.getTotalTeacherMoney()));
			//盈亏比      （市场现价 - 买入价格）/买入价格   *100%
			double lossPatio = CalculatorUtil.percent(currentPrice - param.getCotPrice(), param.getCotPrice()) ;
			param.setLossPatio(lossPatio);
			param.setCurrentMoney(currentPrice * Double.valueOf(param.getCotNum().intValue())); 
			
			//更新总仓位
			customer.setTeacherPosition(CalculatorUtil.percent(customer.getTeacherPosition() - (Double.valueOf(cotNum) * actualCot.getCotPrice()) , customer.getTotalTeacherMoney()));
			
		} else if ((actualCot.getCotNum().intValue() - cotNum) == 0) {//清仓
			//1、保存交易操作记录
			param.setId(actualCot.getId());
			param.setLossMoney(lossMoney);
			saveCustomerLog(param, actualCot);//2--买出
			//清仓,所有信息置空
			param.setCotNum(0L);
			param.setCotMoney(0d);
			param.setCotPrice(0d);
			param.setCurrentMoney(0d);
			param.setPosition(0d);
			param.setLossPatio(0d);
			param.setLossMoney(cotMoney - actualCot.getCotMoney());
			//更新总仓位
			customer.setTeacherPosition(customer.getTeacherPosition() - CalculatorUtil.percent(actualCot.getCotMoney() ,customer.getTotalTeacherMoney()));
			//盈利金额
			if (StrUtils.isNotNullOrBlank(customer.getLossTeacherMoney())) {
				customer.setLossTeacherMoney(param.getLossMoney() + customer.getLossTeacherMoney());//更新盈利金额
			}else {
				customer.setLossTeacherMoney(param.getLossMoney());
			}
			//清仓减少一条持股类型数
			if (customer.getTeacherStockNum() > 1) {
				customer.setTeacherStockNum(customer.getTeacherStockNum() - 1);
			}else {
				customer.setTeacherStockNum(0);
			}
		} else if ((actualCot.getCotNum().intValue() - cotNum) < 0 ) {
			Assert.notNull(null , "交易数量错误，请重新填写");
		}
		
		//2、更新资金表可用资金
		useMoney = useMoney + cotMoney;
		customer.setUseTeacherMoney(useMoney);
		//若所持股票为空，仓位置空
		if (customer.getTeacherStockNum() == 0) {
			customer.setTeacherPosition(0.0);
		}
		customerService.update(customer);
		
		return super.update(param);
		
	}
	
	/**
	 * 批量平仓
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> sellMoreStock( TeacherDirectiveCot param, List<Long> ids, List<String> names,List<String> useMoneys, Long currUser) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Long> idList = new ArrayList<Long>();
		List<String> nameList = new ArrayList<String>();
		if (ids != null && ids.size() > 0) {
			try {
				for (int i = 0 ; i < ids.size() ; i++) {
					TeacherDirectiveCot teacherCot = new TeacherDirectiveCot();
					TeacherDirectiveCot teacherCots = new TeacherDirectiveCot();
					List<Long> list = teacherDirectiveCotMapper.queryByStockCode(param.getStockCode() ,ids.get(i));
					teacherCot.setCustomerId(ids.get(i));
					if (StrUtils.isNotNullOrBlank(names)) {
						teacherCot.setCustomerName(names.get(i));
					}
					if (StrUtils.isNotNullOrBlank(list) && list.size() > 0) {
						teacherCots = this.queryById(list.get(0));
						if (teacherCots.getCotNum().intValue() == 0) {
							idList.add(ids.get(i));
							nameList.add(names.get(i));
							continue;
						}
					} else {
						idList.add(ids.get(i));
						nameList.add(names.get(i));
						continue;
					}
					int num = teacherCots.getCotNum().intValue();//交易数量
					param.setCotMoney(param.getCotPrice() * StrUtils.toDouble(num));//交易金额
					param.setCurrentMoney(StrUtils.toDouble(num) * param.getCurrentPrice());//市值
					teacherCot.setUseMoney(teacherCots.getUseMoney());		
					teacherCot.setDirectionType(3);//平仓
					teacherCot.setStockCode(param.getStockCode());
					teacherCot.setStockName(param.getStockName());
					teacherCot.setCotPrice(param.getCotPrice());
					teacherCot.setCotNum(StrUtils.toLong(num));
					teacherCot.setCotMoney(param.getCotMoney());
					teacherCot.setCreateTime(new Date());
					teacherCot.setCurrentPrice(param.getCurrentPrice());
					teacherCot.setCurrentMoney(param.getCurrentMoney());
					teacherCot.setUpdateBy(currUser);
					//循环修改数据
					this.sellStock(teacherCot);
					//3、更新持仓表
					Customer cus = customerService.queryById(ids.get(i));
					if (StrUtils.isNotNullOrBlank(cus)) {
						this.updateTeacherPosition(cus);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		map.put("ids", idList);
		map.put("names", nameList);
		return map;
	}
	
	/**
	 * 批量平半仓
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> sellHalfStock( TeacherDirectiveCot param, List<Long> ids, List<String> names,List<String> useMoneys, Long currUser) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Long> idList = new ArrayList<Long>();
		List<String> nameList = new ArrayList<String>();
		if (ids != null && ids.size() > 0) {
			for (int i = 0 ; i < ids.size() ; i++) {
				TeacherDirectiveCot teacherCot = new TeacherDirectiveCot();
				TeacherDirectiveCot teacherCots = new TeacherDirectiveCot();
				List<Long> list = teacherDirectiveCotMapper.queryByStockCode(param.getStockCode() ,ids.get(i));
				teacherCot.setCustomerId(ids.get(i));
				if (StrUtils.isNotNullOrBlank(names)) {
					teacherCot.setCustomerName(names.get(i));
				}
				if (StrUtils.isNotNullOrBlank(list) && list.size() > 0) {
					teacherCots = this.queryById(list.get(0));
					if (teacherCots.getCotNum().intValue() == 0) {
						idList.add(ids.get(i));
						nameList.add(names.get(i));
						continue;
					}
				} else {
					idList.add(ids.get(i));
					nameList.add(names.get(i));
					continue;
				}
				int num = teacherCots.getCotNum().intValue();//交易数量
				num = (int) Math.floor(num / 2);//交易数量
				param.setCotMoney(param.getCotPrice() * StrUtils.toDouble(num));//交易金额
				param.setCurrentMoney(StrUtils.toDouble(num) * param.getCurrentPrice());//市值
				teacherCot.setUseMoney(teacherCots.getUseMoney());		
				teacherCot.setDirectionType(4);//平半仓
				teacherCot.setStockCode(param.getStockCode());
				teacherCot.setStockName(param.getStockName());
				teacherCot.setCotPrice(param.getCotPrice());
				teacherCot.setCotNum(StrUtils.toLong(num));
				teacherCot.setCotMoney(param.getCotMoney());
				teacherCot.setCreateTime(new Date());
				teacherCot.setCurrentPrice(param.getCurrentPrice());
				teacherCot.setCurrentMoney(param.getCurrentMoney());
				teacherCot.setUpdateBy(currUser);
				//循环修改数据
				this.sellStock(teacherCot);
				//3、更新持仓表
				Customer cus = customerService.queryById(ids.get(i));
				if (StrUtils.isNotNullOrBlank(cus)) {
					this.updateTeacherPosition(cus);
				}
			}
		}
		map.put("ids", idList);
		map.put("names", nameList);
		return map;
	}
	
	
	/**
	 * 保存仓位操作记录
	 * 
	 */
	public void saveCustomerLog(TeacherDirectiveCot param , TeacherDirectiveCot actCot){
		
		TeacherDirectiveOperationLog teacherCotLog = new TeacherDirectiveOperationLog();
		if (StrUtils.isNotNullOrBlank(actCot) && param.getDirectionType() == 2 ) {//卖出时  成本价与交易价区别开来
			teacherCotLog.setCotPrice(actCot.getCotPrice());//成本价格
			teacherCotLog.setCotMoney(actCot.getCotPrice() * param.getCotNum());//成本金额
		} else {
			teacherCotLog.setCotPrice(param.getCotPrice());//成本价格
			teacherCotLog.setCotMoney(param.getCotMoney());//成本金额
		}
		teacherCotLog.setTeacherDirectiveCotId(param.getId());//所持股票id
		teacherCotLog.setStockCode(param.getStockCode());//股票代码
		teacherCotLog.setStockName(param.getStockName());//股票名称
		teacherCotLog.setTradeNum(param.getCotNum());//交易数量
		teacherCotLog.setTradePrice(param.getCotPrice());//交易价格
		teacherCotLog.setTradeMoney(param.getCotMoney());//交易金额
		teacherCotLog.setThenLossMoney(param.getLossMoney());//盈亏金额
		teacherCotLog.setDirectionType(param.getDirectionType());//操作类型
		teacherCotLog.setCreateTime(new Date());;//操作时间
		teacherCotLog.setCreateBy(param.getUpdateBy());//操作人id
		
		teacherDirectiveOperationLogService.update(teacherCotLog);
	}


	/**
	 * 查询所有股票信息
	 * @return
	 */
	public List<Long> queryAll() {
		return teacherDirectiveCotMapper.queryAll();
	}
	
	/**
	 * 
	 * 删除股票信息时重新计算可用资金
	 * @param param
	 */
	public void delStock(List<Long> ids, Long currUser) {
		if (StrUtils.isNotNullOrBlank(ids) && ids.size() == 1) {
			TeacherDirectiveCot actualCot  = this.queryById(ids.get(0));
			Customer customer = customerService.queryById(actualCot.getCustomerId());
			customer.setUseTeacherMoney(customer.getUseTeacherMoney() + actualCot.getCotMoney());
			if (StrUtils.isNotNullOrBlank(customer) && customer.getTeacherStockNum() > 1) {
				customer.setTeacherStockNum(customer.getTeacherStockNum() - 1);
			}else {
				customer.setTeacherStockNum(0);
			}
			double db = customer.getTeacherPosition() - actualCot.getPosition();
			if (db >= 0) {
				customer.setTeacherPosition(db);//更新总仓位
			} else {
				customer.setTeacherPosition(0d);//更新总仓位
			}
			customer.setUpdateBy(currUser);
			customerService.update(customer);
			//保存删除股票信息记录
			CustomerLog log = new CustomerLog();
			log.setCreateBy(currUser);
			log.setUpdateBy(currUser);
			log.setCreateTime(new Date());
			log.setCustomerId(actualCot.getCustomerId());
			log.setCustomerName(actualCot.getCustomerName());
			log.setType("删除虚盘股票信息");
			logService.update(log);
			
		}
		if (StrUtils.isNotNullOrBlank(ids) && ids.size() > 1) {
			for (Long id : ids) {
				TeacherDirectiveCot actualCot  = this.queryById(id);
				Customer customer = customerService.queryById(actualCot.getCustomerId());
				customer.setUseTeacherMoney(customer.getUseTeacherMoney() + actualCot.getCotMoney());
				if (StrUtils.isNotNullOrBlank(customer) && customer.getTeacherStockNum() > 1) {
					customer.setTeacherStockNum(customer.getTeacherStockNum() - 1);
				}else {
					customer.setTeacherStockNum(0);
				}
				double db = customer.getTeacherPosition() - actualCot.getPosition();
				if (db >= 0) {
					customer.setTeacherPosition(db);//更新总仓位
				} else {
					customer.setTeacherPosition(0d);//更新总仓位
				}
				customer.setUpdateBy(currUser);
				customerService.update(customer);
				//保存删除股票信息记录
				CustomerLog log = new CustomerLog();
				log.setCreateBy(currUser);
				log.setCreateTime(new Date());
				log.setCustomerId(actualCot.getCustomerId());
				log.setCustomerName(actualCot.getCustomerName());
				log.setType("删除股票信息");
				logService.update(log);
			}
			
		}
	}
	
	/**
	 * 当资金量改变时，更新仓位比
	 */
	public void updateTeacherPosition (Customer param) {
		double db = 0; //总仓位
		if (StrUtils.isNotNullOrBlank(param)) {
			List<Long> list =teacherDirectiveCotMapper.queryByCustomerId(param.getId());
			for (Long id : list) {
				TeacherDirectiveCot teacherCot = this.queryById(id);
				if (StrUtils.isNotNullOrBlank(teacherCot) && teacherCot.getCotNum().intValue() > 0) {
					double position = CalculatorUtil.percent(teacherCot.getCotMoney() , param.getTotalTeacherMoney());
					teacherCot.setPosition(position);
					teacherCot = this.update(teacherCot);
					db = db + teacherCot.getPosition();
				}
			}
			param.setTeacherPosition(db);
			customerService.update(param);//更新总仓位
		}
		
	}
	
	/**
	 * 股票维度统计
	 * @param param
	 * @param currUser
	 * @return
	 */
	public Page<TeacherDirectiveCot> queryGroupStock(Map<String, Object> param, Long currUser) {
		int pageSize = StrUtils.toInt(param.get("pageSize"));
		int pageNum = StrUtils.toInt(param.get("pageNum"));
		Page<Long> pages = getPage(param);
		List<TeacherDirectiveCot> actualCotList = teacherDirectiveCotMapper.queryGroupStock(pages,param);
		//模拟分页
		Page<TeacherDirectiveCot> page = new Page<TeacherDirectiveCot>();
		page.setRecords(actualCotList);
		page.setCurrent(pageNum);
		page.setTotal(pages.getTotal());
		page.setSize(pageSize);
		return page;
	}
	
	/**
	 * 当资金量改变时，更新仓位比
	 */
	public void updateTeacherUserMoney (Customer param) {
		double cotMoney = 0 ;//成本金额
		if (StrUtils.isNotNullOrBlank(param)) {
			List<Long> list =teacherDirectiveCotMapper.queryByCustomerId(param.getId());
			for (Long id : list) {
				//1、查询该客户持有的股票信息
				TeacherDirectiveCot teacherCot = this.queryById(id);
				if (StrUtils.isNotNullOrBlank(teacherCot) && teacherCot.getCotNum().intValue() > 0) {
					//2、合计用户所持股票成本金额
					cotMoney = cotMoney + teacherCot.getCotMoney();
				} else if(StrUtils.isNotNullOrBlank(teacherCot) && teacherCot.getCotNum().intValue() == 0){
					cotMoney = cotMoney + 0;
				}
			}
			if (StrUtils.isNullOrBlank(param.getLossTeacherMoney())) {
				param.setLossTeacherMoney(0.00);
			}
			//3、用户可用资金= 投入资金 + 阴亏金额 - 成本金额
			double userMoney = param.getTotalTeacherMoney() + param.getLossTeacherMoney()- cotMoney;
			param.setUseTeacherMoney(userMoney);
			customerService.update(param);
		}
		
	}
}
