package com.lazhu.t.resourcreport.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import com.lazhu.t.resourcreport.entity.ResourceReportVO;
import com.lazhu.t.resourcreport.mapper.ResourceReportChannelMapper;
import com.lazhu.t.resourcreport.mapper.ResourceReportCityMapper;
import com.lazhu.t.resourcreport.mapper.ResourceReportPlanMapper;
import com.lazhu.t.resourcreport.mapper.ResourceReportProvinceMapper;
import com.lazhu.t.resourcreport.mapper.ResourceReportTimeslotMapper;

/**
 * 推广资源报表业务处理
 *
 * @author ty
 * @date 2018年11月21日
 * 
 */
@Service
@CacheConfig(cacheNames = "resourceReport")
public class ResourceReportService {

	@Autowired
	ResourceReportCityMapper resourceReportCityMapper;
	@Autowired
	ResourceReportProvinceMapper resourceReportProvinceMapper;
	@Autowired
	ResourceReportChannelMapper resourceReportChannelMapper;
	@Autowired
	ResourceReportPlanMapper resourceReportPlanMapper;
	@Autowired
	ResourceReportTimeslotMapper resourceReportTimeslotMapper;

	/**
	 * 获取省份维度数据
	 * @param param 
	 */
	public List<ResourceReportVO> queryProvinceDimension(Map<String, Object> param) {
		// 查询省份维度下的数据 如果四次查询效果较慢,可以调整为union all方式
		List<ResourceReportVO> resourceNums = resourceReportProvinceMapper.queryProvinceResourceNum(param);
		List<ResourceReportVO> queryTalkNums = resourceReportProvinceMapper.queryProvinceTalkNum(param);
		List<ResourceReportVO> queryOrderNums = resourceReportProvinceMapper.queryProvinceOrderNum(param);
		List<ResourceReportVO> queryCustomerNums = resourceReportProvinceMapper.queryProvinceCustomerNum(param);
		List<ResourceReportVO> queryInsureMoneys = resourceReportProvinceMapper.queryProvinceInsureMoney(param);

		// 将数据合并到一个集合中
		resourceNums.addAll(queryTalkNums);
		resourceNums.addAll(queryOrderNums);
		resourceNums.addAll(queryCustomerNums);
		resourceNums.addAll(queryInsureMoneys);

		return mergeResourceData(resourceNums,param);
	}
	
	/**
	 * 获取城市维度数据
	 * @param param 
	 */
	public List<ResourceReportVO> queryCityDimension(Map<String, Object> param) {
		// 查询省份维度下的数据 如果四次查询效果较慢,可以调整为union all方式
		List<ResourceReportVO> resourceNums = resourceReportCityMapper.queryCityResourceNum(param);
		List<ResourceReportVO> queryTalkNums = resourceReportCityMapper.queryCityTalkNum(param);
		List<ResourceReportVO> queryOrderNums = resourceReportCityMapper.queryCityOrderNum(param);
		List<ResourceReportVO> queryCustomerNums = resourceReportCityMapper.queryCityCustomerNum(param);
		List<ResourceReportVO> queryInsureMoneys = resourceReportCityMapper.queryCityInsureMoney(param);

		// 将数据合并到一个集合中
		resourceNums.addAll(queryTalkNums);
		resourceNums.addAll(queryOrderNums);
		resourceNums.addAll(queryCustomerNums);
		resourceNums.addAll(queryInsureMoneys);

		return mergeResourceData(resourceNums,param);
	}
	
	/**
	 * 获取渠道维度数据
	 * @param param 
	 */
	public List<ResourceReportVO> queryChannelDimension(Map<String, Object> param) {
		// 查询省份维度下的数据 如果四次查询效果较慢,可以调整为union all方式
		List<ResourceReportVO> resourceNums = resourceReportChannelMapper.queryChannelResourceNum(param);
		List<ResourceReportVO> queryTalkNums = resourceReportChannelMapper.queryChannelTalkNum(param);
		List<ResourceReportVO> queryOrderNums = resourceReportChannelMapper.queryChannelOrderNum(param);
		List<ResourceReportVO> queryCustomerNums = resourceReportChannelMapper.queryChannelCustomerNum(param);
		List<ResourceReportVO> queryInsureMoneys = resourceReportChannelMapper.queryChannelInsureMoney(param);

		// 将数据合并到一个集合中
		resourceNums.addAll(queryTalkNums);
		resourceNums.addAll(queryOrderNums);
		resourceNums.addAll(queryCustomerNums);
		resourceNums.addAll(queryInsureMoneys);

		return mergeResourceData(resourceNums,param);
	}
	
	/**
	 * 获取广告维度数据
	 * @param param 
	 */
	public List<ResourceReportVO> queryPlanDimension(Map<String, Object> param) {
		// 查询省份维度下的数据 如果四次查询效果较慢,可以调整为union all方式
		List<ResourceReportVO> resourceNums = resourceReportPlanMapper.queryPlanResourceNum(param);
		List<ResourceReportVO> queryTalkNums = resourceReportPlanMapper.queryPlanTalkNum(param);
		List<ResourceReportVO> queryOrderNums = resourceReportPlanMapper.queryPlanOrderNum(param);
		List<ResourceReportVO> queryCustomerNums = resourceReportPlanMapper.queryPlanCustomerNum(param);
		List<ResourceReportVO> queryInsureMoneys = resourceReportPlanMapper.queryPlanInsureMoney(param);

		// 将数据合并到一个集合中
		resourceNums.addAll(queryTalkNums);
		resourceNums.addAll(queryOrderNums);
		resourceNums.addAll(queryCustomerNums);
		resourceNums.addAll(queryInsureMoneys);

		return mergeResourceData(resourceNums,param);
	}
	
	/**
	 * 获取时间段维度数据
	 * @param param 
	 */
	public List<ResourceReportVO> queryTimeslotDimension(Map<String, Object> param) {
		// 查询省份维度下的数据 如果四次查询效果较慢,可以调整为union all方式
		List<ResourceReportVO> resourceNums = resourceReportTimeslotMapper.queryTimeslotResourceNum(param);
		List<ResourceReportVO> queryTalkNums = resourceReportTimeslotMapper.queryTimeslotTalkNum(param);
		List<ResourceReportVO> queryOrderNums = resourceReportTimeslotMapper.queryTimeslotOrderNum(param);
		List<ResourceReportVO> queryCustomerNums = resourceReportTimeslotMapper.queryTimeslotCustomerNum(param);
		List<ResourceReportVO> queryInsureMoneys = resourceReportTimeslotMapper.queryTimeslotInsureMoney(param);

		// 将数据合并到一个集合中
		resourceNums.addAll(queryTalkNums);
		resourceNums.addAll(queryOrderNums);
		resourceNums.addAll(queryCustomerNums);
		resourceNums.addAll(queryInsureMoneys);

		return mergeResourceData(resourceNums,param);
	}
	/**
	 * 合并返回的资源数据
	 * 
	 * @param resourceNums 所有资源集合
	 * @param param 前端参数
	 * @return 合并完成的数据
	 */
	public static List<ResourceReportVO> mergeResourceData(List<ResourceReportVO> resourceNums,Map<String, Object> param) {
		// 将相同维度的数据合并为一条结果集
		List<ResourceReportVO> countList = new ArrayList<ResourceReportVO>();// 用于存放最后的结果
		for (int i = 0; i < resourceNums.size(); i++) {
			String dimension = resourceNums.get(i).getDimension();
			if(dimension != null && dimension.length() > 0){
				Integer resourceNum = resourceNums.get(i).getResourceNum();
				Integer talkNum = resourceNums.get(i).getTalkNum();
				Integer orderNum = resourceNums.get(i).getOrderNum();
				Integer customerNum = resourceNums.get(i).getCustomerNum();
				Double insureMoney = resourceNums.get(i).getInsureMoney();
				int flag = 0;// 0为新增数据，1为增加count
				for (int j = 0; j < countList.size(); j++) {
					String dimension_ = countList.get(j).getDimension();

					if (dimension.equals(dimension_)) {
						if (resourceNum != null && resourceNum != 0) {
							countList.get(j).setResourceNum(resourceNum);
						} else if (talkNum != null && talkNum != 0) {
							countList.get(j).setTalkNum(talkNum);
						} else if (orderNum != null && orderNum != 0) {
							countList.get(j).setOrderNum(orderNum);
						} else if (customerNum != null && customerNum != 0) {
							countList.get(j).setCustomerNum(customerNum);
						} else if (insureMoney != null && insureMoney.intValue() != 0) {
							countList.get(j).setInsureMoney(insureMoney);
						}

						flag = 1;
						continue;
					}
				}
				if (flag == 0) {
					countList.add(resourceNums.get(i));
				}
			}
		}
		
		//处理数据排序
		if(param != null && param.get("orderBy") != null){
			return orderByResourceData(countList,param.get("orderBy").toString());
		}else{
			return countList;
		}
		
	}
	
	/**
	 * 对合并后的资源数据排序
	 * 
	 * @param dataList 合并后的数据
	 * @param orderBy 排序规则
	 * @return
	 */
	protected static List<ResourceReportVO> orderByResourceData(List<ResourceReportVO> dataList,final String orderBy){
		Collections.sort(dataList, new Comparator<ResourceReportVO>() {
            public int compare(ResourceReportVO o1, ResourceReportVO o2) {
            	if("resourceNum".toLowerCase().equals(orderBy.toLowerCase())){
            		if(o2.getResourceNum() == null && o1.getResourceNum() == null){
            			return 0;
            		}else if(o2.getResourceNum() == null){
            			return -1; // 小于
            		}else if(o1.getResourceNum() == null){
            			return o2.getResourceNum().compareTo(0);
            		}else{
            			return o2.getResourceNum().compareTo(o1.getResourceNum());
            		}
            	}else if("talkNum".toLowerCase().equals(orderBy.toLowerCase())){
            		if(o2.getTalkNum() == null && o1.getTalkNum() == null){
            			return 0;
            		}else if(o2.getTalkNum() == null){
            			return -1; // 小于
            		}else if(o1.getTalkNum() == null){
            			return o2.getTalkNum().compareTo(0);
            		}else{
            			return o2.getTalkNum().compareTo(o1.getTalkNum());
            		}
            	}else if("talkRate".toLowerCase().equals(orderBy.toLowerCase())){
            		String rate1 = "0";
            		if(o1.getTalkNum() != null && o1.getResourceNum() != null){
            			rate1 = (((double)o1.getTalkNum())/o1.getResourceNum()) + "";
            		}
            		
            		String rate2 = "0";
            		if(o2.getTalkNum() != null && o2.getResourceNum() != null){
            			rate2 = (((double)o2.getTalkNum())/o2.getResourceNum()) + "";
            		}
            		
            		if(rate1.equals("0") && rate2.equals("0")){
            			return -1;
            		}else if(rate2.equals("0")){
            			return rate1.compareTo("0"); // 小于
            		}else if(rate1.equals("0")){
            			return rate2.compareTo("0");
            		}else{
            			return rate2.compareTo(rate1);
            		}
            	}else if("orderNum".toLowerCase().equals(orderBy.toLowerCase())){
            		
            		if(o2.getOrderNum() == null && o1.getOrderNum() == null){
            			return 0;
            		}else if(o2.getOrderNum() == null){
            			return -1; // 小于
            		}else if(o1.getOrderNum() == null){
            			return o2.getOrderNum().compareTo(0);
            		}else{
            			return o2.getOrderNum().compareTo(o1.getOrderNum());
            		}
            		
            	}else if("orderRate".toLowerCase().equals(orderBy.toLowerCase())){
            		String rate1 = "0";
            		if(o1.getOrderNum() != null && o1.getResourceNum() != null){
            			rate1 = (((double)o1.getOrderNum())/o1.getResourceNum()) + "";
            		}
            		
            		String rate2 = "0";
            		if(o2.getOrderNum() != null && o2.getResourceNum() != null){
            			rate2 = (((double)o2.getOrderNum())/o2.getResourceNum()) + "";
            		}
            		
            		if(rate1.equals("0") && rate2.equals("0")){
            			return -1;
            		}else if(rate2.equals("0")){
            			return rate1.compareTo("0"); // 小于
            		}else if(rate1.equals("0")){
            			return rate2.compareTo("0");
            		}else{
            			return rate2.compareTo(rate1);
            		}
            		
            	}else if("customerNum".toLowerCase().equals(orderBy.toLowerCase())){
            		
            		if(o2.getCustomerNum() == null && o1.getCustomerNum() == null){
            			return 0;
            		}else if(o2.getCustomerNum() == null){
            			return -1; // 小于
            		}else if(o1.getCustomerNum() == null){
            			return o2.getCustomerNum().compareTo(0);
            		}else{
            			return o2.getCustomerNum().compareTo(o1.getCustomerNum());
            		}
            		
            	}else if("customerRate".toLowerCase().equals(orderBy.toLowerCase())){
            		String rate1 = "0";
            		if(o1.getCustomerNum() != null && o1.getResourceNum() != null){
            			rate1 = (((double)o1.getCustomerNum())/o1.getResourceNum()) + "";
            		}
            		
            		String rate2 = "0";
            		if(o2.getCustomerNum() != null && o2.getResourceNum() != null){
            			rate2 = (((double)o2.getCustomerNum())/o2.getResourceNum()) + "";
            		}
            		
            		if(rate1.equals("0") && rate2.equals("0")){
            			return -1;
            		}else if(rate2.equals("0")){
            			return rate1.compareTo("0"); // 小于
            		}else if(rate1.equals("0")){
            			return rate2.compareTo("0");
            		}else{
            			return rate2.compareTo(rate1);
            		}
            		
            	}else if("insureMoney".toLowerCase().equals(orderBy.toLowerCase())){
            		
            		if(o2.getInsureMoney() == null && o1.getInsureMoney() == null){
            			return 0;
            		}else if(o2.getInsureMoney() == null){
            			return -1; // 小于
            		}else if(o1.getInsureMoney() == null){
            			return 1;
            		}else{
            			return o2.getInsureMoney().compareTo(o1.getInsureMoney());
            		}
            		
            	}else{
            		if(o2.getResourceNum() == null && o1.getResourceNum() == null){
            			return 0;
            		}else if(o2.getResourceNum() == null){
            			return -1; // 小于
            		}else if(o1.getResourceNum() == null){
            			return o2.getResourceNum().compareTo(0);
            		}else{
            			return o2.getResourceNum().compareTo(o1.getResourceNum());
            		}
            	}
            }
        });
		return dataList;
	}
}
