package org.talend.avro.schema.editor.edit.dnd;

import java.util.List;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class MovePrimitiveTypeChecker implements DragAndDropPolicy.Checker {

	@Override
	public boolean accept(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {

		NodeType targetType = targetNode.getType();
		switch (targetType) {
		case UNION:
			if (AttributeUtil.isChoiceType(targetNode)) {				
				// check that the dragged primitive type node is not already present under the target union node
				PrimitiveType draggedType = AttributeUtil.getPrimitiveType(sourceNode);
				List<AvroNode> primitiveTypeChildren = targetNode.getChildren(NodeType.PRIMITIVE_TYPE);
				for (AvroNode primitiveTypeChild : primitiveTypeChildren) {
					PrimitiveType childType = AttributeUtil.getPrimitiveType(primitiveTypeChild);
					if (childType == draggedType) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		default:
			return false;
		}
		
	}

}
