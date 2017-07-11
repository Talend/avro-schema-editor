package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

public class IntegerAttribute extends AvroAttributeImpl<Integer> {

	public IntegerAttribute(AvroNode node, String name, Integer value) {
		super(node, name, Integer.class, value);
	}

	@Override
	public Integer getCopyOfValue() {		
		return new Integer(getValue());
	}

}
