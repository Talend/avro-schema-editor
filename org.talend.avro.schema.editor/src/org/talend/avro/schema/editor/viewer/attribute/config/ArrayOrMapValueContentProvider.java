package org.talend.avro.schema.editor.viewer.attribute.config;

import java.util.List;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.MultiChoiceValue;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class ArrayOrMapValueContentProvider implements MultiChoiceValueContentProvider<NodeType> {

	@Override
	public NodeType[] getContent(AvroAttribute<MultiChoiceValue<NodeType>> attribute) {		
		
		AvroNode arrayOrMapNode = attribute.getHolder();
		NodeType type = arrayOrMapNode.getType();
		NodeType otherType = type == NodeType.ARRAY ? NodeType.MAP : NodeType.ARRAY;
		
		// if this array/map node is under an union node of type "choice",
		
		AvroNode parent = arrayOrMapNode.getParent();
		if (parent.getType() == NodeType.UNION && AttributeUtil.isChoiceType(parent)) {					
			List<AvroNode> children = parent.getChildren(otherType);
			if (!children.isEmpty()) {
				return new NodeType[] { type };
			}
		}
		
		return NodeType.ARRAY_OR_MAP;
	}


}
