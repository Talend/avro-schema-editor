package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

/**
 * Base implementation of a string attribute.
 * 
 * @author timbault
 *
 */
public class StringAttribute extends AvroAttributeImpl<String> {

	private boolean nullable;
	
	public StringAttribute(AvroNode node, String name, String value) {
		this(node, name, value, false);
	}
	
	public StringAttribute(AvroNode node, String name, String value, boolean nullable) {
		super(node, name, String.class, value);
		this.nullable = nullable;
	}
	
	@Override
	public String getValue() {
		String val = super.getValue();
		if (val == null && !nullable) {
			return "";
		}
		return val;
	}

	@Override
	public void setValue(String value) {
		if (value == null && !nullable) {
			super.setValue("");
		}
		super.setValue(value);
	}

	@Override
	public String getCopyOfValue() {		
		return getValue();
	}	
	
}
