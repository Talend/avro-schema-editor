package org.talend.avro.schema.editor.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class NameSpaceRegistry {

	public static final String DOT = "."; //$NON-NLS-1$
	
	private static final String NS_ROOT_NAME = "root"; //$NON-NLS-1$

	private static final String DEFAULT_NAME_SPACE = "name"; 
	
	private Map<String, Integer> nameSpaceCounter = new HashMap<>();
	
	private NSNode rootNSNode = null;	
	
	public NameSpaceRegistry() {
		super();
		rootNSNode = new NSNode(NS_ROOT_NAME);
	}

	public NSNode getNameSpaceTree() {
		return rootNSNode;
	}	
	
	public String getAvailableNameSpace(NSNode node) {
		return getAvailableNSNodeName(node);
	}
	
	protected boolean isNSNodeNameUsed(NSNode node, String name) {
		return node.getChild(name) != null;
	}
	
	protected String getAvailableNSNodeName(NSNode node) {
		if (!isNSNodeNameUsed(node, DEFAULT_NAME_SPACE)) {
			return DEFAULT_NAME_SPACE;
		}
		int index = 0;
		String name = null;
		boolean used = true;
		while (used) {
			index++;
			name = DEFAULT_NAME_SPACE + "_" + index;
			used = isNSNodeNameUsed(node, name);
		}	
		return name;
	}
	
	public String validateNameSpace(NSNode node, String name) {
		NSNode parent = node.getParent();
		NSNode child = parent.getChild(name);
		if (child != null && child != node) {
			return "Name space already defined";
		}
		return null;
	}
	
	public NSNode addNameSpaceNode(NSNode parent, String name) {
		return addNSNode(parent, name);
	}
	
	public NSNode removeNameSpaceNode(NSNode node) {
		String nameSpace = getNameSpace(node);
		// check that there is no more registered nodes for this name space
		if (getRegisterCount(nameSpace) > 0) {
			throw new IllegalArgumentException("Cannot remove NS node since there is still at least one registered name space");
		}
		NSNode parent = node.getParent();
		parent.removeChild(node);
		return parent;
	}
	
	public void addNameSpace(String namespace) {
//		if (!isRegistered(namespace)) {
//			updateNameSpaceTreeOnAdded(namespace);
//		}
		if (register(namespace)) {
			// first time we register this namespace
			// we have to update the namespace tree
			updateNameSpaceTreeOnAdded(namespace);
		}
	}	
	
	protected void updateNameSpaceTreeOnAdded(String newNameSpace) {
		StringTokenizer tokenizer = new StringTokenizer(newNameSpace, DOT);
		NSNode node = rootNSNode;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			NSNode child = node.getChild(token);
			if (child == null) {
				// add NS node
				child = addNSNode(node, token);
			}
			node = child;
		}
	}
	
	protected NSNode addNSNode(NSNode parent, String name) {
		NSNode node = new NSNode(name);
		parent.addChild(node);
		node.setParent(parent);
		return node;
	}
	
	protected boolean register(String namespace) {
		boolean newNS = false;
		Integer counter = nameSpaceCounter.get(namespace);
		if (counter == null) {
			counter = 1;		
			newNS = true;
		} else {
			newNS = counter == 0;
			counter++;
		}
		nameSpaceCounter.put(namespace, counter);
		return newNS;
	}
	
	public void removeNameSpace(String namespace) {
		if (unregister(namespace)) {
			// last occurrence of this namespace
			// we have to update the namespace tree
			//updateNameSpaceTreeOnRemoved(namespace);
		}
	}
	
//	protected void updateNameSpaceTreeOnRemoved(String removedNameSpace) {
//		// first we have to get the NSNode corresponding to the last token
//		NSNode node = findNSNode(removedNameSpace);
//		NSNode parent = removeNSNode(node);		
//		String nameSpace = getNameSpace(parent);
//		while (!isRegistered(nameSpace)) {
//			node = findNSNode(nameSpace);
//			parent = removeNSNode(node);
//			nameSpace = getNameSpace(parent);
//		}
//	}
	
//	public boolean isRegistered(String namespace) {
//		nameSpaceCounter.get
//	}
	
	public boolean isUsed(NSNode nsNode) {
		String nameSpace = getNameSpace(nsNode);
		if (getRegisterCount(nameSpace) > 0) {
			return true;
		}
		for (NSNode child : nsNode.getChildren()) {
			if (isUsed(child)) {
				return true;
			}
		}
		return false;
	}
	
	public int getRegisterCount(String namespace) {
		Integer counter = nameSpaceCounter.get(namespace);
		if (counter != null) {
			return counter;
		}
		return 0;
	}
	
	public String getNameSpace(NSNode node) {
		if (node == rootNSNode) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(node.toString());
		NSNode parent = node.getParent();
		while (parent != rootNSNode) {
			buffer.insert(0, DOT);
			buffer.insert(0, parent.toString());
			parent = parent.getParent();
		}
		return buffer.toString();
	}
	
	public String getNewNameSpace(NSNode changedNode, String newName, NSNode currentNode) {
		if (currentNode == rootNSNode) {
			return null;
		}
		NSNode node = currentNode;
		StringBuffer buffer = new StringBuffer();
		buffer.append(getName(node, changedNode, newName));
		NSNode parent = node.getParent();
		while (parent != rootNSNode) {
			buffer.insert(0, DOT);
			buffer.insert(0, getName(parent, changedNode, newName));
			parent = parent.getParent();
		}
		return buffer.toString();
	}
	
	public String getNewNameSpace(NSNode removedNode, NSNode currentNode) {
		if (currentNode == rootNSNode) {
			return null;
		}
		boolean empty = true;
		NSNode node = currentNode;
		StringBuffer buffer = new StringBuffer();
		if (node != removedNode) {
			buffer.append(node.toString());
			empty = false;
		}
		NSNode parent = node.getParent();
		while (parent != rootNSNode) {
			if (parent != removedNode) {
				if (!empty) {
					buffer.insert(0, DOT);
				}
				buffer.insert(0, parent.toString());
				empty = false;
			}
			parent = parent.getParent();
		}
		return buffer.toString();
	}
	
	private String getName(NSNode node, NSNode changedNode, String newName) {
		if (node == changedNode) {
			return newName;
		} else {
			return node.toString();
		}
	}
	
	public NSNode getNameSpaceNode(String namespace) {
		return findNSNode(namespace);
	}
	
	protected NSNode removeNSNode(NSNode node) {
		NSNode parent = node.getParent();
		parent.removeChild(node);
		node.setParent(null);
		return parent;
	}
	
	protected NSNode findNSNode(String namespace) {
		StringTokenizer tokenizer = new StringTokenizer(namespace, DOT);
		NSNode node = rootNSNode;
		while (node != null && tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			NSNode child = node.getChild(token);
			node = child;
		}
		return node;
	}
	
	protected boolean unregister(String namespace) {
		Integer counter = nameSpaceCounter.get(namespace);
		if (counter == null) {
			throw new IllegalArgumentException("Unknown namespace");
		}
		if (counter == 0) {
			// shoould not happen
			throw new IllegalArgumentException("Unused namespace");
		}
		counter--;
//		if (counter == 0) {
//			nameSpaceCounter.remove(namespace);
//			return true;
//		} else {
		nameSpaceCounter.put(namespace, counter);
		return false;
//		}
	}
	
	public void clear() {
		nameSpaceCounter.clear();
		// clear root
		List<NSNode> children = new ArrayList<>(rootNSNode.getChildren());
		for (NSNode child : children) {
			removeNSNode(child);
		}
	}
	
	public void dispose() {
		nameSpaceCounter.clear();		
	}
	
}
