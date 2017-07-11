package org.talend.avro.schema.editor.thumbnail;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;

public class RefreshThumbnailViewHandler extends AbstractThumbnailViewHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.thumbnail.refresh"; //$NON-NLS-1$
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ThumbnailView thumbnailView = getThumbnailView(event);
		
		// try to find a Avro Schema Editor
		AvroSchemaEditor editor = getEditor(thumbnailView);
		
		if (editor != null) {
			thumbnailView.downloadContent(editor);
		}
		
		return null;
	}

}
