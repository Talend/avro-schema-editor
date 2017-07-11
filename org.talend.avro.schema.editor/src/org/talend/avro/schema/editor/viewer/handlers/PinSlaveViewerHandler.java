package org.talend.avro.schema.editor.viewer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.IWithAvroSchemaEditor;

public class PinSlaveViewerHandler extends AbstractHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.viewer.pinSlave"; //$NON-NLS-1$
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		
		if (activePart instanceof IWithAvroSchemaEditor) {
			
			AvroSchemaEditor editor = ((IWithAvroSchemaEditor) activePart).getEditor();
			boolean slavePinned = editor.getContentPart().isSlavePinned();
			editor.getContentPart().setSlavePinned(!slavePinned);
			
		}
		
		return null;
	}

}
