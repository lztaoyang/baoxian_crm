package com.lazhu.task.runnable;

import java.util.Date;

import com.lazhu.core.config.SpringContext;
import com.lazhu.core.util.CacheUtil;
import com.lazhu.crm.Constant;
import com.lazhu.crm.resourceleave.service.ResourceLeaveService;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.service.PropertiesService;
import com.lazhu.t.allotplanning.service.AllotPlanningService;
import com.lazhu.t.resourceallot.service.ResourceAllotService;

/**
 * 资源分配线程
 * 
 * @author hz
 * 
 * @date 2018-05-11
 */
public class ExpandAllotSourceThread implements Runnable {
	
	private PropertiesService propertiesService = SpringContext
			.getBean(PropertiesService.class);

	private ResourceAllotService resourceAllotService = SpringContext
			.getBean(ResourceAllotService.class);
	
	private AllotPlanningService allotPlanningService = SpringContext
			.getBean(AllotPlanningService.class);
	
	private ResourceLeaveService resourceLeaveService = SpringContext
			.getBean(ResourceLeaveService.class);

	@Override
	public void run() {
		// 重置心跳和通知
		try {
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			CacheUtil.getCache().set(Constant.ALLOT_SOURCE_PING,
					Long.valueOf(System.currentTimeMillis()));
			CacheUtil.getCache().set(Constant.ALLOT_SOURCE_PRE_NOTICE,
					Long.valueOf(System.currentTimeMillis()));
		} catch (Exception e) {
			System.out.println("分配心跳监测修改缓存数据异常0");
		}
		// 开始数据分配
		System.out.println("开启推广分配");
		int allotNum = 0;
		//是否穿插分配
		boolean isAllotSharedPoolResource = false;
		int trn = 0;//分配推广资源数临界值
		int spn = 0;//穿插分配共享池资源数
		while (true) {
			//是否穿插分配
			isAllotSharedPoolResource = false;
			trn = 0;//分配推广资源数临界值
			spn = 0;//穿插分配共享池资源数
			String sharedPoolRatio = propertiesService.queryKeyString("t.resource.allot.sharedPool.ratio");
			if (null != sharedPoolRatio && sharedPoolRatio.length() >= 3) {
				sharedPoolRatio = sharedPoolRatio.replace("：", ":");
				try {
					if (sharedPoolRatio.indexOf(":") > 0) {
						trn = StrUtils.toInt(sharedPoolRatio.substring(0, sharedPoolRatio.indexOf(":")));
						spn = StrUtils.toInt(sharedPoolRatio.substring(sharedPoolRatio.indexOf(":") + 1));
					}
				} catch (Exception e) {
					System.out.println("分配推广数据时穿插分配共享池资源，获取参数异常："+sharedPoolRatio);
					e.printStackTrace();
				}
			}
			if (trn > 0 && spn > 0) {
				isAllotSharedPoolResource = true;
			}
			int num = 0;
			//每次开始前先查询分配类型
			String type = propertiesService.queryKeyString("t.resource.allot.type");
			//每次分配个数
			String allot = propertiesService.queryKeyString("t.resource.allot.num");
			int resourceAllotNum = StrUtils.toInt(allot);
			if (null == type || "0".equals(type)) {//不分配
				
			} else if (null == type || "1".equals(type)) {//自动分配
				try {
					num = resourceAllotService.syncAllotResource(resourceAllotNum);
				} catch (Exception e) {
					System.out.println("自动分配推广资源发生错误");
					e.printStackTrace();
				}
			} else if (null == type || "2".equals(type)) {//计划分配
				try {
					boolean flag = allotPlanningService.isBetweenEnable(new Date());
					if (flag) {
						num = resourceAllotService.syncAllotResource(resourceAllotNum);
					}
				} catch (Exception e) {
					System.out.println("穿插分配推广资源发生错误");
					e.printStackTrace();
				}
			}
			allotNum += num;
			if (allotNum >= trn) {
				if (isAllotSharedPoolResource && spn > 0) {
					try {
						//穿插分配共享池资源
						resourceLeaveService.getSharedPoolResource(null, spn, false);
					} catch (Exception e) {
						System.out.println("穿插分配共享池资源发生错误");
						e.printStackTrace();
					}
				}
				allotNum = 0;
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
				CacheUtil.getCache().set(Constant.ALLOT_SOURCE_PING,
						Long.valueOf(System.currentTimeMillis()));
			} catch (Exception e) {
				System.out.println("分配心跳监测修改缓存数据异常1");
			}
		}
	}

}
