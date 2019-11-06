package com.lazhu.httpApi;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.lazhu.ecp.utils.HttpUtil;
import com.lazhu.ecp.utils.StrUtils;

/**
 * Date:     2018年4月11日 下午7:49:01
 * @author   hz	 
 */
public class MobileAreaUrl {
	

    //手机号归属地查询URL
    public static final String MOBILE_AREA_URL = "http://mobsec-dianhua.baidu.com/dianhua_api/open/location";
    

	/**
     * 查询手机号归属地
     * @param mobile
     * @return
     */
    public static String[] getMobileArea(String mobile) {
    	if (StrUtils.isNullOrBlank(mobile)) {
    		return null;
    	}
    	String[] s = new String[2];
    	try {
            //请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("tel", mobile);//这是该接口需要的参数
            // 调用 get 请求
            String res = HttpUtil.get(MOBILE_AREA_URL, params, null);
            res = res.substring(res.indexOf("{"));//截取
            JSONObject result = JSONObject.parseObject(res);//转JSON
            JSONObject m = JSONObject.parseObject(result.get("response").toString());
            JSONObject d = JSONObject.parseObject(m.get(mobile).toString());
            JSONObject details = JSONObject.parseObject(d.get("detail").toString());
            s[0] = details.get("province").toString();
            JSONObject area = JSONObject.parseObject(details.get("area").toString().replace("[", "").replace("]", ""));
            s[1] = area.get("city").toString();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请求手机号归属地发生错误，手机号：" + mobile);
        }
    	return null;
    }
    
    public static void main(String[] args) {
		System.out.println(getMobileArea("19909431226")[0]);
		System.out.println(getMobileArea("19909431226")[1]);
	}
	
}

