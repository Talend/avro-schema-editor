package org.talend.avro.schema.editor.studio.attributes;

import org.talend.avro.schema.editor.model.attributes.MultiChoiceValue;

public class StudioSchemaTypes extends MultiChoiceValue<StudioSchemaType> {

	public StudioSchemaTypes(StudioSchemaType value) {
		super(StudioSchemaType.class, value, StudioSchemaType.values());
	}

	public String[] getValuesAsString() {
		String[] result = new String[size()];
		for (int i = 0; i < size(); i++) {
			result[i] = getValueFor(i).getLabel();
		}
		return result;
	}

	@Override
	public StudioSchemaTypes getACopy() {
		return new StudioSchemaTypes(getValue());
	}
	
}
