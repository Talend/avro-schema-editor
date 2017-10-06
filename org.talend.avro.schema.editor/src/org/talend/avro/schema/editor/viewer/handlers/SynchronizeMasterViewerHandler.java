package org.talend.avro.schema.editor.viewer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.AvroContext.Kind;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.IWithAvroSchemaEditor;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.viewer.SchemaViewer;

public class SynchronizeMasterViewerHandler extends AbstractHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.viewer.syncMaster"; //$NON-NLS-1$
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		
		if (activePart instanceof IWithAvroSchemaEditor) {
			
			AvroSchemaEditor editor = ((IWithAvroSchemaEditor) activePart).getEditor();
			
			AvroContext slaveContext = editor.getContext(Kind.SLAVE);
			AvroNode inputNode = slaveContext.getInputNode();
			
			// select and reveal this node in the master context
			SchemaViewer masterSchemaViewer = editor.getContentPart().getSchemaViewer(Kind.MASTER);
			IStructuredSelection selection = new StructuredSelection(inputNode);
			masterSchemaViewer.setSelection(selection);
			masterSchemaViewer.reveal(inputNode);
			
		}
		
		return null;
	}

}
