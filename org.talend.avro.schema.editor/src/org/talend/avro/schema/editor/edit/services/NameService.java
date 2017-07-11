package org.talend.avro.schema.editor.edit.services;

import org.talend.avro.schema.editor.context.services.IContextualService;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;

/**
 * This service provides 
 * 
 * @author timbault
 *
 */
public interface NameService extends IContextualService {		
	
	String validateName(String name, AvroNode node);
	
	String validateNameSpace(String nameSpace, AvroNode node);
		
	String getAvailableName(NodeType type, AvroNode contextualNode);
	
	String getValidNameCopy(AvroNode sourceNode, AvroNode copyNode, AvroNode targetNode);
	
}
