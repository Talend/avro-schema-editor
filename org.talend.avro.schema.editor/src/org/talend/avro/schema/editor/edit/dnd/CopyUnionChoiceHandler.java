package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.validator.AvroNodeAttributesValidators;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.ModelConstants;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class CopyUnionChoiceHandler extends AbstractCopyHandler {
	
	public CopyUnionChoiceHandler(AvroContext context, CopyEngine copyEngine) {
		super(context, copyEngine);
	}

	@Override
	public DnDParams executeDnD(AvroNode sourceNode, AvroNode targetNode, TargetPosition position,
			AvroNodeAttributesValidators validators) {

		BaseDnDParams params = BaseDnDParams.getParams(sourceNode, targetNode, position);
		
		AvroNode copy = copyElement(sourceNode, targetNode, position);
		
		// remove the optional null node
		if (ModelUtil.hasNullChild(copy)) {
			AvroNode nullChild = ModelUtil.getNullChild(copy);
			unlinkNodes(copy, nullChild, ModelConstants.NONE);
		}
		
		NodeType targetType = targetNode.getType();
		switch (targetType) {
		case FIELD:
		case ARRAY:
		case MAP:
			if (targetNode.hasChildren()) {
				AvroNode targetChildNode = targetNode.getChild(0);
				NodeType targetChildType = targetChildNode.getType();
				if (targetChildType == NodeType.UNION) {
					UnionNode unionNode = (UnionNode) targetChildNode;
					if (AttributeUtil.isChoiceType(unionNode)) {						
						raiseException(COPY_ACTION);
					} else {
						// simple optional case
						AvroNode notNullChild = ModelUtil.getFirstNotNullChild(unionNode);
						// this not null child must be a primitive type one
						if (notNullChild.getType() == NodeType.PRIMITIVE_TYPE) {							
							PrimitiveType type = AttributeUtil.getPrimitiveType(notNullChild);
							params.storeObject(OPTIONAL_TARGET_PRIMITIVE_TYPE, type);
							// add a null node to the copy
							createAndLinkPrimitiveTypeNode(copy, ModelConstants.FIRST_POSITION, PrimitiveType.NULL);
						} else {
							raiseException(COPY_ACTION);
						}
					}
				} else {
					raiseException(COPY_ACTION);
				}
			} else {
				PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(targetNode);
				params.storeObject(INITIAL_TARGET_PRIMITIVE_TYPE, primitiveType);				
			}
			break;
		default:
			raiseException(COPY_ACTION);
		}
		
		getController().addElement(targetNode, copy, position);

		params.storeAvroNode(DnDParams.COPY_NODE, copy);		
		
		return params;
	}

	@Override
	public boolean undoDnD(DnDParams params, AvroNodeAttributesValidators validators) {
		
		AvroNode copy = params.getAvroNode(DnDParams.COPY_NODE);
		
		AvroNode currentParent = copy.getParent();
		
		getController().removeElement(copy);
		
		// restore the primitive types
		
		if (params.isObjectDefined(INITIAL_TARGET_PRIMITIVE_TYPE)) {
			PrimitiveType type = (PrimitiveType) params.getObject(INITIAL_TARGET_PRIMITIVE_TYPE);
			AttributeUtil.setPrimitiveType(currentParent, type);
		}
		
		if (params.isObjectDefined(OPTIONAL_TARGET_PRIMITIVE_TYPE)) {
			// it means that the current parent node is optional
			PrimitiveType type = (PrimitiveType) params.getObject(OPTIONAL_TARGET_PRIMITIVE_TYPE);
			UnionNode unionNode = (UnionNode) currentParent.getChild(0);
			AvroNode notNullChild = ModelUtil.getFirstNotNullChild(unionNode);
			AttributeUtil.setPrimitiveType(notNullChild, type);
		}
		
		return true;
	}
	
}
