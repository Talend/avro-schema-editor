package org.talend.avro.schema.editor.viewer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.handlers.ContextualHandler;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.viewer.SchemaViewer;

public class NavigateOutHandler extends ContextualHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.viewer.navigateOut"; //$NON-NLS-1$
	
	@Override
	protected void doExecute(ExecutionEvent event, AvroSchemaEditor schemaEditor, AvroContext context) {
		
		SchemaViewer schemaViewer = schemaEditor.getContentPart().getSchemaViewer(context.getKind());
		
		AvroNode content = schemaViewer.getContent();
		
		AvroNode parent = content.getParent();
		
		if (parent != null) {
			schemaViewer.setContent(parent);
		}
		
	}

}
