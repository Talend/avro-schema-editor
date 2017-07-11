package org.talend.avro.schema.editor.model.cmd;

import org.talend.avro.schema.editor.commands.AbstractSchemaEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicy;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.TargetPosition;

public class MoveUpDownElementCommand extends AbstractSchemaEditCommand {

	private AvroNode parentNode;
	
	private AvroNode node;
	
	private Direction direction;

	private AvroNode targetNode;
	
	private TargetPosition targetPosition;
	
	public MoveUpDownElementCommand(AvroContext context, AvroNode node, Direction direction, int notifications) {
		super(context, notifications);
		this.node = node;
		this.parentNode = node.getParent();
		this.direction = direction;
	}
	
	@Override
	public void run() {
		int index = parentNode.getChildIndex(node);
		switch (direction) {
		case UP:
			targetNode = parentNode.getChild(index - 1);
			targetPosition = TargetPosition.BEFORE;
			break;
		case DOWN:
			targetNode = parentNode.getChild(index + 1);
			targetPosition = TargetPosition.AFTER;
			break;
		}
		getController().executeDnDElement(DragAndDropPolicy.Action.MOVE, node, targetNode, targetPosition);
		doNotifications(parentNode);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		getController().executeDnDElement(DragAndDropPolicy.Action.MOVE, node, targetNode, targetPosition.reverse());
		doNotifications(parentNode);
	}

	@Override
	public boolean canRedo() {
		return true;
	}

	@Override
	public void redo() {
		getController().executeDnDElement(DragAndDropPolicy.Action.MOVE, node, targetNode, targetPosition);
		doNotifications(parentNode);
	}

	@Override
	public String getLabel() {
		return "Move " + direction.toString().toLowerCase() + " " + node.getType().toString().toLowerCase();
	}

	@Override
	public void dispose() {
		// 
	}

}
