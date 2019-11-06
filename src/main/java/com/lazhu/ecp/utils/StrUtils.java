package com.lazhu.ecp.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;

/**
 * 字符串工具类
 * 
 * @author 佘崔
 * 
 */
public class StrUtils {

	public static final String CHINESE = "[\\u4e00-\\u9fa5]";
	public static final String ENGLISH = "[a-z||A-Z]";
	public static final String NUMERIC = "[0-9] ";

	/**
	 * 判断字符串是否不为空或者不为空字符
	 * 
	 * @param str
	 *            允许为NULL
	 * @return
	 */
	public static boolean isNotNullOrBlank(Object o) {
		if (null == o) {
			return false;
		} else {
			String str = o.toString().trim();
			if ("".equals(str.trim())) {
				return false;
			} else if (str.equals("-1")) {
				return false;
			} else if (str.equals("undefined")) {
				return false;
			}  else if (str.toLowerCase().equals("null")) {
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * 判断字符串是否为空或者不为空字符
	 * 
	 * @param str
	 *            允许为NULL
	 * @return
	 */
	public static boolean isNullOrBlank(Object o) {
		return !isNotNullOrBlank(o);
	}

	/**
	 * 将对象转换为String类型
	 * 
	 * @param str
	 * @return
	 */
	public static String toString(Object o) {
		if (StrUtils.isNullOrBlank(o)) {
			return "";
		} else {
			return o.toString();
		}
	}

	public static String toString(Object o, String d) {
		if (null == o) {
			return d;
		} else {
			return o.toString();
		}
	}

	/**
	 * 将对象转换为Long类型
	 * 
	 * @param str
	 * @return
	 */
	public static Long toLong(Object o, Long defaultValue) {
		if (null == o) {
			return defaultValue;
		} else {
			return new Long(o.toString());
		}
	}

	/**
	 * 将对象转换为Long类型
	 * 
	 * @param str
	 * @return
	 */
	public static Long toLong(Object o) {
		if (null == o) {
			return null;
		} else {
			return new Long(o.toString());
		}
	}

	/**
	 * 将对象转换为int类型
	 * 
	 * @param str
	 * @return
	 */
	public static Integer toInteger(Object o) {
		if (null == o || "null".equals(o.toString().trim()) || "".equals(o.toString().trim())) {
			return null;
		} else{
			//if(!o.toString().trim().matches("[-+]*[0-9][.][0-9]+|[-+]*[1-9][0-9]*|^[0]$")){
			//if(!o.toString().trim().matches("[0-9]{1,}")){
			//if(!o.toString().trim().matches("^(\-|\+)?\d+(\.\d+)?$")){
			if(!o.toString().trim().matches("^(-?\\d+)(\\.\\d+)?$")){
				   System.out.println(o+"包含非数字");
				   return null;
			}else{
				try{
					return Integer.valueOf((o.toString().trim()));
				   }catch(Exception e){
				    System.out.println(o+"数字太大");
				    return null;
				   }
			}
		}
	}

	/**
	 * 将对象转换为Long类型
	 * 
	 * @param str
	 * @return
	 */
	public static Integer toInteger(Object o, int value) {
		if (isNullOrBlank(o)) {
			return value;
		} else {
			return toInteger(o.toString());
		}
	}
	
	public static Integer toInt(Object o) {
		if (null == o || "null".equals(o.toString().trim()) || "".equals(o.toString().trim())) {
			return 0;
		} else {
			return toInteger(o.toString().trim());
		}
	}
	
	/**
	 * 将对象转换为double类型
	 * 
	 * @param str
	 * @return
	 */
	public static Double toDouble(Object o, double value) {
		if (isNullOrBlank(o)) {
			return value;
		} else {
			return new Double(o.toString());
		}
	}
	
	public static Double toDouble(Object o) {
		if (isNullOrBlank(o)) {
			return null;
		} else {
			return new Double(o.toString());
		}
	}
	
	public static Date toTime(Object o) {
		if (null == o) {
			return null;
		} else {
			return (Date) o;
		}
	}

	/**
	 * 砍掉前后逗号
	 * 
	 * @param str
	 * @return
	 */
	public static String cutLastDh(String str) {
		if (null == str) {
			return str;
		}
		if (str.endsWith("，")) {
			str = str.substring(0, str.length() - 1);
		} else if (str.endsWith(",")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	public static String firstUpperCase(String name) {
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		return name;
	}

	/**
	 * 获取所有的子ID集合
	 * 
	 * @param parentId
	 * @param ids
	 * @return
	 */
	public static String getChildId(Integer parentId, List list) {
		if (null == parentId) {
			return null;
		}
		StringBuffer sb = new StringBuffer(parentId + ",");
		getChildId(sb, parentId, list);
		return StrUtils.cutLastDh(sb.toString());
	}

	public static void getChildId(StringBuffer sb, int parentId, List list) {
		Map map = null;
		for (int i = 0; i < list.size(); i++) {
			map = (Map) list.get(i);
			if (null != StrUtils.toInteger(map.get("parent_id")) && parentId == StrUtils.toInteger(map.get("parent_id"))) {
				sb.append(StrUtils.toInteger(map.get("id")) + ",");
				getChildId(sb, StrUtils.toInteger(map.get("id")), list);
			}
		}
	}

	public static String getExcelText(HSSFCell cell) {
		if (null == cell) {
			return null;
		} else {
			if (cell.toString().indexOf(".") != -1 && cell.toString().indexOf("E") != -1) {
				DecimalFormat df = new DecimalFormat();
				try {
					return df.parse(cell.toString()).toString();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			return cell.toString();
		}
	}

	public static boolean equal(Object o1, Object o2) {
		if (null != o1 && null != o2 && o1.toString().equals(o2.toString())) {
			return true;
		}
		return false;
	}

	public static String delHTMLTag(Object html) {
		if (null == html) {
			return null;
		}
		String htmlStr = html.toString();
		String regEx_img_html = "<img[^>]+>"; // 定义HTML标签的正则表达式
		String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
		Pattern p_img = Pattern.compile(regEx_img_html, Pattern.CASE_INSENSITIVE);
		Matcher m_img = p_img.matcher(htmlStr);
		htmlStr = m_img.replaceAll("【图片不显示】"); // 过滤script标签
		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签
		return htmlStr.trim(); // 返回文本字符串
	}

	public static String trim(String str) {
		if (null != str) {
			return str.trim();
		}
		return null;
	}
	
	/**
	 * 是否全是相同的数字或者字母（如：000000、111111、aaaaaa）全部相同返回true
	 * 
	 * @param arg
	 *            检测的字符串
	 * @return true/false
	 */
	public static boolean isEqual(String arg) {
		boolean flag = true;
		char str = arg.charAt(0);
		for (int i = 0; i < arg.length(); i++) {
			if (str != arg.charAt(i)) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	/**
	 * 不能是连续的数字--递增（如：123456、12345678）连续数字返回true
	 * 
	 * @param arg
	 *            检测的字符串
	 * @return true/false
	 */
	public static boolean isOrderNumericAsc(String arg) {
		boolean flag = true;// 如果全是连续数字返回true
		boolean isNumeric = true;// 如果全是数字返回true
		for (int i = 0; i < arg.length(); i++) {
			if (!Character.isDigit(arg.charAt(i))) {
				isNumeric = false;
				break;
			}
		}
		if (isNumeric) {// 如果全是数字则执行是否连续数字判断
			for (int i = 0; i < arg.length(); i++) {
				if (i > 0) {// 判断如123456
					int num = Integer.parseInt(arg.charAt(i) + "");
					int num_ = Integer.parseInt(arg.charAt(i - 1) + "") + 1;
					if (num != num_) {
						flag = false;
						break;
					}
				}
			}
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * 不能是连续的数字--递减（如：987654、876543）连续数字返回true
	 * 
	 * @param arg
	 *            检测的字符串
	 * @return true/false
	 */
	public static boolean isOrderNumericDesc(String arg) {
		boolean flag = true;// 如果全是连续数字返回true
		boolean isNumeric = true;// 如果全是数字返回true
		for (int i = 0; i < arg.length(); i++) {
			if (!Character.isDigit(arg.charAt(i))) {
				isNumeric = false;
				break;
			}
		}
		if (isNumeric) {// 如果全是数字则执行是否连续数字判断
			for (int i = 0; i < arg.length(); i++) {
				if (i > 0) {// 判断如654321
					int num = Integer.parseInt(arg.charAt(i) + "");
					int num_ = Integer.parseInt(arg.charAt(i - 1) + "") - 1;
					if (num != num_) {
						flag = false;
						break;
					}
				}
			}
		} else {
			flag = false;
		}
		return flag;
	}
	
	/**是否包含中文，空值返回false**/
	public static boolean isChineseChar(String str){
		if (isNullOrBlank(false)) {
			return true;
		}
        boolean temp = false;
        Pattern p=Pattern.compile("[\u4e00-\u9fa5]"); 
        Matcher m=p.matcher(str); 
        if(m.find()){ 
            temp =  true;
        }
        return temp;
    }
	public static boolean isEnglishChar(String str){
		if (isNullOrBlank(false)) {
			return true;
		}
		boolean temp = false;
		Pattern p=Pattern.compile("[a-z]|[A-Z]"); 
		Matcher m=p.matcher(str); 
		if(m.find()){ 
			temp =  true;
		}
		return temp;
	}
	
	/**
	 * 是否手机号
	 * @param str
	 * @return
	 */
	public static boolean isMobile(String str){
		if (isNullOrBlank(str) || str.length() != 11) {
			return false;
		}
		Pattern pattern = null;
	    try {
	    	//pattern = Pattern.compile("^1\\d{10}$");
	    	pattern = Pattern.compile("^1[3|4|5|6|7|8|9]\\d{9}$");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	    return pattern.matcher(str).matches();
	}
	
	public static String hideNum (String str) {
		if (null == str || str == "" || str.length() <= 0) {
			return "";
		}
		String hide = "";
		if (str.length() > 3 && str.length() < 8) {
			for (int i = 0; i < str.length() - 3; i++) {
				hide += "*";
			}
			return str.substring(0,3) + hide;
		} else if (str.length() >= 8 && str.length() <= 12) {
			for (int i = 0; i < str.length() - 4; i++) {
				hide += "*";
			}
			return str.substring(0,5) + hide;
		} else if (str.length() > 12) {
			for (int i = 0; i < str.length() - 5; i++) {
				hide += "*";
			}
			return str.substring(0,7) + hide;
		}
		return str;
	}
	
	/**
	 * 隐藏字符串中间部分
	 * @param str
	 * @return
	 */
	public static String hideMiddle (String str) {
		if (null == str || str == "" || str.length() <= 0) {
			return "";
		}
		if (str.length() > 4 && str.length() < 7) {
			return str.toString().substring(0, 2) + "***" + str.toString().substring(5, str.toString().length());
		} else if (str.length() >= 7 && str.length() < 11) {
			return str.toString().substring(0, 2) + "****" + str.toString().substring(6, str.toString().length());
		} else if (str.length() == 11) {
			return str.toString().substring(0, 3) + "*****" + str.toString().substring(8, str.toString().length());
		}else if (str.length() > 11) {
			return str.toString().substring(0, 5) + "*****" + str.toString().substring(10, str.toString().length());
		}
		return str;
	}
	
	public static String hideMobile (String str) {
		if (null == str || str == "" || str.length() <= 0) {
			return "";
		}
		StringBuffer s1 = new StringBuffer(str);
		StringBuffer s2 = new StringBuffer(str);
		if (str.length() == 11) {
			return s1.toString().substring(0, 3) + "****" + s2.toString().substring(7, str.toString().length());
		}
		return hideMiddle(str);
	}
	
	/**
	 * 是否以逗号分隔的字符串（不检测一位数的）
	 * str
	 * @return
	 */
	public static boolean isCommaDouble (String str) {
		if (isNullOrBlank(str)) {
			return false;
		}
		if (isChineseChar(str)) {
			return false;
		}
		if (isEnglishChar(str)) {
			return false;
		}
		//
		if (str.length() <= 2 && isNumeric(str)) {
			return true;
		} else {
			Pattern p=Pattern.compile("^\\d+(,)*(.)*(\\d+)+$"); 
			Matcher m=p.matcher(str); 
			if(m.matches()){ 
				return true;
			}
		}
        return false;
	}

	/**
	 * 获取字符串中首个逗号前的值
	 * @param str
	 * @return	Double
	 */
	public static String getFirstCommaValue(String str) {
		if (isNullOrBlank(str)) {
			return null;
		}
		String[] arr = str.replaceAll("，", ",").split(",");
		if (null != arr) {
			for (String string : arr) {
				if (StrUtils.isNotNullOrBlank(string)) {
					return string;
				}
			}
		}
		return null;
	}
	
	/**
	 * 是否纯数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(Object str){
		if (isNullOrBlank(str)) {
			return false;
		}
		String string = String.valueOf(str);
		Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(string).matches();
	}
	/**
	 * 识别身份证年龄
	 * @param identifyNumber	身份证号码
	 * @return
	 */
	public static Integer identifyAge(String identifyNumber) {
		if (isNullOrBlank(identifyNumber) || identifyNumber.length() != 18) {
			return null;
		}
		String yearString = identifyNumber.substring(6, 10);
		Integer currentYear = DateUtils.getYear(new Date());
		if (isNumeric(yearString) && isNumeric(currentYear)) {
			return currentYear - Integer.valueOf(yearString);
		} else {
			System.out.println("身份证号码："+identifyNumber+"，无法识别年龄");
			return null;
		}
	}

	/**
	 * 识别身份证性别
	 * @param identifyNumber	身份证号码
	 * @return
	 */
	public static Integer identifySex(String identifyNumber) {
		if (isNullOrBlank(identifyNumber) || identifyNumber.length() != 18) {
			return null;
		}
		String seventeenString = identifyNumber.substring(16,17);
		if (isNumeric(seventeenString)) {
			Integer seventeen  = Integer.valueOf(seventeenString);
			if (seventeen % 2 == 0) {
				return 2;
			} else {
				return 1;
			}
		} else {
			System.out.println("身份证号码："+identifyNumber+"，无法识别性别");
			return 0;
		}
	}
	
	/**
	 * 按照规则匹配字符串
	 * @param p		规则
	 * [\\u4e00-\\u9fa5] , [a-z||A-Z] , [0-9] 
	 * @param str	原字符串
	 * @return
	 */
	public static String matchResult(Pattern p,String str) {
        StringBuilder sb = new StringBuilder();
        Matcher m = p.matcher(str);
        while (m.find()) {
        	for (int i = 0; i <= m.groupCount(); i++) {  
        		sb.append(m.group());
        	}
        }
        return sb.toString();
    }
	
	/**
	 * 过滤所有非数字字符串
    * @param str	需要过滤的字符串
    * @return
    * @Description:过滤数字以外的字符
    */
   public static String filterUnNumber(String str) {
       // 只允数字
       String regEx = "[^0-9]";
       Pattern p = Pattern.compile(regEx);
       Matcher m = p.matcher(str);
       //替换与模式匹配的所有字符（即非数字的字符将被""替换）
       return m.replaceAll("").trim();
   }

	/**
	 * 删除手机号前缀"0"
	 * @param nasCallederid
	 * @return
	 */
	public static String prefixDelZero(String nasCallederid) {
		if (isNullOrBlank(nasCallederid)) {
			return null;
		}
		if (nasCallederid.indexOf("0") == 0 && nasCallederid.length() == 12) {
			return nasCallederid.substring(1);
		}
		return nasCallederid;
	}
	
	public static int getRandom(int max, int min){
	    Random random = new Random();
	    int s = random.nextInt(max) % (max - min + 1) + min;
	    return s;

	}
	
	/**
	 * 字符串转化为UTF-8格式（慎用）
	 * @param str
	 * @return
	 */
	public static String getUTF8String(String str) {
		if (isNullOrBlank(str)) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(str);
		String xmString = "";
		String xmlUTF8 = "";
		try {
			xmString = new String(sb.toString().getBytes("UTF-8"));
			xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");
			System.out.println("utf-8 编码：" + xmlUTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return xmlUTF8;
	}
	
	/**
	 * 时间口令（年月日时）
	 * @return
	 */
	public static Long hourWordPass () {
		Integer dateInt = DateUtils.DateToInteger(new Date(), DateUtils.HOUR_FORMAT_NUMERIC);
		Integer p1 = StrUtils.toInt(dateInt.toString().substring(1)) * 5;
		Integer p2 = StrUtils.toInt(dateInt.toString().substring(2)) * 4;
		Integer p3 = StrUtils.toInt(dateInt.toString().substring(3)) * 3;
		return (long) (((dateInt * 123) + p1 + p2 + p3) * 2);
	}

	/**
	 * 时间口令（年月日时）判断
	 * @param word
	 * @param size
	 * @return
	 */
	public static boolean hourWord (Long word) {
		if (null == word || word <= 0) {
			return false;
		}
		Long pass = hourWordPass();
		if (word.longValue() == pass.longValue()) {
			return true;
		}
		return false;
	}
	
	public static String MD5(Object key) {
		char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        try {
            byte[] btInput = toString(key).getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
	}
	
	/**
	 * 生产批号（前缀）
	 * @param prefix 
	 * @return
	 */
	public static String newBatchNoShort(String prefix) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
		return prefix + df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
	}

	/**
	 * 生产批号（长）
	 * @return
	 */
	public static String newBatchNoLong() {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");//设置日期格式
		return LocalDateTime.now().format(fmt);
	}
	
	/**
	 * dingIdCheck:(钉钉ID检验是否合法)
	 * @param dingId
	 */
	public static boolean dingIdCheck(String dingId) {
		if (isNullOrBlank(dingId)) {
			return false;
		}
		if (dingId.length() < 8 || (dingId.length() <= 11 && isMobile(dingId))) {
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		System.out.println(newBatchNoShort("dingMsg"));
		System.out.println(newBatchNoLong());
	}
	
}
