package com.lazhu.t.resourcreport.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.lazhu.t.resourcreport.entity.ResourceReportCity;
import com.lazhu.t.resourcreport.entity.ResourceReportVO;
import com.lazhu.core.base.BaseMapper;

/**
 * 推广资源报表城市相关数据库操作
 *
 * @author ty
 * @date 2018年11月21日
 * 
 */
@Component("resourceReportCityMapper")
public interface ResourceReportCityMapper extends BaseMapper<ResourceReportCity>{

	/**
	 * 查询资源总条数
	 */
	List<ResourceReportVO> queryCityResourceNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源可聊数量
	 */
	List<ResourceReportVO> queryCityTalkNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源订单数量
	 */
	List<ResourceReportVO> queryCityOrderNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源会员数量
	 */
	List<ResourceReportVO> queryCityCustomerNum(@Param("rr") Map<String, Object> param);
	
	/**
	 * 查询资源总保费
	 */
	List<ResourceReportVO> queryCityInsureMoney(@Param("rr") Map<String, Object> param);
}
