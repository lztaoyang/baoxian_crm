package com.lazhu.core.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
/**
 * spring上下文
 * @author naxj
 *
 */
@Configuration
public class SpringContext implements ApplicationContextAware{

	private static ApplicationContext context;
	@SuppressWarnings("static-access")
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		this.context = applicationContext;
	}
	/**
	 * 根据类型获得Bean
	 * @param requiredType
	 * @return
	 * @throws BeansException
	 */
	public static<T> T getBean(Class<T> requiredType) throws BeansException{
		return context.getBean(requiredType);
	}
	/**
	 * 根据beanId获得Bean
	 * @param beanName
	 * @return
	 * @throws BeansException
	 */
	public static Object getBean(String beanName) throws BeansException{
		return context.getBean(beanName);
	}

}
