package org.talend.avro.schema.editor.thumbnail;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class ClearThumbnailContentHandler extends AbstractThumbnailViewHandler {

	public static final String CMD_ID = "org.talend.avro.schema.editor.thumbnail.clear"; //$NON-NLS-1$
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ThumbnailView thumbnailView = getThumbnailView(event);
		
		thumbnailView.clear();
		
		return null;
	}	
	
}
