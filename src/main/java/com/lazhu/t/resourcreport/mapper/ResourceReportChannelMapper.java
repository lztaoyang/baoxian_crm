package com.lazhu.t.resourcreport.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.lazhu.t.resourcreport.entity.ResourceReportChannel;
import com.lazhu.t.resourcreport.entity.ResourceReportVO;
import com.lazhu.core.base.BaseMapper;

/**
 * 推广资源报表渠道相关数据库操作
 *
 * @author ty
 * @date 2018年11月21日
 * 
 */
@Component("resourceReportChannelMapper")
public interface ResourceReportChannelMapper extends BaseMapper<ResourceReportChannel>{

	/**
	 * 查询资源总条数
	 */
	List<ResourceReportVO> queryChannelResourceNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源可聊数量
	 */
	List<ResourceReportVO> queryChannelTalkNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源订单数量
	 */
	List<ResourceReportVO> queryChannelOrderNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源会员数量
	 */
	List<ResourceReportVO> queryChannelCustomerNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源总保费
	 */
	List<ResourceReportVO> queryChannelInsureMoney(@Param("rr") Map<String, Object> param);
}
