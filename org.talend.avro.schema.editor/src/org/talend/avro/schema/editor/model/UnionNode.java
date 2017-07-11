package org.talend.avro.schema.editor.model;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.model.attributes.UnionNameProvider;

/**
 * Implementation of a node of type union.
 * 
 * @author timbault
 *
 */
public class UnionNode extends AvroNodeImpl {

	public UnionNode(AvroContext context) {
		super(NodeType.UNION, context);		
	}
	
	@Override
	public void init(AttributeInitializer initializer) {
		buildAttributes(initializer);
		super.init(initializer);
	}

	private void buildAttributes(AttributeInitializer initializer) {
		addNotEditableNameAttribute(initializer, new UnionNameProvider());		
		addBooleanAttribute(AvroAttributes.CHOICE_TYPE, false, initializer);
	}
	
	@Override
	public boolean visitNode(IAvroNodeVisitor visitor) {
		AvroNode visitedNode = visitor.enterUnionNode(this);
		if (visitedNode != null) {
			boolean continueVisit = true;
			continueVisit = visitChildNodes(visitedNode, visitor);
			return visitor.exitUnionNode(visitedNode)  && continueVisit;
		} 
		return visitor.exitUnionNode(this);		
	}

}
