package org.talend.avro.schema.editor.registry.view;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.viewer.SchemaViewer;

public class ShowInEditorHandler extends AbstractRegistryViewHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.registry.view.show"; //$NON-NLS-1$
	
	@Override
	protected Object execute(SchemaRegistryView registryView, AvroSchemaEditor editor, ExecutionEvent event) {
		
		IStructuredSelection selection = (IStructuredSelection) registryView.getSelection();
		
		if (!selection.isEmpty()) {
			Object element = selection.getFirstElement();
			if (element instanceof AvroNode) {
				AvroNode node = (AvroNode) element;
				SchemaViewer masterViewer = editor.getContentPart().getSchemaViewer(AvroContext.Kind.MASTER);
				masterViewer.setSelection(new StructuredSelection(node));
				masterViewer.reveal(node);
			}
		}
		
		return null;
	}

}
