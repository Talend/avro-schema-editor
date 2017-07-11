package org.talend.avro.schema.editor.model;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;

/**
 * Implementation of a node of type enumeration.
 * 
 * @author timbault
 *
 */
public class EnumNode extends AvroNodeImpl {

	public EnumNode(AvroContext context) {
		super(NodeType.ENUM, context);		
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
		addDocAttribute(initializer);		
		addSymbolsAttribute(initializer);
		addCustomPropertiesAttribute(AvroAttributes.CUSTOM_PROPERTIES, initializer);
	}
	
	@Override
	public boolean visitNode(IAvroNodeVisitor visitor) {
		AvroNode visitedNode = visitor.enterEnumNode(this);
		if (visitedNode != null) {
			boolean continueVisit = true;
			continueVisit = visitChildNodes(visitedNode, visitor);
			return visitor.exitEnumNode(visitedNode)  && continueVisit;
		} 
		return visitor.exitEnumNode(this);		
	}

}
