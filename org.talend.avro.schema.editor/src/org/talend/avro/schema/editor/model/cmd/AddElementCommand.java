package org.talend.avro.schema.editor.model.cmd;

import org.talend.avro.schema.editor.commands.AbstractSchemaEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.TargetPosition;

/**
 * 
 * @author timbault
 *
 */
public class AddElementCommand extends AbstractSchemaEditCommand {

	private NodeType type;
	
	private AvroNode targetNode;
	
	private AvroNode addedNode;
		
	public AddElementCommand(AvroContext context, AvroNode targetNode, NodeType type, int notifications) {
		super(context, notifications);
		this.targetNode = targetNode;
		this.type = type;
	}
	
	@Override
	public void run() {
		addedNode = getController().addElement(targetNode, type, TargetPosition.UPON);
		doNotifications(addedNode.getParent(), addedNode);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		AvroNode parentNode = getController().removeElement(addedNode);	
		doNotifications(parentNode);
	}

	@Override
	public boolean canRedo() {
		return true;
	}

	@Override
	public void redo() {
		getController().addElement(targetNode, addedNode, TargetPosition.UPON);
		doNotifications(addedNode.getParent());
	}

	@Override
	public String getLabel() {
		return "Add " + type.toString().toLowerCase();
	}

	@Override
	public void dispose() {
		// nothing to dispose
	}

}
