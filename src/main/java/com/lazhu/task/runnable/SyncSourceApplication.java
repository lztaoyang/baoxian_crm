package com.lazhu.task.runnable;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dingtalk.chatbot.message.TextMessage;
import com.lazhu.core.support.thread.ThreadPoolHelper;
import com.lazhu.core.util.CacheUtil;
import com.lazhu.core.util.PropertiesUtil;
import com.lazhu.crm.Constant;
import com.lazhu.ecp.utils.DingUtil;

/**
 * 同步数据启动类和生命检测
 * 
 * @author whui
 *
 * @date 2018-04-09
 */
@Component
public class SyncSourceApplication {

	/**
	 * 启动推广资源同步
	 * 
	 */
	@PostConstruct
	public void start() {
		ThreadPoolHelper.taskExecute(new ExpandSyncSourceThread());
	}
	
	/**
	 * 检测推广资源同步，假如5分钟未进行同步则报警
	 */
	@Scheduled(cron = "0 */5 * * * ?")
	public void checkPing() {
		try {
			Long time = (Long) CacheUtil.getCache().get(Constant.SYNC_SOURCE_PING);
			Long notice = (Long) CacheUtil.getCache().get(Constant.SYNC_SOURCE_PRE_NOTICE);
			Long cur = System.currentTimeMillis();
			if (cur > (time + 5 * 60 * 1000) && cur > (notice + 30 * 60 * 1000)) {
				DingUtil.sendGroupMesg(PropertiesUtil.getString("dd.group.tg"),new TextMessage("【数据同步异常】: 推广数据同步已停止超过5分钟！"));
				CacheUtil.getCache().set(Constant.SYNC_SOURCE_PRE_NOTICE, Long.valueOf(System.currentTimeMillis()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("同步检测心跳发生错误");
		}
	}

}
