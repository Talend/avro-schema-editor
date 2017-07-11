package org.talend.avro.schema.editor.registry.view;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.registry.NSNode;

public abstract class AbstractRegistryViewHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof SchemaRegistryView) {
			SchemaRegistryView registryView = (SchemaRegistryView) activePart;
			AvroSchemaEditor editor = registryView.getEditor();
			if (editor != null) {
				return execute(registryView, editor, event);
			}
		}
		return null;
	}

	protected NSNode getSelectedNSNode(SchemaRegistryView registryView) {
		IStructuredSelection selection = (IStructuredSelection) registryView.getSelection();
		if (!selection.isEmpty()) {
			Object element = selection.getFirstElement();
			if (element instanceof NSNode) {
				return (NSNode) element;
			}
		}
		return null;
	}
	
	protected abstract Object execute(SchemaRegistryView registryView, AvroSchemaEditor editor, ExecutionEvent event);

}
