package org.talend.avro.schema.editor.debug;

import org.eclipse.core.commands.ExecutionEvent;

public class GlobalRefreshHandler extends AbstractDebugHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.debug.globalrefresh"; //$NON-NLS-1$
	
	@Override
	protected Object execute(AvroSchemaEditorDebugView debugView, ExecutionEvent event) {
		debugView.globalRefresh();
		return null;
	}

}
