package org.talend.avro.schema.editor.edit.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.edit.actions.PasteElementAction;
import org.talend.avro.schema.editor.handlers.ContextualHandler;

public class PasteElementHandler extends ContextualHandler {

	@Override
	protected void doExecute(ExecutionEvent event, AvroSchemaEditor schemaEditor, AvroContext context) {		
		PasteElementAction action = new PasteElementAction("Copy element(s)", Notifications.NOT_REF);
		action.init(context, false);
		action.run();
	}

}
