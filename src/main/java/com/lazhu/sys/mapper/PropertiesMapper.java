package com.lazhu.sys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.sys.model.Properties;
/**
 * <p>
 * 配置表 Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2018-01-07
 */
public interface PropertiesMapper extends BaseMapper<Properties> {

	/** 键查询值 **/
	List<String> queryKeyString(@Param("keyString") String keyString);
	
	/** 键查询值 **/
	List<Long> queryIdByKey(@Param("keyString") String keyString);

}
