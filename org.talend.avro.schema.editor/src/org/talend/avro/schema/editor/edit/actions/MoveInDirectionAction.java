package org.talend.avro.schema.editor.edit.actions;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.commands.IEditCommandFactory;
import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.commands.SchemaEditCompositeCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaController;
import org.talend.avro.schema.editor.edit.EditUtils;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.cmd.Direction;

public class MoveInDirectionAction extends ContextualActionImpl {

	public static final String CMD_ID = "org.talend.avro.schema.editor.edit.MoveElement"; //$NON-NLS-1$ 
	
	private Direction direction;	
	
	public MoveInDirectionAction(String text, int style, Direction direction, int notifications) {
		super(text, style, notifications);
		this.direction = direction;
	}

	public MoveInDirectionAction(String text, Direction direction, int notifications) {
		super(text, notifications);
		this.direction = direction;
	}

	@Override
	protected boolean isEnabled(AvroContext context, List<AvroNode> contextualNodes) {
		AvroSchemaController controller = getController();
		List<AvroNode> nodesToMove = EditUtils.prepareNodesForDirectionalMove(contextualNodes, direction);
		boolean canMove = !nodesToMove.isEmpty();
		for (AvroNode node : nodesToMove) {
			if (!controller.canMoveInDirection(node, direction)) {
				canMove = false;
				break;
			}
		}
		return canMove;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		if (direction == Direction.UP) {
			return AvroSchemaEditorActivator.getImageDescriptor(AvroSchemaEditorImages.MOVE_UP);
		} else {
			return AvroSchemaEditorActivator.getImageDescriptor(AvroSchemaEditorImages.MOVE_DOWN);
		}
	}

	public static String getLabel(Direction direction) {
		return "Move " + direction.toString().toLowerCase() + " element(s)";
	}
	
	@Override
	public String getToolTipText() {
		return getLabel(direction);
	}

	@Override
	public void run() {
		
		List<AvroNode> nodesToMove = EditUtils.prepareNodesForDirectionalMove(getContextualNodes(), direction);
		
		IEditCommandFactory commandFactory = getCommandFactory();		
		
		SchemaEditCompositeCommand moveCommand = commandFactory.createCompositeCommand(getLabel(direction), getNotifications());
		
		for (AvroNode node : nodesToMove) {
			IEditCommand moveCmd = commandFactory.createMoveUpDownElementCommand(node, direction, getNotifications());
			moveCommand.addCommand(moveCmd);
		}
		
		execute(moveCommand);		
	}	
	
}
