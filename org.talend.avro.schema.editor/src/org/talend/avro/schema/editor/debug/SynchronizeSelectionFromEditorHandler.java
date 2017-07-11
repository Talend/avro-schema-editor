package org.talend.avro.schema.editor.debug;

import org.eclipse.core.commands.ExecutionEvent;


public class SynchronizeSelectionFromEditorHandler extends AbstractDebugHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.debug.SynchronizeSelectionFromEditor"; //$NON-NLS-1$
	
	@Override
	protected Object execute(AvroSchemaEditorDebugView debugView, ExecutionEvent event) {

		debugView.synchronizeSelection();
		
		return null;
	}

}
