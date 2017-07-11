package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.utils.BaseParams;

public class BaseDnDParams extends BaseParams implements DnDParams {

	private DnDContext dndContext;

	public static BaseDnDParams getParams(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
		return new BaseDnDParams(sourceNode, targetNode, position); 
	}
	
	public BaseDnDParams(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
		super();
		this.dndContext = new DefaultDnDContext(sourceNode, targetNode, position);
	}

	public AvroNode getSourceNode() {
		return dndContext.getSourceNode();
	}

	public AvroNode getTargetNode() {
		return dndContext.getTargetNode();
	}

	public TargetPosition getPosition() {
		return dndContext.getPosition();
	}
	
}
