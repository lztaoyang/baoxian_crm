package com.lazhu.httpApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.lazhu.core.support.Assert;
import com.lazhu.crm.customeractualcot.service.CustomerActualCotService;
import com.lazhu.crm.teacherdirectivecot.service.TeacherDirectiveCotService;
import com.lazhu.ecp.utils.ArrayUtils;
import com.lazhu.ecp.utils.HttpURLForConnection;
import com.lazhu.ecp.utils.StrUtils;

/**
 * 
 * 股票行情信息获取
 * @author ty
 *
 */
public class StockApi {
	
	  //获取股票信息
    public static final String GET_URL_CTOCK_MUCH = "http://hq.sinajs.cn/list=";//新浪接口--以，拆分字符串
    public static final String GET_URL_TP_CTOCK_MUCH = "http://qt.gtimg.cn/q=";//腾讯接口--以~拆分字符串
    
    @Autowired
    CustomerActualCotService customerCotService;
    
    @Autowired
    TeacherDirectiveCotService teacherCotService;

	/**
	 * 通过多条股票代码获取当前行情
	 */
	public String getCurrentByCode20(String code){
			//如果股票代码不为空
			if(StrUtils.isNotNullOrBlank(code)){
				//请求接口
				String jsonString = HttpURLForConnection.httpURLConectionGET(GET_URL_CTOCK_MUCH+code);
				if (StrUtils.isNotNullOrBlank(jsonString)) {
					//解析出当前行情
					//System.out.println(jsonString);
					return jsonString;
				}
			}
			System.out.println("20行情接口，请求失败！");
			return null;
	}
	
	
	public static void main(String[] args) {
		StockApi api = new StockApi();
		List<String> list  = new ArrayList<String>();
		list.add(0,"000001");
		list.add(1,"000830");
		list.add(2,"000858");
		list.add(3,"399001");
		api.getCurrentByCodeList(list);
		//api.getCurrentByCode20("sz000001,sz000830,s_sh000001,s_sz399001");
		//api.getCurrentByCode20("sz000858");
	}
	
	/**
	 * 通过股票代码获得股票行情 【批量】
	 * 
	 * @param code
	 *            股票代码集合
	 * @return 股票代码+当前行情的集合
	 */
	public List<Map<String, Object>> getCurrentByCodeList(List<String> code) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 接口一次最多可以取20支股票的行情
		List<List<?>> code20 = ArrayUtils.splitListList(code, 20);
		for (int i = 0; i < code20.size(); i++) {
			String codes = "";
			for (int j = 0; j < code20.get(i).size(); j++) {
				String s = StrUtils.toString(code20.get(i).get(j));
				if (s.indexOf("0") == 0 || s.indexOf("3") == 0 || codes.indexOf("2") == 0) {
					s = "sz" + s;
				} else if (s.indexOf("6") == 0 || codes.indexOf("9") == 0 ) {
					s = "sh" + s;
				} else {
					Assert.isNull(null, s + "股票代码错误！");
					System.out.println(s + "股票代码错误！");
				}
				codes += s + ",";
			}
			// 获取行情json
			String prices = this.getCurrentByCode20(codes.substring(0,codes.length() - 1));// 去除末尾逗号
			if (StrUtils.isNotNullOrBlank(prices)) {
				
				String[] stocks = prices.split(";");//拆分各个股票信息
				 //String[] stocks = stock.split("~");
	            String[] datas = null;
	            for (String stock : stocks) {
	            	Map<String, Object> m = new HashMap<String, Object>();
	               
	                //datas = stock.split("~");
	                datas = stock.split(",");
	                datas[0] = datas[0].substring(21);//获取股票名称
	                m.put("stockName", datas[0]);
                	m.put("currentPrice", datas[3]);//当前价
                	list.add(m);
	            }
			}
		}
		System.out.println(list);
		return list;
	}
	
	/**
	 * 通过股票代码获得股票行情
	 * 
	 * @param code
	 *            股票代码集合
	 * @return 股票代码+当前行情的集合
	 */
	public List<String> getCurrentByCode(String codes) {
		String stockPrice = "";
		List<String> list = new  ArrayList<String>();
		if (StrUtils.isNotNullOrBlank(codes) && codes.length() == 6) {
			if (codes.indexOf("0") == 0 || codes.indexOf("3") == 0 || codes.indexOf("2") == 0) {
				codes = "sz" + codes;
			} else if (codes.indexOf("6") == 0 || codes.indexOf("9") == 0) {
				codes = "sh" + codes;
			} else {
				Assert.isNull(null, codes + "股票代码错误！");
				System.out.println(codes + "股票代码错误！");
			}
			// 获取行情json
			String prices = this.getCurrentByCode20(codes);// 去除末尾逗号
			//System.out.println(prices);
 			if (StrUtils.isNotNullOrBlank(prices) && prices.indexOf("FAILED") < 0) {
				String[] datas = null;
				datas = prices.split(",");
				if (datas[0].substring(22).equals(";") ) {
					stockPrice = "0";//当前价格
					datas[0] = "未查询到结果";
				} else{
					datas[0] = datas[0].substring(21);//获取股票名称
					stockPrice = datas[3];//当前价格
				}
				list.add(stockPrice);
				list.add(datas[0]);
				
			}
		}
		
		if (null == list || list.size() <= 0) {
			list.add("0");
			list.add("未查询到结果");
		}
		return list;
	}

	
}
