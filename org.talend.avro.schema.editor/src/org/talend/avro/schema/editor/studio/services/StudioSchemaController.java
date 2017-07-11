package org.talend.avro.schema.editor.studio.services;

import org.talend.avro.schema.editor.edit.AvroSchemaEditorController;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.TargetPosition;

public class StudioSchemaController extends AvroSchemaEditorController {

	@Override
	public boolean canAddElement(AvroNode parentNode, NodeType type, TargetPosition position) {
		switch (type) {
		case FIELD:
			return canAddField(parentNode);
		default:
			return false;
		}
	}
	
}
