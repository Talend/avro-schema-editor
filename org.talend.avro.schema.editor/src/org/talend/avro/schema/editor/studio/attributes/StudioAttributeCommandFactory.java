package org.talend.avro.schema.editor.studio.attributes;

import org.talend.avro.schema.editor.commands.CompositeCommand;
import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.AvroAttributeSet;
import org.talend.avro.schema.editor.model.attributes.cmd.AvroAttributeCommandFactoryImpl;
import org.talend.avro.schema.editor.model.attributes.cmd.ChangeAttributeCommand;
import org.talend.avro.schema.editor.model.attributes.cmd.SetEnabledAttributeCommand;

public class StudioAttributeCommandFactory extends AvroAttributeCommandFactoryImpl {

	@SuppressWarnings("unchecked")
	@Override
	public <T> IEditCommand createChangeAttributeCommand(AvroAttribute<T> attribute, T newValue,
			int notifications) {
		String attributeName = attribute.getName();
		switch (attributeName) {
		case StudioAttributes.TYPE:
			return createChangeTypeAttributeCommand((AvroAttribute<StudioSchemaTypes>) attribute, (StudioSchemaTypes) newValue, notifications);
		default:
			return super.createChangeAttributeCommand(attribute, newValue, notifications);
		}
	}
	
	protected IEditCommand createChangeTypeAttributeCommand(AvroAttribute<StudioSchemaTypes> attribute, StudioSchemaTypes newValue,
			int notifications) {
		CompositeCommand compositeCommand = new CompositeCommand();
		ChangeAttributeCommand<StudioSchemaTypes> changeAttrCmd = new ChangeAttributeCommand<>(getContext(), attribute, newValue, notifications);
		compositeCommand.addCommand(changeAttrCmd);
		AvroAttributeSet attributes = attribute.getHolder().getAttributes();
		AvroAttribute<?> dateFormatAttr = attributes.getAttribute(StudioAttributes.DATE_FORMAT);
		StudioSchemaType newType = newValue.getValue();
		IEditCommand changeDateFormatEnableStateCmd = null;
		if (newType == StudioSchemaType.DATE) {
			// we have to enable the date format attribut
			changeDateFormatEnableStateCmd = new SetEnabledAttributeCommand(getContext(), dateFormatAttr, true, notifications);
		} else {
			// we have to disable the date format attribut
			changeDateFormatEnableStateCmd = new SetEnabledAttributeCommand(getContext(), dateFormatAttr, false, notifications);
		}
		compositeCommand.addCommand(changeDateFormatEnableStateCmd);
		return compositeCommand;
	}
	
}
