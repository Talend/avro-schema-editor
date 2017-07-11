package org.talend.avro.schema.editor.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.dialogs.SearchPattern;
import org.talend.avro.schema.editor.edit.services.IEditorServiceProvider;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.RefNode;
import org.talend.avro.schema.editor.model.attributes.AttributeListener;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.model.attributes.NameAttribute;
import org.talend.avro.schema.editor.model.attributes.NameSpaceAttribute;
import org.talend.avro.schema.editor.model.attributes.NameSpaceDefinition;
import org.talend.avro.schema.editor.utils.StringUtils;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * This class keeps references to all the namespaced {@link AvroNode} created in an avro schema editor.
 * 
 * @author timbault
 * @see AvroNode
 *
 */
public class SchemaRegistry {
	
	private Map<NodeType, NamedNodeRegistry> mainRegistry = new HashMap<>();
	
	private AttributeListener<String> nameAttrListener;
	
	private AttributeListener<String> nameSpaceAttrListener;
	
	private NameSpaceRegistry nameSpaceRegistry;
	
	public SchemaRegistry(IEditorServiceProvider serviceProvider) {
		super();
		this.nameSpaceRegistry = new NameSpaceRegistry();
		init();
	}

	private void init() {
		nameAttrListener = new AttributeListener<String>() {			
			@Override
			public void onAttributeValueChanged(AvroAttribute<String> nameAttr, String oldValue, String newValue) {
				AvroNode node = nameAttr.getHolder();
				String oldNameSpace = null;
				String oldName = null;
				if (AttributeUtil.isNameWithNameSpace(oldValue)) {
					oldNameSpace = AttributeUtil.getNameSpaceFromFullName(oldValue);
					oldName = AttributeUtil.getTrueNameFromFullName(oldValue);
				} else {
					oldNameSpace = AttributeUtil.tryToGetTrueNameSpace(node, NameSpaceDefinition.EXPLICIT, NameSpaceDefinition.INHERITED);
					oldName = oldValue;
				}
				String oldFullName = AttributeUtil.getFullName(oldNameSpace, oldName);
				String newFullName = AttributeUtil.getFullName(node);
				updateRegistryOnFullNameChanged(node, oldFullName, newFullName);
				String newNameSpace = AttributeUtil.getNameSpaceFromFullName(newFullName);
				updateNameSpaceRegistry(oldNameSpace, newNameSpace);
			}
		};		
		nameSpaceAttrListener = new AttributeListener<String>() {			
			@Override
			public void onAttributeValueChanged(AvroAttribute<String> nameSpaceAttr, String oldValue, String newValue) {
				AvroNode node = nameSpaceAttr.getHolder();
				String oldNameSpace = null;
				String oldName = AttributeUtil.getNameFromAttribute(node);
				if (AttributeUtil.isNameWithNameSpace(node)) {
					oldNameSpace = AttributeUtil.getTrueNameSpace(node, NameSpaceDefinition.IN_NAME, false);
					oldName = AttributeUtil.getTrueName(node);
				} else if (oldValue == null || oldValue.trim().isEmpty()) {
					oldNameSpace = AttributeUtil.getTrueNameSpace(node, NameSpaceDefinition.INHERITED, false);			
				} else {
					oldNameSpace = oldValue;
				}
				String oldFullName = AttributeUtil.getFullName(oldNameSpace, oldName);					
				String newFullName = AttributeUtil.getFullName(node);						
				updateRegistryOnFullNameChanged(node, oldFullName, newFullName);
				String newNameSpace = AttributeUtil.getNameSpaceFromFullName(newFullName);
				updateNameSpaceRegistry(oldNameSpace, newNameSpace);
			}
		};
	}
	
	protected void updateRegistryOnFullNameChanged(AvroNode node, String oldFullName, String newFullName) {
		// first check that the given node is registered with its old fullname
		AvroNode registeredNode = getRegisteredNode(node.getType(), oldFullName);
		if (registeredNode != node) {
			// should never happen
			throw new IllegalStateException("Schema registry invalid state");
		}
		NamedNodeRegistry namedNodeRegistry = mainRegistry.get(node.getType());
		namedNodeRegistry.update(registeredNode, oldFullName, newFullName);
		String oldNameSpace = AttributeUtil.getNameSpaceFromFullName(oldFullName);
		String newNameSpace = AttributeUtil.getNameSpaceFromFullName(newFullName);
		if (!StringUtils.areEqual(oldNameSpace, newNameSpace)) {
			// update registry hierarchy only if name space has changed
			updateRegistryHierarchy(registeredNode, oldNameSpace, newNameSpace);
		}
	}
	
	protected void updateRegistryHierarchy(AvroNode node, String oldInheritedNameSpace, String newInheritedNameSpace) {		
		for (AvroNode child : node.getChildren()) {
			NodeType type = child.getType();
			if (type.hasFullName()) {
				NameSpaceDefinition nsDef = AttributeUtil.getNameSpaceDefinition(child);
				if (nsDef == NameSpaceDefinition.INHERITED) {
					// in this case we have to update the registered full name
					NamedNodeRegistry namedNodeRegistry = mainRegistry.get(type);
					String name = AttributeUtil.getTrueName(child);
					String oldFullName = AttributeUtil.getFullName(oldInheritedNameSpace, name);					
					String newFullName = AttributeUtil.getFullName(newInheritedNameSpace, name);		
					namedNodeRegistry.update(child, oldFullName, newFullName);
					updateNameSpaceRegistry(oldInheritedNameSpace, newInheritedNameSpace);
					updateRegistryHierarchy(child, oldInheritedNameSpace, newInheritedNameSpace);
				}
			} else {
				updateRegistryHierarchy(child, oldInheritedNameSpace, newInheritedNameSpace);
			}
		}
	}
	
	protected void updateNameSpaceRegistry(String oldNameSpace, String newNameSpace) {
		if (oldNameSpace != null && !oldNameSpace.trim().isEmpty()) {
			nameSpaceRegistry.removeNameSpace(oldNameSpace);
		}
		if (newNameSpace != null && !newNameSpace.trim().isEmpty()) {
			nameSpaceRegistry.addNameSpace(newNameSpace);
		}
	}
	
	public void clear() {
		for (NodeType type : NodeType.REGISTERED_NODE_TYPES) {
			NamedNodeRegistry namedNodeRegistry = mainRegistry.get(type);
			if (namedNodeRegistry != null) {
				namedNodeRegistry.clear();
			}
		}
		mainRegistry.clear();
		nameSpaceRegistry.clear();
	}
	
	public boolean isRegistrable(AvroNode node) {
		NodeType nodeType = node.getType();
		return nodeType.hasFullName();
	}
	
	public void register(AvroNode node, boolean recursively) {
		registerRecursively(node, recursively);
	}
	
	protected void registerRecursively(AvroNode node, boolean recursively) {
		if (isRegistrable(node)) {
			registerNode(node);
		}
		if (recursively) {
			for (AvroNode child : node.getChildren()) {
				registerRecursively(child, recursively);
			}
		}
	}
	
	protected void registerNode(AvroNode node) {
		
		NamedNodeRegistry namedNodeRegistry = null;
		
		NodeType nodeType = node.getType();
		
		if (nodeType.isRef()) {
			
			RefNode refNode = (RefNode) node;
			AvroNode referencedNode = refNode.getReferencedNode();
			NodeType referencedType = refNode.getReferencedType();
			
			String fullName = AttributeUtil.getFullName(referencedNode);
			
			namedNodeRegistry = mainRegistry.get(referencedType);
			if (namedNodeRegistry == null || !namedNodeRegistry.isRegistered(fullName)) {
				throw new IllegalStateException("Referenced node is not registered");
			}
			
			namedNodeRegistry.addRefNode(refNode);						
			
		} else {
			
			namedNodeRegistry = mainRegistry.get(nodeType);
			if (namedNodeRegistry == null) {
				namedNodeRegistry = new NamedNodeRegistry();
				mainRegistry.put(nodeType, namedNodeRegistry);
			}
			
			addListeners(node);
			
			String fullName = AttributeUtil.getFullName(node);
			namedNodeRegistry.register(fullName, node);
			
		}
		
		registerNameSpace(node);
		
	}
	
	protected void registerNameSpace(AvroNode node) {
		String nameSpace = AttributeUtil.getTrueNameSpace(node);
		if (nameSpace != null && !nameSpace.trim().isEmpty()) {
			nameSpaceRegistry.addNameSpace(nameSpace);
		}
	}
	
	public void unregister(AvroNode node) {
		// first collect all the nodes
		List<AvroNode> nodes = new ArrayList<>();
		ModelUtil.collect(node, nodes);
		// then unregister from last to first collected nodes
		for (int i = nodes.size() - 1; i >= 0; i--) {
			AvroNode collectedNode = nodes.get(i);
			if (isRegistrable(collectedNode)) {
				unregisterNode(collectedNode);
			}
		}
	}
			
	protected void unregisterNode(AvroNode node) {

		NamedNodeRegistry namedNodeRegistry = null;

		NodeType nodeType = node.getType();

		if (nodeType.isRef()) {

			RefNode refNode = (RefNode) node;
			AvroNode referencedNode = refNode.getReferencedNode();
			NodeType referencedType = refNode.getReferencedType();

			String fullName = AttributeUtil.getFullName(referencedNode);

			namedNodeRegistry = mainRegistry.get(referencedType);
			if (namedNodeRegistry == null || !namedNodeRegistry.isRegistered(fullName)) {
				throw new IllegalStateException("Referenced node is not registered");
			}

			namedNodeRegistry.removeRefNode(refNode);

		} else {

			String fullName = AttributeUtil.getFullName(node);

			namedNodeRegistry = mainRegistry.get(nodeType);
			if (namedNodeRegistry == null || !namedNodeRegistry.isRegistered(fullName)) {
				throw new IllegalStateException("Node is not registered");
			}

			removeListeners(node);

			namedNodeRegistry.unregister(fullName);

		}

		unregisterNameSpace(node);
		
	}
	
	protected void unregisterNameSpace(AvroNode node) {
		String nameSpace = AttributeUtil.getTrueNameSpace(node);
		if (nameSpace != null && !nameSpace.trim().isEmpty()) {
			nameSpaceRegistry.removeNameSpace(nameSpace);
		}
	}
	
	protected void addListeners(AvroNode node) {
		NameAttribute nameAttribute = node.getAttributes().getAttributeFromClass(AvroAttributes.NAME, NameAttribute.class);		
		nameAttribute.addListener(nameAttrListener);
		NameSpaceAttribute nameSpaceAttribute = node.getAttributes().getAttributeFromClass(AvroAttributes.NAME_SPACE, NameSpaceAttribute.class);
		nameSpaceAttribute.addListener(nameSpaceAttrListener);
	}
	
	protected void removeListeners(AvroNode node) {
		NameAttribute nameAttribute = node.getAttributes().getAttributeFromClass(AvroAttributes.NAME, NameAttribute.class);		
		nameAttribute.removeListener(nameAttrListener);
		NameSpaceAttribute nameSpaceAttribute = node.getAttributes().getAttributeFromClass(AvroAttributes.NAME_SPACE, NameSpaceAttribute.class);
		nameSpaceAttribute.removeListener(nameSpaceAttrListener);
	}
	
	public boolean isRegistered(NodeType type, String fullName) {
		NamedNodeRegistry namedNodeRegistry = mainRegistry.get(type);
		return namedNodeRegistry != null && namedNodeRegistry.isRegistered(fullName);
	}
	
	public boolean isSchemaNameAlreadyRegistered(NodeType type, String name, String nameSpace) {
		String fullName = AttributeUtil.getFullName(nameSpace, name);
		return isRegistered(type, fullName);
	}
		
	public AvroNode getRegisteredNode(NodeType type, String name) {
		NamedNodeRegistry namedNodeRegistry = mainRegistry.get(type);
		if (namedNodeRegistry != null) {
			return namedNodeRegistry.getRegisteredNode(name);
		}
		return null;
	}
	
	public List<AvroNode> getNodeAndRefNodesFromFullName(NodeType type, String pattern, boolean withRef) {
		NamedNodeRegistry namedNodeRegistry = mainRegistry.get(type);
		if (namedNodeRegistry != null) {
			return namedNodeRegistry.getNodeAndRefNodesFromFullName(pattern, withRef);
		}
		return Collections.emptyList();
	}
	
	public boolean hasRefNodes(AvroNode node) {
		NamedNodeRegistry namedNodeRegistry = mainRegistry.get(node.getType());
		if (namedNodeRegistry != null) {
			return namedNodeRegistry.hasRefNodes(node);
		}
		return false;
	}
	
	public List<RefNode> getRefNodes(AvroNode node) {
		NamedNodeRegistry namedNodeRegistry = mainRegistry.get(node.getType());
		if (namedNodeRegistry != null) {
			return namedNodeRegistry.getRefNodes(node);
		}
		return Collections.emptyList();
	}
	
	public List<AvroNode> getNodesFromNameSpace(String namespace) {
		List<AvroNode> nodes = new ArrayList<>();
		for (NodeType type : NodeType.NAMESPACED_NODE_TYPES) {
			nodes.addAll(getNodesFromNameSpace(type, namespace));
		}
		return nodes;
	}
	
	public List<AvroNode> getNodesFromNameSpace(NodeType type, String namespace) {
		NamedNodeRegistry namedNodeRegistry = mainRegistry.get(type);
		if (namedNodeRegistry != null) {
			return namedNodeRegistry.getNodesFromNameSpace(namespace);
		}
		return Collections.emptyList();
	}
	
	public List<AvroNode> getAllRegisteredNodes() {
		List<AvroNode> nodes = new ArrayList<>();
		for (NodeType type : NodeType.NAMESPACED_NODE_TYPES) {
			nodes.addAll(getAllRegisteredNodes(type));
		}
		return nodes;
	}
	
	public List<AvroNode> getAllRegisteredNodes(NodeType type) {
		NamedNodeRegistry namedNodeRegistry = mainRegistry.get(type);
		if (namedNodeRegistry != null) {
			Collection<AvroNode> allRegisteredNodes = namedNodeRegistry.getAllRegisteredNodes();
			return new ArrayList<>(allRegisteredNodes);
		}
		return Collections.emptyList();
	}
	
	public int getRegisteredNodeCount(NodeType type) {
		NamedNodeRegistry namedNodeRegistry = mainRegistry.get(type);
		if (namedNodeRegistry != null) {
			return namedNodeRegistry.getSize();
		}
		return 0;
	}
	
	public int getNbrOfReferences(AvroNode node) {
		NamedNodeRegistry namedNodeRegistry = mainRegistry.get(node.getType());
		if (namedNodeRegistry != null) {
			return namedNodeRegistry.getNbrOfReferences(node);
		}
		return 0;
	}
	
	public NameSpaceRegistry getNameSpaceRegistry() {
		return nameSpaceRegistry;
	}
	
	public NSNode getNameSpaceTree() {
		return nameSpaceRegistry.getNameSpaceTree();
	}
	
	public void dispose() {
		clear();
		for (NamedNodeRegistry registry : mainRegistry.values()) {
			registry.dispose();
		}
		mainRegistry.clear();
		nameSpaceRegistry.dispose();
	}
	
	private class NamedNodeRegistry {
		
		private Map<String, AvroNode> name2node = new HashMap<>();
		
		private Map<AvroNode, List<RefNode>> node2refs = new HashMap<>();
		
		public void clear() {
			for (AvroNode node : name2node.values()) {
				removeListeners(node);
			}
			name2node.clear();
			node2refs.clear();
		}
		
		public void register(String name, AvroNode node) {
			if (name2node.get(name) != null) {
				throw new IllegalStateException("Node " + name + " already registered");
			}
			name2node.put(name, node);
		}
		
		public void update(AvroNode registeredNode, String oldName, String newName) {
			if (name2node.get(oldName) != registeredNode) {
				throw new IllegalStateException("Invalid state");
			}
			name2node.remove(oldName);
			name2node.put(newName, registeredNode);
		}
		
		public void unregister(String name) {
			if (name2node.get(name) == null) {
				throw new IllegalStateException("Node " + name + " is not registered");
			}
			name2node.remove(name);
		}
		
		public void addRefNode(RefNode refNode) {
			AvroNode referencedNode = refNode.getReferencedNode();
			String name = AttributeUtil.getFullName(referencedNode);
			if (!isRegistered(name)) {
				throw new IllegalStateException("Cannot add ref node with unknown referenced node");
			}
			List<RefNode> refNodes = node2refs.get(referencedNode);
			if (refNodes == null) {
				refNodes = new ArrayList<>();
				node2refs.put(referencedNode, refNodes);
			}
			refNodes.add(refNode);
		}
		
		public boolean hasRefNodes(AvroNode node) {
			List<RefNode> refNodes = node2refs.get(node);
			return refNodes != null && !refNodes.isEmpty();
		}
		
		public List<RefNode> getRefNodes(AvroNode node) {
			List<RefNode> refNodes = new ArrayList<>();
			List<RefNode> refNodeList = node2refs.get(node);
			if (refNodeList != null) {
				refNodes.addAll(refNodeList);
			}
			return refNodes;
		}
		
		public void removeRefNode(RefNode refNode) {
			AvroNode referencedNode = refNode.getReferencedNode();
			List<RefNode> refNodes = node2refs.get(referencedNode);
			if (refNodes != null) {
				if (!refNodes.contains(refNode)) {
					throw new IllegalArgumentException("Unknown ref node");
				}
				refNodes.remove(refNode);
			}
		}
		
		public boolean isRegistered(String name) {
			return name2node.get(name) != null;
		}
		
		public AvroNode getRegisteredNode(String name) {
			return name2node.get(name);
		}
				
		public List<AvroNode> getNodeAndRefNodesFromFullName(String pattern, boolean withRef) {
			List<AvroNode> nodes = new ArrayList<>();
			for (Map.Entry<String, AvroNode> entry : name2node.entrySet()) {
				if (isTextMatchingSearchPattern(entry.getKey(), pattern)) {
					AvroNode node = entry.getValue();
					nodes.add(node);
					if (withRef) {
						// add the ref nodes too
						List<RefNode> refNodes = node2refs.get(node);
						if (refNodes != null) {
							nodes.addAll(refNodes);
						}
					}
				}
			}			
			return nodes;
		}
		
		protected boolean isTextMatchingSearchPattern(String text, String searchPattern) {
	        SearchPattern matcher = new SearchPattern(SearchPattern.RULE_PATTERN_MATCH
	                | SearchPattern.RULE_EXACT_MATCH | SearchPattern.RULE_PREFIX_MATCH
	                | SearchPattern.RULE_BLANK_MATCH);
	        matcher.setPattern("*" + searchPattern);
	        return matcher.matches(text);
	    }
		
		public List<AvroNode> getNodesFromNameSpace(String namespace) {
			List<AvroNode> nodes = new ArrayList<>();
			for (AvroNode node : name2node.values()) {
				String nodeNS = AttributeUtil.getTrueNameSpace(node);
				if ((namespace == null && nodeNS == null) || (namespace != null && namespace.equals(nodeNS))) {
					nodes.add(node);
				}
			}
			return nodes;
		}
		
		public Collection<AvroNode> getAllRegisteredNodes() {
			return name2node.values();
		}
		
		public  int getSize() {
			return name2node.size();
		}
		
		public int getNbrOfReferences(AvroNode node) {
			List<RefNode> refNodes = node2refs.get(node);
			if (refNodes != null) {
				return refNodes.size();
			}
			return 0;
		}
	
		public void dispose() {
			name2node.clear();
			node2refs.clear();
		}
		
	}
	
}
