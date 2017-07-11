package org.talend.avro.schema.editor.log;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class ActivateLogHandler extends AbstractLogViewHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.log.activate"; //$NON-NLS-1$
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		AvroSchemaLogView logView = getLogView(event);
		
		logView.setActive(!logView.isActive());
		
		return null;
	}

}
