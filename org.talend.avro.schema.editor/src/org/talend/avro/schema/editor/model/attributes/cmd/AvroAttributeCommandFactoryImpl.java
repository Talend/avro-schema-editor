package org.talend.avro.schema.editor.model.attributes.cmd;

import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.attributes.ArrayOrMapValue;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.model.attributes.PrimitiveTypes;

/**
 * Default implementation of an {@link IAttributeCommandFactory} for the standard avro schema editor. 
 * 
 * @author timbault
 *
 */
public class AvroAttributeCommandFactoryImpl extends AttributeCommandFactoryImpl {

	@SuppressWarnings("unchecked")
	@Override
	public <T> IEditCommand createChangeAttributeCommand(AvroAttribute<T> attribute, T newValue,
			int notifications) {
		String attributeName = attribute.getName();
		switch (attributeName) {
		case AvroAttributes.PRIMITIVE_TYPE:
			return createChangePrimitiveTypeAttributeCommand((AvroAttribute<PrimitiveTypes>) attribute, (PrimitiveTypes) newValue, notifications);
		case AvroAttributes.OPTIONAL:
			return createChangeOptionalFieldAttributeCommand((AvroAttribute<Boolean>) attribute, (boolean) newValue, notifications);
		case AvroAttributes.ARRAY_OR_MAP:
			return createChangeArrayOrMapAttributeCommand((AvroAttribute<ArrayOrMapValue>) attribute, (ArrayOrMapValue) newValue, notifications);
		default:
			return super.createChangeAttributeCommand(attribute, newValue, notifications);
		}		
	}
	
	protected IEditCommand createChangePrimitiveTypeAttributeCommand(AvroAttribute<PrimitiveTypes> attribute, PrimitiveTypes newValue, int notifications) {
		PrimitiveType newType = newValue.getValue();
		return new ChangePrimitiveTypeAttributeCommand(getContext(), 
				attribute.getHolder(), newType, notifications);
	}
	
	protected IEditCommand createChangeOptionalFieldAttributeCommand(AvroAttribute<Boolean> attribute, boolean newValue, int notifications) {
		return new ChangeOptionalFieldAttributeCommand(getContext()	,
				attribute.getHolder(), newValue, notifications);
	}
	
	protected IEditCommand createChangeArrayOrMapAttributeCommand(AvroAttribute<ArrayOrMapValue> attribute, ArrayOrMapValue newValue, int notifications) {
		return new SwitchArrayMapAttributeCommand(getContext(), attribute.getHolder(), notifications);
	}
	
}
