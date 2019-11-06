package com.lazhu.sys.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lazhu.core.util.CacheUtil;
import com.lazhu.ecp.utils.OperationLogTool;

@Service
public class SysCacheService {
	Logger logger = LogManager.getLogger();

	// 清缓存
	public void flush() {
		logger.info("清缓存开始......");
		//CacheUtil.getCache().delAll("*:sysDics:*");
		//CacheUtil.getCache().delAll("*:sysDicMap:*");
		//CacheUtil.getCache().delAll("*:getAuthorize:*");
		//CacheUtil.getCache().delAll("*:sysPermission:*");
		logger.info("清缓存结束!");
	}
	
	//删除指定表的redis
	public void flush(String key) {
		logger.info("清缓存开始......");
		CacheUtil.getCache().delAll("*:" + key + ":*");
		OperationLogTool.operationLog("删除redis：" + key);
		logger.info("清缓存结束!");
	}
	
	//删除指定key的redis
	public void flush(String key,String id) {
		logger.info("清缓存开始......");
		CacheUtil.getCache().del("*:" + key + ":" + id);
		OperationLogTool.operationLog("删除redis：" + "DEL KEYS \""+ key + ":" + id + "\"");
		logger.info("清缓存结束!");
	}
}