package org.talend.avro.schema.editor.viewer;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.talend.avro.schema.editor.viewer.attribute.column.SchemaViewerColumnEditingSupport;

public class InternalEditingSupport extends EditingSupport {

	private SchemaViewerNodeConverter nodeConverter;
	
	private SchemaViewerColumnEditingSupport editingSupport;
	
	public InternalEditingSupport(ColumnViewer viewer, SchemaViewerColumnEditingSupport editingSupport, SchemaViewerNodeConverter nodeConverter) {
		super(viewer);
		this.editingSupport = editingSupport;
		this.nodeConverter = nodeConverter;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editingSupport.getCellEditor(nodeConverter.convertToAvroNode(element), getViewer());
	}

	@Override
	protected boolean canEdit(Object element) {
		return editingSupport.canEdit(nodeConverter.convertToAvroNode(element));
	}

	@Override
	protected Object getValue(Object element) {
		return editingSupport.getValue(nodeConverter.convertToAvroNode(element));
	}

	@Override
	protected void setValue(Object element, Object value) {
		editingSupport.setValue(nodeConverter.convertToAvroNode(element), value);
	}

}
