package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

public interface NameProvider {

	String getName(AvroNode node);
	
}
