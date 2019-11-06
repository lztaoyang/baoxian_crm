package com.lazhu.task.runnable;

import com.lazhu.core.config.SpringContext;
import com.lazhu.core.util.CacheUtil;
import com.lazhu.crm.Constant;
import com.lazhu.t.resourceallot.service.ResourceAllotService;

/**
 * 资源同步线程
 * 
 * @author whui
 * 
 * @date 2018-04-09
 */
public class ExpandSyncSourceThread implements Runnable {

	private ResourceAllotService resourceAllotService = SpringContext
			.getBean(ResourceAllotService.class);

	@Override
	public void run() {
		// 重置心跳和通知
		try {
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			CacheUtil.getCache().set(Constant.SYNC_SOURCE_PING,
					Long.valueOf(System.currentTimeMillis()));
			CacheUtil.getCache().set(Constant.SYNC_SOURCE_PRE_NOTICE,
					Long.valueOf(System.currentTimeMillis()));
		} catch (Exception e) {
			System.out.println("同步心跳监测修改缓存数据异常0");
		}
		// 开始数据同步
		System.out.println("开启推广同步");
		while (true) {
			int num = 0;
			try {
				num = resourceAllotService.newSyncTResource();
			} catch (Exception e) {
				System.out.println("同步推广资源发生错误");
				e.printStackTrace();
			}
			// 无资源处理的时候休眠20s
			if (num == 0) {
				try {
					Thread.sleep(20 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				CacheUtil.getCache().set(Constant.SYNC_SOURCE_PING,
						Long.valueOf(System.currentTimeMillis()));
			} catch (Exception e) {
				System.out.println("同步心跳监测修改缓存数据异常1");
			}
		}
	}

	public static void main(String[] args) {
		System.out.println("113".compareTo("1121"));
	}

}
