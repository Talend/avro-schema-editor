package org.talend.avro.schema.editor.log;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class ClearLogHandler extends AbstractLogViewHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.log.clear"; //$NON-NLS-1$
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		AvroSchemaLogView logView = getLogView(event);
		
		logView.clear();
		
		return null;
	}

	
	
}
