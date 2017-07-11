package org.talend.avro.schema.editor.viewer.attribute.column;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.talend.avro.schema.editor.model.AvroNode;

public interface SchemaViewerColumnEditingSupport {

	CellEditor getCellEditor(AvroNode node, ColumnViewer viewer);
	
	boolean canEdit(AvroNode node);
	
	Object getValue(AvroNode node);
	
	void setValue(AvroNode node, Object value);
	
}
