package org.talend.avro.schema.editor.edit.dnd;

import java.util.List;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.RefNode;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class MoveNameSpacedElementChecker implements DragAndDropPolicy.Checker {

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
					if (AttributeUtil.isChoiceType(unionNode)) {
						return accept(sourceNode, unionNode);
					} else {
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
		case UNION:
			return AttributeUtil.isChoiceType(targetNode) && accept(sourceNode, (UnionNode) targetNode);
		default:
			return false;
		}
		
	}

	protected boolean accept(AvroNode sourceNode, UnionNode choiceNode) {
		AvroNode referencedNode = getReferencedNode(sourceNode);
		// check that the name spaced source node is not already referenced under the union node
		List<AvroNode> refNodes = choiceNode.getChildren(NodeType.REF);
		for (AvroNode refNode : refNodes) {
			if (((RefNode) refNode).getReferencedNode() == referencedNode) {
				return false;
			}
		}
		return true;
	}
	
	protected static AvroNode getReferencedNode(AvroNode node) {
		if (node.getType().isRef()) {
			return ((RefNode) node).getReferencedNode();
		}
		return node;
	}
	
}
