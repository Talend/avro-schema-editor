package org.talend.avro.schema.editor.debug;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.talend.avro.schema.editor.utils.UIUtils;

public class ExpandAllDebugHandler extends AbstractDebugHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.debug.ExpandAll"; //$NON-NLS-1$
	
	@Override
	protected Object execute(AvroSchemaEditorDebugView debugView, ExecutionEvent event) {
		
		TreeViewer treeViewer = debugView.getViewer();
		UIUtils.expandAll(treeViewer, CMD_ID);
		
		return null;
	}

}
