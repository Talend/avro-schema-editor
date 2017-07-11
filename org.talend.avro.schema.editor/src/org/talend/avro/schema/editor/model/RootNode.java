package org.talend.avro.schema.editor.model;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.NodeTypeNameProvider;

/**
 * Implementation of a node of type root.
 * <p>
 * Root node is not a standard avro element, it is a technical node used to represent the root of the schema.
 * <p>
 * There is only one root node and it cannot have parent.
 * 
 * @author timbault
 *
 */
public class RootNode extends AvroNodeImpl {

	public RootNode(AvroContext context) {
		super(NodeType.ROOT, context);
	}

	@Override
	public void init(AttributeInitializer initializer) {
		buildAttributes(initializer);
		super.init(initializer);
	}
	
	@Override
	public AvroNode getParent() {
		return null;
	}

	@Override
	public void setParent(AvroNode parent) {
		throw new UnsupportedOperationException("Root node cannot have parent");
	}

	private void buildAttributes(AttributeInitializer initializer) {
		addNotEditableNameAttribute(initializer, new NodeTypeNameProvider(NodeType.ROOT));				
	}
	
	@Override
	public boolean visitNode(IAvroNodeVisitor visitor) {
		AvroNode visitedNode = visitor.enterRootNode(this);
		if (visitedNode != null) {
			boolean continueVisit = true;
			continueVisit = visitChildNodes(visitedNode, visitor);
			return visitor.exitRootNode(visitedNode)  && continueVisit;
		} 
		return visitor.exitRootNode(this);		
	}

}
