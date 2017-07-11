package org.talend.avro.schema.editor.edit.validator;

import org.talend.avro.schema.editor.model.attributes.AvroAttributes;

/**
 * Implementation of an {@link AvroNodeAttributesValidators} for the Avro Schema Editor.
 * @author timbault
 *
 */
public class AvroSchemaEditorNodeAttributesValidators extends AvroNodeAttributesValidatorsImpl {

	public AvroSchemaEditorNodeAttributesValidators() {
		super();
		init();
	}

	private void init() {
		registerValidator(AvroAttributes.PRIMITIVE_TYPE, new PrimitiveTypeValidator());
	}
	
}
