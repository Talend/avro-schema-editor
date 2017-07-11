package org.talend.avro.schema.editor.log;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractLogViewHandler extends AbstractHandler {

	protected AvroSchemaLogView getLogView(ExecutionEvent event) {
		return (AvroSchemaLogView) HandlerUtil.getActivePart(event);
	}
	
}
