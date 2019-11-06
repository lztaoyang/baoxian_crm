package com.lazhu.ad.channelcost.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lazhu.ad.channelcost.entity.ChannelCost;
import com.lazhu.core.base.BaseMapper;
/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author phong
 * @since 2017-11-02
 */
public interface ChannelCostMapper extends BaseMapper<ChannelCost> {
	
	List<ChannelCost> selectCostIdPage(@Param("cm") Map<String, Object> params);
	
	// 删除日推广费总表ID相关的渠道明细
	Integer delChannelByCostId(@Param("cm") Long costId);
}
