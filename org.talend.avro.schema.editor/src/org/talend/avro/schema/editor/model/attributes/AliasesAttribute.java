package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

public class AliasesAttribute extends StringListAttribute {

	public AliasesAttribute(AvroNode node, StringList value) {
		super(node, AvroAttributes.ALIASES, value);
	}		
	
}
