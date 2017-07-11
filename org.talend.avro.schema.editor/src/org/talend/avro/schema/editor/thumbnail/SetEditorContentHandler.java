package org.talend.avro.schema.editor.thumbnail;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;

public class SetEditorContentHandler extends AbstractThumbnailViewHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.thumbnail.upload"; //$NON-NLS-1$
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ThumbnailView thumbnailView = getThumbnailView(event);
		
		AvroSchemaEditor editor = getEditor(thumbnailView);
		
		if (editor != null) {
			thumbnailView.uploadContent(editor);
		}
		
		return null;
	}

}
