package org.talend.avro.schema.editor.viewer.attribute.ui;

/**
 * Build controls for an attribute of type String.
 * 
 * @author timbault
 *
 */
public class StringAttributeControl extends TextAttributeControl<String> {

	@Override
	protected String getAttributeValueAsString() {
		return getAttribute().getValue();
	}

	@Override
	protected String parseValue(String value) {	
		return value;
	}
	
}
