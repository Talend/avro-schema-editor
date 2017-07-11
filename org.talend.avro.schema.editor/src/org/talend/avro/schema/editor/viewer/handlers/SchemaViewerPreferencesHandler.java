package org.talend.avro.schema.editor.viewer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.handlers.SchemaHandler;
import org.talend.avro.schema.editor.viewer.actions.OpenPreferencesShellAction;

public class SchemaViewerPreferencesHandler extends SchemaHandler {
	
	@Override
	protected void doExecute(ExecutionEvent event, AvroSchemaEditor schemaEditor) {
		OpenPreferencesShellAction action = new OpenPreferencesShellAction(schemaEditor.getServiceProvider());
		action.run();
	}

}
