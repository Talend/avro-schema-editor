package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.TargetPosition;

public class DefaultDnDContext implements DnDContext {

	private AvroNode sourceNode;
	
	private AvroNode targetNode;
	
	private TargetPosition position;
	
	public DefaultDnDContext(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
		super();
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.position = position;
	}

	public AvroNode getSourceNode() {
		return sourceNode;
	}

	public AvroNode getTargetNode() {
		return targetNode;
	}

	public TargetPosition getPosition() {
		return position;
	}
	
}
