package org.talend.avro.schema.editor.viewer;

/**
 * This interface is responsible for configuration of the Drag and Drop behaviors on a {@link SchemaViewer} component.
 * 
 * @author timbault
 *
 */
public interface DragAndDropConfiguration {

	int getSupportedDropOperations(SchemaViewer schemaViewer);
	
    SchemaViewerDropPolicy getDropPolicy(SchemaViewer schemaViewer);
	
}
