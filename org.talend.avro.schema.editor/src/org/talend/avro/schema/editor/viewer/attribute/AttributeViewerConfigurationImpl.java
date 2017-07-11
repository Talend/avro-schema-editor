package org.talend.avro.schema.editor.viewer.attribute;

import java.util.Comparator;

import org.talend.avro.schema.editor.model.attributes.AttributesComparator;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfigurations;

public class AttributeViewerConfigurationImpl implements AttributeViewerConfiguration {

	@Override
	public AttributeContentProvider getAttributeContentProvider() {
		return new AvroAttributeContentProvider();
	}

	@Override
	public AttributeControlProvider getAttributeControlProvider() {
		return new AvroAttributeControlProvider(new AttributeControlConfigurations());
	}

	@Override
	public Comparator<AvroAttribute<?>> getAttributeComparator() {
		return new AttributesComparator(AvroAttributes.SORTED_ATTRIBUTES);
	}

}
