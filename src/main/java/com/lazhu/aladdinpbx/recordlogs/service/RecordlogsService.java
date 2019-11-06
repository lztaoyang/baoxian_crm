package com.lazhu.aladdinpbx.recordlogs.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.aladdinpbx.recordlogs.entity.Recordlogs;
import com.lazhu.aladdinpbx.recordlogs.mapper.RecordlogsMapper;
import com.lazhu.core.util.ComplexMD5Util;
import com.lazhu.crm.Constant;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.resource.entity.Resource;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.crm.serverrecordmobile.service.ServerRecordMobileService;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;

/**
 * <p>
 * 电话录音记录表 服务实现类
 * </p>
 *
 * @author hz
 * @since 2017-12-02
 */
@Service
@CacheConfig(cacheNames = "recordlogs")
public class RecordlogsService extends BusinessBaseService<Recordlogs> {
	
	@Autowired//客服通话记录
	ServerRecordMobileService serverRecordMobileService;

	@Autowired//会员
	CustomerService customerService;
	
	@Autowired//业务
	ResourceService resourceService;
	
	@Autowired
	RecordlogsMapper recordlogsMapper;
	
	/**
	 * 通过录音文件名查询一个唯一的实体
	 * @param callFile
	 * @return
	 */
	public Recordlogs queryByCallFile (String callFile) {
		List<Long> ids = recordlogsMapper.queryByCallFile(callFile);
		Recordlogs recordlogs = null;
		if (null != ids && ids.size() > 0) {
			if (ids.size() == 1) {
				recordlogs = this.queryById(ids.get(0));
			} else {
				OperationLogTool.operationLog(Constant.ERROR_LOG, callFile + "，在aladdinpbx_recordlogs匹配到多条记录");
			}
		} else {
			OperationLogTool.operationLog(Constant.ERROR_LOG, callFile + "，在aladdinpbx_recordlogs未匹配到记录");
		}
		return recordlogs;
	}
	
	/**
	 * 通过录音文件查询一个唯一的实体ID
	 * @param callFile
	 * @return
	 */
	public Long queryLengthByCallFile (String callFile) {
		List<Long> recordLengths = recordlogsMapper.queryLengthByCallFile(callFile);
		if (null != recordLengths && recordLengths.size() > 0) {
			if (recordLengths.size() == 1) {
				return recordLengths.get(0);
			} else {
				OperationLogTool.operationLog(Constant.ERROR_LOG, callFile + "，在aladdinpbx_recordlogs匹配到多条记录");
			}
		} else {
			OperationLogTool.operationLog(Constant.ERROR_LOG, callFile + "，在aladdinpbx_recordlogs未匹配到记录");
		}
		return null;
	}

	/**
	 * 查询已接电话录音记录表
	 * @param param
	 * @return
	 */
	public Page<Recordlogs> answerList(Map<String, Object> param) {
		Page<Long> page = getPage(param);
		page.setRecords(recordlogsMapper.selectIdPage(page, param));
		Page<Recordlogs> recordlogsPage = super.getPage(page);
		if (recordlogsPage != null && recordlogsPage.getRecords() != null && recordlogsPage.getRecords().size() > 0) {
			Customer customer = null;
			Resource resource = null;
			for (Recordlogs item : recordlogsPage.getRecords()) {
				//去除首位0
				String mobile = StrUtils.prefixDelZero(item.getRecCallederid());
				mobile = StrUtils.hideMobile(mobile);
				//手机号
				item.setRecCallederid(mobile);
				String md5Mobile = ComplexMD5Util.MD5Encode(mobile);
				//客服接听时的备注
				String serverRecord = serverRecordMobileService.queryServerRecordByCallFile(item.getRecRecordpath() + item.getRecRecordfile());
				item.setRemark(serverRecord);
				Long userId = StrUtils.toLong(item.getRecAgentid());
				if (null != userId) {
					item.setServerName(userService.queryById(userId).getAccount());
				}
				//
				if (StrUtils.isNotNullOrBlank(md5Mobile)) {
					customer = customerService.queryByMd5Mobile(md5Mobile);
					if (StrUtils.isNotNullOrBlank(customer)) {
						item.setCustomerId(customer.getId());
						item.setCustomerName(customer.getName());
						item.setIsClub(1);
						item.setFlowId(Constant.FLOW_CJ);
						item.setEnterTime(customer.getEnterTime());
						//item.setServerName(customer.getServerName());特殊处理
						//
						String directorName = customer.getDirectorName()==null?"未分配":customer.getDirectorName();
						String managerName	= customer.getManagerName()==null?"未分配":customer.getManagerName();
						String salerName	= customer.getSalerName()==null?"未分配":customer.getSalerName();
						item.setBelong(directorName+"-"+managerName+"-"+salerName);
						item.setSignNum(customer.getInsureNum());
					} else {
						resource = resourceService.queryByMd5Mobile(md5Mobile);
						if (StrUtils.isNotNullOrBlank(resource)) {
							item.setCustomerId(resource.getId());
							item.setCustomerName(resource.getName());
							item.setIsClub(0);
							item.setFlowId(resource.getFlowId());
							item.setEnterTime(resource.getEnterTime());
							//
							String directorName = resource.getDirectorName()==null?"未分配":resource.getDirectorName();
							String managerName	= resource.getManagerName()==null?"未分配":resource.getManagerName();
							String salerName	= resource.getSalerName()==null?"未分配":resource.getSalerName();
							item.setBelong(directorName+"-"+managerName+"-"+salerName);
						}
					}
				}
			}
		}
		return recordlogsPage;
	}

	/**
	 * 查询最新已经通话记录
	 * @param answerMaxId
	 * @return
	 */
	public List<Long> queryAnswerByMaxId(Long answerMaxId) {
		return recordlogsMapper.queryAnswerByMaxId(answerMaxId);
	}
	
}
