package com.lazhu.ecp.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.springframework.util.Assert;

/**
 * 基础系统静态常量类
 * 
 * @Apr 26, 2010
 */
public class ConstantUtils {

	public static Properties CONSTANTS = null;// 系统变量存放区

	/**
	 * 获取系统变量
	 * 
	 * @param constantName
	 *            属性名称
	 * @return
	 */
	public static String getConstant(String constantName) {
		String property = null;
		if (null == CONSTANTS) {
			initSystemProperties();
		}
		try {
			property = CONSTANTS.getProperty(constantName);
			//Properties文件默认机制是采用ISO8859-1处理
			if (null != property) {
				property = new String(property.getBytes("ISO8859_1"),"UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Assert.notNull(property, "系统变量" + constantName + "不存在");
		return property;
	}

	/**
	 * 初始化系统属性文件
	 */
	private static void initSystemProperties() {
		CONSTANTS = new Properties();
		ClassLoader cl = ConstantUtils.class.getClassLoader();
		// 加载数据字典分类初始化文件
		InputStream stream = cl.getResourceAsStream("config/system.properties");
		try {
			CONSTANTS.load(stream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != stream) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 获取实时获取系统变量
	 * 
	 * @param constantName
	 *            属性名称
	 * @return
	 */
	public static String getLoadConstant(String constantName) {
		String property = null;
		initSystemProperties();
		try {
			property = CONSTANTS.getProperty(constantName);
			//Properties文件默认机制是采用ISO8859-1处理
			if (null != property) {
				property = new String(property.getBytes("ISO8859_1"),"UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Assert.notNull(property, "系统变量" + constantName + "不存在");
		return property;
	}
	
	
	/**
	 * 更新配置文件变量
	 * 
	 * @param constantName
	 *            属性名称
	 * @return
	 */
	public static void setLoadConstant(String constantName,String value) {
		
		try {   
			CONSTANTS.load(new FileInputStream("config/system.properties"));   
            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。   
            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。   
            OutputStream fos = new FileOutputStream("config/system.properties");              
            CONSTANTS.setProperty(constantName, value);   
            // 以适合使用 load 方法加载到 Properties 表中的格式，   
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流   
            CONSTANTS.store(fos, "Update '" + constantName + "' value");   
        } catch (IOException e) {   
            System.err.println("属性文件更新错误");   
        }   
		
	}
	
	
	public static void main(String [] args){
		System.out.println(ConstantUtils.getConstant("session.redis.namespace"));
		//util.setLoadConstant("TG_SPROJECT_ADD", "2017-03-01 01:01:01");
		System.out.println(ConstantUtils.getLoadConstant("session.redis.namespace"));
	}
}
