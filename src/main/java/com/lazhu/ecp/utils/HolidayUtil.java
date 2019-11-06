package com.lazhu.ecp.utils;

import java.util.Date;
import java.util.List;

import com.lazhu.core.util.DateUtil;
import com.lazhu.core.util.InstanceUtil;

/**
 * hz
 * 2018年4月23日 下午1:41:18
 */
public class HolidayUtil {

	public static List<String> HOLIDAY;
	
//	2018
//	节日	放假时间	调休上班日期	放假天数
//	元旦	12月30日~1月1日	1月1日（周一）补休	共3天
//	春节	2月15日(除夕）~2月21日	2月11日（周日）上班，2月24日（周六）上班	共7天
//	清明节	4月5日~4月7日	4月8日（周日）上班	共3天
//	劳动节	4月29日~5月1日	4月28日（周六）上班	共3天
//	端午节	6月16日~6月18日	6月18日（周一）补休	共3天
//	中秋节	9月22日~9月24日	9月24日（周一）补休	共3天
//	国庆节	10月1日~10月7日	9月29日（周六）、9月30日（周日）上班	共7天
	
	static {
		HOLIDAY = InstanceUtil.newArrayList();
		//元旦
		HOLIDAY.add("2017-12-30");
		HOLIDAY.add("2018-12-31");
		HOLIDAY.add("2018-01-01");
		//春节
		HOLIDAY.add("2018-02-15");
		HOLIDAY.add("2018-02-16");
		HOLIDAY.add("2018-02-17");
		HOLIDAY.add("2018-02-18");
		HOLIDAY.add("2018-02-19");
		HOLIDAY.add("2018-02-20");
		HOLIDAY.add("2018-02-21");
		//清明节
		HOLIDAY.add("2018-04-05");
		HOLIDAY.add("2018-04-06");
		HOLIDAY.add("2018-04-07");
		//劳动节
		HOLIDAY.add("2018-04-29");
		HOLIDAY.add("2018-04-30");
		HOLIDAY.add("2018-05-01");
		//端午节
		HOLIDAY.add("2018-06-16");
		HOLIDAY.add("2018-06-17");
		HOLIDAY.add("2018-06-18");
		//中秋节
		HOLIDAY.add("2018-09-22");
		HOLIDAY.add("2018-09-23");
		HOLIDAY.add("2018-09-24");
		//国庆节
		HOLIDAY.add("2018-10-01");
		HOLIDAY.add("2018-10-02");
		HOLIDAY.add("2018-10-03");
		HOLIDAY.add("2018-10-04");
		HOLIDAY.add("2018-10-05");
		HOLIDAY.add("2018-10-06");
		HOLIDAY.add("2018-10-07");
	}

	public static List<String> getHOLIDAY() {
		return HOLIDAY;
	}

	public static void setHOLIDAY(List<String> hOLIDAY) {
		HOLIDAY = hOLIDAY;
	}
	
	public static Date checkHoliday (Date endDate) {
		/*boolean flag = false;
		int i = 0; 
		for (;i < HOLIDAY.size();i++) {
			if (HOLIDAY.get(i).equals(endDate)) {
				flag = true;
				break;
			}
		}
		if (true) {
			if (i++ < HOLIDAY.size()) {
				
			}
			return checkHoliday(HOLIDAY.get(i));
		}*/
		return endDate;
	}
	
	public static void main(String[] args) {
		System.out.println("本年度节日共："+HOLIDAY.size()+"天");
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (String str : HOLIDAY) {
			System.out.println(str);
		}
	}
}

