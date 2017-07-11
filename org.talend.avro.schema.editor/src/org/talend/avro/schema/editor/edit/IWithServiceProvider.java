package org.talend.avro.schema.editor.edit;

import org.talend.avro.schema.editor.edit.services.IEditorServiceProvider;

public interface IWithServiceProvider {

	IEditorServiceProvider getServiceProvider();
	
}
