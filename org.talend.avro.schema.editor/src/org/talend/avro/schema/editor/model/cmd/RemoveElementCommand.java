package org.talend.avro.schema.editor.model.cmd;

import org.talend.avro.schema.editor.commands.AbstractSchemaEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.TargetPosition;

public class RemoveElementCommand extends AbstractSchemaEditCommand {

	private AvroNode parentNode;
	
	private AvroNode targetNode;
	
	private TargetPosition initialPosition;
	
	private AvroNode removedNode;	
	
	public RemoveElementCommand(AvroContext context, AvroNode node, int notifications) {
		super(context, notifications);
		this.removedNode = node;		
	}
	
	protected AvroNode getParentNode() {
		return parentNode;
	}

	protected AvroNode getRemovedNode() {
		return removedNode;
	}

	protected void prepareParameters() {
		AvroNode parentNode = removedNode.getParent();
		if (parentNode.getChildrenCount() > 1) {
			int removedNodeIndex = parentNode.getChildIndex(removedNode);
			if (removedNodeIndex == 0) {
				targetNode = parentNode.getChild(1);
				initialPosition = TargetPosition.BEFORE;
			} else {
				targetNode = parentNode.getChild(removedNodeIndex - 1);
				initialPosition = TargetPosition.AFTER;
			}
		} else {
			// only one child
			// no target node
		}
	}
	
	@Override
	public void run() {
		prepareParameters();
		parentNode = getController().removeElement(removedNode);
		doNotifications(parentNode);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		if (targetNode == null) {
			getController().addElement(parentNode, removedNode, TargetPosition.UPON);
		} else {
			getController().addElement(targetNode, removedNode, initialPosition);
		}
		doNotifications(parentNode);
	}

	@Override
	public boolean canRedo() {
		return true;
	}

	@Override
	public void redo() {
		AvroNode parentNode = getController().removeElement(removedNode);
		doNotifications(parentNode);
	}

	@Override
	public String getLabel() {
		return "Remove " + removedNode.getType().toString().toLowerCase();
	}

	@Override
	public void dispose() {
		// 
	}
	
}
