package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

/**
 * Avro name attribute
 * 
 * @author timbault
 *
 */
public class NameAttribute extends StringAttribute {
		
	public NameAttribute(AvroNode node) {
		this(node, "");
	}
	
	public NameAttribute(AvroNode node, String value) {
		super(node, AvroAttributes.NAME, value);
	}	

}
