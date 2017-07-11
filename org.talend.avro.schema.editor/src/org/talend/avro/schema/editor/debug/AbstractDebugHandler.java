package org.talend.avro.schema.editor.debug;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractDebugHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof AvroSchemaEditorDebugView) {
			return execute((AvroSchemaEditorDebugView)activePart, event);
		}
		return null;
	}

	protected abstract Object execute(AvroSchemaEditorDebugView debugView, ExecutionEvent event);
	
}
