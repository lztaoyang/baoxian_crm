package com.lazhu.product.insuranceproduct.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.lazhu.core.base.BaseService;
import com.lazhu.crm.Constant;
import com.lazhu.crm.signapply.service.SignApplyService;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.product.insuranceproduct.entity.InsuranceProduct;
import com.lazhu.product.insuranceproduct.mapper.InsuranceProductMapper;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hz
 * @since 2017-06-20
 */
@Service
@CacheConfig(cacheNames = "insuranceProduct")
public class InsuranceProductService extends BaseService<InsuranceProduct> {
	
	@Autowired
	SignApplyService applyService;
	
	public List<InsuranceProduct> queryList(Map<String, Object> params) {
		List<Long> ids = mapper.selectIdPage(params);
		List<InsuranceProduct> productList = getList(ids);
		return productList;
	}

	public void product() {
		OperationLogTool.operationLog(Constant.AUTO_LOG,"执行产品签单情况统计");
		//获取所有的产品
		List<Long> ids = ((InsuranceProductMapper)mapper).queryAll();
		
		for (Long id : ids) {
			InsuranceProduct product = super.queryById(id);
			//客户数
			Long effectiveNum = applyService.customerNumByProductId(id);
			//总单数
			Long signNum = applyService.signNumByProductId(id);
			//总签单金额
			Double sumAmount = applyService.sumAmountByProductId(id);
			
			product.setEffectiveNum(effectiveNum);
			product.setSignNum(signNum);
			product.setSumAmount(sumAmount);
			//
			super.update(product);
		}
		
	}
	
}
