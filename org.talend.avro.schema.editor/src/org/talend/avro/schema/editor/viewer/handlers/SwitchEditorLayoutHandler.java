package org.talend.avro.schema.editor.viewer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.EditorLayout;
import org.talend.avro.schema.editor.edit.IWithAvroSchemaEditor;

public class SwitchEditorLayoutHandler extends AbstractHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.viewer.switchEditorLayout"; //$NON-NLS-1$
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		
		if (activePart instanceof IWithAvroSchemaEditor) {
			
			AvroSchemaEditor editor = ((IWithAvroSchemaEditor) activePart).getEditor();
			
			EditorLayout editorLayout = editor.getContentPart().getEditorLayout();
			EditorLayout newLayout = null;
			switch (editorLayout) {
			case TREE_AND_ATTRIBUTES:
				newLayout = EditorLayout.TWO_TREES;
				break;
			case TWO_TREES:
				newLayout = EditorLayout.TREE_AND_ATTRIBUTES;
				break;
			default:
				throw new UnsupportedOperationException();
			}
			editor.getContentPart().setEditorLayout(newLayout);
			
		}
		
		return null;
	}

}
