package com.lazhu.sys.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.base.BaseService;
import com.lazhu.core.util.CacheUtil;
import com.lazhu.sys.mapper.SysDeptMapper;
import com.lazhu.sys.model.SysDept;
import com.lazhu.sys.model.TreeNode;

/**
 * @author naxj
 * 
 */
@Service
@CacheConfig(cacheNames = "sysDept")
public class SysDeptService extends BaseService<SysDept> {

	
	@Autowired
	SysDeptMapper deptMapper;
	
	/** 查询一个部门下的直接部门*/
	@Transactional
	public Page<SysDept> queryDept(ModelMap modelMap,Map<String, Object> param){
		param.put("id", param.get("parentId"));
		Page<SysDept> page = super.query(param);
		return page;
	}
	
	//获取到完整树
	@Transactional
	public List<TreeNode> getAllTree( Long parentId){
		List<TreeNode>list  = new ArrayList<>();
		Map<String ,Object> map = new HashMap<>();
		map.put("parentId", parentId);
		//map.put("id", parentId);
		List<SysDept> depts =super.queryList(map);
		for (SysDept dept : depts) {
			TreeNode tree =new TreeNode();
			tree.setId(dept.getId()+"");
			tree.setLabel(dept.getDeptName());
			if(dept.getLeaf()==0){
				List<TreeNode> children =(List<TreeNode>) getAllTree(dept.getId());
				tree.setChildren(children);
			}	
			list.add(tree);
		}
		return list;
	}
	
	//获取到用户部门树
	@Transactional
	public List<TreeNode> getTree( Long parentId){
		List<TreeNode>list  = new ArrayList<>();
		Map<String ,Object> map = new HashMap<>();
		map.put("parentId", parentId);
		map.put("enable", 1);
		List<SysDept> depts =super.queryList(map);
		for (SysDept dept : depts) {
			TreeNode tree =new TreeNode();
			tree.setId(dept.getId()+"");
			tree.setLabel(dept.getDeptName());
			if(dept.getLeaf()==0){
				List<TreeNode> children =(List<TreeNode>) getTree(dept.getId());
				tree.setChildren(children);
		    }	
			list.add(tree);
		}
		return list;
	}
	
	
	
	/*
	 * 添加部门并将上级部门改为父节点
	 */
	@Transactional
	public SysDept  toUpdateDept(SysDept dept){
		//判断是否有主键id，没有则是添加，有就是修改
				if(dept.getId()==null){
					SysDept sysDept = super.queryById(dept.getParentId());
					CacheUtil.getCache().del(getCacheKey(sysDept.getId()));
					sysDept.setLeaf(Integer.valueOf(0));
					dept = super.update(dept);
					mapper.updateById(sysDept);
					CacheUtil.getCache().set(getCacheKey(sysDept.getId()), sysDept);
				}
				else{
					//修改部门
					dept = super.update(dept);
					//查询修改之后的部门是否有下级部门
					SysDept sysDept = super.queryById(dept.getParentId());
					Map<String ,Object> param = new HashMap<>();
					param.put("parentId", sysDept.getId());
					List<SysDept> list= super.queryList(param);
					//判断是否有下级部门
					if(list!=null&&list.size()>0){
						//清空缓存
						CacheUtil.getCache().del(getCacheKey(sysDept.getId()));
						sysDept.setLeaf(Integer.valueOf(0));
						mapper.updateById(sysDept);
						//修改完成后放入缓存
						CacheUtil.getCache().set(getCacheKey(sysDept.getId()), sysDept);
					}
				}
				return dept;
	}

	public List<Long> queryByDept(String deptName) {
		return deptMapper.queryByDept(deptName);
	}
}
