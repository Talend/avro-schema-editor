package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

public class CustomPropertiesAttribute extends AvroAttributeImpl<CustomProperties> {

	public static final Class<CustomProperties> VALUE_CLASS = CustomProperties.class;
	
	public CustomPropertiesAttribute(AvroNode node, String attributeName) {
		this(node, attributeName, new CustomProperties());
	}
	
	public CustomPropertiesAttribute(AvroNode node, String attributeName, CustomProperties value) {
		super(node, attributeName, VALUE_CLASS, value);
	}

	@Override
	public CustomProperties getCopyOfValue() {
		return getValue().getACopy();
	}

}
