package org.talend.avro.schema.editor.viewer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.handlers.ContextualHandler;
import org.talend.avro.schema.editor.utils.UIUtils;
import org.talend.avro.schema.editor.viewer.SchemaViewer;

public class CollapseAllHandler extends ContextualHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.viewer.CollapseAll"; //$NON-NLS-1$
	
	@Override
	protected void doExecute(ExecutionEvent event, AvroSchemaEditor schemaEditor, AvroContext context) {		
		SchemaViewer viewer = schemaEditor.getContentPart().getSchemaViewer(context.getKind());		
		UIUtils.collapseAll(viewer);		
	}

}
