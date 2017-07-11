package org.talend.avro.schema.editor.edit.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.avro.schema.editor.model.AvroNode;

/**
 * Default implementation of an {@link AvroNodeAttributesValidators}
 * 
 * @author timbault
 *
 */
public class AvroNodeAttributesValidatorsImpl implements AvroNodeAttributesValidators {

	private List<String> sortedKeys = new ArrayList<>();
	
	private Map<String, AvroNodeAttributesValidator> validators = new HashMap<>();
	
	@Override
	public final void registerValidator(String key, AvroNodeAttributesValidator validator) {
		if (!sortedKeys.contains(key)) {
			sortedKeys.add(key);
		}
		validators.put(key, validator);
	}

	@Override
	public List<String> getAllValidatorKeys() {
		return Collections.unmodifiableList(sortedKeys);
	}
	
	@Override
	public final void unregisterValidator(String key) {
		validators.remove(key);
		sortedKeys.remove(key);
	}

	@Override
	public final void validateAll(AvroNode node) {
		for (String key : sortedKeys) {
			validators.get(key).validate(node);
		}
	}

	@Override
	public final void validate(AvroNode node, String validatorKey) {
		AvroNodeAttributesValidator validator = validators.get(validatorKey);
		if (validator != null) {
			validator.validate(node);
		}		
	}

}
