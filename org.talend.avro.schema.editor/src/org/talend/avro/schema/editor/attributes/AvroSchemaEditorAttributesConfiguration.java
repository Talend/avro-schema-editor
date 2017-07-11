package org.talend.avro.schema.editor.attributes;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.custom.CustomAttributeConfiguration;
import org.talend.avro.schema.editor.viewer.attribute.AttributeViewerConfiguration;
import org.talend.avro.schema.editor.viewer.attribute.AttributeViewerConfigurationImpl;
import org.talend.avro.schema.editor.viewer.attribute.column.AttributeColumnConfiguration;
import org.talend.avro.schema.editor.viewer.attribute.column.AttributeColumnConfigurationImpl;

public class AvroSchemaEditorAttributesConfiguration implements AttributesConfiguration {

	@Override
	public CustomAttributeConfiguration getCustomAttributeConfiguration(AvroContext context) {
		// no custom attributes in the default avro schema editor
		return null;
	}

	@Override
	public AttributeViewerConfiguration getAttributeViewerConfiguration(AvroContext context) {
		return new AttributeViewerConfigurationImpl();
	}

	@Override
	public AttributeColumnConfiguration getAttributeColumnConfiguration(AvroContext context) {
		return new AttributeColumnConfigurationImpl(context);
	}	
	
}
