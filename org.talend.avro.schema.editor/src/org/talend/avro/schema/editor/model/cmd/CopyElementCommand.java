package org.talend.avro.schema.editor.model.cmd;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicy.Action;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.TargetPosition;

/**
 * This command performs a copy of an avro node and add it to the specified target node at the given target position.
 * 
 * @author timbault
 *
 */
public class CopyElementCommand extends AbstractDnDElementCommand {
	
	public CopyElementCommand(AvroContext context, AvroNode sourceNode, AvroNode targetNode, 
			TargetPosition position, int notifications) {
		super(context, Action.COPY, sourceNode, targetNode, position, notifications);
	}
	
	@Override
	public void run() {
		super.run();
		doNotifications(getTargetNode().getParent());
	}

	@Override
	public void undo() {
		super.undo();
		doNotifications(getTargetNode().getParent());
	}

	@Override
	public void redo() {
		super.redo();
		doNotifications(getTargetNode().getParent());
	}

}
