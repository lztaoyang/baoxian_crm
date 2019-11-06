package com.lazhu.aladdinpbx.noanswercall.mapper;

import com.lazhu.aladdinpbx.noanswercall.entity.Noanswercall;
import com.lazhu.core.base.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
/**
 * <p>
 * 未接电话记录表 Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2017-12-18
 */
public interface NoanswercallMapper extends BaseMapper<Noanswercall> {

	/** 查询最新未接通话记录 **/
	List<Long> queryByMaxId(@Param("noanswerMaxId") Long noanswerMaxId);

}
