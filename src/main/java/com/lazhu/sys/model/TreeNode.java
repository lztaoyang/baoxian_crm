package com.lazhu.sys.model;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
	private String id;
	private String label;
	private List<TreeNode> children =new ArrayList<>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public List<TreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}
	@Override
	public String toString() {
		return "TreeNode [id=" + id + ", label=" + label + ", children=" + children + "]";
	}
	
	
}
