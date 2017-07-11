package org.talend.avro.schema.editor.model.attributes.validator;

import org.eclipse.jface.dialogs.IInputValidator;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;

public class AvroNameValidatorProvider implements InputValidatorProvider {

	@Override
	public IInputValidator getInputValidator(AvroNode node, AvroContext context) {
		
		NodeType type = node.getType();
		switch (type) {
		case RECORD:
		case ENUM:
		case FIXED:
			return new AvroNameValidator(context, node);
		default:
			return null;
		}
		
	}

}
