package org.talend.avro.schema.editor.edit.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.edit.actions.CopyElementAction;
import org.talend.avro.schema.editor.handlers.ContextualHandler;

public class CopyElementHandler extends ContextualHandler {

	@Override
	protected void doExecute(ExecutionEvent event, AvroSchemaEditor schemaEditor, AvroContext context) {
		CopyElementAction action = new CopyElementAction("Copy selected element(s)", Notifications.NONE);
		action.init(context, false);
		action.run();
	}

}
