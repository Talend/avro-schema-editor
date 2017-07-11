package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.context.AvroContext;

public interface CopyStrategyProvider {

	CopyStrategy getCopyStrategy(AvroContext context, DnDContext dndContext);
	
}
