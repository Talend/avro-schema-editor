package org.talend.avro.schema.editor.viewer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.handlers.ContextualHandler;
import org.talend.avro.schema.editor.model.AvroNode;

public class ShowNextNodeHandler extends ContextualHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.viewer.ShowNextNode"; //$NON-NLS-1$
	
	@Override
	protected void doExecute(ExecutionEvent event, AvroSchemaEditor schemaEditor, AvroContext context) {
		AvroNode node = schemaEditor.getContext().getSearchNodeContext().next();
		if (node != null) {
			schemaEditor.getContentPart()
				.getSchemaViewer(context.getKind())
				.setSelection(new StructuredSelection(node), true);
		}
	}

}
