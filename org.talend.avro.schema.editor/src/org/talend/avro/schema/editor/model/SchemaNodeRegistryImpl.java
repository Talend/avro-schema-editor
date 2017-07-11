package org.talend.avro.schema.editor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.avro.schema.editor.registry.SchemaRegistry;

/**
 * This class is responsible for creation of {@link SchemaNode}. 
 * It keeps links between avro nodes (the model) and schema nodes (the nodes used to display the model in a tree viewer).
 * 
 * @author timbault
 * @see SchemaNode
 * @see AvroNode
 *
 */
public class SchemaNodeRegistryImpl implements SchemaNodeRegistry {

	private SchemaRegistry schemaRegistry;
	
	private SchemaContentProvider contentProvider = new DefaultSchemaContentProvider();
	
	private Map<AvroNode, SchemaNode> avro2schema = new HashMap<>();
	
	private Map<RefNode, Map<AvroNode, SchemaNode>> ref2nodeMaps = new HashMap<>();
		
	public SchemaNodeRegistryImpl(SchemaRegistry schemaRegistry) {
		super();
		this.schemaRegistry = schemaRegistry;
	}
	
	public void setSchemaContentProvider(SchemaContentProvider contentProvider) {
		if (contentProvider == null) {
			this.contentProvider = new DefaultSchemaContentProvider();
		} else {
			this.contentProvider = contentProvider;
		}
	}
	
	protected AvroNode getNodeParent(AvroNode node) {
		return contentProvider.getParent(node);
	}
	
	protected List<AvroNode> getNodeChildren(AvroNode node) {
		return Arrays.asList(contentProvider.getChildren(node));
	}
	
	public SchemaNode getSchemaNode(AvroNode node) {
		return getSchemaNode(avro2schema, node);
	}
	
	public List<SchemaNode> getSchemaNodes(AvroNode node) {
		List<SchemaNode> schemaNodes = new ArrayList<>();
		schemaNodes.add(getSchemaNode(node));
		// get the ref schema nodes (use the schema registry)
		List<RefNode> refNodes = schemaRegistry.getRefNodes(node);
		for (RefNode refNode : refNodes) {
			schemaNodes.add(getSchemaNode(refNode));
		}
		return schemaNodes;
	}
	
	public SchemaNode getParent(AvroNode node) {
		SchemaNode schemaNode = null;
		AvroNode parent = getNodeParent(node);
		if (parent != null) {
			schemaNode = getSchemaNode(avro2schema, parent);
		}
		return schemaNode;
	}
	
	public boolean hasChildren(AvroNode node) {
		if (node.getType().isRef()) {
			RefNode refNode = (RefNode) node;
			AvroNode referencedNode = refNode.getReferencedNode();
			return contentProvider.hasChildren(referencedNode);
		} else {
			return contentProvider.hasChildren(node);
		}
	}
	
	public List<SchemaNode> getChildren(AvroNode node) {
		List<SchemaNode> schemaNodes = new ArrayList<>();
		if (node.getType().isRef()) {
			RefNode refNode = (RefNode) node;
			Map<AvroNode, SchemaNode> refMap = getRefMap(refNode);
			AvroNode referencedNode = refNode.getReferencedNode();
			List<AvroNode> referencedChildren = getNodeChildren(referencedNode);
			for (AvroNode referencedChild : referencedChildren) {
				SchemaNode schemaNode = getSchemaNode(refMap, referencedChild, refNode);
				schemaNodes.add(schemaNode);
			}
		} else {
			List<AvroNode> children = getNodeChildren(node);
			for (AvroNode child : children) {
				SchemaNode schemaNode = getSchemaNode(avro2schema, child);
				schemaNodes.add(schemaNode);
			}
		}
		return schemaNodes;
	}
	
	public SchemaNode getParent(AvroNode node, RefNode refNode) {
		SchemaNode schemaNode = null;
		AvroNode parent = getNodeParent(node);
		if (parent == refNode.getReferencedNode()) {
			schemaNode = getSchemaNode(avro2schema, refNode);
		} else {
			Map<AvroNode, SchemaNode> refMap = getRefMap(refNode);
			schemaNode = getSchemaNode(refMap, parent, refNode);
		}
		return schemaNode;
	}
	
	public List<SchemaNode> getChildren(AvroNode node, RefNode refNode) {
		List<SchemaNode> schemaNodes = new ArrayList<>();
		Map<AvroNode, SchemaNode> refMap = getRefMap(refNode);
		List<AvroNode> children = getNodeChildren(node);
		for (AvroNode child : children) {
			SchemaNode schemaNode = getSchemaNode(refMap, child, refNode);
			schemaNodes.add(schemaNode);
		}
		return schemaNodes;
	}
	
	protected SchemaNode getSchemaNode(Map<AvroNode, SchemaNode> map, AvroNode node) {
		return getSchemaNode(map, node, null);
	}
	
	protected SchemaNode getSchemaNode(Map<AvroNode, SchemaNode> map, AvroNode node, RefNode refNode) {
		SchemaNode schemaNode = map.get(node);
		if (schemaNode == null) {
			schemaNode = new SchemaNodeImpl(this, node, refNode);
			map.put(node, schemaNode);
		}
		return schemaNode;
	}
	
	protected Map<AvroNode, SchemaNode> getRefMap(RefNode refNode) {
		Map<AvroNode, SchemaNode> refMap = ref2nodeMaps.get(refNode);
		if (refMap == null) {
			refMap = new HashMap<>();
			ref2nodeMaps.put(refNode, refMap);
		}
		return refMap;
	}
	
	public void dispose() {
		avro2schema.clear();
		ref2nodeMaps.clear();
	}
	
}
