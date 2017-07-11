package org.talend.avro.schema.editor.model.path;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.services.IEditorService;
import org.talend.avro.schema.editor.model.AvroNode;

public interface PathService extends IEditorService {

	boolean hasPath(AvroNode node);
	
	String getPath(AvroNode node);
	
	String getPath(AvroNode node, AvroContext context);
	
	AvroNode getNode(String path);
	
	AvroNode getNode(String path, AvroContext context);
	
}
