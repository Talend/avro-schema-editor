package org.talend.avro.schema.editor.edit.validator;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.FieldNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * This attributes validator performs some checks on the PrimitiveType attributes.
 * 
 * @author timbault
 *
 */
public class PrimitiveTypeValidator implements AvroNodeAttributesValidator {

	@Override
	public void validate(AvroNode node) {
		validatePrimitiveTypes(node);
	}

	protected void validatePrimitiveTypes(AvroNode node) {
		FieldNode validationNode = ModelUtil.getFirstParentOfType(node, true, FieldNode.class);
		if (validationNode != null) {
			validatePrimitiveType(validationNode);
		}
	}
	
	protected void validatePrimitiveType(AvroNode node) {
		NodeType type = node.getType();		
		switch (type) {
		case FIELD:
			boolean isTypedNodeOfPrimitiveType = ModelUtil.isTypedNodeOfPrimitiveType(node);
			setPrimitiveTypeAttrEnabled(node, isTypedNodeOfPrimitiveType);
			if (!isTypedNodeOfPrimitiveType) {
				PrimitiveType primitiveType = ModelUtil.getPrimitiveTypeFromChildren(node);
				if (primitiveType != null) {
					AttributeUtil.setPrimitiveType(node, primitiveType);
				}
			}
			// if it is a choice-type field, its type must be synchronized with the first primitive type found in its children
			if (ModelUtil.isChoiceType(node)) {
				UnionNode unionNode = (UnionNode) node.getChild(0);
				AvroNode firstNotNullPrimitiveTypeChild = ModelUtil.getFirstNotNullPrimitiveTypeChild(unionNode);
				if (firstNotNullPrimitiveTypeChild != null) {
					PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(firstNotNullPrimitiveTypeChild);
					AttributeUtil.setPrimitiveType(node, primitiveType);
				}
			}
			validatePrimitiveTypeChildren(node);
			break;
		case ARRAY:
		case MAP:
			isTypedNodeOfPrimitiveType = ModelUtil.isTypedNodeOfPrimitiveType(node);
			setPrimitiveTypeAttrEnabled(node, isTypedNodeOfPrimitiveType);
			if (!isTypedNodeOfPrimitiveType) {
				PrimitiveType primitiveType = ModelUtil.getPrimitiveTypeFromChildren(node);
				if (primitiveType != null) {
					AttributeUtil.setPrimitiveType(node, primitiveType);
				}
			}
			validatePrimitiveTypeChildren(node);
			break;
		case PRIMITIVE_TYPE:
			if (!ModelUtil.isNullNode(node)) {
				// parent of primitive type node is necessarily an union node
				UnionNode unionNode = (UnionNode) node.getParent();
				// check if it is an optional or/and multi choice case
				boolean optional = ModelUtil.hasNullChild(unionNode);
				boolean choiceType = AttributeUtil.isChoiceType(unionNode);
				if (optional && !choiceType) {
					// change the type of this node
					AvroNode pTypedParent = ModelUtil.getClosestParentWithAttribute(unionNode, AvroAttributes.PRIMITIVE_TYPE);
					if (pTypedParent != null) {
						PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(pTypedParent);
						AttributeUtil.setPrimitiveType(node, primitiveType);
					}
				}
			}
			break;
		default:
			validatePrimitiveTypeChildren(node);
			break;
		}
	}
	
	protected void validatePrimitiveTypeChildren(AvroNode node) {
		for (int i = 0; i < node.getChildrenCount(); i++) {
			validatePrimitiveType(node.getChild(i));
		}
	}
	
	protected void setPrimitiveTypeAttrEnabled(AvroNode node, boolean enabled) {
		if (!AttributeUtil.hasPrimitiveTypeAttribute(node)) {
			throw new IllegalArgumentException("Node " + node + " has not primitive type");
		}
		AttributeUtil.setAttributeEnabled(node, AvroAttributes.PRIMITIVE_TYPE, enabled);
	}	
	
}
