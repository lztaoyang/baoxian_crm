package com.lazhu.crm.signapply.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.crm.signapply.entity.SignApply;
/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
public interface SignApplyMapper extends BaseMapper<SignApply> {
	

	/**查询历史审核通过的次数**/
	Long queryAuditNum(@Param("customerId") Long customerId);

	/**查询签单（通过会员ID）**/
	List<Long> queryByCustomerId(@Param("customerId") Long customerId);

	/**查询审核通过签单（通过会员ID）**/
	List<Long> queryDealByCustomerId(@Param("customerId") Long customerId);

	/** 统计某天录入的资源成功总签单数 **/
	Long applyNumByDate(@Param("someday") Date someday);

	/** 统计某天总签单金额 **/
	Double sumAmountByDate(@Param("someday") Date someday);

	/** 统计市场部的资源成交数 **/
	Long enterNumByFromInfoAndBusiness(@Param("fromInfoList") List<Long> fromInfoList, @Param("cm") Map<String, Object> params);
	//Long enterNumByFromInfoAndBusiness(@Param("fromInfo") Long fromInfo, @Param("cm") Map<String, Object> params);
	/** 统计市场部的资源成交数 **/
	Long applyNumByFromInfoAndBusiness(@Param("fromInfoList") List<Long> fromInfoList, @Param("cm") Map<String, Object> params);
	//Long applyNumByFromInfoAndBusiness(@Param("fromInfo") Long fromInfo, @Param("cm") Map<String, Object> params);
	/** 统计市场部的资源成交金额 **/
	Double insureMoneyByEnterAndBusiness(@Param("fromInfoList") List<Long> fromInfoList, @Param("cm") Map<String, Object> params);
	//Double insureMoneyByEnterAndBusiness(@Param("fromInfo") Long fromInfo, @Param("cm") Map<String, Object> params);

	List<Long> selectFirstByCustomerId(@Param("customerId") Long customerId);

	List<Long> selectAllSuccess();
	List<Long> selectAllSign();
	List<Long> selectAll();

	
	/** 推广每日新客统计 **/
	
	Long effectiveNumFromInfo(@Param("fromInfoList") List<Long> fromInfoList,@Param("someday") Date promotionDate);
	Long signNumFromInfo(@Param("fromInfoList") List<Long> fromInfoList,@Param("someday") Date promotionDate);
	Double sumAmountFromInfo(@Param("fromInfoList") List<Long> fromInfoList,@Param("someday") Date promotionDate);
	Long daySignNumFromInfo(@Param("fromInfoList") List<Long> fromInfoList,@Param("someday") Date promotionDate);
	Double daySignPremiumFromInfo(@Param("fromInfoList") List<Long> fromInfoList,@Param("someday") Date promotionDate);
	
	Double isLongTermCommission(@Param("isLongTerm") Integer isLongTerm,@Param("someday") Date promotionDate);
	
	
	Long customerNumByProductId(@Param("productId") Long productId);
	Long signNumByProductId(@Param("productId") Long productId);
	Double sumAmountByProductId(@Param("productId") Long productId);

	/** 统计已成功升级签单次数 **/
	Integer countUpgradeNum(@Param("customerId") Long customerId);
	
	

	// 报表中心-合规统计排名-经理保费排名
	List<Map<String, Object>> queryAmountByManager(@Param("cm") Map<String, Object> map);
	// 报表中心-合规统计排名-总监保费排名
	List<Map<String, Object>> queryAmountByDirector(@Param("cm") Map<String, Object> map);
	// 报表中心-合规统计排名-总经理保费排名
	List<Map<String, Object>> queryAmountByMinister(@Param("cm") Map<String, Object> map);
	// 报表中心-合规统计排名-总保费
	Map<String, Object> queryAmount(@Param("cm") Map<String, Object> map);

	/** 
	 * 业务员当日新单统计
	 * @author ty
	 * @return
	 */
	public List<Map<String, Object>> queryToDaySignNumGroupSalerId(@Param("cm") Map<String, Object> map);
	
	/** 
	 * 经理当日新单统计
	 * @author ty
	 * @return
	 */
	public List<Map<String, Object>> queryToDaySignNumGroupManagerId(@Param("cm") Map<String, Object> map);
	
	/** 
	 * 总监当日新单统计
	 * @author ty
	 * @return
	 */
	public List<Map<String, Object>> queryToDaySignNumGroupDirectorId(@Param("cm") Map<String, Object> map);
	
	/** 
	 * 总经理当日新单统计
	 * @author ty
	 * @return
	 */
	public List<Map<String, Object>> queryToDaySignNumGroupMinisterId(@Param("cm") Map<String, Object> map);
	
	
	/** 今日成交资源数（加保人员） **/
	Integer queryToDaySignNumByUpgraderId(@Param("userId") Long userId);

	/**
	 * 当日新单缴费
	 * @param map
	 * @return
	 */
	public Map<String, Object> queryToDayAmount(@Param("cm") Map<String, Object> map);
	
	/** 
	 * 业务员当日签单统计
	 * @author ty
	 * @return
	 */
	public List<Map<String, Object>> queryToDaySignNumTotalSalerId(@Param("cm") Map<String, Object> map);
	
	/** 
	 * 经理当日签单统计
	 * @author ty
	 * @return
	 */
	public List<Map<String, Object>> queryToDaySignNumTotalManagerId(@Param("cm") Map<String, Object> map);
	
	/** 
	 * 总监当日签单统计
	 * @author ty
	 * @return
	 */
	public List<Map<String, Object>> queryToDaySignNumTotalDirectorId(@Param("cm") Map<String, Object> map);
	
	/** 
	 * 总经理当日签单统计
	 * @author ty
	 * @return
	 */
	public List<Map<String, Object>> queryToDaySignNumTotalMinisterId(@Param("cm") Map<String, Object> map);
	
	
	/**
	 * 当日签单缴费
	 * @param map
	 * @return
	 */
	public Map<String, Object> queryToDayTotalAmount(@Param("cm") Map<String, Object> map);
}
