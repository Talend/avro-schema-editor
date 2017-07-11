package org.talend.avro.schema.editor.model.attributes.custom;

import org.talend.avro.schema.editor.model.attributes.AvroAttribute;

public interface CustomAttributeIO<T> {

	static final String NULL = "null"; //$NON-NLS-1$
	
	String encodeAttributeValue(AvroAttribute<T> attribute);
	
	T decodeAttributeValue(String attributeName, String value);
	
}
