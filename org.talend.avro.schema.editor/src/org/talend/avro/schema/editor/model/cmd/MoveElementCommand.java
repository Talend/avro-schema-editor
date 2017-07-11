package org.talend.avro.schema.editor.model.cmd;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicy.Action;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.TargetPosition;

public class MoveElementCommand extends AbstractDnDElementCommand {
	
	public MoveElementCommand(AvroContext context, AvroNode sourceNode, AvroNode targetNode, 
			TargetPosition position, int notifications) {
		super(context, Action.MOVE, sourceNode, targetNode, position, notifications);
	}
	
	@Override
	public void run() {
		AvroNode sourceParent = getSourceNode().getParent();
		AvroNode targetParent = getTargetNode().getParent();
		super.run();
		doNotifications(sourceParent);
		doNotifications(targetParent);
	}
	
	@Override
	public void undo() {
		super.undo();
		doNotifications(getSourceNode().getParent());
		doNotifications(getTargetNode().getParent());
	}
	
	@Override
	public void redo() {
		AvroNode sourceParent = getSourceNode().getParent();
		AvroNode targetParent = getTargetNode().getParent();
		super.redo();
		doNotifications(sourceParent);
		doNotifications(targetParent);
	}
	
}
