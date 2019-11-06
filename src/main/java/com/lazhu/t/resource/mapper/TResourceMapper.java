package com.lazhu.t.resource.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.t.resource.entity.TResource;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2017-10-27
 */
public interface TResourceMapper extends BaseMapper<TResource> {

	List<Long> queryNewTResouce(@Param("maxTId") Long maxTId);

}
