package org.talend.avro.schema.editor.model.cmd;

import org.talend.avro.schema.editor.commands.AbstractSchemaEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.dnd.DnDParams;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicy;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public abstract class AbstractDnDElementCommand extends AbstractSchemaEditCommand {

	protected static final String SOURCE_PARENT = "SourceParent"; //$NON-NLS-1$
	
	protected static final String TARGET_PARENT = "TargetParent"; //$NON-NLS-1$
	
	private DragAndDropPolicy.Action action;
	
	private AvroNode sourceNode;
	
	private AvroNode targetNode;
	
	private TargetPosition position;

	private DnDParams dndParams;
	
	protected AbstractDnDElementCommand(AvroContext context, DragAndDropPolicy.Action action, AvroNode sourceNode, AvroNode targetNode, 
			TargetPosition position, int notifications) {
		super(context, notifications);
		this.action = action;
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.position = position;
	}
	
	protected AvroNode getSourceNode() {
		return sourceNode;
	}

	protected AvroNode getTargetNode() {
		return targetNode;
	}

	protected TargetPosition getPosition() {
		return position;
	}
	
	protected DnDParams getDndParams() {
		return dndParams;
	}

	@Override
	public void run() {
		dndParams = getController().executeDnDElement(action, sourceNode, targetNode, position);
	}
	
	@Override
	public boolean canUndo() {
		return true;
	}
	
	@Override
	public void undo() {
		getController().undoDnDElement(action, dndParams);
	}

	@Override
	public boolean canRedo() {
		return true;
	}

	@Override
	public void redo() {
		dndParams = getController().executeDnDElement(action, sourceNode, targetNode, position);
	}

	@Override
	public String getLabel() {
		return action.getLabel() + " " + AttributeUtil.getNameFromAttribute(sourceNode);
	}

	@Override
	public void dispose() {
		dndParams.dispose();
	}

}
