package com.lazhu.t.mobileexport.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lazhu.t.mobileexport.entity.ResourceMobileExport;
import com.lazhu.core.base.BaseMapper;


/**
 * 推广资源手机号信息导出数据库操作
 *
 * @author ty
 * @date 2018年11月21日
 */
public interface ResourceMobileExportMapper extends BaseMapper<ResourceMobileExport>{

	
	/**
	 * 查询资源表中的手机号码
	 * 
	 * @param par
	 * @return
	 */
	public List<Map<String,Object>> queryMobileList(@Param("par") Map<String, Object> par);
}
