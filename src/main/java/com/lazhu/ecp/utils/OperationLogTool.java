package com.lazhu.ecp.utils;
/**
 * 敏感操作日志记录
 */
import java.util.Date;

import com.lazhu.core.util.PropertiesUtil;

public class OperationLogTool {
	
	private static String LOG_PATH;
	
	static {
		LOG_PATH = PropertiesUtil.getString("operation.log.path");
	}
	
	public static void operationLog(String info){
		String datestr = DateUtils.DateToStr(new Date(),DateUtils.TIME_FORMAT);
	    String path = LOG_PATH + "/logs/operationLog/operationLog_"+DateUtils.DateToStr(new Date(),DateUtils.DATE_FORMAT)+".log";
		FileTool.writeToFile(path, datestr+":"+info);
	}
	
	public static void operationLog(String path,String info){
		String datestr = DateUtils.DateToStr(new Date(),DateUtils.TIME_FORMAT);
		String pathAll = LOG_PATH + "/logs/" + path + "/"+path+"_"+DateUtils.DateToStr(new Date(),DateUtils.DATE_FORMAT)+".log";
		FileTool.writeToFile(pathAll, datestr+":"+info);
	}
	
	/*public static void operationLog(String info){
		String datestr = DateUtils.DateToStr(new Date(),DateUtils.TIME_FORMAT);
		String s = OperationLogTool.class.getClassLoader().getResource("").toString();
		//file:/D:/stock_weixin/ROOT/WEB-INF/classes/
		s = s.replace("file:/", "").replace("WEB-INF/classes/", "");
		//String path = ServletActionContext.getServletContext().getRealPath("/")+"operationLog\\";
		String path = s +"operationLog\\operationLog_"+DateUtils.DateToStr(new Date(),DateUtils.DATE_FORMAT)+".log";
		FileTool.writeToFile(path, datestr+":"+info);
	}
	
	public static void operationLog(String path,String info){
		String datestr = DateUtils.DateToStr(new Date(),DateUtils.TIME_FORMAT);
		String s = OperationLogTool.class.getClassLoader().getResource("").toString();
		//file:/D:/stock_weixin/ROOT/WEB-INF/classes/
		s = s.replace("file:/", "").replace("WEB-INF/classes/", "");
		//String path = ServletActionContext.getServletContext().getRealPath("/")+"operationLog\\";
		String pathAll = s + path + "\\"+path+"_"+DateUtils.DateToStr(new Date(),DateUtils.DATE_FORMAT)+".log";
		FileTool.writeToFile(pathAll, datestr+":"+info);
	}*/
}
