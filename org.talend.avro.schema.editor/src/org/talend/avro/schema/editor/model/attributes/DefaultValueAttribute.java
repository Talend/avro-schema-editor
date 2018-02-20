package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

/**
 * This class defines an attribute of type DefaultValue. 
 * 
 * @author timbault
 *
 */
public class DefaultValueAttribute extends AvroAttributeImpl<DefaultValue> {

	public DefaultValueAttribute(AvroNode node) {
		this(node, AvroAttributes.DEFAULT, new DefaultValue());
	}
	
	public DefaultValueAttribute(AvroNode node, String name, DefaultValue defaultValue) {
		super(node, name, DefaultValue.class, defaultValue);
	}

	@Override
	public DefaultValue getValue() {
		return super.getValue().getACopy();
	}

	@Override
	public void setValue(DefaultValue defaultValue) {
		super.getValue().apply(defaultValue);
	}

	@Override
	public DefaultValue getCopyOfValue() {
		return getValue().getACopy();
	}	
	
}
