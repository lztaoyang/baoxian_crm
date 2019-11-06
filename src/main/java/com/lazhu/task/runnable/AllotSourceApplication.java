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
 * 数据分配启动类和生命检测
 * 
 * @author hz
 *
 * @date 2018-05-11
 */
@Component
public class AllotSourceApplication {

	/**
	 * 启动推广资源分配
	 * 
	 */
	@PostConstruct
	public void start() {
		ThreadPoolHelper.taskExecute(new ExpandAllotSourceThread());
	}
	
	/**
	 * 检测推广资源分配，假如5分钟未进行分配则报警
	 */
	@Scheduled(cron = "0 */5 * * * ?")
	public void checkAllotPing() {
		try {
			Long time = (Long) CacheUtil.getCache().get(Constant.ALLOT_SOURCE_PING);
			Long notice = (Long) CacheUtil.getCache().get(Constant.ALLOT_SOURCE_PRE_NOTICE);
			Long cur = System.currentTimeMillis();
			if (cur > (time + 5 * 60 * 1000) && cur > (notice + 30 * 60 * 1000)) {
				DingUtil.sendGroupMesg(PropertiesUtil.getString("dd.group.tg"),new TextMessage("【数据分配异常】: 推广数据分配已停止超过5分钟！"));
				CacheUtil.getCache().set(Constant.ALLOT_SOURCE_PRE_NOTICE, Long.valueOf(System.currentTimeMillis()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("分配检测心跳发生错误");
		}
	}

}
