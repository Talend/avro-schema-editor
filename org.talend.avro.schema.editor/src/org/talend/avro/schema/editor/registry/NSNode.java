package org.talend.avro.schema.editor.registry;

import java.util.ArrayList;
import java.util.List;

public class NSNode {

	private String name;
	
	private NSNode parent;
	
	private List<NSNode> children = new ArrayList<>();

	public NSNode(String name) {
		super();
		this.name = name;
	}

	public NSNode getParent() {
		return parent;
	}

	public void setParent(NSNode parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	public List<NSNode> getChildren() {
		return children;
	}
	
	public void addChild(NSNode node) {
		children.add(node);
	}

	public void removeChild(NSNode node) {
		children.remove(node);
	}
	
	public boolean hasChild(String name) {
		for (NSNode child : children) {
			if (child.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public NSNode getChild(String name) {
		for (NSNode child : children) {
			if (child.getName().equals(name)) {
				return child;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
