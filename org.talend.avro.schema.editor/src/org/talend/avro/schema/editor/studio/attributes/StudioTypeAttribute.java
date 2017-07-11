package org.talend.avro.schema.editor.studio.attributes;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.attributes.AvroAttributeImpl;

public class StudioTypeAttribute extends AvroAttributeImpl<StudioSchemaTypes> {

	public StudioTypeAttribute(AvroNode node, String name, StudioSchemaTypes value) {
		super(node, name, StudioSchemaTypes.class, value);
	}

	@Override
	public StudioSchemaTypes getCopyOfValue() {		
		return getValue().getACopy();
	}
	
}
