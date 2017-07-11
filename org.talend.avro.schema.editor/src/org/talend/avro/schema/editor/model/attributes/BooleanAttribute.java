package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

public class BooleanAttribute extends AvroAttributeImpl<Boolean> {

	public BooleanAttribute(AvroNode node, String name, Boolean value) {
		super(node, name, Boolean.class, value);
	}

	@Override
	public Boolean getCopyOfValue() {
		return new Boolean(getValue());
	}	
	
}
