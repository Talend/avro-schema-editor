package org.talend.avro.schema.editor.attributes;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.custom.CustomAttributeConfiguration;
import org.talend.avro.schema.editor.viewer.attribute.AttributeViewerConfiguration;
import org.talend.avro.schema.editor.viewer.attribute.column.AttributeColumnConfiguration;

/**
 * This provides all the configuration (UI and not UI) for the schema attributes. 
 * 
 * @author timbault
 *
 */
public interface AttributesConfiguration {

	CustomAttributeConfiguration getCustomAttributeConfiguration(AvroContext context);
	
	AttributeViewerConfiguration getAttributeViewerConfiguration(AvroContext context);
	
	AttributeColumnConfiguration getAttributeColumnConfiguration(AvroContext context);
	
}
