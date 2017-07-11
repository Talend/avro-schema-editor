package org.talend.avro.schema.editor.edit.validator;

import java.util.List;

import org.talend.avro.schema.editor.model.AvroNode;

/**
 * This interface represents a validator registry. It allows to register validators and call them on a specific node.
 * 
 * @author timbault
 *
 */
public interface AvroNodeAttributesValidators {

	/**
	 * Register the given validator. If the specified key already exists, the corresponding validator is replaced by the new one.
	 * @param key
	 * @param validator
	 */
	void registerValidator(String key, AvroNodeAttributesValidator validator);
	
	/**
	 * Returns the registered validator keys.
	 * 
	 * @return
	 */
	List<String> getAllValidatorKeys();
	
	/**
	 * Unregister a validator via its key.
	 * 
	 * @param key
	 */
	void unregisterValidator(String key);
	
	/**
	 * Call all the registered validators on the specified node.
	 * 
	 * @param node
	 */
	void validateAll(AvroNode node);
	
	/**
	 * Call the validator corresponding to the specified key on the given node.
	 * 
	 * @param node
	 * @param validatorKey
	 */
	void validate(AvroNode node, String validatorKey);
	
}
