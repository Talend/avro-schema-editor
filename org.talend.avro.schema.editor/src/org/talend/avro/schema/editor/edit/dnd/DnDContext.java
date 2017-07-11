package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.TargetPosition;

/**
 * This gives contextual information about a drag and drop action (basically the source node, the target node and the target position)
 * 
 * @author timbault
 *
 */
public interface DnDContext {

	AvroNode getSourceNode();

	AvroNode getTargetNode();

	TargetPosition getPosition();
	
}
