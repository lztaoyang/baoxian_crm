package com.lazhu.t.resource.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.base.BaseService;
import com.lazhu.core.support.Assert;
import com.lazhu.core.support.security.coder.RSACoder;
import com.lazhu.crm.Constant;
import com.lazhu.ecp.utils.AliyunCoder;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.t.resource.entity.TResource;
import com.lazhu.t.resource.mapper.TResourceMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hz
 * @since 2017-10-27
 */
@Service
@CacheConfig(cacheNames = "tResource")
public class TResourceService extends BaseService<TResource> {

	@Autowired
	TResourceMapper mapper;

	public List<Long> queryNewTResouce(long maxTId) {
		return mapper.queryNewTResouce(maxTId);
	}

	@SuppressWarnings("rawtypes")
	public Page queryResource(Map<String, Object> param) {
		// 初始化秘钥
		RSACoder.initTResourcePrivateKey();
		Page<Long> page = getPage(param);
		page.setRecords(((TResourceMapper) mapper).selectIdPage(page, param));
		Page<TResource> ResourcePage = super.getPage(page);
		// 拥有客户天数
		if (ResourcePage != null && ResourcePage.getRecords() != null && ResourcePage.getRecords().size() > 0) {
			List<TResource> newList = new ArrayList<TResource>();
			for (TResource item : ResourcePage.getRecords()) {
				String mobile = null;
				// 1.解密推广资源RSA手机号
				try {
					mobile = AliyunCoder.testSimpleDecode(item.getSecretMobile());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (null == mobile || mobile.length() > 15) {
					OperationLogTool.operationLog(Constant.ERROR_LOG, "分配推广资源时，解密手机号错误！");
					Assert.notNull(null, "分配推广资源时，解密手机号错误！");
				} else {
					item.setFuzzyMobile(mobile);
				}
				newList.add(item);
			}
			// 暂时不需要重新排序
			ResourcePage.setRecords(newList);
		}
		return ResourcePage;
	}

}
