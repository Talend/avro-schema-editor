package org.talend.avro.schema.editor.model;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;

/**
 * Implementation of a node of type field.
 * 
 * @author timbault
 *
 */
public class FieldNode extends AvroNodeImpl {

	public FieldNode(AvroContext context) {
		super(NodeType.FIELD, context);
	}
	
	@Override
	public void init(AttributeInitializer initializer) {
		buildAttributes(initializer);
		super.init(initializer);
	}

	private void buildAttributes(AttributeInitializer initializer) {
		addNameAttribute(initializer);
		addDocAttribute(initializer);
		addPrimitiveTypeAttribute(initializer);
		addBooleanAttribute(AvroAttributes.OPTIONAL, false, initializer);
		addAliasesAttribute(initializer);
		addDefaultValueAttribute(initializer);
		addCustomPropertiesAttribute(AvroAttributes.CUSTOM_PROPERTIES, initializer);
		//addCustomPropertiesAttribute(AvroAttributes.TYPE_PROPERTIES, initializer);
	}
	
	@Override
	public boolean visitNode(IAvroNodeVisitor visitor) {
		AvroNode visitedNode = visitor.enterFieldNode(this);
		if (visitedNode != null) {
			boolean continueVisit = true;
			continueVisit = visitChildNodes(visitedNode, visitor);
			return visitor.exitFieldNode(visitedNode)  && continueVisit;
		} 
		return visitor.exitFieldNode(this);		
	}
	
}
