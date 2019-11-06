package com.lazhu.business.excel.service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lazhu.business.excel.util.ExportExcelSXSSFWorkbook;
import com.lazhu.business.excel.util.ExportExcelSeedBack;
import com.lazhu.core.util.SecurityUtil;
import com.lazhu.crm.mobile.service.MobileService;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.ecp.utils.DateUtils;
import com.lazhu.ecp.utils.StrUtils;

/**
 * Date:     2018年5月9日 上午11:29:22
 * @author   hz 
 */
@Service
@Transactional(readOnly = false)
public class ExcelService {
	
	@Autowired
	ResourceService resourceService;
	
	@Autowired
	MobileService mobileService;

	public List<Map<String, Object>> queryList(String sql) {
		return resourceService.queryBySql(sql);
	}

	@SuppressWarnings("rawtypes")
	public void exportExcel(List<Map<String, Object>> list,HttpServletResponse response) {
		
		if (list != null && list.size()>0) {//查询的数据不为空就对数据进行导出
            //导出文件的标题
            String title = "导出"+DateUtils.DateToStr(new Date(), DateUtils.TIME_FORMAT_CHINESE)+".xlsx";
            //设置表格标题行
            String[] headers = new String[list.get(0).size() + 1];
            headers[0] = "序号";
            for (int i = 1; i < headers.length; i++) {
            	headers[i] = "名称";
			}
            
            List<Object[]> dataList = new ArrayList<Object[]>();
            int num = 0;
            int nums = list.size();
            Object[] objs = null;
            for (Map map : list) {//循环每一条数据
                objs = new Object[headers.length + 1];
                objs[0] = 0;//任意
                int index = 1;
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
        		    if ("md5Mobile".equals(key) || "md5_mobile".equals(key)) {
        		    	value = StrUtils.MD5(mobileService.getMobile(value));
        		    }
        		    if ("mobile".equals(key) || "MOBILE".equals(key)) {
						value = mobileService.getMobile(value);
					}
        		    if ("rsaMobile".equals(key)) {
        		    	value = mobileService.getMobileByRsaMobile(value);
        		    }
        		    objs[index] = value;
        		    index ++;
        		}
        		num ++;
        		if (num % 100 == 0) {
        			System.out.println("已导出：" + num + "/" + nums);
				}
                //数据添加到excel表格
                dataList.add(objs);
            }
            //使用流将数据导出
            OutputStream out = null;
            try {
                //防止中文乱码
                String headStr = "attachment; filename=\"" + new String( title.getBytes("gb2312"), "ISO8859-1" ) + "\"";
                response.setContentType("octets/stream");
                response.setContentType("APPLICATION/OCTET-STREAM");
                response.setHeader("Content-Disposition", headStr);
                out = response.getOutputStream();
                //ExportExcel ex = new ExportExcel(title, headers, dataList);//有标题
                ExportExcelSXSSFWorkbook ex = new ExportExcelSXSSFWorkbook(title, headers, dataList);//没有标题
                ex.export(out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		
	}
	
	/**
	 * 导出方法
	 * 
	 * @param list 导出数据的list
	 * @param response 导出response
	 * @param fileName	导出的文件名称
	 * @param execlTitle 导出的excel标题
	 */
	@SuppressWarnings("rawtypes")
	public void exportExcelName(List<Map<String, Object>> list,HttpServletResponse response,String fileName,String[] execlTitle) {
			
			if (list != null && list.size()>0) {//查询的数据不为空就对数据进行导出
	            //导出文件的标题
	            String title = fileName+DateUtils.DateToStr(new Date(), DateUtils.TIME_FORMAT_CHINESE)+".xlsx";
	            //设置表格标题行
	            String[] headers = execlTitle;//new String[list.get(0).size() + 1];
	            headers[0] = "序号";
//	            for (int i = 1; i < headers.length; i++) {
//	            	headers[i] = "名称";
//				}
	            
	            List<Object[]> dataList = new ArrayList<Object[]>();
	            Object[] objs = null;
	            int num = 0;
	            int nums = list.size();
	            for (Map map : list) {//循环每一条数据
	                objs = new Object[headers.length + 1];
	                objs[0] = 0;//任意
	                int index = 1;
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
	        		    //System.out.println(key+"===="+value);
	        		    if ("md5Mobile".equals(key) || "md5_mobile".equals(key)) {
							value = mobileService.getMobile(value);
						}
	        		    if("shaMobile".equals(key) || "sha_mobile".equals(key)){
	        		    	value = mobileService.getMobile(value);
	        		    	//加密成sha256
	        		    	value = SecurityUtil.encryptSHA256(value);
	        		    }
	        		    if ("rsaMobile".equals(key) || "rsa_mobile".equals(key)) {
	        		    	value = mobileService.getMobileByRsaMobile(value);
	        		    }
	        		    objs[index] = value;
	        		    index ++;
	        		}
	        		num ++;
	        		if (num % 100 == 0) {
	        			System.out.println("已导出：" + num + "/" + nums);
					}
	                //数据添加到excel表格
	                dataList.add(objs);
	            }
	            //使用流将数据导出
	            OutputStream out = null;
	            try {
	                //防止中文乱码
	                String headStr = "attachment; filename=\"" + new String( title.getBytes("gb2312"), "ISO8859-1" ) + "\"";
	                response.setContentType("octets/stream");
	                response.setContentType("APPLICATION/OCTET-STREAM");
	                response.setHeader("Content-Disposition", headStr);
	                out = response.getOutputStream();
	                //ExportExcel ex = new ExportExcel(title, headers, dataList);//有标题
	                ExportExcelSXSSFWorkbook ex = new ExportExcelSXSSFWorkbook(title, headers, dataList);//没有标题
	                ex.export(out);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
			
		}

}

