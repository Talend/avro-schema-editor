package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

public class UnionNameProvider implements NameProvider {

	@Override
	public String getName(AvroNode node) {
		Boolean choiceType = node.getAttributes().getAttributeValue(AvroAttributes.CHOICE_TYPE, Boolean.class);
		return choiceType ? "choice" : "union";
	}
	
}
