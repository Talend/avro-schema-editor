package org.talend.avro.schema.editor.model;

import java.util.List;

/**
 * <b>Implementation of a schema node.</b>
 * <p>
 * There is no direct links between schema nodes (i.e. a schema node does not have references to its parent and children).
 * <p>
 * The relationship between schema nodes are computed indirectly by using the model hierarchy.
 * <p>
 * A schema node exists in two contexts: 
 * <ul>
 * <li>1) a simple context in which it has a reference to a standard model node (i.e. an avro node which is not a RefNode)
 * <li>2) a ref node context in which is has a reference to a RefNode model node and one of the child of this ref node.
 * </ul>
 * The second context is used to handle the node hierarchy under a ref node.
 * <p>
 * @author timbault
 *
 */
public class SchemaNodeImpl implements SchemaNode {

	private SchemaNodeRegistry registry;
	
	private RefNode refNode;
	
	private AvroNode avroNode;
	
	// TODO very important!!!!!
	// We should have a mechanism to handle safely the recursive case.
	// We should handle the following case: record_a#field_1/record_a#field_1/record_a#field_1.........
	// We should store the 'level' of the current schema node. This level could be updated dynamically.
	// private int level;
	
	public SchemaNodeImpl(SchemaNodeRegistry registry, AvroNode avroNode) {
		this(registry, avroNode, null);
	}
	
	public SchemaNodeImpl(SchemaNodeRegistry registry, AvroNode avroNode, RefNode refNode) {
		super();
		this.registry = registry;
		this.avroNode = avroNode;
		this.refNode = refNode;
	}

	@Override
	public boolean hasParent() {
		return avroNode.hasParent();
	}

	@Override
	public SchemaNode getParent() {
		if (refNode == null) {
			return registry.getParent(avroNode);
		} else {
			return registry.getParent(avroNode, refNode);
		}
	}

	@Override
	public boolean hasChildren() {
		if (avroNode.getType().isRef()) {
			// here we have to be very careful since we could introduce an infinite hierarchy (if this ref node encapsulates a record node which reference itself)
			// The rule is: if this ref node contains itself in its ancestors, then we consider that it has not more children here (just to stop the hierarchy)
			RefNode refNode = (RefNode) avroNode;
			AvroNode referencedNode = refNode.getReferencedNode();
			if (ModelUtil.isAncestor(referencedNode, refNode)) {
				return false;
			}
			return registry.hasChildren(referencedNode);
		} else {
			return registry.hasChildren(avroNode);
		}
	}

	@Override
	public List<SchemaNode> getChildren() {
		if (refNode == null) {
			return registry.getChildren(avroNode);
		} else {
			return registry.getChildren(avroNode, refNode);
		}
	}

	@Override
	public int getChildrenCount() {
		if (avroNode.getType().isRef()) {
			return ((RefNode) avroNode).getReferencedNode().getChildrenCount();
		} else {
			return avroNode.getChildrenCount();
		}
	}

	@Override
	public AvroNode getAvroNode() {
		return avroNode;
	}
	
}
