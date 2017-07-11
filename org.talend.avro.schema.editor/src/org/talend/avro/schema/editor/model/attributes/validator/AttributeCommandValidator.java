package org.talend.avro.schema.editor.model.attributes.validator;

import org.talend.avro.schema.editor.model.attributes.AvroAttribute;

public interface AttributeCommandValidator {

	<T> boolean canExecuteCommand(AvroAttribute<T> attribute, T oldValue, T newValue);
	
}
