package org.talend.avro.schema.editor.registry.view;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.utils.UIUtils;

public class ExpandAllHandler extends AbstractRegistryViewHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.registry.view.expandAll"; //$NON-NLS-1$

	@Override
	protected Object execute(SchemaRegistryView registryView, AvroSchemaEditor editor, ExecutionEvent event) {
		
		TreeViewer treeViewer = registryView.getTreeViewer();
		
		UIUtils.expandAll(treeViewer, CMD_ID);
		
		return null;
	}
	
}
