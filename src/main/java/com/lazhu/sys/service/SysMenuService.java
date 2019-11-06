package com.lazhu.sys.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.lazhu.core.base.BaseService;
import com.lazhu.sys.mapper.SysMenuMapper;
import com.lazhu.sys.model.SysMenu;

/**
 * @author naxj
 * 
 */
@Service
@CacheConfig(cacheNames = "sysMenu")
public class SysMenuService extends BaseService<SysMenu> {
	@Autowired
	private SysDicService sysDicService;

	public List<SysMenu> queryList(Map<String, Object> params) {
		List<SysMenu> pageInfo = super.queryList(params);
		Map<String, String> menuTypeMap = sysDicService.queryDicByType("MENUTYPE");
		EntityWrapper<SysMenu> wrapper = new EntityWrapper<SysMenu>();
		for (SysMenu sysMenu : pageInfo) {
			if (sysMenu.getMenuType() != null) {
				sysMenu.setTypeName(menuTypeMap.get(sysMenu.getMenuType().toString()));
			}
			SysMenu menu = new SysMenu();
			menu.setParentId(sysMenu.getId());
			wrapper.setEntity(menu);
			int count = mapper.selectCount(wrapper);
			if (count > 0) {
				sysMenu.setLeaf(0);
			}
		}
		return pageInfo;
	}

	public List<Map<String, String>> getPermissions() {
		return ((SysMenuMapper) mapper).getPermissions();
	}

	/** 更新is_show **/
	public void isShow(ModelMap modelMap, SysMenu param) {
		try {
			// 更新
			((SysMenuMapper) mapper).isShow(param.getIsShow() == true ? 1L : 0, param.getId());
			super.update(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
