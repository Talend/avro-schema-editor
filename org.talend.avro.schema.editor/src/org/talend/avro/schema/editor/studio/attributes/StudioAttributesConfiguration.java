package org.talend.avro.schema.editor.studio.attributes;

import org.talend.avro.schema.editor.attributes.AttributesConfiguration;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.custom.CustomAttributeConfiguration;
import org.talend.avro.schema.editor.viewer.attribute.AttributeViewerConfiguration;
import org.talend.avro.schema.editor.viewer.attribute.column.AttributeColumnConfiguration;

public class StudioAttributesConfiguration implements AttributesConfiguration {

	@Override
	public CustomAttributeConfiguration getCustomAttributeConfiguration(AvroContext context) {
		return new StudioCustomAttributeConfiguration();
	}

	@Override
	public AttributeViewerConfiguration getAttributeViewerConfiguration(AvroContext context) {
		return new StudioAttributeViewerConfiguration();
	}

	@Override
	public AttributeColumnConfiguration getAttributeColumnConfiguration(AvroContext context) {
		return new StudioAttributeColumnConfigurationImpl(context);
	}	
	
}
