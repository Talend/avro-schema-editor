package org.talend.avro.schema.editor.viewer;

import org.eclipse.swt.dnd.DND;
import org.talend.avro.schema.editor.edit.dnd.AvroSchemaViewerDropPolicy;

public class SchemaViewerDragAndDropConfiguration implements DragAndDropConfiguration {

	@Override
	public int getSupportedDropOperations(SchemaViewer schemaViewer) {
		return DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
	}

	@Override
	public SchemaViewerDropPolicy getDropPolicy(SchemaViewer schemaViewer) {
		return new AvroSchemaViewerDropPolicy(schemaViewer.getContext());
	}
	
}
