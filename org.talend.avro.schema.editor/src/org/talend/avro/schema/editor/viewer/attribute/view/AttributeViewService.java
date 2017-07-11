package org.talend.avro.schema.editor.viewer.attribute.view;

import org.talend.avro.schema.editor.edit.services.IEditorService;

public interface AttributeViewService extends IEditorService {
	
	void attachToView();
	
	boolean isAttached();
	
	void detachFromView();
		
}

