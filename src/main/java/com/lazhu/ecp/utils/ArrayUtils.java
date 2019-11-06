package com.lazhu.ecp.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class ArrayUtils {

	/**
	 * 按指定大小，分隔集合，将集合按规定个数分为n个部分
	 * 
	 * @param list
	 * @param len
	 * @return
	 */
	public static List<List<?>> splitListList(List<?> list, int len) {
		if (list == null || list.size() == 0 || len < 1) {
			return null;
		}
		List<List<?>> result = new ArrayList<List<?>>();
		int size = list.size();
		int count = (size + len - 1) / len;
		for (int i = 0; i < count; i++) {
			List<?> subList = list.subList(i * len,
					((i + 1) * len > size ? size : len * (i + 1)));
			result.add(subList);
		}
		return result;
	}

	public static Field[] addArray(Field[] fields1, Field[] fields12) {
		Field[] fields = new Field[fields1.length + fields12.length];
		for (int i = 0; i < fields1.length; i++) {
			fields[i] = fields1[i];
		}
		for (int i = 0; i < fields12.length; i++) {
			fields[fields1.length] = fields12[i];
		}
		return fields;
	}

	/**
	 * 将list分组
	 * 
	 * @param list
	 * @param length
	 *            分组的长度
	 * @return
	 */
	public static List splitList(List list, int length) {
		List resultList = new ArrayList();
		int pc = 0;
		if (null != list && list.size() > 0) {
			pc = list.size() / length;
			for (int i = 0; i < pc; i++) {
				List pcList = new ArrayList();
				pcList = list.subList(i * length, (i + 1) * length);
				resultList.add(pcList);
			}
			if (pc * length < list.size()) {
				resultList.add(list.subList(pc * length, list.size()));
			}
		}
		return resultList;
	}

	/**
	 * 将list分组
	 * 
	 * @param list
	 * @param num
	 *            分组的个数
	 * @return
	 */
	public static List splitListByNum(List list, int num) {
		List resultList = new ArrayList();
		int len = list.size() / num;
		int lastLen = len + (list.size() - len * num);
		if (null != list && list.size() > 0) {
			if (num <= 1) {
				resultList.add(list);
				return resultList;
			} else {
				List lastList = new ArrayList();
				lastList = list.subList(0, lastLen);
				resultList.add(lastList);
				for (int i = 0; i < num - 1; i++) {
					List pcList = new ArrayList();
					pcList = list.subList(lastLen + i * len, lastLen + (i + 1)
							* len);
					resultList.add(pcList);
				}
			}
		}
		return resultList;
	}
}
