package org.talend.avro.schema.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.IWithAvroSchemaEditor;

public abstract class SchemaHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		
		if (activePart instanceof IWithAvroSchemaEditor) {
			
			AvroSchemaEditor editor = ((IWithAvroSchemaEditor) activePart).getEditor();
			
			doExecute(event, editor);
			
		}
		
		return null;
	}
	
	protected abstract void doExecute(ExecutionEvent event, AvroSchemaEditor editor);
	
}
