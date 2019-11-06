package com.lazhu.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ClassName: ChineseCalender <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2019年8月16日 上午11:27:23 <br/>
 *
 * @author huangzhong
 * @version 
 * @since JDK 1.8
 */
public class ChineseCalendar {
	
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	
    // 法律规定的放假日期
    private static List<String> lawHolidays = new ArrayList<String>(
    	Arrays.asList(
    		"2018-12-30", "2018-12-31", "2019-01-01",	//元旦是1月1日，2018年12月30日、31日、2019年1月1日共放假3天。其中，1月1日是法定假日。2018年12月29日（星期六）上班
            
    		"2019-02-04", "2019-02-05", "2019-02-06",
            "2019-02-07", "2019-02-08", "2019-02-09",
            "2019-02-10",								//春节2月4日至2月10日，共7天。 其中，2月5日、2月6日、2月7日是法定假日。 2月2日（星期六）、2月3日（星期日）上班。
            
            "2019-04-05", "2019-04-06", "2019-04-07",	//清明节4月5日至4月7日，共3天
            
            "2019-05-01", "2019-05-02", "2019-05-03",
            "2019-05-04",								//劳动节是5月1日至5月4日，共4天。其中，5月1日劳动节是法定假日
            
            "2019-06-07", "2019-06-08", "2019-06-09",	//端午节6月7日至6月9日
            
            "2019-09-13", "2019-09-14", "2019-09-15",	//中秋节9月13日至9月15日
            
            "2019-10-01", "2019-10-02", "2019-10-03",
            "2019-10-04", "2019-10-05", "2019-10-06",
            "2019-10-07")								//国庆节是10月1日至10月7日，共7天。其中，10月1日、10月2日、10月3日是法定假日。 9月29日（星期日）、10月12日（周六）为正常班，需要上班
    );
    // 由于放假需要额外工作的周末
    private static List<String> extraWorkdays = new ArrayList<String>(
    	Arrays.asList(
            "2018-12-29",					//元旦补班
            
            "2019-02-02", "2019-02-03",		//春节补班
            
            "2019-05-05",					//劳动节补班
            
            "2019-09-29", "2019-10-12") 	//国庆节补班
     );

    /**
     * 判断是否是法定假日
     * 
     * @param calendar
     * @return
     * @throws Exception
     */
    public static boolean isLawHoliday(String calendar) throws Exception {
        isMatchDateFormat(calendar);
        if (lawHolidays.contains(calendar)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是周末
     * 
     * @param calendar
     * @return
     * @throws ParseException
     */
    public static boolean isWeekends(String calendar) throws Exception {
        isMatchDateFormat(calendar);
        // 先将字符串类型的日期转换为Calendar类型
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Date date = sdf.parse(calendar);
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        if (ca.get(Calendar.DAY_OF_WEEK) == 1
                || ca.get(Calendar.DAY_OF_WEEK) == 7) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是需要额外补班的周末
     * 
     * @param calendar
     * @return
     * @throws Exception
     */
    public static boolean isExtraWorkday(String calendar) throws Exception {
        isMatchDateFormat(calendar);
        if (extraWorkdays.contains(calendar)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是休息日（包含法定节假日和不需要补班的周末）
     * 
     * @param calendar
     * @return
     * @throws Exception
     */
    public static boolean isHoliday(String calendar) throws Exception {
        isMatchDateFormat(calendar);
        // 首先法定节假日必定是休息日
        if (isLawHoliday(calendar)) {
            return true;
        }
        // 排除法定节假日外的非周末必定是工作日
        if (!isWeekends(calendar)) {
            return false;
        }
        // 所有周末中只有非补班的才是休息日
        if (isExtraWorkday(calendar)) {
            return false;
        }
        return true;
    }
    public static boolean isHolidayDate(Date date) {
    	try {
    		String calendar = DateToStr(date, DATE_FORMAT);
    		// 首先法定节假日必定是休息日
			if (isLawHoliday(calendar)) {
				return true;
			}
			// 排除法定节假日外的非周末必定是工作日
			if (!isWeekends(calendar)) {
				return false;
			}
			// 所有周末中只有非补班的才是休息日
			if (isExtraWorkday(calendar)) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return true;
    }

    /**
     * 判断是否是工作日
     * 
     * @param calendar
     * @return
     * @throws Exception
     */
    public static boolean isWorkday(String calendar) throws Exception {
        isMatchDateFormat(calendar);
        return !(isHoliday(calendar));
    }
    public static boolean isWorkdayDate(Date date) {
    	return !(isHolidayDate(date));
    }
    /**
     * 查询工作日的天数
     */
    public static int workdayNum(List<Date> list) {
    	if (null != list && list.size() > 0) {
    		int num = 0;
    		for (Date date : list) {
    			if (isWorkdayDate(date)) {
    				num ++;
    			}
			}
			return num;
		} else {
			return 0;
		}
    }

    public static int getTotalDays() {
        return new GregorianCalendar().isLeapYear(Calendar.YEAR) ? 366 : 365;
    }

    public static int getTotalLawHolidays() {
        return lawHolidays.size();
    }

    public static int getTotalExtraWorkdays() {
        return extraWorkdays.size();
    }

    /**
     * 获取一年中所有周末的天数
     * @return
     */
    public static int getTotalWeekends() {
        List<String> saturdays = new ArrayList<String>();
        List<String> sundays = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int nextYear = 1 + calendar.get(Calendar.YEAR);
        Calendar cstart = Calendar.getInstance();
        Calendar cend = Calendar.getInstance();
        cstart.set(currentYear, 0, 1);// 今年的元旦
        cend.set(nextYear, 0, 1);// 明年的元旦
        return getTotalSaturdays(saturdays, calendar, cstart, cend,
                currentYear)
                + getTotalSundays(sundays, calendar, cstart, cend,
                        currentYear);
    }

    private static int getTotalSaturdays(List<String> saturdays, Calendar calendar,
            Calendar cstart, Calendar cend, int currentYear) {
        // 将日期设置到上个周六
        calendar.add(Calendar.DAY_OF_MONTH, -calendar.get(Calendar.DAY_OF_WEEK));
        // 从上周六往这一年的元旦开始遍历，定位到去年最后一个周六
        while (calendar.get(Calendar.YEAR) == currentYear) {
            calendar.add(Calendar.DAY_OF_YEAR, -7);
        }
        // 将日期定位到今年第一个周六
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        // 从本年第一个周六往下一年的元旦开始遍历
        for (; calendar.before(cend); calendar.add(Calendar.DAY_OF_YEAR, 7)) {
            saturdays.add(calendar.get(Calendar.YEAR) + "-"
                    + calendar.get(Calendar.MONTH) + "-"
                    + calendar.get(Calendar.DATE));
        }
        return saturdays.size();
    }

    private static int getTotalSundays(List<String> sundays, Calendar calendar,
            Calendar cstart, Calendar cend, int currentYear) {
        // 将日期设置到上个周日
        calendar.add(Calendar.DAY_OF_MONTH,
                -calendar.get(Calendar.DAY_OF_WEEK) + 1);
        // 从上周日往这一年的元旦开始遍历，定位到去年最后一个周日
        while (calendar.get(Calendar.YEAR) == currentYear) {
            calendar.add(Calendar.DAY_OF_YEAR, -7);
        }
        // 将日期定位到今年第一个周日
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        // 从本年第一个周日往下一年的元旦开始遍历
        for (; calendar.before(cend); calendar.add(Calendar.DAY_OF_YEAR, 7)) {
            sundays.add(calendar.get(Calendar.YEAR) + "-"
                    + calendar.get(Calendar.MONTH) + "-"
                    + calendar.get(Calendar.DATE));
        }
        return sundays.size();
    }

    public static int getTotalHolidays(){
        //先获取不需要补班的周末天数
        int noWorkWeekends = getTotalWeekends() - getTotalExtraWorkdays();
        return noWorkWeekends + getTotalLawHolidays();
    }

    /**
     * 使用正则表达式匹配日期格式
     * 
     * @throws Exception
     */
    private static void isMatchDateFormat(String calendar) throws Exception {
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(calendar);
        boolean flag = matcher.matches();
        if (!flag) {
            throw new Exception("输入日期格式不正确，应该为2019-01-01");
        }
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
	
	/**
	 * 获取两日期之间的所有日期
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return
	 */
	public static List<Date> getDates(Date startDate, Date endDate) {
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
		List<Date> result = new ArrayList<Date>();
		Calendar temp = (Calendar) start.clone();
		temp.add(Calendar.DAY_OF_YEAR, 0);
		while (temp.before(end)) {
			result.add(temp.getTime());
			temp.add(Calendar.DAY_OF_YEAR, 1);//增加一天 放入集合
		}
		return result;
	}
	/** 计算保护期到期时间 **/
	public static Date exactWeekdays(Date startDate, Integer retainDay) {
		Date endDate = dayChange(startDate, retainDay);
		//如果最后一天不是工作日，则继续+1，直到工作日
		while (isHolidayDate(endDate)) {
			endDate = dayChange(endDate, 1);
		}
		int weekDaysNum = workdayNum(getDates(startDate, endDate));
		while (retainDay.intValue() > weekDaysNum) {
			endDate = dayChange(endDate, 1);
			weekDaysNum = workdayNum(getDates(startDate, endDate));
		}
		//如果最后一天不是工作日，则继续+1，直到工作日
		while (isHolidayDate(endDate)) {
			endDate = dayChange(endDate, 1);
		}
		return endDate;
	}

    public static void main(String[] args) throws Exception {
        Date date = new Date();
        //date = convertDate("2019-10-01", DateUtils.DATE_FORMAT);
        String calendar = DateToStr(date, DATE_FORMAT);
        System.out.println(calendar);
        System.out.println("是否是法定节假日：" + isLawHoliday(calendar));
        System.out.println("是否是周末：" + isWeekends(calendar));
        System.out.println("是否是需要额外补班的周末：" + isExtraWorkday(calendar));
        System.out.println("是否是休息日：" + isHoliday(calendar));
        System.out.println("是否是工作日：" + isWorkday(calendar));
        System.out.println("今年总共有" + getTotalDays() + "天");
        System.out.println("今年总共有" + getTotalLawHolidays() + "天法定节假日");
        System.out.println("今年总共有" + getTotalExtraWorkdays() + "天需要补班的周末");
        System.out.println("今年总共有" + getTotalWeekends() + "天周末");
        System.out.println("今年总共有" + getTotalHolidays() + "天休息日");
        
        System.out.println(DateToStr(exactWeekdays(new Date(),2), DATE_FORMAT));
    }

}