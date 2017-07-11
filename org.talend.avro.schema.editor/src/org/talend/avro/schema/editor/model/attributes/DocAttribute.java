package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

/**
 * Avro Doc attribute.
 * 
 * @author timbault
 *
 */
public class DocAttribute extends StringAttribute {
		
	public DocAttribute(AvroNode node) {
		this(node, "");
	}
	
	public DocAttribute(AvroNode node, String value) {
		super(node, AvroAttributes.DOC, value);
	}	

}
