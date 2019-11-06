package com.lazhu.crm.serverrecord.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.crm.serverrecord.entity.ServerRecord;
/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hz
 * @since 2017-05-26
 */
public interface ServerRecordMapper extends BaseMapper<ServerRecord> {

	/**查询服务记录（通过会员ID）**/
	List<Long> queryByCustomerId(@Param("customerId") Long customerId);

}
