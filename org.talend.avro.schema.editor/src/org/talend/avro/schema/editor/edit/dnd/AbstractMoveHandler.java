package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.validator.AvroNodeAttributesValidators;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.ModelConstants;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.PrimitiveTypeNode;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

public abstract class AbstractMoveHandler extends AbstractDnDHandler {

	protected static final String SOURCE_PRIMITIVE_TYPE_NODE = "SourcePrimitiveTypeNode"; //$NON-NLS-1$
	
	protected static final String UNION_PARENT_NODE = "UnionParentNode"; //$NON-NLS-1$
		
	protected static final String CONVERTED_CHOICE_TYPE = "ConvertedChoiceType"; //$NON-NLS-1$
	
	protected AbstractMoveHandler(AvroContext context) {
		super(context);
	}

	@Override
	public DnDParams executeDnD(AvroNode sourceNode, AvroNode targetNode, TargetPosition position,
			AvroNodeAttributesValidators validators) {

		BaseDnDParams params = BaseDnDParams.getParams(sourceNode, targetNode, position);
		
		AvroNode sourceParent = sourceNode.getParent();
		params.storeAvroNode(DnDParams.SOURCE_PARENT, sourceParent);
		
		NodeType sourceParentType = sourceParent.getType();
		
		switch (sourceParentType) {
		case FIELD:
		case MAP:
		case ARRAY:
			unlinkNodes(sourceParent, sourceNode, ModelConstants.UNREGISTER);
			break;
		case UNION:
			UnionNode unionNode = (UnionNode) sourceParent;
			AvroNode unionParentNode = unionNode.getParent();
			unlinkNodes(sourceParent, sourceNode, ModelConstants.UNREGISTER);
			if (AttributeUtil.isChoiceType(unionNode)) {
				// check if we have to remove the union node
				if (!unionNode.hasChildren()) {
					// no more children, we can remove it
					unlinkNodes(unionParentNode, unionNode, ModelConstants.UNREGISTER);
					params.storeAvroNode(UNION_PARENT_NODE, unionParentNode);					
				} else if (unionNode.getChildrenCount() == 1 
						&& unionNode.getChild(0).getType() == NodeType.PRIMITIVE_TYPE
						&& AttributeUtil.getPrimitiveType(unionNode.getChild(0)) == PrimitiveType.NULL) {
					// it is an optional choice type
					// we keep it and we add a primitive type node
					AvroNode primitiveTypeNode = addPrimitiveTypeNode(sourceParent, sourceNode);				
					params.storeAvroNode(SOURCE_PRIMITIVE_TYPE_NODE, primitiveTypeNode);
					AttributeUtil.setAttributeValue(unionNode, AvroAttributes.CHOICE_TYPE, false);
					params.storeBoolean(CONVERTED_CHOICE_TYPE, true);
				}
			} else {
				// simple optional case				
				// we have to add a primitive type node
				AvroNode primitiveTypeNode = addPrimitiveTypeNode(sourceParent, sourceNode);				
				params.storeAvroNode(SOURCE_PRIMITIVE_TYPE_NODE, primitiveTypeNode);				
			}
			break;
		default:
			throw new IllegalStateException();
		}
		
		NodeType targetType = targetNode.getType();
		switch (targetType) {
		case FIELD:
		case MAP:
		case ARRAY:
			if (targetNode.hasChildren()) {
				AvroNode targetChildNode = targetNode.getChild(0);
				NodeType targetChildType = targetChildNode.getType();
				if (targetChildType == NodeType.UNION) {
					UnionNode unionNode = (UnionNode) targetChildNode;
					if (AttributeUtil.isChoiceType(unionNode)) {						
						linkNodes(unionNode, sourceNode, ModelConstants.LAST_POSITION, ModelConstants.REGISTER);
					} else {
						// simple optional case
						AvroNode notNullChild = ModelUtil.getFirstNotNullChild(unionNode);
						// this not null child must be a primitive type one
						if (notNullChild.getType() == NodeType.PRIMITIVE_TYPE) {
							// replace this primitive type node by the moved one
							PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(notNullChild);
							params.storeObject(OPTIONAL_TARGET_PRIMITIVE_TYPE, primitiveType);
							unlinkNodes(unionNode, notNullChild, ModelConstants.NONE);
							linkNodes(unionNode, sourceNode, ModelConstants.LAST_POSITION, ModelConstants.REGISTER);
						} else {
							// should not happen
							throw new IllegalArgumentException("Cannot move source node on this target node");
						}
					}
				} else {
					// should not happen
					throw new IllegalArgumentException("Cannot move source node on this target node");
				}
			} else {
				PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(targetNode);
				params.storeObject(INITIAL_TARGET_PRIMITIVE_TYPE, primitiveType);
				linkNodes(targetNode, sourceNode, ModelConstants.REGISTER);
			}
			break;
		case UNION:
			linkNodes(targetNode, sourceNode, ModelConstants.LAST_POSITION, ModelConstants.NONE);
			break;
		default:
			// should not happen
			throw new IllegalArgumentException("Cannot move source node on this target node");
		}
		
		validators.validate(sourceParent, AvroAttributes.PRIMITIVE_TYPE);
		validators.validate(targetNode, AvroAttributes.PRIMITIVE_TYPE);
		
		return params;
	}

	protected AvroNode addPrimitiveTypeNode(AvroNode parent, AvroNode node) {
		PrimitiveTypeNode primitiveTypeNode = 
				createAndLinkPrimitiveTypeNode(parent, ModelConstants.LAST_POSITION, AttributeUtil.getPrimitiveType(node));
		return primitiveTypeNode;
	}
	
	@Override
	public boolean undoDnD(DnDParams params, AvroNodeAttributesValidators validators) {

		AvroNode sourceParent = params.getAvroNode(DnDParams.SOURCE_PARENT);
		AvroNode movedNode = params.getSourceNode();
		AvroNode currentParentNode = movedNode.getParent();
		
		unlinkNodes(currentParentNode, movedNode, ModelConstants.UNREGISTER);
		
		if (params.isObjectDefined(INITIAL_TARGET_PRIMITIVE_TYPE)) {
			PrimitiveType primitiveType = (PrimitiveType) params.getObject(INITIAL_TARGET_PRIMITIVE_TYPE);
			AttributeUtil.setPrimitiveType(currentParentNode, primitiveType);
		}
		
		if (params.isObjectDefined(OPTIONAL_TARGET_PRIMITIVE_TYPE)) {
			PrimitiveType primitiveType = (PrimitiveType) params.getObject(OPTIONAL_TARGET_PRIMITIVE_TYPE);
			createAndLinkPrimitiveTypeNode(currentParentNode, ModelConstants.LAST_POSITION, primitiveType);
		}
		
		if (params.isAvroNodeDefined(SOURCE_PRIMITIVE_TYPE_NODE)) {
			AvroNode primitiveTypeNode = params.getAvroNode(SOURCE_PRIMITIVE_TYPE_NODE);
			AvroNode unionNode = primitiveTypeNode.getParent();
			unlinkNodes(unionNode, primitiveTypeNode, ModelConstants.NONE);
			linkNodes(unionNode, movedNode, ModelConstants.LAST_POSITION, ModelConstants.REGISTER);
			if (params.isBooleanDefined(CONVERTED_CHOICE_TYPE)) {
				AttributeUtil.setAttributeValue(unionNode, AvroAttributes.CHOICE_TYPE, true);
			}
		} else {
			if (params.isAvroNodeDefined(UNION_PARENT_NODE)) {
				// source parent was a choice node, we have to add it again
				AvroNode unionParentNode = params.getAvroNode(UNION_PARENT_NODE);
				linkNodes(unionParentNode, sourceParent, ModelConstants.REGISTER);
			}
			linkNodes(sourceParent, movedNode, ModelConstants.REGISTER);
		}
		
		validators.validate(sourceParent, AvroAttributes.PRIMITIVE_TYPE);
		validators.validate(currentParentNode, AvroAttributes.PRIMITIVE_TYPE);
		
		return true;
	}
	
}
