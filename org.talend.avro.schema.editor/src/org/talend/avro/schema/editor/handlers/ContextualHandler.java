package org.talend.avro.schema.editor.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.AvroContext.Kind;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;

public abstract class ContextualHandler extends SchemaHandler {

	protected AvroContext getContext(ExecutionEvent event, AvroSchemaEditor schemaEditor) {
		Kind kind = null;
		String contextName = event.getParameter(AvroContext.ID);		
		if (contextName == null) {
			// use the active context
			kind = schemaEditor.getActiveContext().getKind();
		} else {
			kind = AvroContext.Kind.valueOf(contextName.toUpperCase());
		}
		AvroContext context = schemaEditor.getContext();
		if (kind == Kind.MASTER) {
			return context.getMaster();
		} else {
			return context.getSlave();
		}
	}

	@Override
	protected void doExecute(ExecutionEvent event, AvroSchemaEditor schemaEditor) {
		doExecute(event, schemaEditor, getContext(event, schemaEditor));
	}

	protected abstract void doExecute(ExecutionEvent event, AvroSchemaEditor schemaEditor, AvroContext context);
	
}
