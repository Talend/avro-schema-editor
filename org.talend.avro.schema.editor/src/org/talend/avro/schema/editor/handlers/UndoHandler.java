package org.talend.avro.schema.editor.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.talend.avro.schema.editor.commands.ICommandExecutor;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;

public class UndoHandler extends SchemaHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.undo"; //$NON-NLS-1$
	
	@Override
	protected void doExecute(ExecutionEvent event, AvroSchemaEditor schemaEditor) {
		schemaEditor.getServiceProvider().getService(ICommandExecutor.class).undo();
	}

}
