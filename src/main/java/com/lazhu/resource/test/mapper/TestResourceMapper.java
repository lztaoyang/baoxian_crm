package com.lazhu.resource.test.mapper;

import com.lazhu.resource.test.entity.TestResource;
import com.lazhu.core.base.BaseMapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2018-10-11
 */
public interface TestResourceMapper extends BaseMapper<TestResource> {

	List<Long> queryEnable();

}
