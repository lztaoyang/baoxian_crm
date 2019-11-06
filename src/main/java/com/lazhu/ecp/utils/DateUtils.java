package com.lazhu.ecp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 时间工具类
 * 
 * @author 佘崔
 * 
 */
public class DateUtils {
	public static Calendar cal = Calendar.getInstance();
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String MINUTE_FORMAT = "yyyy-MM-dd HH:mm";
	public static final String SHORT_TIME_FORMAT = "MM-dd HH:mm:ss";
	public static final String MONTH_FORMAT = "yyyy-MM";
	
	public static final String TIME_FORMAT_CHINESE = "MM月dd日HH点mm分";
	public static final String TIME_FORMAT_CHINESE_SECOND = "MM月dd日HH:mm:ss";
	public static final String HOUR_FORMAT_NUMERIC = "MMddHH";
	
	/**
	 * 返回指定格式的字符串日期
	 * 
	 * @param date
	 *            日期 允许NULL,为NULL时返回空字符
	 * @param format
	 *            返回的字符串日期格式
	 * @return
	 */
	public static String DateToStr(Object date, String format) {
		String dateStr = null;
		if (date != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			dateStr = simpleDateFormat.format(date);
		}
		return dateStr;
	}

	public static Integer DateToInteger(Date date, String format) {
		String dateStr = null;
		if (date != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			dateStr = simpleDateFormat.format(date).replaceAll("-", "");
		}
		return new Integer(StrUtils.toInt(dateStr));
	}
	
	/**
	 * @param before
	 * @return
	 */
	public static Date getBeforeDayDate(Integer before){
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE,0 - before);
		Date time=cal.getTime();
		return time;
	}

	/**
	 * 日期转换为时间
	 * 
	 * @param date
	 * @return
	 */
	public static Long toLong(Date date) {
		String dateStr = DateToStr(date, "yyyyMMdd");
		return new Long(dateStr);
	}

	public static String IntegerToStr(Integer dateInteger, String oldFormat,
			String newFormat) {
		String dateStr = null;
		Date date = convertDate(dateInteger.toString(), oldFormat);
		dateStr = DateToStr(date, newFormat);
		return dateStr;
	}

	public static Integer strDateToIntegerDate(String start, String oldFormat,
			String newFormat) {
		String dateStr = null;
		Date date = convertDate(start, oldFormat);
		dateStr = DateToStr(date, newFormat);
		return StrUtils.toInt(dateStr);
	}

	/**
	 * 将英文格式的时间字符串转换为中文格式
	 * 
	 * @param enDateStr
	 * @param format
	 * @return
	 */
	public static String enDateStrToZhDateStr(String enDateStr, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE MMM d HH:mm:ss Z SSS yyyy", Locale.US);
		Date date = null;
		try {
			date = sdf.parse(enDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String dateStr = DateUtils.DateToStr(date, format);
		return dateStr;
	}

	public static Date enDateStrToDate(String enDateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE MMM d HH:mm:ss Z SSS yyyy", Locale.US);
		Date date = null;
		try {
			date = sdf.parse(enDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 根据字符串返回指定格式的日期
	 * 
	 * @param dateStr
	 *            日期(字符串)
	 * @param format
	 *            日期格式
	 * @return 日期(Date)
	 * @throws ParseException
	 */
	public static Date convertDate (String dateStr, String format) {
		if (null == dateStr || "".equals(dateStr.trim())) {
			return null;
		}
		java.util.Date date = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		try {
			date = simpleDateFormat.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String format(String dateStr, String format)
			throws ParseException {
		Date date = convertDate(dateStr, format);
		return DateToStr(date, format);
	}

	/**
	 * 小时的变动
	 * 
	 * @param hour
	 * @return
	 */
	public static Date minuteChange(Date date, Integer minute) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(java.util.Calendar.MINUTE, minute);
		return calendar.getTime();
	}

	/**
	 * 小时的变动
	 * 
	 * @param hour
	 * @return
	 */
	public static Date hourChange(Date date, Integer hour) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(java.util.Calendar.HOUR_OF_DAY, hour);
		return calendar.getTime();
	}

	/**
	 * 天的变动
	 * 
	 * @param hour
	 * @return
	 */
	public static Date dayChange(Date date, Integer day) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(java.util.Calendar.DAY_OF_WEEK, day);
		return calendar.getTime();
	}

	/**
	 * 月的变动
	 * 
	 * @param hour
	 * @return
	 */
	public static Date monthChange(Date date, Integer month) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(java.util.Calendar.MONTH, month);
		return calendar.getTime();
	}

	/**
	 * 年的变动
	 * 
	 * @param hour
	 * @return
	 */
	public static Date yearChange(Date date, Integer year) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(java.util.Calendar.YEAR, year);
		return calendar.getTime();
	}

	/**
	 * 获取年
	 * 
	 * @param date
	 * @return
	 */
	public static Integer getYear(Date date) {
		if (null == date) {
			return null;
		}
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 获取月
	 * 
	 * @param date
	 * @return
	 */
	public static Integer getMonth(Date date) {
		if (null == date) {
			return null;
		}
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取日期
	 * 
	 * @param date
	 * @return
	 */
	public static Integer getDay(Date date) {
		if (null == date) {
			return null;
		}
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DATE);
	}

	/**
	 * 是否是同一天
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean isTheSameDay(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(d1);
		c2.setTime(d2);
		return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
				&& (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
				&& (c1.get(Calendar.DAY_OF_MONTH) == c2
						.get(Calendar.DAY_OF_MONTH));
	}

	public static String getDateForInt(Date d1) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(d1);
		return date.replace("-", "");

	}

	/**
	 * 相隔多少天
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 * @throws Exception
	 */
	public static int getDistanceDays(Date one, Date two) {
		long days = 0;
		long time1 = one.getTime();
		long time2 = two.getTime();
		long diff = time1 - time2;
		days = diff / (1000 * 60 * 60 * 24);
		return (new Long(days).intValue() + 1);
	}

	/**
	 * 判断是否是最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isLastDay(Date date) {
		if (date == null) {
			return false;
		}
		int day = DateUtils.getDay(date);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		if (day == lastDay) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取两日期之间的所有日期
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return
	 */
	public static List<Date> getDates(Calendar startDate, Calendar endDate) {
		List<Date> result = new ArrayList<Date>();
		Calendar temp = (Calendar) startDate.clone();
		temp.add(Calendar.DAY_OF_YEAR, 1);
		while (temp.before(endDate)) {
			result.add(temp.getTime());
			temp.add(Calendar.DAY_OF_YEAR, 1);
		}
		return result;
	}

	/** 两个日期间所有月份 **/
	public static List<String> getMonthBetween(String minDate, String maxDate)
			throws ParseException {
		ArrayList<String> result = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");// 格式化为年月

		Calendar min = Calendar.getInstance();
		Calendar max = Calendar.getInstance();

		min.setTime(sdf.parse(minDate));
		min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

		max.setTime(sdf.parse(maxDate));
		max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

		Calendar curr = min;
		while (curr.before(max)) {
			result.add(sdf.format(curr.getTime()));
			curr.add(Calendar.MONTH, 1);
		}

		return result;
	}

	/** 两个日期间所有月份 **/
	public static String[] getAllMonths(String start, String end) {
		String splitSign = "-";
		String regex = "\\d{4}" + splitSign + "(([0][1-9])|([1][012]))"; // 判断YYYY-MM时间格式的正则表达式
		if (!start.matches(regex) || !end.matches(regex))
			return new String[0];

		List<String> list = new ArrayList<String>();
		if (start.compareTo(end) > 0) {
			// start大于end日期时，互换
			String temp = start;
			start = end;
			end = temp;
		}

		String temp = start; // 从最小月份开始
		while (temp.compareTo(start) >= 0 && temp.compareTo(end) < 0) {
			list.add(temp); // 首先加上最小月份,接着计算下一个月份
			String[] arr = temp.split(splitSign);
			int year = Integer.valueOf(arr[0]);
			int month = Integer.valueOf(arr[1]) + 1;
			if (month > 12) {
				month = 1;
				year++;
			}
			if (month < 10) {// 补0操作
				temp = year + splitSign + "0" + month;
			} else {
				temp = year + splitSign + month;
			}
		}

		int size = list.size();
		String[] result = new String[size];
		for (int i = 0; i < size; i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	
	/**
	 * 是否超时（分钟）
	 * 
	 * @param time	时间参数
	 * @param allow	允许范围（分钟）
	 * @return
	 */
	public static boolean timeOut(Date time,Long allow) {
		boolean timeOut = false;
		if (time != null) {
			long gap = (new Date().getTime() - time.getTime()) / (60 * 1000);// 分钟
			if (gap <= allow) {
				timeOut = true;
			}
		}
		return timeOut;
	}
	
	/**
	 * 是否小于当前时间（多少天）
	 * 
	 * @param time	时间参数
	 * @param allow	允许范围（天）
	 * @return
	 */
	public static boolean timeOutDay(Date time,Long allow) {
		if (time != null) {
			long gap = (new Date().getTime() - time.getTime()) / (24 * 60 * 60 * 1000);// 天
			if (gap >= allow) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 周六周日，自动计算到下周一
	 * @param date	日期
	 * @param dawn	凌晨
	 * @return
	 */
	public static Date getMonday (Date date,boolean dawn) {
		if (null == date) {
			return null;
		}
		Date currentDate = date;
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		if (dawn) {
			// 将时分秒,毫秒域清零
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY) {
			//如果是周六录入时间加到下周一
			Date Monday = new Date(cal.getTimeInMillis() + 2 * 24 * 60 * 60 * 1000);
			return Monday;
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
			//如果是周日录入时间加到下周一
			Date Monday = new Date(cal.getTimeInMillis() + 24 * 60 * 60 * 1000);
			return Monday;
		} else {
			//录入时间为当前时间
			return date;
		}
	}
	
	/**
	 * 获取本周一的日期
	 * @param date
	 * @return
	 */
	public static Date getFrontMonday (Date date) {
		if (null == date) {
			return null;
		}
		Date currentDate = date;
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY) {
			Date Monday = new Date(currentDate.getTime() - 24 * 60 * 60 * 1000);
			return Monday;
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY) {
			Date Monday = new Date(currentDate.getTime() - 2 * 24 * 60 * 60 * 1000);
			return Monday;
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY) {
			Date Monday = new Date(currentDate.getTime() - 3 * 24 * 60 * 60 * 1000);
			return Monday;
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY) {
			Date Monday = new Date(currentDate.getTime() - 4 * 24 * 60 * 60 * 1000);
			return Monday;
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY) {
			Date Monday = new Date(currentDate.getTime() - 5 * 24 * 60 * 60 * 1000);
			return Monday;
		} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
			Date Monday = new Date(currentDate.getTime() - 6 * 24 * 60 * 60 * 1000);
			return Monday;
		} else {
			//当前时间
			return currentDate;
		}
	}
	
	/**
	 * 计算保护期到期时间
	 * @param retainTime
	 * @param retainDay
	 * @param flag 			是否开发资源
	 * @return
	 */
	/*public static Date ResourceRetainTime(Date retainTime, Long retainDay,boolean flag) {
		Date date = new Date(retainTime.getTime() + retainDay * 24 * 60 * 60 * 1000);
		if (flag) {
			//忽略周末
			//当前时间为周几（如果为周四、周五、周六，则多加2天，周日多加1天）
			Calendar cal = Calendar.getInstance();
			cal.setTime(retainTime);
			if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY) {
				date = new Date(retainTime.getTime() + (retainDay + 2) * 24 * 60 * 60 * 1000);
			} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY) {
				date = new Date(retainTime.getTime() + (retainDay + 2) * 24 * 60 * 60 * 1000);
			} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY) {
				date = new Date(retainTime.getTime() + (retainDay + 2) * 24 * 60 * 60 * 1000);
			} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
				date =  new Date(retainTime.getTime() + (retainDay + 1) * 24 * 60 * 60 * 1000);
			}
			//忽略节日
			//date = HolidayUtil.checkHoliday(date);
		}
		return date;
	}
	*//**
	 * 计算抽取资源保护期到期时间
	 * @param retainTime
	 * @param retainDay
	 * @param flag 			是否开发资源
	 * @return
	 *//*
	public static Date ResourceRetainTimeCq(Date retainTime, Long retainDay,boolean flag) {
		Date date = new Date(retainTime.getTime() + retainDay * 24 * 60 * 60 * 1000);
		if (flag) {
			//忽略周末
			//当前时间为周几（如果为周五、周六，则多加2天，周日多加1天）
			Calendar cal = Calendar.getInstance();
			cal.setTime(retainTime);
			 if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY) {
				date = new Date(retainTime.getTime() + (retainDay + 2) * 24 * 60 * 60 * 1000);
			} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY) {
				date = new Date(retainTime.getTime() + (retainDay + 2) * 24 * 60 * 60 * 1000);
			} else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
				date =  new Date(retainTime.getTime() + (retainDay + 1) * 24 * 60 * 60 * 1000);
			}
			//忽略节日
			//date = HolidayUtil.checkHoliday(date);
		}
		return date;
	}*/

	/**
	 * 判断时间是否在今天时间段内
	 * @param nowTime
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public static boolean dayBelongCalendar(Date nowTime, Date beginTime, Date endTime){
	    SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
	    try {
	        nowTime = df.parse(df.format(new Date()));
	        beginTime = df.parse(df.format(beginTime));
	        endTime = df.parse(df.format(endTime));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return belongCalendar(nowTime, beginTime, endTime);
	}


	/**
	 * 判断时间是否在时间段内
	 * @param nowTime
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
	    Calendar date = Calendar.getInstance();
	    date.setTime(nowTime);
	
	    Calendar begin = Calendar.getInstance();
	    begin.setTime(beginTime);
	
	    Calendar end = Calendar.getInstance();
	    end.setTime(endTime);
	
	    if (date.after(begin) && date.before(end)) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
	/**
     * 获取当前日期是星期几
     * 
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekStringOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
    public static int getWeekOfDate(Date dt) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(dt);
    	return cal.get(Calendar.DAY_OF_WEEK);
    }
	
	public static void main(String[] args) {
		Date yesterdayDate = null;
		String eighteenString ="";
		//昨天18点前（周一分配上周五）
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY) {
			yesterdayDate = DateUtils.getBeforeDayDate(3);
			eighteenString = DateUtils.DateToStr(yesterdayDate, DateUtils.TIME_FORMAT).substring(0, 10) + " 21:00:00";
		} else {
			yesterdayDate = DateUtils.getBeforeDayDate(2);
			eighteenString = DateUtils.DateToStr(yesterdayDate, DateUtils.TIME_FORMAT).substring(0, 10) + " 18:00:00";
		}
		Date date = DateUtils.convertDate(eighteenString, DateUtils.TIME_FORMAT);
		System.out.println(date);
		//System.out.println(enDateStrToDate("2018-05-01"));
	}

}