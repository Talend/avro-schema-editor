package org.talend.avro.schema.editor.studio.viewer;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.viewer.SchemaViewerConfigurationImpl;
import org.talend.avro.schema.editor.viewer.SchemaViewerLabelProvider;
import org.talend.avro.schema.editor.viewer.ToolBarConfiguration;
import org.talend.avro.schema.editor.viewer.PopupMenuConfiguration;
import org.talend.avro.schema.editor.viewer.SchemaViewer.DisplayMode;

/**
 * This class provides the configuration for the studio schema viewer.
 * 
 * @author timbault
 *
 */
public class StudioSchemaViewerConfiguration extends SchemaViewerConfigurationImpl {	
	
	@Override
	public DisplayMode getInitialDisplayMode(AvroSchemaEditor editor, AvroContext context) {	
		return DisplayMode.WITH_COLUMNS;
	}
	
	@Override
	public SchemaViewerLabelProvider getLabelProvider(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode) {
		return new StudioTreeLabelProviderImpl(editor.getServiceProvider(), displayMode);
	}

	@Override
	public ToolBarConfiguration getToolBarConfiguration(AvroSchemaEditor editor, AvroContext context, DisplayMode displayMode) {
		return new StudioToolBarConfigurationImpl(editor.getServiceProvider(), context);
	}

	@Override
	public PopupMenuConfiguration getPopupMenuConfiguration(AvroSchemaEditor editor, AvroContext context,
			DisplayMode displayMode) {		
		return new StudioPopupMenuConfigurationImpl(editor.getServiceProvider(), context);
	}
	
}
