package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;

public class NodeTypeNameProvider implements NameProvider {

	private NodeType type;
	
	public NodeTypeNameProvider(NodeType type) {
		super();
		this.type = type;
	}

	@Override
	public String getName(AvroNode node) {
		return type.getDefaultLabel();
	}
	
}
