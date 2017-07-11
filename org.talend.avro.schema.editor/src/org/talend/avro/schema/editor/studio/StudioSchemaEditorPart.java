package org.talend.avro.schema.editor.studio;

import org.talend.avro.schema.editor.edit.AvroSchemaEditorPart;

public class StudioSchemaEditorPart extends AvroSchemaEditorPart {

	public static final String EDITOR_ID = "org.talend.avro.schema.editor.studio.schema.StudioSchemaEditor"; //$NON-NLS-1$

	@Override
	protected String getContextId() {
		return EDITOR_ID;
	}
	
}
