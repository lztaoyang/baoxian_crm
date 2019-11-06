package com.lazhu.product.insuranceproduct.mapper;

import java.util.List;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.product.insuranceproduct.entity.InsuranceProduct;
/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2017-06-20
 */
public interface InsuranceProductMapper extends BaseMapper<InsuranceProduct> {

	/** 所有的产品 **/
	List<Long> queryAll();
	
	/** 所有的上架产品 **/
	List<Long> queryByIsPutaway();
	
	/** 所有的上架长险产品 **/
	List<Long> queryLongTermByIsPutaway();

}
