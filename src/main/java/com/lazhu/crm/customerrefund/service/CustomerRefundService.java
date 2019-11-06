package com.lazhu.crm.customerrefund.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.dingtalk.chatbot.message.MarkdownMessage;
import com.lazhu.core.util.DataUtil;
import com.lazhu.core.util.DateUtil;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.customerrefund.entity.CustomerRefund;
import com.lazhu.crm.customerrefund.mapper.CustomerRefundMapper;
import com.lazhu.crm.signapply.entity.SignApply;
import com.lazhu.crm.signapply.service.SignApplyService;
import com.lazhu.ecp.utils.DingUtil;
import com.lazhu.ecp.utils.StrUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
@Service
@CacheConfig(cacheNames = "customerRefund")
public class CustomerRefundService extends BusinessBaseService<CustomerRefund> {

	@Autowired
	private SignApplyService signApplyService;
	
	@Autowired
	private CustomerService customerService;
	/**
	 * 退款处理
	 */
	public CustomerRefund update(CustomerRefund record) {
		// 查询出退款的订单数据
		SignApply sa = signApplyService.queryById(record.getSignApplyId());
		// 退款处理完成并且成功退款时要会写订单表的是否成功退款字段和退款类型字段
		if (DataUtil.isNotEmpty(record.getDealerStatus()) && record.getDealerStatus().intValue() == 1) {
			// 更新订单表
			signApplyService.update(sa);
		}
		record = super.update(record);
		// 发送钉钉消息
		try {
			sendDDMsg(sa, record);
		} catch (Exception e) {
			System.out.println("钉钉消息群，处理退款，发送失败!");
		}
		return record;
	}
	
	public List<Long> queryByCustomerId(Long customerId) {
		return ((CustomerRefundMapper)mapper).queryByCustomerId(customerId);
	}
	
	/**
	 * 退款订单查询 
	 * @param param
	 * @return
	 */
	public Page<Map<String, String>> queryRefundSignApply(Map<String, Object> param) {
		//手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		super.setMd5MobileWhenMobile(param);
		Page<Map<String, String>> page = getPageMap(param);
		page.setRecords(((CustomerRefundMapper) mapper).queryRefundSignApply(page, param));
		return page;
	}
	
	/**
	 * 退款订单查询
	 * @param param
	 * @return
	 */
	public Page<Map<String, String>> list4zx(Map<String, Object> param) {
		Page<Map<String, String>> page = getPageMap(param);
		page.setRecords(((CustomerRefundMapper) mapper).list4zx(page, param));
		return page;
	} 
	
	/**
	 * 10天未处理的退款申请发送钉钉客服群消息通知
	 */
	public void remind() {
		// 查询10天未处理的退款申请数据
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("dealerStatus", 0);// 待处理数据
		param.put("remind", "true");// 待处理数据
		List<CustomerRefund> list = super.queryList(param);
		if (DataUtil.isNotEmpty(list)) {
			for (CustomerRefund record : list) {
				// 查询出退款的订单数据
				SignApply sa = signApplyService.queryById(record.getSignApplyId());
				if (DataUtil.isNotEmpty(sa)) {
					try {
						sendDDMsg(sa, record);
					} catch (Exception e) {
						System.out.println("钉钉消息群，10天未处理退款，发送失败!");
					}
				}
			}
		}
	}
	
	/**
	 * 发送钉钉消息
	 * @param sa
	 * @param record
	 */
	private void sendDDMsg(SignApply sa, CustomerRefund record) {
		// 退款处理完成并且成功退款时要会写订单表的是否成功退款字段和退款类型字段
		if (DataUtil.isNotEmpty(record.getDealerStatus()) && record.getDealerStatus().intValue() == 1) {
			MarkdownMessage message = new MarkdownMessage();
			message.setTitle("CRM退款已处理！");
	        message.add(MarkdownMessage.getHeaderText(5, "CRM退款已处理！"));
	        message.add(MarkdownMessage.getBoldText("处理客户："));
	        message.add(sa.getCustomerName());
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("受理订单："));
	        message.add(sa.getInsuranceName() + "(" + sa.getAmount() + ")");
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("申请时间："));
	        message.add(DateUtil.format(record.getCreateTime(), "yyyy年MM月dd日"));
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("申请退款说明："));
	        message.add(sa.getInsuranceName() + "(" + record.getRemark() + ")");
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("签单时间："));
	        message.add(DateUtil.format(sa.getAuditTime(), "yyyy年MM月dd日"));
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("市场部归属："));
	        message.add(sa.getBelong());
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("是否成功退款："));
	        message.add(DataUtil.isNotEmpty(record.getIsSuccess()) && record.getIsSuccess().intValue() == 1? "是" : "否");
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("退款类型："));
	        message.add(fmtRefundType(record.getRefundType()));
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("处理结果："));
	        if (StrUtils.isNullOrBlank(record.getDealerResult())) {
	        	record.setDealerResult("无");
			}
	        message.add(record.getDealerResult());
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("处理时间："));
	        message.add(DateUtil.format(new Date(), "MM月dd日 HH:mm"));
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("受理客服："));
	        message.add(record.getDealerName());
			DingUtil.sendGroupMesg("dd.group.kefu", message);
		} 
		// 待处理
		else {
			MarkdownMessage message = new MarkdownMessage();
			message.setTitle("CRM退款待处理申请！");
	        message.add(MarkdownMessage.getHeaderText(5, "CRM退款待处理申请！"));
	        message.add(MarkdownMessage.getBoldText("申请客户："));
	        message.add(sa.getCustomerName());
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("受理订单："));
	        message.add(sa.getInsuranceName() + "(" + sa.getAmount() + ")");
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("签单时间："));
	        message.add(DateUtil.format(sa.getAuditTime(), "yyyy年MM月dd日"));
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("市场部归属："));
	        message.add(sa.getBelong());
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("申请时间："));
	        message.add(DateUtil.format(DataUtil.isEmpty(record.getCreateTime()) ? new Date() : record.getCreateTime(), "MM月dd日 HH:mm"));
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("申请退款说明："));
	        if (StrUtils.isNullOrBlank(record.getRemark())) {
	        	record.setRemark("无");
			}
	        message.add(record.getRemark());
	        message.add("\n");
	        message.add(MarkdownMessage.getBoldText("受理客服："));
	        message.add(record.getDealerName());
			DingUtil.sendGroupMesg("dd.group.kefu", message);
		}
	}
	
	private String fmtRefundType(Integer refundType) {
		if (DataUtil.isEmpty(refundType)) {
			return "";
		} else if (refundType.intValue() == 0) {
			return "服务期前退款";
		} else if (refundType.intValue() == 1) {
			return "服务期中退款";
		} else if (refundType.intValue() == 2) {
			return "服务期后退款";
		} else {
			return "未知";
		}
	}
}
