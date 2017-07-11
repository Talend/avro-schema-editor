package org.talend.avro.schema.editor.edit.validator;

import org.talend.avro.schema.editor.model.AvroNode;

/**
 * This interface performs some validation checks on the attributes of the given node.
 * It is called by the {@link AvroSchemaController}.
 * 
 * @author timbault
 *
 */
public interface AvroNodeAttributesValidator {

	/**
	 * Performs some modifications on the attributes of the given node in order to stay in a valid state.
	 * 
	 * @param node
	 */
	void validate(AvroNode node);
	
}
