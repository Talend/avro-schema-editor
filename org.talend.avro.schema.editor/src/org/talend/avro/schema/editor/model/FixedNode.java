package org.talend.avro.schema.editor.model;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;

/**
 * Implementation of a node of type fixed.
 * 
 * @author timbault
 *
 */
public class FixedNode extends AvroNodeImpl {

	public FixedNode(AvroContext context) {
		super(NodeType.FIXED, context);		
	}
	
	@Override
	public void init(AttributeInitializer initializer) {
		buildAttributes(initializer);
		super.init(initializer);
	}

	private void buildAttributes(AttributeInitializer initializer) {
		addNameSpaceAttribute(initializer);
		addNameAttribute(initializer);		
		addAliasesAttribute(initializer);
		addIntegerAttribute(AvroAttributes.SIZE, 1, initializer);
		addCustomPropertiesAttribute(AvroAttributes.CUSTOM_PROPERTIES, initializer);
	}
	
	@Override
	public boolean visitNode(IAvroNodeVisitor visitor) {
		AvroNode visitedNode = visitor.enterFixedNode(this);
		if (visitedNode != null) {
			boolean continueVisit = true;
			continueVisit = visitChildNodes(visitedNode, visitor);
			return visitor.exitFixedNode(visitedNode)  && continueVisit;
		} 
		return visitor.exitFixedNode(this);	
	}
	
}
