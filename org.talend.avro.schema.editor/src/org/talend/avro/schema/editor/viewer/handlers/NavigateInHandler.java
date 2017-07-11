package org.talend.avro.schema.editor.viewer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.handlers.ContextualHandler;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.viewer.SchemaViewer;

public class NavigateInHandler extends ContextualHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.viewer.navigateIn"; //$NON-NLS-1$
	
	@Override
	protected void doExecute(ExecutionEvent event, AvroSchemaEditor schemaEditor, AvroContext context) {

		SchemaViewer schemaViewer = schemaEditor.getContentPart().getSchemaViewer(context.getKind());
		IStructuredSelection selection = (IStructuredSelection) schemaViewer.getSelection();
		
		if (!selection.isEmpty() && selection.size() == 1) {
			AvroNode node = (AvroNode) selection.getFirstElement();
			if (isNavigableNode(node)) {
				schemaViewer.setContent(node);
			}
		}
		
	}

	protected boolean isNavigableNode(AvroNode node) {
		NodeType type = node.getType();
		return type == NodeType.RECORD 
				|| type == NodeType.UNION 
				|| (type == NodeType.FIELD && !node.getChildren().isEmpty() && node.getChildren().get(0).getType() == NodeType.UNION);
	}
	
}
