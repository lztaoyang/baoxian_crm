package com.lazhu.sys.mapper;

import java.util.List;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.sys.model.SysSession;

public interface SysSessionMapper extends BaseMapper<SysSession> {

    void deleteBySessionId(String sessionId);

    Long queryBySessionId(String sessionId);

    List<String> querySessionIdByAccount(String account);
    
    List<Long> queryIdByAccount(String account);
    
    void deleteByAccount(String account);
}