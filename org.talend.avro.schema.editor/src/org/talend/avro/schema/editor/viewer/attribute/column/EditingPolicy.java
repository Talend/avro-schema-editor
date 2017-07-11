package org.talend.avro.schema.editor.viewer.attribute.column;

import org.talend.avro.schema.editor.model.AvroNode;

public interface EditingPolicy {

	boolean isEditable(AvroNode node, String attributeName);
	
}
