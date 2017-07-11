package org.talend.avro.schema.editor.edit.dnd;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.talend.avro.schema.editor.viewer.SchemaViewer;

public class DnDSourceListener implements DragSourceListener {

	private SchemaViewer schemaViewer;
	
	public DnDSourceListener(SchemaViewer schemaViewer) {
		super();
		this.schemaViewer = schemaViewer;
	}

	@Override
    public void dragFinished(DragSourceEvent event) {
        LocalSelectionTransfer.getTransfer().setSelection(null);
    }

    @Override
    public void dragSetData(DragSourceEvent event) {
        if (schemaViewer.getSelection() instanceof IStructuredSelection) {
            LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
            IStructuredSelection ss = (IStructuredSelection) schemaViewer.getSelection();
            Object[] objects = ss.toArray();
            transfer.setSelection(new StructuredSelection(objects));
            event.data = transfer.getSelection();
        }
    }

    @Override
    public void dragStart(DragSourceEvent event) {
        if (schemaViewer.getSelection() instanceof IStructuredSelection) {
            event.doit = !schemaViewer.getSelection().isEmpty();
            dragSetData(event);
        }
    }
	
}
