package com.lazhu.ad.cost.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.ad.channelcost.mapper.ChannelCostMapper;
import com.lazhu.ad.channelcost.service.ChannelCostService;
import com.lazhu.ad.cost.entity.Cost;
import com.lazhu.ad.cost.mapper.CostMapper;
import com.lazhu.core.base.BaseService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author phong
 * @since 2017-11-02
 */
@Service
@CacheConfig(cacheNames = "cost")
public class CostService extends BaseService<Cost> {
	
	@Autowired
	ChannelCostMapper channelCostMapper;// 费用明细
	@Autowired
	ChannelCostService channelService;
	
	/**
	 * 删除推广费用维护
	 */
	@Transactional
	public void del(Long id, Long userId) {
		try {
			// 删除相关的推广渠道明细
			channelService.delByCostId(id, userId);
			// 删除日推广总费记录
			super.del(id, userId);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * 日统计报表
	 * @param param
	 * @return
	 */
	public Page<Map<String, String>> queryReortDay(Map<String, Object> param) {
		Page<Map<String, String>> page = getPageMap(param);
		page.setRecords(((CostMapper) mapper).queryReortDay(page, param));
		return page;
	}
	
	/**
	 * 推广客户明细表
	 * @param param
	 * @return
	 */
	public Page<Map<String, String>> queryReportCustomerDetail(Map<String, Object> param) {
		Page<Map<String, String>> page = getPageMap(param);
		page.setRecords(((CostMapper) mapper).queryReportCustomerDetail(page, param));
		return page;
	}
	
	/**
	 * 渠道统计
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> queryChannelTotal(Map<String, Object> param) {
		return ((CostMapper) mapper).queryChannelTotal(param);
	}
	
	/**
	 * 执行人统计
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> queryExecuterTotal(Map<String, Object> param) {
		return ((CostMapper) mapper).queryExecuterTotal(param);
	}
	
	/**
	 * 市场人员统计
	 * @param param
	 * @return
	 */
	public Page<Map<String, String>> queryUserTotal(Map<String, Object> param) {
		Page<Map<String, String>> page = getPageMap(param);
		page.setRecords(((CostMapper) mapper).queryUserTotal(page, param));
		return page;
	}

	/**
	 * 推广签单明细
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> exportApplyDetail(Map<String, Object> param) {
		return ((CostMapper) mapper).exportApplyDetail(param);
	}
	
}
