package com.lazhu.sys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.sys.model.SysDept;

public interface SysDeptMapper extends BaseMapper<SysDept> {

	List<Long> selectIdPage(@Param("cm") SysDept sysDept);

	List<Long> queryByDept(@Param("deptName") String deptName);
}