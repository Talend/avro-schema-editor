package org.talend.avro.schema.editor.viewer.attribute;

import java.util.Comparator;

import org.talend.avro.schema.editor.model.attributes.AvroAttribute;

/**
 * This provides the attribute viewer configuration (content/label providers and attribute comparator).
 * 
 * @author timbault
 *
 */
public interface AttributeViewerConfiguration {

	AttributeContentProvider getAttributeContentProvider();
	
	AttributeControlProvider getAttributeControlProvider();
		
	Comparator<AvroAttribute<?>> getAttributeComparator();
		
}
