package org.talend.avro.schema.editor.model.attributes.validator;

import org.eclipse.jface.dialogs.IInputValidator;

public class DefaultInputValidtor implements IInputValidator {

	@Override
	public String isValid(String newText) {
		// always valid
		return null;
	}
	
}
