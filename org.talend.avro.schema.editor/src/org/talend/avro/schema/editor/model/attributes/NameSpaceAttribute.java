package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

/**
 * Avro namespace attribute.
 * 
 * @author timbault
 *
 */
public class NameSpaceAttribute extends StringAttribute {
		
	public NameSpaceAttribute(AvroNode node) {
		this(node, "");
	}
	
	public NameSpaceAttribute(AvroNode node, String value) {
		super(node, AvroAttributes.NAME_SPACE, value);
	}	
	
}
