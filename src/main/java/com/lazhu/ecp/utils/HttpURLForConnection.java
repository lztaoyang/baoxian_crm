package com.lazhu.ecp.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpURLForConnection {
    //public static final String GET_URL = "http://112.4.27.9/mall-back/if_user/store_list?storeId=32";
    //public static final String POST_URL = "http://112.4.27.9/mall-back/if_user/store_list";
   
    //public static final String GET_URL_CTOCK_TEST = "http://stockapi2.gp58.com/stockapi/stockinfo.htm?code=sz000001";
    
	public static final String POST_URL_POLICY_NO = "https://admin.wts9999.net/tools/policy/detail";
	
    /**
     * 接口调用 GET
     */
    public static String httpURLConectionGET(String parameter) {
    	if (StrUtils.isNotNullOrBlank(parameter)) {
    		try {
    			URL url = new URL(parameter);// 把字符串转换为URL请求地址
    			HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
    			
    			//System.setProperty("sun.net.client.defaultConnectTimeout","5000"); //超时毫秒数字符串);
    			//System.setProperty("sun.net.client.defaultReadTimeout","5000"); //超时毫秒数字符串);
    			//其中： sun.net.client.defaultConnectTimeout：连接主机的超时时间（单位：毫秒）
    			//sun.net.client.defaultReadTimeout：从主机读取数据的超时时间（单位：毫秒）
    			connection.setConnectTimeout(1000);
    			connection.setReadTimeout(1000);
    			
    			connection.connect();// 连接会话
    			// 获取输入流
    			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"GBK"));
    			
    			String line;
    			StringBuilder sb = new StringBuilder();
    			while ((line = br.readLine()) != null) {// 循环读取流
    				sb.append(line);
    			}
    			br.close();// 关闭流
    			connection.disconnect();// 断开连接
    			return sb.toString();
    		} catch (Exception e) {
    			//e.printStackTrace();
    			System.out.println("URL连接失败!(关键参数："+parameter.substring(parameter.length()-8)+")");
    		}
		}
        return "";
    }
    /**
     * 接口调用 GET
     */
    public static void httpURLConectionGET() {
    	try {
    		URL url = new URL("");
    		//URL url = new URL(GET_URL);    // 把字符串转换为URL请求地址
    		HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
    		connection.connect();// 连接会话
    		// 获取输入流
    		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    		String line;
    		StringBuilder sb = new StringBuilder();
    		while ((line = br.readLine()) != null) {// 循环读取流
    			sb.append(line);
    		}
    		br.close();// 关闭流
    		connection.disconnect();// 断开连接
    		System.out.println(sb.toString());
    	} catch (Exception e) {
    		e.printStackTrace();
    		System.out.println("失败!");
    	}
    }
   
    /**
     * 接口调用  POST
     */
    @SuppressWarnings("unused")
	public static void httpURLConnectionPOST () {
        try {
            URL url = new URL("");
            //URL url = new URL(POST_URL);
           
            // 将url 以 open方法返回的urlConnection  连接强转为HttpURLConnection连接  (标识一个url所引用的远程对象连接)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 此时cnnection只是为一个连接对象,待连接中
           
            // 设置连接输出流为true,默认false (post 请求是以流的方式隐式的传递参数)
            connection.setDoOutput(true);
           
            // 设置连接输入流为true
            connection.setDoInput(true);
           
            // 设置请求方式为post
            connection.setRequestMethod("POST");
           
            // post请求缓存设为false
            connection.setUseCaches(false);
           
            // 设置该HttpURLConnection实例是否自动执行重定向
            connection.setInstanceFollowRedirects(true);
           
            // 设置请求头里面的各个属性 (以下为设置内容的类型,设置为经过urlEncoded编码过的from参数)
            // application/x-javascript text/xml->xml数据 application/x-javascript->json对象 application/x-www-form-urlencoded->表单数据
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
           
            // 建立连接 (请求未开始,直到connection.getInputStream()方法调用时才发起,以上各个参数设置需在此方法之前进行)
            connection.connect();
           
            // 创建输入输出流,用于往连接里面输出携带的参数,(输出内容为?后面的内容)
            DataOutputStream dataout = new DataOutputStream(connection.getOutputStream());
            String parm = "storeId=" + URLEncoder.encode("32", "utf-8"); //URLEncoder.encode()方法  为字符串进行编码
           
            // 将参数输出到连接
            dataout.writeBytes(parm);
           
            // 输出完成后刷新并关闭流
            dataout.flush();
            dataout.close(); // 重要且易忽略步骤 (关闭流,切记!)
           
            System.out.println(connection.getResponseCode());
           
            // 连接发起请求,处理服务器响应  (从连接获取到输入流并包装为bufferedReader)
            BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder(); // 用来存储响应数据
           
            // 循环读取流,若不到结尾处
            while ((line = bf.readLine()) != null) {
                sb.append(bf.readLine());
            }
            bf.close();    // 重要且易忽略步骤 (关闭流,切记!)
            connection.disconnect(); // 销毁连接
            System.out.println(sb.toString());
   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
	 * post请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            post提交的参数
	 * @return
	 */
	public static String postMethod(String url, Map<String, Object> params) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(2000).setConnectTimeout(2000).build();// 设置请求和传输超时时间
		httppost.setConfig(requestConfig);
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		Set<String> keys = params.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			BasicNameValuePair basic = new BasicNameValuePair(key,
					(String) params.get(key));
			formparams.add(basic);
		}
		UrlEncodedFormEntity uefEntity = null;
		CloseableHttpResponse response = null;
		String result = null;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httppost.setEntity(uefEntity);
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
   
    public static void main(String[] args) {
    	//httpURLConectionGET();
        //httpURLConnectionPOST();
    	System.out.println(httpURLConectionGET("http://hq.sinajs.cn/list=sz000001,sz000830"));
    }
    
    public static byte[] getUTF8ByGBKString(String gbkStr) {  
         int n = gbkStr.length();  
         byte[] utfBytes = new byte[3 * n];  
         int k = 0;  
         for (int i = 0; i < n; i++) {  
             int m = gbkStr.charAt(i);  
             if (m < 128 && m >= 0) {  
                 utfBytes[k++] = (byte) m;  
                 continue;  
             }  
             utfBytes[k++] = (byte) (0xe0 | (m >> 12));  
             utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));  
             utfBytes[k++] = (byte) (0x80 | (m & 0x3f));  
         }  
         if (k < utfBytes.length) {  
             byte[] tmp = new byte[k];  
             System.arraycopy(utfBytes, 0, tmp, 0, k);  
             return tmp;  
         }  
         return utfBytes;  
     } 
}
