package org.talend.avro.schema.editor.edit.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.edit.actions.MoveInDirectionAction;
import org.talend.avro.schema.editor.handlers.ContextualHandler;
import org.talend.avro.schema.editor.model.cmd.Direction;

public class MoveElementHandler extends ContextualHandler {	
	
	protected Direction getDirection(ExecutionEvent event) {
		String directionName = event.getParameter(Direction.ID);
		return Direction.valueOf(directionName.toUpperCase());
	}
	
	@Override
	protected void doExecute(ExecutionEvent event, AvroSchemaEditor schemaEditor, AvroContext context) {		
		Direction direction = getDirection(event);		
		MoveInDirectionAction action = new MoveInDirectionAction(MoveInDirectionAction.getLabel(direction), direction, Notifications.NOT_REF);
		action.init(context, false);
		action.run();
	}

}
