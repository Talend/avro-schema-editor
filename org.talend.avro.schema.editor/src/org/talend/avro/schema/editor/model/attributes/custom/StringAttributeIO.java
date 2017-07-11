package org.talend.avro.schema.editor.model.attributes.custom;

import org.talend.avro.schema.editor.model.attributes.AvroAttribute;

public class StringAttributeIO implements CustomAttributeIO<String> {

	private static final String EMPTY = ""; //$NON-NLS-1$
	
	private boolean nullable;
	
	public StringAttributeIO() {
		this(false);
	}
	
	public StringAttributeIO(boolean nullable) {
		super();
		this.nullable = nullable;
	}

	@Override
	public String encodeAttributeValue(AvroAttribute<String> attribute) {		
		String attrValue = attribute.getValue();
		if (attrValue == null && !nullable) {
			return EMPTY;
		}
		return attrValue;
	}

	@Override
	public String decodeAttributeValue(String attributeName, String value) {
		if (value == null && !nullable) {
			return EMPTY;
		}
		return value;
	}	
	
}
