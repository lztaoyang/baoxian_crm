package com.lazhu.sys.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.core.base.BaseController;
import com.lazhu.ecp.utils.MapUtil;
import com.lazhu.sys.model.SysDept;
import com.lazhu.sys.model.TreeNode;
import com.lazhu.sys.service.SysDeptService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 部门管理控制类
 * 
 * @author naxj
 * 
 */
@RestController
@Api(value = "部门管理", description = "部门管理")
@RequestMapping(value = "sys/dept")
public class SysDeptController extends BaseController<SysDept> {
	
	@Autowired
	private SysDeptService sysDeptService;

	@ApiOperation(value = "查询部门")
//	@RequiresPermissions("sys.base.dept.read")
	@PutMapping(value = "/read/list")
	public Object query(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		return super.query(modelMap, param);
	}

	@ApiOperation(value = "部门详情")
	//@RequiresPermissions("sys.base.dept.read")
	@PutMapping(value = "/read/detail")
	public Object get(ModelMap modelMap, @RequestBody SysDept param) {
		return super.get(modelMap, param);
	}

	@PostMapping
	@ApiOperation(value = "修改部门")
	//@RequiresPermissions("sys.base.dept.update")
	public Object update(ModelMap modelMap, @RequestBody SysDept param) {
		return  super.setSuccessModelMap(modelMap, sysDeptService.toUpdateDept(param));
	}

	@DeleteMapping
	@ApiOperation(value = "删除部门")
	//@RequiresPermissions("sys.base.dept.delete")
	public Object delete(ModelMap modelMap, @RequestBody Map<String, Object> param) {
		Assert.notNull(param.get("ids"), "IDS");
		List<Long> ids = null;
		if (param.get("ids") instanceof String){
			ids = new ArrayList<Long>();
			ids.add(Long.valueOf(param.get("ids").toString()));
		}else if(param.get("ids") instanceof JSONArray) {
			ids = ((JSONArray)param.get("ids")).toJavaList(Long.class);
		}
		return super.del(modelMap, ids);
	}
	
	@PutMapping(value="/read/dept")
	@ApiOperation(value = "查询部门")
	public Object queryDept(ModelMap modelMap,@RequestBody Map<String, Object> param){
		Page<SysDept> page = sysDeptService.queryDept(modelMap, param);
		return super.setSuccessModelMap(modelMap, page);
	}
	
	@PutMapping(value="/read/tree")
	@ApiOperation(value = "得到树")
	@ResponseBody
	public Object getTree(ModelMap modelMap,@RequestBody Map<String, Object> param){
		//包装树结构
		TreeNode node = new TreeNode();
		Long parentId = MapUtils.getLong(param, "parentId", 0L);
		TreeNode treeNode = sysDeptService.getTree(parentId).get(0);
		node.setId(treeNode.getId());
		node.setLabel(treeNode.getLabel());
		List<TreeNode> list =new ArrayList<>();
		list.add(treeNode);
		node.setChildren(list);
		return super.setSuccessModelMap(modelMap, node);
	}
	@PutMapping(value="/read/allTree")
	@ApiOperation(value = "得到树")
	@ResponseBody
	public Object getAllTree(ModelMap modelMap,@RequestBody Map<String, Object> param){
		//包装树结构
		TreeNode node = new TreeNode();
		TreeNode treeNode = sysDeptService.getAllTree((long)0).get(0);
		node.setId(treeNode.getId());
		node.setLabel(treeNode.getLabel());
		List<TreeNode> list =new ArrayList<>();
		list.add(treeNode);
		node.setChildren(list);
		return super.setSuccessModelMap(modelMap, node);
	}
}
