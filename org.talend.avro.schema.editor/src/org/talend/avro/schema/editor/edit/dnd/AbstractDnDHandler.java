package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.AvroSchemaController;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicy.Action;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicy.DnDHandler;
import org.talend.avro.schema.editor.edit.validator.AvroNodeAttributesValidators;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.ModelConstants;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.PrimitiveTypeNode;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * Base abstract implementation of a {@link DnDHandler}.
 * 
 * @author timbault
 * @see DragAndDropPolicy
 *
 */
public abstract class AbstractDnDHandler implements DnDHandler {

	private AvroContext context;

	protected static final String OPTIONAL_TARGET_PRIMITIVE_TYPE = "OptionalTargetPrimitiveType"; //$NON-NLS-1$
	
	protected static final String INITIAL_TARGET_PRIMITIVE_TYPE = "InitialTargetPrimitiveType"; //$NON-NLS-1$
	
	protected static final String TARGET_OPTIONAL_UNION_NODE = "TargetOptionalUnionNode"; //$NON-NLS-1$
	
	protected static final DragAndDropPolicy.Action MOVE_ACTION = Action.MOVE;
	
	protected static final DragAndDropPolicy.Action COPY_ACTION = Action.COPY;
	
	protected static final DragAndDropPolicy.Action REF_ACTION = Action.REFERENCE;
	
	protected AbstractDnDHandler(AvroContext context) {
		super();
		this.context = context;
	}

	protected AvroContext getContext() {
		return context;
	};
	
	protected AvroSchemaController getController() {
		return context.getService(AvroSchemaController.class);
	}
	
	protected void linkNodes(AvroNode parentNode, AvroNode childNode, int index, int policy) {
		ModelUtil.linkNodes(getContext(), parentNode, childNode, index, policy);
	}
	
	protected void linkNodes(AvroNode parentNode, AvroNode childNode, int policy) {
		ModelUtil.linkNodes(getContext(), parentNode, childNode, policy);
	}
	
	protected void linkNodes(AvroNode parentNode, AvroNode childNode, AvroNode targetNode, TargetPosition position, int policy) {
		ModelUtil.linkNodes(getContext(), parentNode, childNode, targetNode, position, policy);
	}
	
	protected void unlinkNodes(AvroNode parentNode, AvroNode childNode, int policy) {
		ModelUtil.unlinkNodes(getContext(), parentNode, childNode, policy);
	}
	
	protected AttributeInitializer getAttributeInitializer() {
		return context.getService(AvroSchemaController.class).getAttributeInitializer();
	}
	
	protected UnionNode createAndLinkUnionNode(AvroNode parentNode, int policy) {
		UnionNode unionNode = new UnionNode(getContext());
		unionNode.init(getAttributeInitializer());
		linkNodes(parentNode, unionNode, policy);
		return unionNode;
	}
	
	protected PrimitiveTypeNode createAndLinkPrimitiveTypeNode(AvroNode targetNode, int index, PrimitiveType type) {
		PrimitiveTypeNode primitiveTypeNode = createPrimitiveTypeNode();
		AttributeUtil.setPrimitiveType(primitiveTypeNode, type);
		linkNodes(targetNode, primitiveTypeNode, index, ModelConstants.NONE);		
		return primitiveTypeNode;
	}
	
	protected PrimitiveTypeNode createPrimitiveTypeNode() {
		PrimitiveTypeNode primTypeNode = new PrimitiveTypeNode(getContext());
		primTypeNode.init(getAttributeInitializer());
		return primTypeNode;
	}
	
	protected void validate(AvroNode node, AvroNodeAttributesValidators validators) {
		validators.validateAll(node);
	}
	
	protected void storeOriginalPosition(AvroNode node, BaseDnDParams params) {
		AvroNode parentNode = node.getParent();
		int sourceIndex = parentNode.getChildIndex(node);
		AvroNode sourceRefNode = null;
		TargetPosition sourceRefNodePos = null;
		if (sourceIndex > 0) {
			sourceRefNode = parentNode.getChild(sourceIndex - 1);
			sourceRefNodePos = TargetPosition.BEFORE;
		} else if (parentNode.getChildrenCount() > 1) {
			sourceRefNode = parentNode.getChild(1);
			sourceRefNodePos = TargetPosition.AFTER;
		}
		if (sourceRefNode != null) {
			params.storeAvroNode(DnDParams.SOURCE_REF_NODE, sourceRefNode);
			params.storeObject(DnDParams.SOURCE_REF_NODE_POSITION, sourceRefNodePos);
		}		
	}
		
	protected void raiseException(DragAndDropPolicy.Action action) {
		throw new IllegalArgumentException("Cannot " + action.toString().toLowerCase() + " source node on this target node");
	}
	
}
