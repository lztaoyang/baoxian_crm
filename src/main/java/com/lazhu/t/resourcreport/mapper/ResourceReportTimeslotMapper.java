package com.lazhu.t.resourcreport.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.lazhu.t.resourcreport.entity.ResourceReportTimeslot;
import com.lazhu.t.resourcreport.entity.ResourceReportVO;
import com.lazhu.core.base.BaseMapper;

/**
 * 推广资源报表时段相关数据库操作
 *
 * @author ty
 * @date 2018年11月21日
 * 
 */
@Component("resourceReportTimeslotMapper")
public interface ResourceReportTimeslotMapper extends BaseMapper<ResourceReportTimeslot>{

	/**
	 * 查询资源总条数
	 */
	List<ResourceReportVO> queryTimeslotResourceNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源可聊数量
	 */
	List<ResourceReportVO> queryTimeslotTalkNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源订单数量
	 */
	List<ResourceReportVO> queryTimeslotOrderNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源会员数量
	 */
	List<ResourceReportVO> queryTimeslotCustomerNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源总保费
	 */
	List<ResourceReportVO> queryTimeslotInsureMoney(@Param("rr") Map<String, Object> param);
}
