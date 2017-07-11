package org.talend.avro.schema.editor.studio.attributes;

import java.util.Comparator;

import org.talend.avro.schema.editor.model.attributes.AttributesComparator;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.viewer.attribute.AttributeContentProvider;
import org.talend.avro.schema.editor.viewer.attribute.AttributeControlProvider;
import org.talend.avro.schema.editor.viewer.attribute.AttributeViewerConfiguration;
import org.talend.avro.schema.editor.viewer.attribute.AvroAttributeContentProvider;
import org.talend.avro.schema.editor.viewer.attribute.AvroAttributeControlProvider;

public class StudioAttributeViewerConfiguration implements AttributeViewerConfiguration {

	@Override
	public AttributeContentProvider getAttributeContentProvider() {
		return new AvroAttributeContentProvider();
	}

	@Override
	public AttributeControlProvider getAttributeControlProvider() {
		return new AvroAttributeControlProvider(new StudioAttributeControlConfigurations());
	}

	@Override
	public Comparator<AvroAttribute<?>> getAttributeComparator() {
		return new AttributesComparator(StudioAttributes.SORTED_STUDIO_ATTRIBUTES);
	}

}
