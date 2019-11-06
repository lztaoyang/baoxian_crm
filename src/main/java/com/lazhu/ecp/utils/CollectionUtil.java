package com.lazhu.ecp.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.lazhu.core.util.InstanceUtil;

/**
 * Title:
* Description: 
* @author huang
* @date 2016年7月17日下午4:08:58
* Map工具类，快速遍历打印，控制台输出，方便代码调试
 */
public class CollectionUtil {

	//遍历map
	public static void mapPrint(Map map){
		System.out.println("map的数据有  "+map.size()+"  个");
		Iterator it=map.entrySet().iterator();
		while (it.hasNext()) {
			String key = "";
			String value = "";
		    Map.Entry entry = (Map.Entry)it.next();
		    if (entry.getKey() != null) {
		        key=entry.getKey().toString();
			}
		    if (entry.getValue() != null) {
		        value=entry.getValue().toString();
		    }
		    System.out.println(key+"===="+value);
		}
	}
	//遍历list
	public static void listPrint(List list){
		System.out.println("list的数据有  "+list.size()+"  个");
		Iterator it=list.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}
	
	//数组转集合（并去除空格）
	public static List<String> arrayToList (String[] array) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			if (StrUtils.isNotNullOrBlank(array[i])) {
				list.add(array[i]);
			}
		}
		return list;
	}
	
	/**
	 * 数组去重     
	 * @param dicNameList
	 */
	public static void removeDuplicate(List<String> dicNameList) {      
		HashSet h = new HashSet(dicNameList);      
		dicNameList.clear();      
		dicNameList.addAll(h);
	}
	
	/**
	 * 模拟分页
	 * @param idList
	 * @param pageSize
	 * @param pageNum
	 * @return
	 */
	public static List<Long> getPageList(List<Long> idList,int pageNum, int pageSize) {
		List<Long> ids = InstanceUtil.newArrayList();
		if (null == idList || idList.size() <= 0 || pageNum <= 0 || pageSize <= 0) {
			return ids;
		}
		//判断有多少页
		int page = (idList.size() / pageSize) + 1;
		if (pageNum > page) {
			return ids;
		}
		int firstIndex = pageSize * (pageNum - 1);
		int lastIndex = idList.size();
		if (pageSize * pageNum < lastIndex) {
			lastIndex = pageSize * pageNum;
		}
		ids = idList.subList(firstIndex, lastIndex);
		return ids;
	}
	/**
	 * 模拟分页
	 * @param idList
	 * @param pageSize
	 * @param pageNum
	 * @return
	 */
	public static List getFreePageList(List idList,int pageNum, int pageSize) {
		List ids = InstanceUtil.newArrayList();
		if (null == idList || idList.size() <= 0 || pageNum <= 0 || pageSize <= 0) {
			return ids;
		}
		//判断有多少页
		int page = (idList.size() / pageSize) + 1;
		if (pageNum > page) {
			return ids;
		}
		int firstIndex = pageSize * (pageNum - 1);
		int lastIndex = idList.size();
		if (pageSize * pageNum < lastIndex) {
			lastIndex = pageSize * pageNum;
		}
		ids = idList.subList(firstIndex, lastIndex);
		return ids;
	}
}
