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
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public class MoveUnionChoiceHandler extends AbstractDnDHandler {
	
	private static final String SOURCE_OPTIONAL_UNION_NODE = "SourceOptionalUnionNode"; //$NON-NLS-1$	
		
	public MoveUnionChoiceHandler(AvroContext context) {
		super(context);
	}

	@Override
	public DnDParams executeDnD(AvroNode sourceNode, AvroNode targetNode, TargetPosition position,
			AvroNodeAttributesValidators validators) {

		BaseDnDParams params = BaseDnDParams.getParams(sourceNode, targetNode, position);
		
		AvroNode sourceParent = sourceNode.getParent();
		params.storeAvroNode(DnDParams.SOURCE_PARENT, sourceParent);
		
		boolean isOptional = ModelUtil.hasNullChild(sourceNode);		
		
		unlinkNodes(sourceParent, sourceNode, ModelConstants.UNREGISTER);
		
		if (isOptional) {
			// we have to keep the optional union node
			UnionNode unionNode = createAndLinkUnionNode(sourceParent, ModelConstants.REGISTER);
			createAndLinkPrimitiveTypeNode(unionNode, ModelConstants.FIRST_POSITION, PrimitiveType.NULL);
			createAndLinkPrimitiveTypeNode(unionNode, ModelConstants.LAST_POSITION, AttributeUtil.getPrimitiveType(sourceParent));
			params.storeAvroNode(SOURCE_OPTIONAL_UNION_NODE, unionNode);
			// and remove the null node from the choice node
			AvroNode nullChild = ModelUtil.getNullChild(sourceNode);
			unlinkNodes(sourceNode, nullChild, ModelConstants.NONE);
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
						raiseException(MOVE_ACTION);
					} else {
						// simple optional case
						AvroNode notNullChild = ModelUtil.getFirstNotNullChild(unionNode);
						// this not null child must be a primitive type one
						if (notNullChild.getType() == NodeType.PRIMITIVE_TYPE) {
							// remove the optional union node
							unlinkNodes(targetNode, unionNode, ModelConstants.UNREGISTER);
							params.storeAvroNode(TARGET_OPTIONAL_UNION_NODE, unionNode);
							// add the choice union node
							linkNodes(targetNode, sourceNode, ModelConstants.REGISTER);
							// add null node
							createAndLinkPrimitiveTypeNode(sourceNode, ModelConstants.FIRST_POSITION, PrimitiveType.NULL);
						} else {
							raiseException(MOVE_ACTION);
						}
					}
				} else {
					raiseException(MOVE_ACTION);
				}
			} else {
				PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(targetNode);
				params.storeObject(INITIAL_TARGET_PRIMITIVE_TYPE, primitiveType);
				linkNodes(targetNode, sourceNode, ModelConstants.REGISTER);
			}
			break;
		default:
			raiseException(MOVE_ACTION);
		}
		
		validate(sourceParent, validators);
		validate(targetNode, validators);
		
		return params;
	}	
	
	@Override
	public boolean undoDnD(DnDParams params, AvroNodeAttributesValidators validators) {

		AvroNode sourceParent = params.getAvroNode(DnDParams.SOURCE_PARENT);
		AvroNode movedNode = params.getSourceNode();
		AvroNode currentParentNode = movedNode.getParent();		
		
		unlinkNodes(currentParentNode, movedNode, ModelConstants.UNREGISTER);
		
		if (params.isAvroNodeDefined(TARGET_OPTIONAL_UNION_NODE)) {
			// remove the added null node
			AvroNode nullChild = ModelUtil.getNullChild(movedNode);
			unlinkNodes(movedNode, nullChild, ModelConstants.NONE);
		}
		
		if (params.isObjectDefined(INITIAL_TARGET_PRIMITIVE_TYPE)) {
			PrimitiveType primitiveType = (PrimitiveType) params.getObject(INITIAL_TARGET_PRIMITIVE_TYPE);
			AttributeUtil.setPrimitiveType(currentParentNode, primitiveType);
		}
		
		if (params.isAvroNodeDefined(TARGET_OPTIONAL_UNION_NODE)) {
			AvroNode unionNode = params.getAvroNode(TARGET_OPTIONAL_UNION_NODE);
			linkNodes(currentParentNode, unionNode, ModelConstants.REGISTER);
		}
		
		if (params.isAvroNodeDefined(SOURCE_OPTIONAL_UNION_NODE)) {
			AvroNode unionNode = params.getAvroNode(SOURCE_OPTIONAL_UNION_NODE);
			unlinkNodes(sourceParent, unionNode, ModelConstants.UNREGISTER);
		}
		
		linkNodes(sourceParent, movedNode, ModelConstants.REGISTER);
		
		if (params.isAvroNodeDefined(SOURCE_OPTIONAL_UNION_NODE)) {
			// add null node
			createAndLinkPrimitiveTypeNode(movedNode, ModelConstants.FIRST_POSITION, PrimitiveType.NULL);			
		}
		
		// validate
		validators.validate(sourceParent, AvroAttributes.PRIMITIVE_TYPE);
		validators.validate(currentParentNode, AvroAttributes.PRIMITIVE_TYPE);
		
		return true;
	}
	
}
