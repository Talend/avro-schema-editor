package org.talend.avro.schema.editor.model.attributes.validator;

import org.eclipse.jface.dialogs.IInputValidator;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;

public class FixedSizeValidatorProvider implements InputValidatorProvider {

	@Override
	public IInputValidator getInputValidator(AvroNode node, AvroContext context) {
		return new FixedSizeValidator();
	}	
	
}
