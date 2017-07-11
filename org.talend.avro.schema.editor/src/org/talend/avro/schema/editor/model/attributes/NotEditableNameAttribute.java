package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

public class NotEditableNameAttribute extends NameAttribute {

	private NameProvider nameProvider;
	
	public NotEditableNameAttribute(AvroNode node, NameProvider nameProvider) {
		super(node);
		this.nameProvider = nameProvider;
	}

	@Override
	public String getValue() {
		return nameProvider.getName(getHolder());
	}

	@Override
	public void setValue(String value) {
		throw new UnsupportedOperationException("Cannot change the value of NotEditableNameAttribute");
	}
	
}
