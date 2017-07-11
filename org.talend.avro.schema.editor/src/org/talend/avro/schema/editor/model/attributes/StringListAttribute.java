package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

public class StringListAttribute extends AvroAttributeImpl<StringList> {

	public static final Class<StringList> VALUE_CLASS = StringList.class;
	
	public StringListAttribute(AvroNode node, String name, StringList value) {
		super(node, name, VALUE_CLASS, value);
	}

	@Override
	public StringList getValue() {
		return super.getValue().getACopy();
	}

	@Override
	public void setValue(StringList value) {
		super.getValue().apply(value);
	}

	@Override
	public StringList getCopyOfValue() {
		return getValue().getACopy();
	}	
	
}
