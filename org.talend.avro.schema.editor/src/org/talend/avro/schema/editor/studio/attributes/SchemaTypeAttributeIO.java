package org.talend.avro.schema.editor.studio.attributes;

import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.custom.CustomAttributeIO;

public class SchemaTypeAttributeIO implements CustomAttributeIO<StudioSchemaTypes> {

	@Override
	public String encodeAttributeValue(AvroAttribute<StudioSchemaTypes> attribute) {
		return attribute.getValue().getValue().toString().toLowerCase();
	}

	@Override
	public StudioSchemaTypes decodeAttributeValue(String attributeName, String value) {
		StudioSchemaType schemaType = StudioSchemaType.valueOf(value.toUpperCase());
		return new StudioSchemaTypes(schemaType); 
	}

}
