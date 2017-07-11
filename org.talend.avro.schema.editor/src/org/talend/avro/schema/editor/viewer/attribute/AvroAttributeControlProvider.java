package org.talend.avro.schema.editor.viewer.attribute;

import org.eclipse.swt.widgets.Layout;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfiguration;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfigurations;

/**
 * Default implementation of {@link AttributeControlProvider} for the standard avro schema editor.
 * 
 * @author timbault
 *
 */
public class AvroAttributeControlProvider implements AttributeControlProvider {

	private AttributeControlConfigurations configurations;
	
	public AvroAttributeControlProvider(AttributeControlConfigurations configurations) {
		super();
		this.configurations = configurations;
	}

	@Override
	public Layout getMainLayout() {
		return configurations.getMainLayout();
	}

	@Override
	public AttributeControl<?> getAttributeControl(AvroAttribute<?> attribute) {
		return configurations.getAttributeControl(attribute);
	}

	@Override
	public Object getLayoutData(AvroAttribute<?> attribute) {
		return configurations.getLayoutData(attribute);
	}

	@Override
	public AttributeControlConfiguration getAttributeControlConfiguration(AvroAttribute<?> attribute) {
		return configurations.getAttributeControlConfiguration(attribute);
	}

	@Override
	public boolean isVisible(AvroAttribute<?> attribute) {
		return attribute.isVisible();
	}

	protected AttributeControlConfiguration createAttributeControlConfiguration(Object... confData) {
		return configurations.createAttributeControlConfiguration(confData);
	}	
	
}
