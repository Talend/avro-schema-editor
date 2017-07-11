package org.talend.avro.schema.editor.viewer;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.TargetPosition;

public interface SchemaViewerDropPolicy {

	int getTargetPositionTolerance();
	
	int dragOver(AvroNode draggedNode, AvroNode targetNode, TargetPosition position);
	
	void drop(AvroNode draggedNode, AvroNode targetNode, TargetPosition position);
	
}
