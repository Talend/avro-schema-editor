package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.PrimitiveType;

/**
 * This class is used as the value of the attribute defining a {@link PrimitiveType}.
 *  
 * @author timbault
 *
 */
public class PrimitiveTypes extends MultiChoiceValue<PrimitiveType> {

	public PrimitiveTypes(PrimitiveType initialType) {
		super(PrimitiveType.class, initialType, PrimitiveType.values());
	}

	public String[] getValuesAsString() {
		String[] result = new String[size()];
		for (int i = 0; i < size(); i++) {
			result[i] = getValueFor(i).getName();
		}
		return result;
	}

	@Override
	public PrimitiveTypes getACopy() {
		return new PrimitiveTypes(getValue());
	}
		
}
