package com.lazhu.t.resourcreport.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.lazhu.t.resourcreport.entity.ResourceReportPlan;
import com.lazhu.t.resourcreport.entity.ResourceReportVO;
import com.lazhu.core.base.BaseMapper;

/**
 * 推广资源报表广告相关数据库操作
 *
 * @author ty
 * @date 2018年11月21日
 * 
 */
@Component("resourceReportPlanMapper")
public interface ResourceReportPlanMapper extends BaseMapper<ResourceReportPlan>{

	/**
	 * 查询资源总条数
	 */
	List<ResourceReportVO> queryPlanResourceNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源可聊数量
	 */
	List<ResourceReportVO> queryPlanTalkNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源订单数量
	 */
	List<ResourceReportVO> queryPlanOrderNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源会员数量
	 */
	List<ResourceReportVO> queryPlanCustomerNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源总保费
	 */
	List<ResourceReportVO> queryPlanInsureMoney(@Param("rr") Map<String, Object> param);
}
