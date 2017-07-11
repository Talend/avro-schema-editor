package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class MoveUnionChoiceChecker implements DragAndDropPolicy.Checker {

	@Override
	public boolean accept(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {

		NodeType targetType = targetNode.getType();

		switch (targetType) {
		case FIELD:
		case MAP:
		case ARRAY:
			// these cases need some more validation
			if (targetNode.hasChildren()) {
				AvroNode targetChildNode = targetNode.getChild(0);
				NodeType targetChildType = targetChildNode.getType();
				if (targetChildType == NodeType.UNION) {
					UnionNode unionNode = (UnionNode) targetChildNode;
					if (!AttributeUtil.isChoiceType(unionNode)) {
						// simple optional case
						AvroNode notNullChild = ModelUtil.getFirstNotNullChild(unionNode);
						// this not null child must be a primitive type one
						return notNullChild.getType() == NodeType.PRIMITIVE_TYPE;
					}
				} else {
					return false;
				}
			} else {
				return true;
			}		
		default:
			return false;
		}

	}

}
