package org.talend.avro.schema.editor.model.attributes.cmd;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;

public class ChangeAttributeCommand<T> extends AbstractChangeAttributeCommand<T> {
	
	public ChangeAttributeCommand(AvroContext context, AvroAttribute<T> attribute, T newValue, int notifications) {
		super(context, attribute, newValue, notifications);		
	}
	
	@Override
	protected T getValue(AvroAttribute<T> attribute) {
		return attribute.getValue();
	}

	@Override
	protected void applyValue(AvroAttribute<T> attribute, T value) {
		attribute.setValue(value);
	}

}
