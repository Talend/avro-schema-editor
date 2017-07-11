package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

public class SymbolsAttribute extends StringListAttribute {
	
	public SymbolsAttribute(AvroNode node, StringList value) {
		super(node, AvroAttributes.SYMBOLS, value);
	}	

}
