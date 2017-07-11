package org.talend.avro.schema.editor.edit.services;

import org.talend.avro.schema.editor.context.AbstractContextualService;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;

public abstract class AbstractEditorService extends AbstractContextualService implements IEditorService {

	private AvroSchemaEditor editor;
	
	@Override
	public void init(AvroSchemaEditor editor) {
		this.editor = editor;
		init(editor.getContext());
	}

	protected AvroSchemaEditor getEditor() {
		return editor;
	}

}
