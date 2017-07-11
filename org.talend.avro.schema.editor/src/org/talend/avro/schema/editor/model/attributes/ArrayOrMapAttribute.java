package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

public class ArrayOrMapAttribute extends AvroAttributeImpl<ArrayOrMapValue> {
	
	public static final Class<ArrayOrMapValue> VALUE_CLASS = ArrayOrMapValue.class;
	
	public ArrayOrMapAttribute(AvroNode node) {
		this(node, null);
	}
	
	public ArrayOrMapAttribute(AvroNode node, ArrayOrMapValue value) {
		super(node, AvroAttributes.ARRAY_OR_MAP, VALUE_CLASS, value);
	}

	@Override
	public ArrayOrMapValue getCopyOfValue() {
		return getValue().getACopy();
	}	

}
