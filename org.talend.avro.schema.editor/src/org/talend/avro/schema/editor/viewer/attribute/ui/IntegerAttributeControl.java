package org.talend.avro.schema.editor.viewer.attribute.ui;

/**
 * Build controls for an attribute of type Integer.
 * 
 * @author timbault
 *
 */
public class IntegerAttributeControl extends TextAttributeControl<Integer> {

	@Override
	protected String getAttributeValueAsString() {		
		return Integer.toString(getAttribute().getValue());
	}

	@Override
	protected Integer parseValue(String value) {		
		return Integer.parseInt(value);
	}
	
}
