package org.talend.avro.schema.editor.model.cmd;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.dnd.DnDParams;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicy;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.TargetPosition;

public class ReferenceElementCommand extends AbstractDnDElementCommand {
	
	public ReferenceElementCommand(AvroContext context, AvroNode sourceNode, AvroNode targetNode, 
			TargetPosition position, int notifications) {
		super(context, DragAndDropPolicy.Action.REFERENCE, sourceNode, targetNode, position, notifications);
	}

	@Override
	public void run() {
		AvroNode targetParent = getTargetNode().getParent();
		super.run();
		doNotifications(targetParent);
	}

	@Override
	public void undo() {
		AvroNode refNode = getDndParams().getAvroNode(DnDParams.REF_NODE);
		AvroNode refParent = refNode.getParent();
		super.undo();
		doNotifications(refParent);
	}

	@Override
	public void redo() {
		AvroNode targetParent = getTargetNode().getParent();
		super.redo();
		doNotifications(targetParent);
	}

}
