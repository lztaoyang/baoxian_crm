package com.lazhu.core.support.rest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import com.lazhu.core.config.SpringContext;
/**
 * 基于RestTemplate进行http访问的辅助类
 * @author naxj
 * modify by panghong for 修改返回类型不是String的时候会报类型转换错误 20170517
 */
public class RestTemplateHelper {
	//
	private static RestTemplate restTemplate;

	// rest操作对象
	private static RestTemplate getRestTemplate() {
		if (restTemplate == null) {
			synchronized (RestTemplate.class) {
				if (restTemplate == null) {
					restTemplate = SpringContext.getBean(RestTemplate.class);
				}
			}
		}
		return restTemplate;
	}
	/** 
    *  
    * 发送post请求 
    *  
    * @param url 
    *            请求URL地址 
    * @param data 
    *            json数据 
    * @param token 
    *            RSA加密token【RSA({PlatformCode+TenantCode+DateTime.Now()})//平台代码 
    *            +会员/租户代码+当前时间，然后进行RSA加密】 
    * @param clazz 返回类型          
    */  
   public static <T> T post(String url, String data, String token, Class<T> clazz)  
           throws Exception {  
       HttpHeaders headers = new HttpHeaders();  
       /*headers.add(HttpHeadersImpl.ACCEPT, "application/json"); 
       headers.add(HttpHeadersImpl.ACCEPT_ENCODING, "gzip"); 
       headers.add(HttpHeadersImpl.CONTENT_ENCODING, "UTF-8"); 
       headers.add(HttpHeadersImpl.CONTENT_TYPE, 
               "application/json; charset=UTF-8"); 
       headers.add(HttpHeadersImpl.COOKIE, token); 
       headers.add("Token", token);*/  
       headers.add("Accept", "application/json");  
       headers.add("Accpet-Encoding", "gzip");  
       headers.add("Content-Encoding", "UTF-8");  
       headers.add("Content-Type", "application/json; charset=UTF-8");  
       headers.add("Token", token);  
 
       HttpEntity<String> formEntity = new HttpEntity<String>(data, headers);  
       return getRestTemplate().postForObject(url, formEntity, clazz);  
   }  
     
   /** 
    *  
    * 发送post请求 
    *  
    * @param url 
    *            请求URL地址 
    * @param data 
    *            json数据 
    * @param token 
    *            RSA加密token【RSA({PlatformCode+TenantCode+DateTime.Now()})//平台代码 
    *            +会员/租户代码+当前时间，然后进行RSA加密】 
    * @param platformCode 
    *            平台编码{平台分配} 
    * @param tenantCode 
    *            租户号{平台分配} 
    * @param clazz 返回类型           
    */  
   public static <T> T post(String url, String data, String token,String platformCode,String tenantCode, Class<T> clazz)  
           throws Exception {  
       HttpHeaders headers = new HttpHeaders();  
       /*headers.add(HttpHeadersImpl.ACCEPT, "application/json"); 
       headers.add(HttpHeadersImpl.ACCEPT_ENCODING, "gzip"); 
       headers.add(HttpHeadersImpl.CONTENT_ENCODING, "UTF-8"); 
       headers.add(HttpHeadersImpl.CONTENT_TYPE, 
               "application/json; charset=UTF-8"); 
       headers.add(HttpHeadersImpl.COOKIE, token); 
       headers.add("Token", token);*/  
       headers.add("Accept", "application/json");  
       headers.add("Accpet-Encoding", "gzip");  
       headers.add("Content-Encoding", "UTF-8");  
       headers.add("Content-Type", "application/json; charset=UTF-8");  
       headers.add("Token", token);  
       headers.add("PlatformCode", platformCode);  
       headers.add("TenantCode", tenantCode);  
 
       HttpEntity<String> formEntity = new HttpEntity<String>(data, headers);  
       return getRestTemplate().postForObject(url, formEntity, clazz);  
   }  
     
   /** 
    * get根据url获取对象 
    */  
   public <T> T get(String url, Class<T> clazz) {  
       return getRestTemplate().getForObject(url, clazz,  
               new Object[] {});  
   }  
 
   /** 
    * getById根据ID获取对象 
    */  
   public <T> T getById(String url, String id, Class<T> clazz) {  
       return getRestTemplate().getForObject(url, clazz,  
               id);  
   }  
 
   /** 
    * post提交对象 
    */  
   public <T> T post(String url, String data, Class<T> clazz) {  
       return getRestTemplate().postForObject(url, null,  
    		   clazz, data);  
   }  
 
   /** 
    * put修改对象 
    */  
   public void put(String url, String data) {  
	   getRestTemplate().put(url, null, data);  
   }  
 
   /** 
    * delete根据ID删除对象 
    */  
   public void delete(String url, String id) {  
	   getRestTemplate().delete(url, id);  
   }  
     
   public static void main(String[] args) {  
         
   }  
 
}  
