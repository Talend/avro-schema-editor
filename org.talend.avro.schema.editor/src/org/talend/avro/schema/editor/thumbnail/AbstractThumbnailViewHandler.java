package org.talend.avro.schema.editor.thumbnail;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.handlers.HandlerUtil;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.utils.UIUtils;

public abstract class AbstractThumbnailViewHandler extends AbstractHandler {

	protected ThumbnailView getThumbnailView(ExecutionEvent event) {
		return (ThumbnailView) HandlerUtil.getActivePart(event);
	}
	
	protected AvroSchemaEditor getEditor(ThumbnailView view) {
		
		// try to find a Avro Schema Editor
		AvroSchemaEditor editor = UIUtils.pickAvroSchemaEditorFromEditorParts();				
		
		return editor;
	}
	
}
