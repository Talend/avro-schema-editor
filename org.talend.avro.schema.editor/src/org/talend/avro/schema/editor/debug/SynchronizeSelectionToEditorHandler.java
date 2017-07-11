package org.talend.avro.schema.editor.debug;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.utils.UIUtils;
import org.talend.avro.schema.editor.viewer.SchemaViewer;

public class SynchronizeSelectionToEditorHandler extends AbstractDebugHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.debug.SynchronizeSelectionToEditor"; //$NON-NLS-1$
	
	@Override
	protected Object execute(AvroSchemaEditorDebugView debugView, ExecutionEvent event) {

		IStructuredSelection selection = (IStructuredSelection) debugView.getSelection();

		if (!selection.isEmpty()) {
		
			AvroNode node = (AvroNode) selection.getFirstElement();
			
			AvroSchemaEditor editor = UIUtils.pickAvroSchemaEditorFromEditorParts();
			SchemaViewer masterViewer = editor.getContentPart().getSchemaViewer(AvroContext.Kind.MASTER);
				
			masterViewer.setSelection(new StructuredSelection(node));
			
		}
		
		return null;
	}

}
