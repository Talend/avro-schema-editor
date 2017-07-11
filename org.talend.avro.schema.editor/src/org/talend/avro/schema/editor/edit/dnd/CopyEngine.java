package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.model.AvroNode;

public interface CopyEngine {

	AvroNode copy(DnDContext dndContext, CopyStrategy strategy);
	
}
