package com.lazhu.crm.auditmobile.mapper;

import java.util.List;

import com.lazhu.crm.auditmobile.entity.AuditMobile;
import com.lazhu.core.base.BaseMapper;

/**
 * <p>
 * 手机号明文申请表 Mapper 接口
 * </p>
 *
 * @author taoyang
 * @since 2019-07-30
 */
public interface AuditMobileMapper extends BaseMapper<AuditMobile> {

	public List<Long> queryPassTime();

}
