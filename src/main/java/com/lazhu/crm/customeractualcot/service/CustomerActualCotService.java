package com.lazhu.crm.customeractualcot.service;


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
import com.lazhu.crm.customeractualcot.entity.CustomerActualCot;
import com.lazhu.crm.customeractualcot.mapper.CustomerActualCotMapper;
import com.lazhu.crm.customercotoperationlog.entity.CustomerCotOperationLog;
import com.lazhu.crm.customercotoperationlog.service.CustomerCotOperationLogService;
import com.lazhu.crm.customerlog.entity.CustomerLog;
import com.lazhu.crm.customerlog.service.CustomerLogService;
import com.lazhu.crm.teacherdirectivecot.entity.TeacherDirectiveCot;
import com.lazhu.crm.teacherdirectivecot.service.TeacherDirectiveCotService;
import com.lazhu.ecp.utils.CalculatorUtil;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.httpApi.StockApi;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author taoyang
 * @since 2018-11-16
 */

@Service
@CacheConfig(cacheNames = "customerActualCot")
public class CustomerActualCotService extends BaseService<CustomerActualCot> {
	
	@Autowired
	CustomerActualCotMapper cusCotMapper;
	
	/**
	 * 仓位操作记录
	 */
	@Autowired
	CustomerCotOperationLogService customerCotLogService;
	
	@Autowired
	TeacherDirectiveCotService teacherCotService;
	
	/**
	 * 查询客户信息
	 */
	@Autowired
	CustomerService customerService;
	
	@Autowired
	CustomerLogService logService;
	/**
	 * 实盘建仓
	 * 
	 * 
	 * 
	 * @param param 实盘仓位信息对象
	 * @return	CustomerActualCot 实盘仓位信息对象
	 */
	@Transactional
	public CustomerActualCot addStock(CustomerActualCot param) {
		if (StrUtils.isNullOrBlank(param.getUseMoney())) {
			Assert.notNull(null ,"资金未设定，建仓失败");
		}
		double money = param.getUseMoney();
		if (money <= 0) {
			Assert.notNull(null ,"资金不足(或未设定)，建仓失败");
		}
		//查询是否持有该股票
		List<Long> list = cusCotMapper.queryByStockCode(param.getStockCode(),param.getCustomerId());
		if (StrUtils.isNotNullOrBlank(list) && list.size() > 0) {
			Assert.notNull(null , "建仓失败，该客户已持有此种股票，请找到相应股票，执行买入或卖出操作");
		}
		Customer customer = customerService.queryById(param.getCustomerId());
		//总资金
		double totalMoney = customer.getTotalMoney();
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
		customer.setUseMoney(useMoney);
		//建仓增加一条持股类型数
		if (StrUtils.isNotNullOrBlank(customer.getStockNum())) {
			customer.setStockNum(customer.getStockNum() + 1);
		} else {
			customer.setStockNum(1);
		}
		
		//更新总仓位
		if (StrUtils.isNotNullOrBlank(customer.getPosition())) {
			customer.setPosition(customer.getPosition() + CalculatorUtil.percent(cotMoney,totalMoney));
		} else {
			customer.setPosition(CalculatorUtil.percent(cotMoney,totalMoney));
		}
		customerService.update(customer);
		
		param.setUseMoney(useMoney);//可用资金
		param.setLossMoney(lossMoney);//盈亏金额
		//2、计算所有股票仓位
		//param.setPosition((cotMoney*100)/totalMoney);
		param.setPosition(CalculatorUtil.percent(cotMoney, totalMoney));
		//盈亏比      （市场现价 - 买入价格）/买入价格   *100%
		//double lossPatio = (currentPrice - cotPrice)/cotPrice * 100;
		double lossPatio = CalculatorUtil.divisionKeepDecimal(currentPrice - cotPrice, cotPrice * 100, 2);
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
	 * 批量建仓建仓
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> addMoreStock( CustomerActualCot param, List<Long> ids, List<String> names,List<String> useMoneys, Long currUser) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Long> idList = new ArrayList<Long>();
		List<String> nameList = new ArrayList<String>();
		Integer num ;
		if (ids != null && ids.size() > 0) {
			try {
				for (int i = 0 ; i < ids.size() ; i++) {
					CustomerActualCot actualCot = new CustomerActualCot();
					List<Long> list = cusCotMapper.queryByStockCode(param.getStockCode() ,ids.get(i));
					Customer customer = customerService.queryById(ids.get(i));
					actualCot.setCustomerId(ids.get(i));
					if (StrUtils.isNotNullOrBlank(names)) {
						actualCot.setCustomerName(names.get(i));
					}
					if (StrUtils.isNotNullOrBlank(useMoneys)) {
						if (StrUtils.isNotNullOrBlank(useMoneys.get(i))) {
							actualCot.setUseMoney(Double.valueOf(useMoneys.get(i)));
							num =(int)Math.floor(param.getPosition() * customer.getTotalMoney() / param.getCotPrice());//交易数量
							param.setCotMoney(param.getCotPrice() * StrUtils.toDouble(num));//交易金额
							param.setCurrentMoney(StrUtils.toDouble(num) * param.getCurrentPrice());//市值
							
							if (param.getCotMoney() > Double.valueOf(useMoneys.get(i))) {//可用资金不足
								idList.add(ids.get(i));
								nameList.add(names.get(i));
								System.out.println("可用资金不足，建仓失败！");
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
					}
					actualCot.setDirectionType(0);//建仓
					actualCot.setStockCode(param.getStockCode());
					actualCot.setStockName(param.getStockName());
					actualCot.setCotPrice(param.getCotPrice());
					actualCot.setCotNum(param.getCotNum());
					actualCot.setCotMoney(param.getCotMoney());//交易金额
					actualCot.setCreateTime(new Date());
					actualCot.setCurrentPrice(param.getCurrentPrice());
					actualCot.setCurrentMoney(param.getCurrentMoney());//当前市值
					actualCot.setUpdateBy(currUser);
					//循环修改数据
					this.addStock(actualCot);
					//3、更新持仓表
					Customer cus = customerService.queryById(ids.get(i));
					if (StrUtils.isNotNullOrBlank(cus)) {
						this.updatePosition(cus);
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
	 * 买入
	 * @param param
	 * @return
	 */
	public CustomerActualCot buyStock(CustomerActualCot param) {
		if (StrUtils.isNullOrBlank(param.getUseMoney())) {
			Assert.notNull(null ,"资金未设定，建仓失败");
		}
		double money = param.getUseMoney();
		if (money <= 0) {
			Assert.notNull(null ,"资金不足(或未设定)，建仓失败");
		}
		Customer customer = customerService.queryById(param.getCustomerId());
		//查询当前买入股票信息
		List<Long> list = cusCotMapper.queryByStockCode(param.getStockCode() ,param.getCustomerId());
		CustomerActualCot actualCot = null;
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
			Assert.notNull(null , "可用资金不足，买入失败！");
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
		//交易记录的盈亏金额
		param.setLossMoney(lossMoney);
		saveCustomerLog(param ,null);//1--买入
		
		//2、更新资金表可用资金
		useMoney = useMoney - param.getCotMoney();
		customer.setUseMoney(useMoney);
		if (actualCot.getCotNum().intValue() == 0) {
			customer.setStockNum(customer.getStockNum() + 1);
		}
		//更新总仓位
		if (StrUtils.isNotNullOrBlank(customer.getPosition())) {
			customer.setPosition(customer.getPosition() + CalculatorUtil.percent(cotMoney,customer.getTotalMoney()));
		} else {
			customer.setPosition(CalculatorUtil.percent(cotMoney,customer.getTotalMoney()));
		}
		customerService.update(customer);
		
		param.setUseMoney(useMoney);//可用资金
		//当前总持股数
		param.setCotNum(StrUtils.toLong(cotNum + actualCot.getCotNum().intValue()));
		//交易总金额及成本总金额
		param.setCotMoney(cotMoney + actualCot.getCotMoney());
		//成本价  = 总成本/总持仓
		double costPrice = CalculatorUtil.divisionKeepDecimal(cotMoney + actualCot.getCotMoney() , StrUtils.toDouble(cotNum + actualCot.getCotNum().intValue()) ,2);
		//四舍五入保留两位小数点
		param.setCotPrice(costPrice);
		//盈亏金额
		param.setLossMoney(param.getCurrentPrice() * param.getCotNum() - param.getCotMoney());
		//仓位比
		double position = CalculatorUtil.percent(param.getCotMoney(),customer.getTotalMoney());
		param.setPosition(position);
		//盈亏比      （市场现价 - 成本价格）/成本价格   *100%
		//double lossPatio = (currentPrice - cotPrice)* 100/cotPrice ;
		double lossPatio = CalculatorUtil.divisionKeepDecimal((currentPrice - cotPrice)* 100,cotPrice ,2);
		param.setLossPatio(lossPatio);
		param.setCurrentMoney(currentPrice * Double.valueOf(param.getCotNum().intValue())); 
		//3、更新持仓表
		this.updatePosition(customer);
		return super.update(param);
		
	}
	
	/**
	 * 
	 * 卖出
	 * @param param
	 * @return
	 */
	public CustomerActualCot sellStock(CustomerActualCot param) {
		
		Customer customer = customerService.queryById(param.getCustomerId());
		CustomerActualCot actualCot = null;
		//查询所持有该股票
		List<Long> list = cusCotMapper.queryByStockCode(param.getStockCode() ,param.getCustomerId());
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
		double lossMoney = cotMoney - Double.valueOf(cotNum) * actualCot.getCotPrice();
		//可用资金
		double useMoney = param.getUseMoney();
		if (cotPrice <=0 || cotMoney <= 0 || cotNum <= 0 || currentPrice < 0 ) {
			Assert.notNull(null , "参数错误，请重新填写");
		}
		if ( (actualCot.getCotNum().intValue() - cotNum) < 0 ) {
			
			Assert.notNull(null , "交易数量错误，请重新填写");
		} else if ( (actualCot.getCotNum().intValue() - cotNum) > 0 ) {//卖出
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
			param.setLossMoney(param.getCurrentPrice() * param.getCotNum() - param.getCotMoney());
			//仓位比
			param.setPosition( CalculatorUtil.percent(param.getCotMoney(),customer.getTotalMoney() ));
			//盈亏比      （市场现价 - 买入价格）/买入价格   *100%
			//double lossPatio = (currentPrice - param.getCotPrice()) * 100 / param.getCotPrice() ;
			double lossPatio = CalculatorUtil.divisionKeepDecimal((currentPrice - param.getCotPrice()) * 100, param.getCotPrice(), 2);
			param.setLossPatio(lossPatio);
			param.setCurrentMoney(currentPrice * Double.valueOf(param.getCotNum().intValue())); 
			//更新总仓位
			customer.setPosition(customer.getPosition() - CalculatorUtil.percent(Double.valueOf(cotNum) * actualCot.getCotPrice() , customer.getTotalMoney()));
		
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
			customer.setPosition(customer.getPosition() - CalculatorUtil.percent(actualCot.getCotMoney(),customer.getTotalMoney()));
			//盈利金额
			if (StrUtils.isNotNullOrBlank(customer.getLossMoney())) {
				customer.setLossMoney(param.getLossMoney() + customer.getLossMoney());//更新盈利金额
			}else {
				customer.setLossMoney(param.getLossMoney());
			}
			//清仓减少一条持股类型数
			if (customer.getStockNum() > 1) {
				customer.setStockNum(customer.getStockNum() - 1);
			}else {
				customer.setStockNum(0);
			}
			
		}
		
		//2、更新资金表可用资金
		useMoney = useMoney + cotMoney;
		customer.setUseMoney(useMoney);
		//若所持股票为空，仓位置空
		if (customer.getStockNum() == 0) {
			customer.setPosition(0.0);
		}
		customerService.update(customer);
		
		return super.update(param);
	}
	
	/**
	 * 
	 * 保存仓位操作记录
	 * 
	 */
	public void saveCustomerLog(CustomerActualCot param , CustomerActualCot actCot){
		
		CustomerCotOperationLog customerCotLog = new CustomerCotOperationLog();
		if (StrUtils.isNotNullOrBlank(actCot) && param.getDirectionType() == 2 ) {//卖出时  成本价与交易价区别开来
			customerCotLog.setCotPrice(actCot.getCotPrice());//成本价格
			customerCotLog.setCotMoney(actCot.getCotPrice() * param.getCotNum());//成本金额
			customerCotLog.setTradePrice(param.getCotPrice());//交易价格
			customerCotLog.setTradeMoney(param.getCotMoney());//交易金额
		} else {
			customerCotLog.setCotPrice(param.getCotPrice());//成本价格
			customerCotLog.setCotMoney(param.getCotMoney());//成本金额
			customerCotLog.setTradePrice(param.getCotPrice());//交易价格
			customerCotLog.setTradeMoney(param.getCotMoney());//交易金额
		}
		customerCotLog.setCustomerActualCotId(param.getId());//所持股票id
		customerCotLog.setStockCode(param.getStockCode());//股票代码
		customerCotLog.setStockName(param.getStockName());//股票名称
		customerCotLog.setTradeNum(param.getCotNum());//交易数量
		customerCotLog.setThenLossMoney(param.getLossMoney());//盈亏金额
		customerCotLog.setDirectionType(param.getDirectionType());//操作类型
		customerCotLog.setCreateTime(new Date());;//操作时间
		customerCotLog.setCreateBy(param.getUpdateBy());//操作人id
		
		customerCotLogService.update(customerCotLog);
	}

	
	/**
	 * 
	 * 删除股票信息时重新计算可用资金
	 * @param param
	 */
	public void delStock(List<Long> ids , Long currUser) {
		if (StrUtils.isNotNullOrBlank(ids) && ids.size() == 1) {
			CustomerActualCot actualCot  = this.queryById(ids.get(0));
			Customer customer = customerService.queryById(actualCot.getCustomerId());
			customer.setUseMoney(customer.getUseMoney() + actualCot.getCotMoney());
			if (StrUtils.isNotNullOrBlank(customer) && customer.getStockNum() > 1) {
				customer.setStockNum(customer.getStockNum() - 1);
			}else {
				customer.setStockNum(0);
			}
			double db = customer.getPosition() - actualCot.getPosition();
			if (db >= 0) {
				customer.setPosition(db);//更新总仓位
			} else {
				customer.setPosition(0d);//更新总仓位
			}
			customer.setUpdateBy(currUser);
			customerService.update(customer);
			//保存删除股票信息记录
			CustomerLog log = new CustomerLog();
			log.setCreateBy(currUser);
			log.setCreateTime(new Date());
			log.setUpdateBy(currUser);
			log.setUpdateTime(new Date());
			log.setCustomerId(actualCot.getCustomerId());
			log.setCustomerName(actualCot.getCustomerName());
			log.setType("删除实盘股票信息");
			logService.update(log);
		}
		if (StrUtils.isNotNullOrBlank(ids) && ids.size() > 1) {
			for (Long id : ids) {
				CustomerActualCot actualCot  = this.queryById(id);
				Customer customer = customerService.queryById(actualCot.getCustomerId());
				customer.setUseMoney(customer.getUseMoney() + actualCot.getCotMoney());
				if (customer.getStockNum() > 1) {
					customer.setStockNum(customer.getStockNum() - 1);
				}else {
					customer.setStockNum(0);
				}
				double db = customer.getPosition() - actualCot.getPosition();
				if (db >= 0) {
					customer.setPosition(db);//更新总仓位
				} else {
					customer.setPosition(0d);//更新总仓位
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
	 * 更新所有客户股票信息
	 * @param flow
	 */
	public void updateAllCustomerStock() {
		StockApi stockApi = new StockApi();
		//更新实盘股票信息
		List<Long> cusList = this.queryAll();
		if (StrUtils.isNotNullOrBlank(cusList) && cusList.size() > 0 ) {
			for (Long ids : cusList) {
				//获取的当前股市详情
				CustomerActualCot customerCot = this.queryById(ids);
				if (StrUtils.isNotNullOrBlank(customerCot.getStockCode()) && customerCot.getCotNum().intValue() > 0) {
					String currentPrice = stockApi.getCurrentByCode(customerCot.getStockCode()).get(0);
					customerCot.setCurrentPrice(Double.valueOf(currentPrice.toString()));
					Double currentMoney = Double.valueOf(currentPrice) * customerCot.getCotNum();
					customerCot.setCurrentMoney(currentMoney);
					customerCot.setLossMoney(currentMoney-customerCot.getCotMoney());//盈亏金额
					//Double lossPatio = (Double.valueOf(currentPrice.toString())-customerCot.getCotPrice()) * 100 / customerCot.getCotPrice();
					Double lossPatio = CalculatorUtil.divisionKeepDecimal((Double.valueOf(currentPrice.toString())-customerCot.getCotPrice()) * 100, customerCot.getCotPrice(), 2);
					customerCot.setLossPatio(lossPatio);//盈亏比
					this.update(customerCot);
					
				}
				
			}
		}
		//更新虚盘股票信息
		List<Long> teacherList = teacherCotService.queryAll();
		if (StrUtils.isNotNullOrBlank(teacherList) && teacherList.size() > 0 ) {
			for (Long ids : teacherList) {
				//获取的当前股市详情
				TeacherDirectiveCot teacherCot = teacherCotService.queryById(ids);
				if (StrUtils.isNotNullOrBlank(teacherCot.getStockCode()) && teacherCot.getCotNum().intValue() > 0) {
					String currentPrice = stockApi.getCurrentByCode(teacherCot.getStockCode()).get(0);
					teacherCot.setCurrentPrice(Double.valueOf(currentPrice.toString()));
					Double currentMoney = Double.valueOf(currentPrice) * teacherCot.getCotNum();
					teacherCot.setCurrentMoney(currentMoney);
					teacherCot.setLossMoney(currentMoney-teacherCot.getCotMoney());//盈亏金额
					//Double lossPatio = (Double.valueOf(currentPrice.toString())-teacherCot.getCotPrice()) * 100 / teacherCot.getCotPrice();
					Double lossPatio = CalculatorUtil.divisionKeepDecimal((Double.valueOf(currentPrice.toString())-teacherCot.getCotPrice()) * 100, teacherCot.getCotPrice(), 2);
					teacherCot.setLossPatio(lossPatio);//盈亏比
					teacherCotService.update(teacherCot);
				}
				
			}
		}
	}

	/**
	 * 查询所有实盘股票信息
	 * @return
	 */
	public List<Long> queryAll() {
		return cusCotMapper.queryAll();
	}


	/**
	 * 实时更新股票信息
	 * @param stockCode
	 * @return
	 */
	public List<String> qureyPriceByStock(String stockCode) {
		StockApi stockApi = new StockApi();
		return stockApi.getCurrentByCode(stockCode);
	}
	
	
	/**
	 * 当资金量改变时，更新仓位比
	 */
	public void updatePosition (Customer param) {
		double db = 0 ;//总仓位
		if (StrUtils.isNotNullOrBlank(param)) {
			List<Long> list =cusCotMapper.queryByCustomerId(param.getId());
			for (Long id : list) {
				CustomerActualCot customerCot = this.queryById(id);
				if (StrUtils.isNotNullOrBlank(customerCot) && customerCot.getCotNum().intValue() > 0) {
					double position = CalculatorUtil.percent(customerCot.getCotMoney(),param.getTotalMoney());
					customerCot.setPosition(position);
					customerCot = this.update(customerCot);
					db = db + customerCot.getPosition();
				}
			}
			param.setPosition(db);
			customerService.update(param);
		}
	}

	/**
	 * 股票维度统计
	 * @param param
	 * @param currUser
	 * @return
	 */
	public Page<CustomerActualCot> queryGroupStock(Map<String, Object> param, Long currUser) {
		int pageSize = StrUtils.toInt(param.get("pageSize"));//每页条数
		int pageNum = StrUtils.toInt(param.get("pageNum"));
		Page<Long> pages = getPage(param);
		List<CustomerActualCot> actualCotList = cusCotMapper.queryGroupStock(pages,param);
		//模拟分页
		Page<CustomerActualCot> page = new Page<CustomerActualCot>();
		page.setRecords(actualCotList);
		page.setCurrent(pageNum);
		page.setTotal(pages.getTotal());
		page.setSize(pageSize);
		return page;
	}
	
	/**
	 * 客户可用资金校正
	 * @param param
	 */
	public void updateUserMoney(Customer param){
		double cotMoney = 0 ;//成本金额
		if (StrUtils.isNotNullOrBlank(param)) {
			List<Long> list =cusCotMapper.queryByCustomerId(param.getId());
			for (Long id : list) {
				//1、查询该客户持有的股票信息
				CustomerActualCot customerCot = this.queryById(id);
				if (StrUtils.isNotNullOrBlank(customerCot) && customerCot.getCotNum().intValue() > 0) {
					//2、合计用户所持股票成本金额
					cotMoney = cotMoney + customerCot.getCotMoney();
				} else if(StrUtils.isNotNullOrBlank(customerCot) && customerCot.getCotNum().intValue() == 0){
					cotMoney = cotMoney + 0.00;
				}
			}
			if (StrUtils.isNullOrBlank(param.getLossMoney())) {
				param.setLossMoney(0.00);
			}
			//3、用户可用资金= 投入资金 + 阴亏金额 - 成本金额
			double userMoney = param.getTotalMoney() + param.getLossMoney() - cotMoney;
			param.setUseMoney(userMoney);
			customerService.update(param);
		}
	}
}
