package com.lazhu.ad.channelcost.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.lazhu.ad.channelcost.entity.ChannelCost;
import com.lazhu.ad.cost.entity.Cost;
import com.lazhu.ad.cost.service.CostService;
import com.lazhu.core.base.BaseService;
import com.lazhu.core.util.DataUtil;
import com.lazhu.core.util.DateUtil;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author phong
 * @since 2017-11-02
 */
@Service
@CacheConfig(cacheNames = "channelCost")
public class ChannelCostService extends BaseService<ChannelCost> {
	
	@Autowired
	CostService costService;
	/**
	 * 删除日推广总费用表相关的推广费渠道明细表
	 * @param costId 日总推广费用ID
	 * @param userId 
	 */
	public void delByCostId(Long costId, Long userId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("costId", costId);
		List<ChannelCost> channels= super.queryList(params);
		for(ChannelCost channel: channels) {
			super.del(channel.getId(), userId);
		}
	}
	
	/**
	 * 保存推广费
	 */
	@Transactional
	public Cost updateChannel(ChannelCost record) {
		Cost cost = null;
		try {
			Assert.notNull(record.getCostTime(), "日期");
			// 新增推广明细
			if (record.getId() == null) {
				// 查询录入的推广日期是否有推广费录入主表记录
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("costTime", DateUtil.format(record.getCostTime()));
				List<Cost> list = costService.queryList(map);
				// 有主表记录
				if(DataUtil.isNotEmpty(list)) {
					// 取出主表记录
					cost = list.get(0);
					// 新增直接增加费用
					BigDecimal newCost = cost.getTotalCost().add(record.getCost());
					cost.setTotalCost(newCost);
					
				} 
				// 无主表记录-新增主表记录
				else {
					cost = new Cost();
					// 推广日期
					cost.setCostTime(record.getCostTime());
					// 推广费用
					cost.setTotalCost(record.getCost());
					
				}
				// 更新主表记录
				cost = costService.update(cost);
				// 把主表记录ID塞入明细表中
				record.setCostId(cost.getId());
			} 
			// 修改推广明细
			else {
				// 通过明细表中的主表ID查询出主表
				cost = costService.queryById(record.getCostId());
				// 修改先增加新的费用然后减掉老的费用
				BigDecimal newCost = cost.getTotalCost().add(record.getCost()).subtract(record.getOldCost());
				cost.setTotalCost(newCost);
				// 更新总费用
				cost = costService.update(cost);
			}
			// 更新明细
			record = super.update(record);
		} catch (Exception e) { 
			throw new RuntimeException(e.getMessage(), e);
		}
		
		return cost;
	}
	
	/**
	 * 删除推广明细
	 */
	@Transactional
	public Cost delChannel(Long id) {
		Cost cost = null;
		try {
			ChannelCost record = super.queryById(id);
			// 删除明细
			super.delete(id);
			// 先查询还有不有同一主表下的记录，如果没有则把主表记录一起删了
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("costId", record.getCostId());
			List<ChannelCost> records = super.queryList(map);
			if (DataUtil.isEmpty(records)) {
				// 删主表
				costService.delete(record.getCostId());
			} else {
				// 查询主表记录
				cost = costService.queryById(record.getCostId());
				// 减掉删除的总费用
				cost.setTotalCost(cost.getTotalCost().subtract(record.getCost()));
				// 更新总费用
				costService.update(cost);
			}
		} catch (Exception e) { 
			throw new RuntimeException(e.getMessage(), e);
		}
		
		return cost;
	}
}
