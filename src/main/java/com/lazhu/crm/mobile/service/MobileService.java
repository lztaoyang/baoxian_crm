package com.lazhu.crm.mobile.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.base.BaseService;
import com.lazhu.core.support.Assert;
import com.lazhu.core.util.ComplexMD5Util;
import com.lazhu.crm.Constant;
import com.lazhu.crm.mobile.entity.Mobile;
import com.lazhu.crm.mobile.mapper.MobileMapper;
import com.lazhu.ecp.utils.AliyunCoder;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;

/**
 * <p>
 * 客户联系方式表 服务实现类
 * </p>
 *
 * @author hz
 * @since 2018-02-01
 */
@Service
@CacheConfig(cacheNames = "mobile")
public class MobileService extends BaseService<Mobile> {

	@Autowired
	MobileMapper mobileMapper;
	
	/**
	 * 查询
	 * @param modelMap
	 * @param params
	 * @return
	 */
	public Page<Mobile> queryMobile(ModelMap modelMap, Map<String, Object> params) {
		Page<Long> page = getPage(params);
		//手机号查询（需先加密成MD5手机号，然后查询MD5手机号）
		setMd5MobileWhenMobile(params);
		page.setRecords(((MobileMapper)mapper).selectIdPage(page, params));
		Page<Mobile> mobilePage = super.getPage(page);
		if (null != mobilePage && mobilePage.getRecords().size() > 0) {
			for (Mobile mobile : mobilePage.getRecords()) {
				mobile.setMobile(getMobile(mobile.getMd5Mobile()));
			}
		}
		return mobilePage;
	}

	/**
	 * 新增
	 * @param param
	 * @param ip
	 * @return
	 */
	public Mobile addMobile(Mobile param, String ip) {
		Assert.notNull(param.getMobile(), "手机号不能为空");
		Mobile mobile = new Mobile();
		mobile.setCustomerId(param.getCustomerId());
		mobile.setFuzzyMobile(StrUtils.hideMobile(StrUtils.toString(param.getMobile())));
		mobile.setMd5Mobile(ComplexMD5Util.MD5Encode(param.getMobile()));
		String rsaMobile = null;
		try {
			rsaMobile = AliyunCoder.testSimpleEncode(param.getMobile());
		} catch (Exception e1) {
			System.out.println("RSA错误");
			e1.printStackTrace();
		}
		mobile.setRsaMobile(rsaMobile);
		this.update(mobile);
		return mobile;
	}
	

	/**
	 * 修改
	 * @param param
	 * @param ip
	 * @return
	 */
	public Mobile updateMobile(Mobile param, String ip) {
		Assert.notNull(param.getMobile(), "手机号不能为空");
		Mobile mobile = new Mobile();
		mobile.setFuzzyMobile(StrUtils.hideMobile(StrUtils.toString(param.getMobile())));
		mobile.setMd5Mobile(ComplexMD5Util.MD5Encode(param.getMobile()));
		String rsaMobile = null;
		try {
			rsaMobile = AliyunCoder.testSimpleEncode(param.getMobile());
		} catch (Exception e1) {
			System.out.println("RSA错误");
			e1.printStackTrace();
		}
		mobile.setRsaMobile(rsaMobile);
		this.update(mobile);
		return mobile;
	}
	
	/**
	 * 通话MD5手机号查询解密手机号
	 * @param md5Mobile
	 * @return
	 */
	public String getMobile (String md5Mobile) {
		String mobile = null;
		//通过MD5手机号查询RSA加密手机号
		List<String> rsaMobile = ((MobileMapper)mapper).getRsaMobileByMd5(md5Mobile);
		if (null != rsaMobile && rsaMobile.size() > 0) {
			//解密资源RSA手机号
			try {
				mobile = AliyunCoder.testSimpleDecode(rsaMobile.get(0));
			} catch (Exception e) {
				System.out.println(md5Mobile+"--解密资源RSA手机号发送错误！");
				mobile = "解密失败";
				e.printStackTrace();
			}
			if (null == mobile || mobile.length() != 11) {
				mobile = "解密错误";
				OperationLogTool.operationLog(Constant.ERROR_LOG, md5Mobile+"--"+rsaMobile.get(0)+"客户联系方式表解密RSA手机号时，解密手机号错误！");
			}
		}
		return mobile;
	}

	/**
	 * MD5手机号生成（需先加密成MD5手机号，然后查询MD5手机号）
	 * @param params
	 */
	public void setMd5MobileWhenMobile(Map<String, Object> params) {
		if (params.containsKey("mobile") && StrUtils.isNotNullOrBlank(params.get("mobile"))) {
			params.put("md5Mobile", ComplexMD5Util.MD5Encode(StrUtils.toString(params.get("mobile"))));
		}
	}

	/**
	 * 通过密文查询解密手机号
	 * @param md5Mobile
	 * @return
	 */
	public String getMobileByRsaMobile (String rsaMobile) {
		String mobile = null;
		//通过MD5手机号查询RSA加密手机号
		if (null != rsaMobile && rsaMobile.length() > 0) {
			//解密资源RSA手机号
			try {
				mobile = AliyunCoder.testSimpleDecode(rsaMobile);
			} catch (Exception e) {
				System.out.println(rsaMobile+"--解密资源RSA手机号发生错误！");
				mobile = "解密失败";
				e.printStackTrace();
			}
			if (null == mobile || mobile.length() != 11) {
				mobile = "解密错误";
				OperationLogTool.operationLog(Constant.ERROR_LOG, rsaMobile+"客户联系方式表解密RSA手机号时，解密手机号错误！");
			}
		}
		return mobile;
	}
	
	/**
	 * 批量插入客户信息管理表
	 * @param mobileList
	 */
	public void insertBatchMobile(List<Mobile> mobileList) {
		mobileMapper.insertBatchMobile(mobileList);
	}
	
}
