package org.talend.avro.schema.editor.model;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.NameProvider;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * Implementation of a node of type PrimitiveType (i.e. null, boolean, int, long, float, double, bytes or string).
 * 
 * @author timbault
 * @see PrimitiveType
 *
 */
public class PrimitiveTypeNode extends AvroNodeImpl {

	public PrimitiveTypeNode(AvroContext context) {
		super(NodeType.PRIMITIVE_TYPE, context);
	}

	@Override
	public void init(AttributeInitializer initializer) {
		buildAttributes(initializer);
		super.init(initializer);
	}

	private void buildAttributes(AttributeInitializer initializer) {		
		addNotEditableNameAttribute(initializer, new NameProvider() {
			@Override
			public String getName(AvroNode node) {				
				return AttributeUtil.getPrimitiveType(node).getName();
			}
		});
		addPrimitiveTypeAttribute(initializer);
		//addCustomPropertiesAttribute(AvroAttributes.TYPE_PROPERTIES, initializer);
	}
	
	@Override
	public boolean visitNode(IAvroNodeVisitor visitor) {
		AvroNode visitedNode = visitor.enterPrimitiveTypeNode(this);
		if (visitedNode != null) {
			boolean continueVisit = true;
			continueVisit = visitChildNodes(visitedNode, visitor);
			return visitor.exitPrimitiveTypeNode(visitedNode)  && continueVisit;
		} 
		return visitor.exitPrimitiveTypeNode(this);	
	}

}
