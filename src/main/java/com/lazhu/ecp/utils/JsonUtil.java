package com.lazhu.ecp.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.map.ListOrderedMap;

import com.lazhu.crm.Constant;



public class JsonUtil {
	
	public static String getJsonStr (String json) {
		String content = "";
		if (StrUtils.isNotNullOrBlank(json)) {
			try {
				List<Map<String,Object>> list = parseJSON2List(json);
				if (null != list && list.size() > 0) {
					for (Map<String, Object> map : list) {
						content += map.get("key") + "：" + map.get("value") + "；";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return content;
	}
	
	/**
     * 
    * json转换list.
    * <br>详细说明
    * @param jsonStr json字符串
    * @return
    * @return List<Map<String,Object>> list
    * @throws
    * @author slj
    * @date 2013年12月24日 下午1:08:03
     */
    public static List<Map<String, Object>> parseJSON2List(String jsonStr){
        JSONArray jsonArr = JSONArray.fromObject(jsonStr);
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        Iterator<JSONObject> it = jsonArr.iterator();
        while(it.hasNext()){
            JSONObject json2 = it.next();
            list.add(parseJSON2Map(json2.toString()));
        }
        return list;
    }

   /**
    * 
   * json转换map.
   * <br>详细说明
   * @param jsonStr json字符串
   * @return
   * @return Map<String,Object> 集合
   * @throws
   * @author slj
    */
    public static Map<String, Object> parseJSON2Map(String jsonStr){
        ListOrderedMap map = new ListOrderedMap();
        //最外层解析
        JSONObject json = JSONObject.fromObject(jsonStr);
        for(Object k : json.keySet()){
            Object v = json.get(k); 
            //如果内层还是数组的话，继续解析
            if(v instanceof JSONArray){
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
                Iterator<JSONObject> it = ((JSONArray)v).iterator();
                while(it.hasNext()){
                    JSONObject json2 = it.next();
                    list.add(parseJSON2Map(json2.toString()));
                }
                map.put(k.toString(), list);
            } else {
                map.put(k.toString(), v);
            }
        }
        return map;
    }

    /**
     * 
    * 通过HTTP获取JSON数据.
    * <br>通过HTTP获取JSON数据返回list
    * @param url 链接
    * @return
    * @return List<Map<String,Object>> list
    * @throws
    * @author slj
     */
    public static List<Map<String, Object>> getListByUrl(String url){
        try {
            //通过HTTP获取JSON数据
            InputStream in = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line=reader.readLine())!=null){
                sb.append(line);
            }
            return parseJSON2List(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

   /**
    * 
   * 通过HTTP获取JSON数据.
   * <br>通过HTTP获取JSON数据返回map
   * @param url 链接
   * @return
   * @return Map<String,Object> 集合
   * @throws
   * @author slj
    */
    public static Map<String, Object> getMapByUrl(String url){
        try {
            //通过HTTP获取JSON数据
            InputStream in = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line=reader.readLine())!=null){
                sb.append(line);
            }
            return parseJSON2Map(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 
    * map转换json.
    * <br>详细说明
    * @param map 集合
    * @return
    * @return String json字符串
    * @throws
    * @author slj
     */
    public static String mapToJson(Map<String, String> map) {
        Set<String> keys = map.keySet();
        String key = "";
        String value = "";
        StringBuffer jsonBuffer = new StringBuffer();
        jsonBuffer.append("{");
        for (Iterator<String> it = keys.iterator(); it.hasNext();) {
            key = (String) it.next();
            value = map.get(key);
            jsonBuffer.append(key + ":" +"\""+ value+"\"");
            if (it.hasNext()) {
                jsonBuffer.append(",");
            }
        }
        jsonBuffer.append("}");
        return jsonBuffer.toString();
    }

    //test
    public static void main(String[] args) {
        String url = "http://...";
        List<Map<String,Object>> list = getListByUrl(url);
        System.out.println(list);
    }
    
    
    
    
    
    
    
    
    
	
	//过滤搜索特殊字符（输入）
	public static String replaceSpecialSearch(Object obj){
		String str = StrUtils.toString(obj);
		str = str.replace(":","#");
		str = str.replace("'","#");
		str = str.replace("\"","#");
		str = str.replace("\"","#");
		str = str.replace("[","#");
		str = str.replace("]","#");
		str = str.replace("{","#");
		str = str.replace("}","#");
		str = str.replace("/","#");
		str = str.replace("\\","#");
		str = str.replace("?","#");
		str = str.replace(">","#");
		str = str.replace("<","#");
		str = str.replace("=","#");
		str = str.replace("&","#");
		return str;
	}
	
	//过滤json特殊字符（输出）
	public static String replaceSpecial(Object obj){
		String str = StrUtils.toString(obj);
		str = str.replace("（","#");
		str = str.replace("）","#");
		str = str.replace("(","#");
		str = str.replace(")","#");
		str = str.replace("【","#");
		str = str.replace("】","#");
		str = str.replace("[","#");
		str = str.replace("]","#");
		str = str.replace("{","#");
		str = str.replace("}","#");
		
		//str = str.replace(":","#");
		//str = str.replace("：","#");
		//str = str.replace(";","#");
		//str = str.replace("；","#");
		str = str.replace("'","#");
		str = str.replace("\"","#");
		str = str.replace("\\","#");
		str = str.replace("|","#");
		
		//str = str.replace("《","#");
		//str = str.replace("》","#");
		//str = str.replace("<","#");
		//str = str.replace(">","#");
		//str = str.replace(",","#");
		//str = str.replace(".","#");
		//str = str.replace("，","#");
		//str = str.replace("。","#");
		str = str.replace("/","#");
		str = str.replace("？","#");
		str = str.replace("?","#");
		str = str.replace("、","#");
		
		str = str.replace("·","#");
		str = str.replace("`","#");
		str = str.replace("~","#");
		str = str.replace("！","#");
		str = str.replace("!","#");
		str = str.replace("@","#");
		str = str.replace("$","#");
		str = str.replace("￥","#");
		str = str.replace("%","#");
		str = str.replace("^","#");
		str = str.replace("……","#");
		str = str.replace("&","#");
		str = str.replace("*","#");
		str = str.replace("+","#");
		str = str.replace("-","#");
		str = str.replace("——","#");
		str = str.replace("_","#");
		str = str.replace("=","#");
		
		str = str.replace("\b","#");
		str = str.replace("\t","#");
		str = str.replace("\n","#");
		str = str.replace("\f","#");
		str = str.replace("\r","#");
		return str;
	}
	
	public static void replaceSpecialMap(Map map){
		//{ : " 等
		String id = null;
		Iterator it=map.entrySet().iterator();  
		while(it.hasNext()){    
			Map.Entry entry = (Map.Entry)it.next();           
		    if (StrUtils.isNullOrBlank(entry.getValue())) {
		    	//正则表达式
		    	StrUtils.toString(entry.getValue()).replace(":","#");
		    	StrUtils.toString(entry.getValue()).replace("'","#");
		    	StrUtils.toString(entry.getValue()).replace("\"","#");
		    	StrUtils.toString(entry.getValue()).replace("[","#");
		    	StrUtils.toString(entry.getValue()).replace("]","#");
		    	StrUtils.toString(entry.getValue()).replace("{","#");
		    	StrUtils.toString(entry.getValue()).replace("}","#");
		    	StrUtils.toString(entry.getValue()).replace("=","#");
		    	StrUtils.toString(entry.getValue()).replace("+","#");
		    	StrUtils.toString(entry.getValue()).replace("-","#");
		    	StrUtils.toString(entry.getValue()).replace("*","#");
		    	StrUtils.toString(entry.getValue()).replace("/","#");
		    	StrUtils.toString(entry.getValue()).replace("\\","#");
			}
		}
	}
	

	//从json中取出某个参数
	public static String getParameterByJson(String jsonString, String string){
		try {
			if (StrUtils.isNotNullOrBlank(jsonString) && StrUtils.isNotNullOrBlank(string)) {
				// 将json字符串分组
				int i = countStr(jsonString,string);
				//确定该参数唯一
				if (i == 1) {
					String str = jsonString.substring(jsonString.indexOf(string)+8,(jsonString.indexOf(string)+8)+6);
					str = str.replace("\"", "0");
					//System.out.println(str);
					boolean result=str.matches("\\d+(\\.\\d+)?");
					if (result) {
						return str;
					}else{
						return "error";
					}	
				}else{
					return "error";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error";
	}

	public static int countStr(String str1, String str2) {
		int counter = 0;
        if (str1.indexOf(str2) == -1) {  
            return 0;  
        } else if (str1.indexOf(str2) != -1) {  
            counter++;  
            countStr(str1.substring(str1.indexOf(str2) +  
                   str2.length()), str2);
               return counter;  
        }  
            return 0;  
    } 
	
}
