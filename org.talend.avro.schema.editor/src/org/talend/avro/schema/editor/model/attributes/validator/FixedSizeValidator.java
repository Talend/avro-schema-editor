package org.talend.avro.schema.editor.model.attributes.validator;

import org.eclipse.jface.dialogs.IInputValidator;

public class FixedSizeValidator implements IInputValidator {

	@Override
	public String isValid(String newText) {
		
		try {
			int value = Integer.parseInt(newText);
			if (value <= 0) {
				return "Fixed size must be a strictly positive value";
			}
		} catch (NumberFormatException e) {
			return "Please enter a valid integer";
		}
		
		return null;
	}

}
