package org.talend.avro.schema.editor.viewer;

import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.viewer.SchemaViewer.DisplayMode;

/**
 * This class provides the configuration for the standard avro schema viewer.
 * 
 * @author timbault
 *
 */
public class SchemaViewerConfigurationImpl implements SchemaViewerConfiguration {
	
    private static final boolean[] MASTER_TOOLBAR_ALIGNMENTS = new boolean[] { false, false, true};
    
    private static final boolean[] SLAVE_TOOLBAR_ALIGNMENTS = new boolean[] { true, false, true};
    
	public SchemaViewerConfigurationImpl() {
		super();
	}
	
	@Override
	public DisplayMode getInitialDisplayMode(AvroSchemaEditor editor, AvroContext context) {	
		return DisplayMode.WITHOUT_COLUMNS;
	}

	@Override
	public int getTreeViewerStyle(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode) {
		return SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL;
	}

	@Override
	public SchemaViewerContentProvider getContentProvider(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode) {
		return new SchemaViewerContentProviderImpl(context);
	}

	@Override
	public SchemaViewerLabelProvider getLabelProvider(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode) {
		return new SchemaTreeLabelProviderImpl(editor.getServiceProvider(), displayMode);
	}

	@Override
	public PopupMenuConfiguration getPopupMenuConfiguration(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode) {
		return new SchemaPopupMenuConfigurationImpl(editor.getServiceProvider(), context);
	}

	@Override
	public ToolBarConfiguration getToolBarConfiguration(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode) {
		if (context.isMaster()) {
			return new SchemaToolBarConfigurationImpl(editor.getServiceProvider(), context, MASTER_TOOLBAR_ALIGNMENTS);
		} else {
			return new SchemaToolBarConfigurationImpl(editor.getServiceProvider(), context, SLAVE_TOOLBAR_ALIGNMENTS);
		}
	}

	@Override
	public DragAndDropConfiguration getDragAndDropConfiguration(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode) {
		return new SchemaViewerDragAndDropConfiguration();
	}

	@Override
	public ViewerFilter[] getViewerFilters(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode) {
		return new ViewerFilter[0];
	}

	@Override
	public ViewerComparator getViewerComparator(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode) {
		// no specific comparator
		return null;
	}
	
}
