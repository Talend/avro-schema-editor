package org.talend.avro.schema.editor.viewer;

import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.viewer.SchemaViewer.DisplayMode;

/**
 * This class provides the full configuration of a schema viewer.
 * 
 * @author timbault
 *
 */
public interface SchemaViewerConfiguration {

	DisplayMode getInitialDisplayMode(AvroSchemaEditor editor, AvroContext context);
	
	/**
	 * Return the tree viewer style
	 * 
	 * @param context
	 * @return
	 */
	int getTreeViewerStyle(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode);
	
	SchemaViewerContentProvider getContentProvider(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode);
	
	SchemaViewerLabelProvider getLabelProvider(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode);
	
	ViewerFilter[] getViewerFilters(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode);
	
	ViewerComparator getViewerComparator(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode);
	
	PopupMenuConfiguration getPopupMenuConfiguration(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode);
	
	ToolBarConfiguration getToolBarConfiguration(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode);
	
	DragAndDropConfiguration getDragAndDropConfiguration(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode);
	
}
