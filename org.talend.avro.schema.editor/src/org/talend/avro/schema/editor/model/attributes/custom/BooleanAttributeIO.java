package org.talend.avro.schema.editor.model.attributes.custom;

import org.talend.avro.schema.editor.model.attributes.AvroAttribute;

public class BooleanAttributeIO implements CustomAttributeIO<Boolean> {

	@Override
	public String encodeAttributeValue(AvroAttribute<Boolean> attribute) {		
		Boolean value = attribute.getValue();
		if (value != null) {
			return value.toString();
		}
		return NULL;
	}

	@Override
	public Boolean decodeAttributeValue(String attributeName, String value) {
		if (!NULL.equals(value)) {
			return Boolean.parseBoolean(value);
		}
		return null;
	}

}
