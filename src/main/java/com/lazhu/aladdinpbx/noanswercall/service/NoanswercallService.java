package com.lazhu.aladdinpbx.noanswercall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.aladdinpbx.noanswercall.entity.Noanswercall;
import com.lazhu.aladdinpbx.noanswercall.mapper.NoanswercallMapper;
import com.lazhu.core.util.ComplexMD5Util;
import com.lazhu.crm.Constant;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.resource.entity.Resource;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.ecp.utils.StrUtils;

/**
 * <p>
 * 未接电话记录表 服务实现类
 * </p>
 *
 * @author hz
 * @since 2017-12-18
 */
@Service
@CacheConfig(cacheNames = "noanswercall")
public class NoanswercallService extends BusinessBaseService<Noanswercall> {
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	ResourceService resourceService;

	/**
	 * 查询未接电话记录及客户相关
	 * 
	 * @param param
	 * @return
	 */
	public Page<Noanswercall> queryListCustomer(Map<String, Object> param) {
		Page<Long> page = getPage(param);
		page.setRecords(((NoanswercallMapper)mapper).selectIdPage(page, param));
		Page<Noanswercall> noanswercallPage = super.getPage(page);
		if (noanswercallPage != null && noanswercallPage.getRecords() != null && noanswercallPage.getRecords().size() > 0) {
			Customer customer = null;
			Resource resource = null;
			for (Noanswercall item : noanswercallPage.getRecords()) {
				//去除首位0
				String mobile = StrUtils.prefixDelZero(item.getNasCallederid());
				mobile = StrUtils.hideMobile(mobile);
				//手机号
				item.setNasCallederid(mobile);
				String md5Mobile = ComplexMD5Util.MD5Encode(mobile);
				Long userId = StrUtils.toLong(item.getNasAgentid());
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
						//item.setServerName(customer.getServerName());
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
		return noanswercallPage;
	}

	/**
	 * 查询最新未接通话记录
	 * @param noanswerMaxId
	 * @return
	 */
	public List<Long> queryByMaxId(Long noanswerMaxId) {
		return ((NoanswercallMapper)mapper).queryByMaxId(noanswerMaxId);
	}
	
}
