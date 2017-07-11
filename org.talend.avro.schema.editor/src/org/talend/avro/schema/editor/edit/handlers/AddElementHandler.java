package org.talend.avro.schema.editor.edit.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.edit.actions.AddElementAction;
import org.talend.avro.schema.editor.handlers.ContextualHandler;

public class AddElementHandler extends ContextualHandler {

	@Override
	protected void doExecute(ExecutionEvent event, AvroSchemaEditor schemaEditor, AvroContext context) {
		AddElementAction action = new AddElementAction("Add new element", Notifications.notifyRefreshReveal(context));
		action.init(context, false);
		action.run();
	}
	
}
