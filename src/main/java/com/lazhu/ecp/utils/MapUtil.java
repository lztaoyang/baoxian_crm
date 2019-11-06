package com.lazhu.ecp.utils;

import java.util.Iterator;
import java.util.Map;

/**
 * Title: map判断不含空值
* Description: 1.控制整个页面的参数都为必填，2.sql传参时整体判断不为空
* @author hz
* @date 2017年1月11日上午9:42:26
 */
public class MapUtil {

	public static boolean isFull(Map map){
		boolean flag = true;
		Iterator it=map.entrySet().iterator();           
		/*String key;           
		String value;*/    
		while(it.hasNext()){    
			Map.Entry entry = (Map.Entry)it.next();           
		    /*key=entry.getKey().toString();           
		    value=entry.getValue().toString();           
		    System.out.println(key+"===="+value);*/
		    if (StrUtils.isNullOrBlank(entry.getValue())) {
		    	System.out.println("参数不能为空："+entry.getKey());
				flag = false;
				//break;
			}
		}
		return flag;
	}
	
		
}
