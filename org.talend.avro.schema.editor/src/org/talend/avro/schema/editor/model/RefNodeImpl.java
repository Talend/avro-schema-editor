package org.talend.avro.schema.editor.model;

import java.util.Collections;
import java.util.List;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.AvroAttributeSet;

/**
 * Implementation of a reference node. 
 * <p>
 * A ref node is not a standard avro element, it is a technical node used internally in the editor to represents the reuse of a namespaced avro schema (record, enum or fiexed). 
 * <p>
 * It encapsulates a namespaced avro node.
 * <p>
 * A ref node is not directly editable, it cannot have children.
 * 
 * @author timbault
 *
 */
public class RefNodeImpl extends AvroNodeImpl implements RefNode {

	/**
	 * The referenced node (which should not be a ref node)
	 */
	private AvroNode referencedNode;
			
	public RefNodeImpl(AvroNode referencedNode, AvroContext context) {
		super(NodeType.REF, context);
		this.referencedNode = referencedNode;
	}
	
	public AvroNode getReferencedNode() {
		return referencedNode;
	}

	@Override
	public void init(AttributeInitializer initializer) {
		addPathAttribute(initializer);
	}

	public NodeType getReferencedType() {
		return referencedNode.getType();
	}
	
	@Override
	public AvroAttributeSet getReferencedAttributes() {	
		return referencedNode.getAttributes();
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public boolean hasChildren(NodeType type) {
		return false;
	}

	@Override
	public List<AvroNode> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public int getChildrenCount() {
		return 0;
	}

	@Override
	public List<AvroNode> getChildren(NodeType type) {
		return Collections.emptyList();
	}

	@Override
	public boolean hasChild(AvroNode child) {
		return false;
	}

	@Override
	public AvroNode getChild(int index) {
		return null;
	}

	@Override
	public int getChildIndex(AvroNode child) {
		throw new UnsupportedOperationException("Reference node has no child");
	}

	@Override
	public void addChild(AvroNode child) {
		throw new UnsupportedOperationException("Cannot add child on reference node");
	}

	@Override
	public void addChild(AvroNode child, AvroNode target, TargetPosition position) {
		throw new UnsupportedOperationException("Cannot add child on reference node");
	}

	@Override
	public void moveChild(AvroNode child, AvroNode target, TargetPosition position) {
		throw new UnsupportedOperationException("Cannot move child on reference node");
	}

	@Override
	public void removeChild(AvroNode child) {
		throw new UnsupportedOperationException("Cannot remove child from reference node");
	}

	@Override
	public boolean visitNode(IAvroNodeVisitor visitor) {
		AvroNode visitedNode = visitor.enterRefNode(this);
		if (visitedNode != null) {
			boolean continueVisit = true;
			continueVisit = visitChildNodes(visitedNode, visitor);
			return visitor.exitRefNode(visitedNode)  && continueVisit;
		} 
		return visitor.exitRefNode(this);		
	}	
	
}
