package org.talend.avro.schema.editor.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.AvroContext.Kind;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.SchemaNode;
import org.talend.avro.schema.editor.model.SchemaNodeRegistry;
import org.talend.avro.schema.editor.viewer.SchemaViewer;
import org.talend.avro.schema.editor.viewer.SchemaViewerNodeConverter;

public abstract class ContextualHandler extends SchemaHandler {

	protected AvroContext getContext(ExecutionEvent event, AvroSchemaEditor schemaEditor) {
		Kind kind = null;
		String contextName = event.getParameter(AvroContext.ID);		
		if (contextName == null) {
			// use the active context
			kind = schemaEditor.getActiveContext().getKind();
		} else {
			kind = AvroContext.Kind.valueOf(contextName.toUpperCase());
		}
		AvroContext context = schemaEditor.getContext();
		if (kind == Kind.MASTER) {
			return context.getMaster();
		} else {
			return context.getSlave();
		}
	}

	@Override
	protected void doExecute(ExecutionEvent event, AvroSchemaEditor schemaEditor) {
		doExecute(event, schemaEditor, getContext(event, schemaEditor));
	}

	protected abstract void doExecute(ExecutionEvent event, AvroSchemaEditor schemaEditor, AvroContext context);
	
	protected SchemaNode getSelectedSchemaNode(SchemaViewer viewer, AvroContext context) {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		if (!selection.isEmpty() && selection.size() == 1) {
			AvroNode node = (AvroNode) selection.getFirstElement();
			SchemaNodeRegistry schemaNodeRegistry = context.getSchemaNodeRegistry();
			SchemaViewerNodeConverter converter = new SchemaViewerNodeConverter(schemaNodeRegistry);
			return converter.convertToSchemaNode(node);
		}
		return null;
	}
	
}
