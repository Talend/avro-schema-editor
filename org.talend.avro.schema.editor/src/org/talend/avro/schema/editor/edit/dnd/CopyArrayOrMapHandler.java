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

public class CopyArrayOrMapHandler extends AbstractCopyHandler {	
	
	public CopyArrayOrMapHandler(AvroContext context, CopyEngine copyEngine) {
		super(context, copyEngine);	
	}

	@Override
	public DnDParams executeDnD(AvroNode sourceNode, AvroNode targetNode, TargetPosition position,
			AvroNodeAttributesValidators validators) {
		
		// make the copy		
		AvroNode nodeCopy = copyElement(sourceNode, targetNode, position);
		
		// insert the copy at the target location
		BaseDnDParams params = BaseDnDParams.getParams(sourceNode, targetNode, position);
		
		params.storeAvroNode(DnDParams.COPY_NODE, nodeCopy);
		
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
						// TODO
					} else {
						// simple optional case
						AvroNode notNullChild = ModelUtil.getFirstNotNullChild(unionNode);
						// this not null child must be a primitive type one
						if (notNullChild.getType() == NodeType.PRIMITIVE_TYPE) {
							// replace this primitive type node by the moved one
							PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(notNullChild);
							params.storeObject(OPTIONAL_TARGET_PRIMITIVE_TYPE, primitiveType);
							unlinkNodes(unionNode, notNullChild, ModelConstants.NONE);
							linkNodes(unionNode, nodeCopy, ModelConstants.LAST_POSITION, ModelConstants.REGISTER);
						} else {
							// should not happen
							throw new IllegalArgumentException("Cannot copy array/map node on this target node");
						}
					}
				} else {
					// should not happen
					throw new IllegalArgumentException("Cannot copy array/map node on this target node");
				}
			} else {
				PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(targetNode);
				params.storeObject(INITIAL_TARGET_PRIMITIVE_TYPE, primitiveType);
				linkNodes(targetNode, nodeCopy, ModelConstants.REGISTER);
			}
			break;
		case UNION:
			// TODO multi choice case
			break;
		default:
			// should not happen
			throw new IllegalArgumentException("Cannot copy array/map node on this target node");
		}
		
		validators.validate(targetNode, AvroAttributes.PRIMITIVE_TYPE);
		
		return params;
	}

	@Override
	public boolean undoDnD(DnDParams params, AvroNodeAttributesValidators validators) {

		AvroNode copy = params.getAvroNode(DnDParams.COPY_NODE);
		AvroNode currentParent = copy.getParent();
		
		unlinkNodes(currentParent, copy, ModelConstants.UNREGISTER);
		
		if (params.isObjectDefined(INITIAL_TARGET_PRIMITIVE_TYPE)) {
			PrimitiveType primitiveType = (PrimitiveType) params.getObject(INITIAL_TARGET_PRIMITIVE_TYPE);
			AttributeUtil.setPrimitiveType(currentParent, primitiveType);
		}
		
		if (params.isObjectDefined(OPTIONAL_TARGET_PRIMITIVE_TYPE)) {
			PrimitiveType primitiveType = (PrimitiveType) params.getObject(OPTIONAL_TARGET_PRIMITIVE_TYPE);
			createAndLinkPrimitiveTypeNode(currentParent, ModelConstants.LAST_POSITION, primitiveType);
		}
		
		validators.validate(currentParent, AvroAttributes.PRIMITIVE_TYPE);
		
		return true;
		
	}

}
