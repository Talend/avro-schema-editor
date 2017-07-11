package org.talend.avro.schema.editor.studio.attributes;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.viewer.attribute.AttributeControl;
import org.talend.avro.schema.editor.viewer.attribute.SimpleLabelProvider;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfigurations;
import org.talend.avro.schema.editor.viewer.attribute.ui.MultiChoiceAttributeControl;

public class StudioAttributeControlConfigurations extends AttributeControlConfigurations {

	public StudioAttributeControlConfigurations() {
		super();
		initializeStudioConfigurations();
	}

	private void initializeStudioConfigurations() {
		
		registerDefaultAttributeControlClass(StudioSchemaTypes.class, (Class<? extends AttributeControl<?>>) MultiChoiceAttributeControl.class);
		
		registerDefaultLayoutData(StudioSchemaTypes.class, createTwoColumnsLayoutData());
		
		registerNamedAttributeControlConfigurations(AvroAttributes.NAME, createAttributeControlConfiguration(
				AttributeControlConfigurations.LABEL_PROVIDER, new SimpleLabelProvider("Field Name")));
		
		registerNamedAttributeControlConfigurations(AvroAttributes.DOC, createAttributeControlConfiguration(
				AttributeControlConfigurations.LABEL_PROVIDER, new SimpleLabelProvider("Comment"),
				AttributeControlConfigurations.TEXT_STYLE, SWT.MULTI | SWT.V_SCROLL));
		
		registerNamedAttributeControlConfigurations(StudioAttributes.KEY, createAttributeControlConfiguration(
				AttributeControlConfigurations.LABEL_PROVIDER, new SimpleLabelProvider("Key")));
		
		registerNamedAttributeControlConfigurations(StudioAttributes.TYPE, createAttributeControlConfiguration(
				AttributeControlConfigurations.LABEL_PROVIDER, new LabelProvider() {
					@Override
					public String getText(Object element) {
						if (element instanceof AvroAttribute<?>) {
							return "Type";
						} else if (element instanceof StudioSchemaType) {
							return ((StudioSchemaType) element).getLabel();
						}
						return super.getText(element);
					}
				}));
		
		registerNamedAttributeControlConfigurations(StudioAttributes.NULLABLE, createAttributeControlConfiguration(
				AttributeControlConfigurations.LABEL_PROVIDER, new SimpleLabelProvider("Nullable")));
		
		registerNamedAttributeControlConfigurations(StudioAttributes.DATE_FORMAT, createAttributeControlConfiguration(
				AttributeControlConfigurations.LABEL_PROVIDER, new SimpleLabelProvider("Date Format")));
		
	}	
	
}
