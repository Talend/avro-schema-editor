package org.talend.avro.schema.editor.model.attributes.validator;

import org.eclipse.jface.dialogs.IInputValidator;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;

public interface InputValidatorProvider {

	IInputValidator getInputValidator(AvroNode node, AvroContext context);
	
}
