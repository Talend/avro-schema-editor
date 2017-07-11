package org.talend.avro.schema.editor.model;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;

/**
 * Implementation of a node of type record.
 * 
 * @author timbault
 *
 */
public class RecordNode extends AvroNodeImpl {

	public RecordNode(AvroContext context) {
		super(NodeType.RECORD, context);		
	}

	@Override
	public void init(AttributeInitializer initializer) {
		buildAttributes(initializer);
		super.init(initializer);
	}

	private void buildAttributes(AttributeInitializer initializer) {
		addNameSpaceAttribute(initializer);
		addNameAttribute(initializer);		
		addDocAttribute(initializer);
		addAliasesAttribute(initializer);
		addCustomPropertiesAttribute(AvroAttributes.CUSTOM_PROPERTIES, initializer);
	}
	
	@Override
	public boolean visitNode(IAvroNodeVisitor visitor) {
		AvroNode visitedNode = visitor.enterRecordNode(this);
		if (visitedNode != null) {
			boolean continueVisit = true;
			continueVisit = visitChildNodes(visitedNode, visitor);
			return visitor.exitRecordNode(visitedNode)  && continueVisit;
		} 
		return visitor.exitRecordNode(this);	
	}

}
