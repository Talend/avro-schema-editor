package org.talend.avro.schema.editor.studio.viewer;

import org.talend.avro.schema.editor.edit.services.IEditorServiceProvider;
import org.talend.avro.schema.editor.viewer.SchemaTreeLabelProviderImpl;
import org.talend.avro.schema.editor.viewer.SchemaViewer.DisplayMode;

/**
 * Label provider used for the Studio schema editor tree.
 * 
 * @author timbault
 *
 */
public class StudioTreeLabelProviderImpl extends SchemaTreeLabelProviderImpl {

	public StudioTreeLabelProviderImpl(IEditorServiceProvider serviceProvider, DisplayMode displayMode) {
		super(serviceProvider, displayMode);
	}

	@Override
	protected int getImageVersion() {
		return 0;
	}
	
}
