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
public class AreaUrl {
	

    //手机号归属地查询URL
    public static final String AREA_URL = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm";
    

	/**
     * 查询手机号归属地
     * @param mobile
     * @return
     */
    public static String getMobileArea(String mobile) {
    	if (StrUtils.isNullOrBlank(mobile)) {
			return null;
		}
    	try {
            //请求参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("tel", mobile);//这是该接口需要的参数
            // 调用 get 请求
            String res = HttpUtil.get(AREA_URL, params, null);
            res = res.substring(res.indexOf("{"));//截取
            JSONObject result = JSONObject.parseObject(res);//转JSON
            if (StrUtils.isNullOrBlank(result.get("areaVid"))) {
				return null;
			}
            return result.get("province").toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请求手机号归属地发生错误，手机号：" + mobile);
        }
    	return null;
    }
    
	public static void main(String[] args) {
		System.out.println(getMobileArea("19909431226"));
	}
	
}

