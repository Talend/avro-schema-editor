package org.talend.avro.schema.editor.model;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.model.attributes.NodeTypeNameProvider;

/**
 * Implementation of a node of type array.
 * 
 * @author timbault
 *
 */
public class ArrayNode extends AvroNodeImpl {

	public ArrayNode(AvroContext context) {
		super(NodeType.ARRAY, context);
	}
	
	@Override
	public void init(AttributeInitializer initializer) {
		buildAttributes(initializer);
		super.init(initializer);
	}

	private void buildAttributes(AttributeInitializer initializer) {
		addNotEditableNameAttribute(initializer, new NodeTypeNameProvider(NodeType.ARRAY));		
		addPrimitiveTypeAttribute(initializer);
		addBooleanAttribute(AvroAttributes.OPTIONAL, false, initializer);
		addArrayOrMapAttribute(initializer);
		//addCustomPropertiesAttribute(AvroAttributes.TYPE_PROPERTIES, initializer);
	}
	
	@Override
	public boolean visitNode(IAvroNodeVisitor visitor) {		
		AvroNode visitedNode = visitor.enterArrayNode(this);
		if (visitedNode != null) {
			boolean continueVisit = true;
			continueVisit = visitChildNodes(visitedNode, visitor);
			return visitor.exitArrayNode(visitedNode)  && continueVisit;
		} 
		return visitor.exitArrayNode(this);		
	}
	
}
