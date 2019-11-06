package com.lazhu.sys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.sys.model.SysDic;

public interface SysDicMapper extends BaseMapper<SysDic> {
    
	/**通过type查询**/
    List<Long> queryByType(@Param("type") String type);
    
    /**通过type、code查询codeText**/
    List<String> queryCodeTextByTypeCode(@Param("type") String type,@Param("code") String code);
}