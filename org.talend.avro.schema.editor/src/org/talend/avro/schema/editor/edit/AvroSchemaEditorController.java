package org.talend.avro.schema.editor.edit;

import java.util.ArrayList;
import java.util.List;

import org.talend.avro.schema.editor.context.AbstractContextualService;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.dnd.AvroSchemaCopyEngine;
import org.talend.avro.schema.editor.edit.dnd.BaseDnDParams;
import org.talend.avro.schema.editor.edit.dnd.CopyEngine;
import org.talend.avro.schema.editor.edit.dnd.CopyStrategy;
import org.talend.avro.schema.editor.edit.dnd.CopyStrategyProvider;
import org.talend.avro.schema.editor.edit.dnd.DefaultDnDContext;
import org.talend.avro.schema.editor.edit.dnd.DnDContext;
import org.talend.avro.schema.editor.edit.dnd.DnDParams;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicy;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicy.Action;
import org.talend.avro.schema.editor.edit.dnd.DragAndDropPolicyConfiguration;
import org.talend.avro.schema.editor.edit.validator.AvroNodeAttributesValidators;
import org.talend.avro.schema.editor.model.ArrayNode;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.EnumNode;
import org.talend.avro.schema.editor.model.FieldNode;
import org.talend.avro.schema.editor.model.FixedNode;
import org.talend.avro.schema.editor.model.MapNode;
import org.talend.avro.schema.editor.model.ModelConstants;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.PrimitiveTypeNode;
import org.talend.avro.schema.editor.model.RecordNode;
import org.talend.avro.schema.editor.model.RefNode;
import org.talend.avro.schema.editor.model.RefNodeImpl;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.model.cmd.Direction;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * Default implementation of an {@link AvroSchemaController} for the standard avro schema editor.
 * <p>
 * 
 * 
 * @author timbault
 *
 */
public class AvroSchemaEditorController extends AbstractContextualService implements AvroSchemaController {
	
	private AttributeInitializer attributeInitializer;
	
	private AvroNodeAttributesValidators validators;
	
	private DragAndDropPolicy dndPolicy;
	
	private DragAndDropPolicyConfiguration dragAndDropPolicyConfiguration;
		
	private CopyStrategyProvider copyStrategyProvider;
	
	private CopyEngine copyEngine;
	
	public AvroSchemaEditorController() {
		super();		
	}	
	
	@Override
	public void setAvroNodeValidators(AvroNodeAttributesValidators validators) {
		this.validators = validators;
		this.dndPolicy = new DragAndDropPolicy(validators);
	}

	@Override
	public void setAttributeInitializer(AttributeInitializer attributeInitializer) {
		this.attributeInitializer = attributeInitializer;
	}
	
	public AttributeInitializer getAttributeInitializer() {
		return attributeInitializer;
	}

	@Override
	public void setDragAndDropPolicyConfiguration(DragAndDropPolicyConfiguration dndPolicyConfig) {
		this.dragAndDropPolicyConfiguration = dndPolicyConfig;
		dndPolicy.clearAll();
		configureDragAndDropPolicy(dndPolicy);
	}

	@Override
	public void setCopyStrategyProvider(CopyStrategyProvider copyStrategyProvider) {
		this.copyStrategyProvider = copyStrategyProvider;
	}
	
	@Override
	public CopyStrategyProvider getCopyStrategyProvider() {
		return copyStrategyProvider;
	}

	protected boolean validateAttributes(int policy) {
		return (policy & ModelConstants.VALIDATE) != 0;
	}
	
	@Override
	public void init(AvroContext context) {
		super.init(context);
		this.copyEngine = createCopyEngine(context);
		configureDragAndDropPolicy(dndPolicy);
	}
	
	protected CopyEngine createCopyEngine(AvroContext context) {
		return new AvroSchemaCopyEngine(context);
	}
	
	protected void configureDragAndDropPolicy(DragAndDropPolicy dndPolicy) {
		if (dragAndDropPolicyConfiguration != null) {
			dragAndDropPolicyConfiguration.configureDragAndDropPolicy(dndPolicy);
		}
	}

	protected AttributeInitializer getAttrInitializer() {
		return attributeInitializer;
	}
	
	//************************
	// CAN ADD ELEMENT (type)
	//************************

	@Override
	public boolean canAddElement(AvroNode targetNode, NodeType type, TargetPosition position) {
		switch (type) {
		case FIELD:
			return canAddField(targetNode);
		case RECORD:
			return canAddRecord(targetNode);
		case ARRAY:
			return canAddArrayOrMap(targetNode, NodeType.ARRAY);
		case MAP:
			return canAddArrayOrMap(targetNode, NodeType.MAP);
		case ENUM:
			return canAddEnum(targetNode);
		case FIXED:
			return canAddFixed(targetNode);
		case PRIMITIVE_TYPE:
			return canAddPrimitiveType(targetNode, position);
		case UNION:
			return canAddChoice(targetNode);
		default:
			return false;
		}
	}
	
	protected boolean canAddChoice(AvroNode targetNode) {
		NodeType targetType = targetNode.getType();
		switch (targetType) {
		case FIELD:
		case MAP:
		case ARRAY:
			// it is always possible to add a choice node under a field/map/array node, unless it has already a choice node of course
			if (targetNode.hasChildren(NodeType.UNION)) {
				// if it is an union node for optional case, we can add choice of course
				UnionNode unionNode = (UnionNode) targetNode.getChild(0);
				return !AttributeUtil.isChoiceType(unionNode);
			} else {
				return true;
			}
		default:
			return false;
		}
	}
	
	protected boolean canAddPrimitiveType(AvroNode targetNode, TargetPosition position) {
		NodeType targetType = targetNode.getType();
		switch (targetType) {
		case FIELD:
		case MAP:
		case ARRAY:
			checkTargetPosition(position, TargetPosition.UPON);
			if (targetNode.hasChildren(NodeType.UNION)) {
				UnionNode unionNode = (UnionNode) targetNode.getChild(0);
				if (AttributeUtil.isChoiceType(unionNode)) {
					// we have to check if there is still a place for a  new primitive type
					// (as it is not possible to have twice the same primitive type)
					List<PrimitiveType> freePrimitiveTypes = ModelUtil.getFreePrimitiveTypes(unionNode, false);				
					return !freePrimitiveTypes.isEmpty();
				}				 
			}
			return false;
		case UNION:
			UnionNode unionNode = (UnionNode) targetNode;
			return AttributeUtil.isChoiceType(unionNode);
		case PRIMITIVE_TYPE:
			//checkTargetPosition(position, TargetPosition.RELATIVE_POSITIONS);
			AvroNode parent = targetNode.getParent();
			if (parent.getType() == NodeType.UNION) {
				// parent should be always an union node
				unionNode = (UnionNode) parent;
				List<PrimitiveType> freePrimitiveTypes = ModelUtil.getFreePrimitiveTypes(unionNode, false);
				return !freePrimitiveTypes.isEmpty();
			}
			return false;
		default:
			return false;
		}
	}
	
	protected boolean canAddFixed(AvroNode targetNode) {
		return canAddNameSpacedType(targetNode);
	}
	
	protected boolean canAddEnum(AvroNode targetNode) {
		return canAddNameSpacedType(targetNode);
	}
	
	protected boolean canAddRecord(AvroNode targetNode) {
		return canAddNameSpacedType(targetNode);
	}	
	
	protected boolean canAddNameSpacedType(AvroNode targetNode) {
		NodeType targetType = targetNode.getType();
		switch (targetType) {
		case ROOT:
			return true;
		case FIELD:		
			if (targetNode.hasChildren()) {
				// a field element has only one child
				AvroNode childNode = targetNode.getChild(0);
				NodeType childType = childNode.getType();
				switch (childType) {
				case RECORD:
				case FIXED:
				case ENUM:
					// the field element has already a namespaced child, we cannot add another one
					return false;
				case MAP:
				case ARRAY:			
				case UNION:
					return canAddNameSpacedType(childNode);
				default:
					return false;
				}
			} else {
				// no children, it is a primitive type field
				return true;
			}		
		case ARRAY:
		case MAP:
			if (targetNode.hasChildren()) {
				// a map/array element has only one child
				AvroNode childNode = targetNode.getChild(0);
				NodeType childType = childNode.getType();
				switch (childType) {
				case RECORD:
				case FIXED:
				case ENUM:
					// the map/array element has already a namespaced child, we cannot add another one
					return false;
				case MAP:
				case ARRAY:
					return false;
				case UNION:
					return canAddNameSpacedType(childNode);
				default:
					return false;
				}
			} else {
				// no children, it is a primitive type map/array
				return true;
			}		
		case UNION:
			// check if it is a multi-choice or not
			if (AttributeUtil.isChoiceType(targetNode)) {
				// we can add a new namespaced element
				return true;
			} else {				
				AvroNode notNullChild = ModelUtil.getFirstNotNullChild((UnionNode) targetNode);
				return notNullChild.getType() == NodeType.PRIMITIVE_TYPE;
			}			
		default:
			return false;
		}
	}
	
	protected boolean canAddField(AvroNode targetNode) {
		NodeType type = targetNode.getType();
		return type == NodeType.RECORD 
				|| type == NodeType.FIELD
				|| (type == NodeType.REF && ((RefNode) targetNode).getReferencedNode().getType() == NodeType.RECORD);
	}
	
	protected boolean canAddArrayOrMap(AvroNode targetNode, NodeType arrayOrMapType) {
		ModelUtil.checkType(arrayOrMapType, NodeType.ARRAY_OR_MAP);
		NodeType targetType = targetNode.getType();
		switch (targetType) {
		case MAP:
		case ARRAY:
		case FIELD:
			if (targetNode.hasChildren()) {
				// only one child
				AvroNode child = targetNode.getChild(0);
				NodeType childType = child.getType();
				switch (childType) {
				case RECORD:
				case REF:
				case FIXED:
				case ENUM:
					return true;
				case MAP:
				case ARRAY:
					return false;
				case UNION:
					UnionNode unionNode = (UnionNode) child;
					if (!AttributeUtil.isChoiceType(child)) {
						// optional case
						AvroNode firstNotNullChild = ModelUtil.getFirstNotNullChild(unionNode);
						NodeType notNullType = firstNotNullChild.getType();
						switch (notNullType) {
						case ENUM:
						case FIXED:
						case RECORD:
						case REF:
						case PRIMITIVE_TYPE:
							return true;
						case MAP:
						case ARRAY:
							return false;
						default:
							return false;
						}
					} else {
						return canAddArrayOrMap(unionNode, arrayOrMapType);
					}
				default:
					return false;
				}
			}
			return true;
		case UNION:
			UnionNode unionNode = (UnionNode) targetNode;
			if (AttributeUtil.isChoiceType(unionNode)) {
				// check that there is not already an array or map node under this union node
				return !unionNode.hasChildren(arrayOrMapType);
			}
			return false;
		default:
			return false;
		}
	}
		
	//************************
	// ADD ELEMENT (type)
	//************************
	
	@Override
	public AvroNode addElement(AvroNode targetNode, NodeType type, TargetPosition position) {
		switch (type) {
		case FIELD:
			return addField(targetNode, position);
		case RECORD:
			return addRecord(targetNode, position);
		case ARRAY:
			return addArrayOrMap(targetNode, NodeType.ARRAY, position);
		case MAP:
			return addArrayOrMap(targetNode, NodeType.MAP, position);
		case PRIMITIVE_TYPE:
			return addPrimitiveType(targetNode, position);
		case ENUM:
			return addEnum(targetNode, position);
		case FIXED:
			return addFixed(targetNode, position);
		case UNION:
			return addChoice(targetNode, position);
		default:
			throw new UnsupportedOperationException(
					"Cannot add element " + type + " on " + targetNode.getType() + " node " + targetNode.toString());
		}
	}
	
	public UnionNode addChoice(AvroNode targetNode, TargetPosition position) {
		ModelUtil.checkNodeTypes(targetNode, NodeType.TYPED_NODE_TYPES);		
		if (targetNode.hasChildren(NodeType.UNION)) {
			throw new IllegalStateException("Cannot add choice since the target node has already an union node. Use setChoice method instead.");			
		}
		// create an union node
		UnionNode choiceNode = createUnionNode();			
		// set choice nature
		AttributeUtil.setAttributeValue(choiceNode, AvroAttributes.CHOICE_TYPE, true);
		return addChoiceNode(targetNode, choiceNode, position, ModelConstants.REGISTER_AND_VALIDATE);
	}
	
	public void setChoice(UnionNode unionNode, boolean choice) {
		AttributeUtil.checkAttributeValue(unionNode, AvroAttributes.CHOICE_TYPE, !choice);
		AttributeUtil.setAttributeValue(unionNode, AvroAttributes.CHOICE_TYPE, choice);		
	}
	
	public FixedNode addFixed(AvroNode targetNode, TargetPosition position) {
		FixedNode fixedNode = createFixedNode();
		return addFixedNode(targetNode, fixedNode, position, ModelConstants.REGISTER_AND_VALIDATE);
	}
	
	protected FixedNode createFixedNode() {
		FixedNode fixedNode = new FixedNode(getContext());
		fixedNode.init(attributeInitializer);
		return fixedNode;
	}
	
	public EnumNode addEnum(AvroNode targetNode, TargetPosition position) {		
		EnumNode enumNode = createEnumNode();
		return addEnumNode(targetNode, enumNode, position, ModelConstants.REGISTER_AND_VALIDATE);
	}
	
	protected EnumNode createEnumNode() {
		EnumNode enumNode = new EnumNode(getContext());
		enumNode.init(attributeInitializer);
		return enumNode;
	}
	
	public PrimitiveTypeNode addPrimitiveType(AvroNode targetNode, TargetPosition position) {
		PrimitiveTypeNode primitiveTypeNode = createPrimitiveTypeNode();
		UnionNode unionNode = null;
		NodeType targetType = targetNode.getType();
		String targetName = AttributeUtil.getNameFromAttribute(targetNode);
		switch (targetType) {
		case UNION:
			unionNode = (UnionNode) targetNode;
			break;
		case FIELD:
		case MAP:
		case ARRAY:
			unionNode = (UnionNode) targetNode.getChild(0);
			break;
		case PRIMITIVE_TYPE:
			unionNode = (UnionNode) targetNode.getParent();
			break;
		default:
			throw new UnsupportedOperationException("Cannot add a primitive type on target node " + targetName);
		}
		List<PrimitiveType> freePrimitiveTypes = ModelUtil.getFreePrimitiveTypes(unionNode, false);
		if (freePrimitiveTypes.isEmpty()) {
			throw new UnsupportedOperationException("Cannot add primitive type anymore on target node " + targetName);
		}
		PrimitiveType primitiveType = freePrimitiveTypes.get(0);
		AttributeUtil.setPrimitiveType(primitiveTypeNode, primitiveType);
		return addPrimitiveTypeNode(targetNode, primitiveTypeNode, position, ModelConstants.REGISTER_AND_VALIDATE);
	}
	
	protected PrimitiveTypeNode createPrimitiveTypeNode() {
		return createPrimitiveTypeNode(null);
	}
	
	protected PrimitiveTypeNode createPrimitiveTypeNode(PrimitiveType type) {
		PrimitiveTypeNode primTypeNode = new PrimitiveTypeNode(getContext());
		primTypeNode.init(attributeInitializer);
		if (type != null) {
			AttributeUtil.setPrimitiveType(primTypeNode, type);
		}
		return primTypeNode;
	}
	
	protected PrimitiveTypeNode createAndLinkPrimitiveTypeNode(AvroNode targetNode, int index) {
		return createAndLinkPrimitiveTypeNode(targetNode, index, null);
	}
	
	protected PrimitiveTypeNode createAndLinkPrimitiveTypeNode(AvroNode targetNode, int index, PrimitiveType type) {
		PrimitiveTypeNode primitiveTypeNode = createPrimitiveTypeNode(type);
		linkNodes(targetNode, primitiveTypeNode, index, ModelConstants.REGISTER);		
		return primitiveTypeNode;
	}
	
	public FieldNode addField(AvroNode targetNode, TargetPosition position) {
		FieldNode fieldNode = new FieldNode(getContext());
		fieldNode.init(attributeInitializer);
		return addFieldNode(targetNode, fieldNode, position, ModelConstants.REGISTER_AND_VALIDATE);		
	}
	
	public RecordNode addRecord(AvroNode targetNode, TargetPosition position) {
		RecordNode recordNode = new RecordNode(getContext());
		recordNode.init(attributeInitializer);
		return addRecordNode(targetNode, recordNode, position, ModelConstants.REGISTER_AND_VALIDATE);
	}
	
	protected UnionNode createUnionNode() {
		UnionNode unionNode = new UnionNode(getContext());
		unionNode.init(attributeInitializer);
		return unionNode;
	}
	
	protected UnionNode addUnionNode(AvroNode parentNode, int policy) {
		UnionNode unionNode = createUnionNode();
		linkNodes(parentNode, unionNode, policy);
		return unionNode;
	}
	
	protected AvroNode createArrayOrMapNode(NodeType type) {
		if (type == NodeType.ARRAY) {
			return createArrayNode();
		} else if (type == NodeType.MAP) {
			return createMapNode(); 
		} else {
			throw new IllegalArgumentException("Invalid node type");
		}
	}
	
	protected ArrayNode createArrayNode() {
		ArrayNode arrayNode = new ArrayNode(getContext());
		arrayNode.init(attributeInitializer);
		return arrayNode;
	}
	
	protected MapNode createMapNode() {
		MapNode mapNode = new MapNode(getContext());
		mapNode.init(attributeInitializer);
		return mapNode;
	}
	
	protected AvroNode addArrayOrMapNode(AvroNode targetNode, AvroNode arrayOrMapNode, TargetPosition position, int policy) {
		NodeType targetType = targetNode.getType();
		AvroNode targetParent = targetNode.getParent();
		switch (targetType) {
		case FIELD:
		case MAP:
		case ARRAY:
			if (targetNode.hasChildren()) {
				// field, map and array node can have only one child				
				AvroNode child = targetNode.getChild(0);
				NodeType childType = child.getType();
				switch (childType) {
				case RECORD:
				case FIXED:
				case ENUM:
				case UNION:
					addArrayOrMapNode(child, arrayOrMapNode, TargetPosition.UPON, policy);
					break;
				default:
					throw new IllegalArgumentException("Invalid target node");
				}
			} else {
				// just add array node
				linkNodes(targetNode, arrayOrMapNode, policy);
			}
			break;
		case RECORD:
		case FIXED:
		case ENUM:			
			// insert the array node between the target node and its parent
			insertNode(targetParent, targetNode, arrayOrMapNode, policy);			
			break;
		case UNION:
			UnionNode unionNode = (UnionNode) targetNode;
			if (AttributeUtil.isChoiceType(unionNode)) {
				if (unionNode.hasChildren(arrayOrMapNode.getType())) {
					throw new IllegalArgumentException("Choice has already an array/map node, cannot add another one");
				}
				linkNodes(unionNode, arrayOrMapNode, policy);
			} else {
				// "optionl" use case
				AvroNode notNullChild = ModelUtil.getFirstNotNullChild(unionNode);
				addArrayOrMapNode(notNullChild, arrayOrMapNode, position, policy);
			}
			break;
		case PRIMITIVE_TYPE:
			// should happen only on primitive type node under union node (see case UNION above)
			if (targetParent.getType() == NodeType.UNION) {
				if (AttributeUtil.isChoiceType(targetParent)) {
					// TODO
				} else {
					// optional case
					// we have to replace the primitive type node by the map/array node
					unlinkNodes(targetParent, targetNode, ModelConstants.UNREGISTER);
					linkNodes(targetParent, arrayOrMapNode, policy);
				}
			}
			break;
		default:
			throw new IllegalArgumentException("Invalid target type");
		}		
		validateAttributes(arrayOrMapNode, policy);
		return arrayOrMapNode;
	}
	
	public AvroNode addArrayOrMap(AvroNode targetNode, NodeType type, TargetPosition position) {
		AvroNode arrayOrMapNode = createArrayOrMapNode(type);
		// initialize the type of the new map/array
		if (ModelUtil.isTyped(targetNode) && ModelUtil.isTypedNodeOfPrimitiveType(targetNode)) {
			PrimitiveType primitiveType = ModelUtil.getPrimitiveTypeOfTypedNode(targetNode);
			AttributeUtil.setPrimitiveType(arrayOrMapNode, primitiveType);
		}
		return addArrayOrMapNode(targetNode, arrayOrMapNode, position, ModelConstants.REGISTER_AND_VALIDATE);
	}
	
	//************************
	// ADD ELEMENT (node)
	//************************
	
	@Override
	public AvroNode addElement(AvroNode targetNode, AvroNode elementNode, TargetPosition position) {
		checkNodeClass(elementNode);
		NodeType type = elementNode.getType();
		switch (type) {
		case FIELD:
			return addFieldNode(targetNode, (FieldNode) elementNode, position, ModelConstants.REGISTER_AND_VALIDATE);
		case RECORD:
			return addRecordNode(targetNode, (RecordNode) elementNode, position, ModelConstants.REGISTER_AND_VALIDATE);
		case ARRAY:			
		case MAP:
			return addArrayOrMapNode(targetNode, elementNode, position, ModelConstants.REGISTER_AND_VALIDATE);
		case PRIMITIVE_TYPE:
			return addPrimitiveTypeNode(targetNode, (PrimitiveTypeNode) elementNode, position, ModelConstants.REGISTER_AND_VALIDATE);
		case ENUM:
			return addEnumNode(targetNode, (EnumNode) elementNode, position, ModelConstants.REGISTER_AND_VALIDATE);
		case FIXED:
			return addFixedNode(targetNode, (FixedNode) elementNode, position, ModelConstants.REGISTER_AND_VALIDATE);
		case REF:
			return addRefNode(targetNode, (RefNode) elementNode, position, ModelConstants.REGISTER_AND_VALIDATE);
		case UNION:
			return addChoiceNode(targetNode, (UnionNode) elementNode, position, ModelConstants.REGISTER_AND_VALIDATE);
		default:
			throw new UnsupportedOperationException(
					"Cannot add " + elementNode.getType() + " element " + elementNode +
					" on " + targetNode.getType() + " node " + targetNode);
		}
	}

	protected UnionNode addChoiceNode(AvroNode targetNode, UnionNode choiceNode, TargetPosition position, int policy) {
		AttributeUtil.checkAttributeValue(choiceNode, AvroAttributes.CHOICE_TYPE, true);
		ModelUtil.checkNodeTypes(targetNode, NodeType.TYPED_NODE_TYPES);
		if (targetNode.hasChildren()) {
			// complex case
			// only one child
			AvroNode child = targetNode.getChild(0);
			NodeType childType = child.getType();
			switch (childType) {
			case RECORD:
			case REF:
			case ENUM:
			case FIXED:
			case MAP:
			case ARRAY:
				// not so difficult case
				// we have simply to insert choice node between target and child nodes
				insertNode(targetNode, child, choiceNode, policy);
				break;
			case UNION:
				// a little bit more difficult case
				UnionNode unionNode = (UnionNode) child;
				// it most case it should be the choice node
				// in this case we have nothing to do
				if (unionNode != choiceNode) {
					// it could happen
					// so we have to handle it by replacing the existing union node by the new one
					unlinkNodes(targetNode, unionNode, ModelConstants.UNREGISTER);
					linkNodes(targetNode, choiceNode, policy);					
				}
				break;
			default:
				throw new IllegalArgumentException("Cannot add choice on given target node");
			}
		} else {
			// simple case
			linkNodes(targetNode, choiceNode, policy);
			// check if we have to add a primitive type node
			if (!choiceNode.hasChildren()) {
				PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(targetNode);
				PrimitiveTypeNode primitiveTypeNode = createAndLinkPrimitiveTypeNode(choiceNode, ModelConstants.FIRST_POSITION);
				AttributeUtil.setPrimitiveType(primitiveTypeNode, primitiveType);
			}
		}
		// validate
		validateAttributes(targetNode, policy);
		return choiceNode;
	}
	
	protected FixedNode addFixedNode(AvroNode targetNode, FixedNode fixedNode, TargetPosition position, int policy) {
		addNameSpacedNode(targetNode, fixedNode, position, policy);
		return fixedNode;
	}
	
	protected RefNode addRefNode(AvroNode targetNode, RefNode refNode, TargetPosition position, int policy) {
		addNameSpacedNode(targetNode, refNode, position, policy);
		return refNode;
	}
	
	protected EnumNode addEnumNode(AvroNode targetNode, EnumNode enumNode, TargetPosition position, int policy) {
		addNameSpacedNode(targetNode, enumNode, position, policy);
		return enumNode;
	}
	
	protected PrimitiveTypeNode addPrimitiveTypeNode(AvroNode targetNode, PrimitiveTypeNode typeNode, TargetPosition position, int policy) {
		NodeType targetType = targetNode.getType();
		switch (targetType) {
		case FIELD:
		case MAP:
		case ARRAY:
			UnionNode unionNode = (UnionNode) targetNode.getChild(0);
			linkNodes(unionNode, typeNode, policy);
			break;
		case UNION:			
			linkNodes(targetNode, typeNode, policy);
			break;
		case PRIMITIVE_TYPE:
			AvroNode parentNode = targetNode.getParent();
			linkNodes(parentNode, typeNode, targetNode, position, policy);
			break;
		case RECORD:
		case ENUM:
		case FIXED:
			// this case happens when the primitive type node was under a choice node with record/enum/fixed brothers.
			parentNode = targetNode.getParent();
			linkNodes(parentNode, typeNode, targetNode, position, policy);
			break;
		default:
			throw new IllegalArgumentException("Cannot add primitive type node on target " + AttributeUtil.getNameFromAttribute(targetNode));
		}		
		validateAttributes(targetNode, policy);
		return typeNode;
	}
	
	protected FieldNode addFieldNode(AvroNode targetNode, FieldNode fieldNode, TargetPosition position, int policy) {
		ModelUtil.checkNodeTypes(targetNode, NodeType.RECORD, NodeType.FIELD, NodeType.REF);
		NodeType targetType = targetNode.getType();
		if (targetType == NodeType.FIELD) {
			linkNodes(targetNode.getParent(), fieldNode, targetNode, position, policy);		
		} else if (targetType == NodeType.RECORD) {
			checkTargetPosition(position, TargetPosition.UPON);			
			linkNodes(targetNode, fieldNode, policy);			
		} else {
			RefNode refNode = (RefNode) targetNode;
			AvroNode referencedNode = refNode.getReferencedNode();			
			return addFieldNode(referencedNode, fieldNode, position, policy);
		}
		validateAttributes(fieldNode, policy);
		return fieldNode;
	}
	
	protected AvroNode addNameSpacedNode(AvroNode targetNode, AvroNode nsNode, TargetPosition position, int policy) {		
		// add record, enum, fixed or ref node
		NodeType targetType = targetNode.getType();
		switch (targetType) {
		case ROOT:
			if (targetNode.hasChildren()) {
				// the target node has already children (union or namespaced node).
				// we have to check if we need to add an union node
				UnionNode unionNode = null;				
				// need to add union node
				if (targetNode.hasChildren(NodeType.UNION)) {
					unionNode = (UnionNode) targetNode.getChild(0);
				} else {
					AvroNode child = targetNode.getChild(0);
					unlinkNodes(targetNode, child, ModelConstants.NONE);					
					// add a union node				
					unionNode = addUnionNode(targetNode, ModelConstants.NONE);
					// add this child
					linkNodes(unionNode, child, ModelConstants.NONE);
				}
				// and then add the new node
				addNameSpacedNode(unionNode, nsNode, TargetPosition.UPON, policy);		
			} else {
				// no children under the root node
				// it is not necessary to add an union node
				linkNodes(targetNode, nsNode, policy);
			}			
			break;
		case RECORD:
		case ENUM:
		case FIXED:
			// in fact we try to add again a record/enum/fixed which was a brother of another record/enum/fixed
			// there is only one use case : it is a root record, i.e. a child of root union node			
			AvroNode parent = targetNode.getParent();
			if (parent.getType() == NodeType.ROOT) {
				// we have to add an union node
				unlinkNodes(parent, targetNode, ModelConstants.NONE);
				UnionNode unionNode = addUnionNode(parent, ModelConstants.NONE);
				linkNodes(unionNode, targetNode, ModelConstants.NONE);
				linkNodes(unionNode, nsNode, targetNode, position, policy);
			} else {
				linkNodes(parent, nsNode, targetNode, position, policy);
			}
			break;
		case FIELD:
			FieldNode fieldNode = (FieldNode) targetNode;
			if (fieldNode.hasChildren()) {
				AvroNode childNode = fieldNode.getChild(0);
				addNameSpacedNode(childNode, nsNode, position, policy);
			} else {
				linkNodes(targetNode, nsNode, policy);
			}
			break;
		case ARRAY:
		case MAP:
			// there are two valid cases:
			// 1) map/array has no child
			// 2) map/array has an union node
			if (!targetNode.hasChildren()) {
				// just link nodes
				linkNodes(targetNode, nsNode, policy);
			} else {
				AvroNode child = targetNode.getChild(0);
				ModelUtil.checkNodeTypes(child, NodeType.UNION);
				UnionNode unionNode = (UnionNode) child;
				addNameSpacedNode(unionNode, nsNode, TargetPosition.UPON, policy);
			}			
			break;
		case UNION:
			UnionNode unionNode = (UnionNode) targetNode;
			if (ModelUtil.isRootUnion(unionNode)) {
				// it is the root union
				// just add the record/enum/fixed node
				linkNodes(unionNode, nsNode, policy);
			} else if (AttributeUtil.isChoiceType(unionNode)) {
				linkNodes(targetNode, nsNode, policy);
			} else {
				// optional use case
				// there is a null child				
				AvroNode notNullChild = ModelUtil.getFirstNotNullChild(unionNode);
				// check the kind of the second node
				NodeType childType = notNullChild.getType();
				switch (childType) {
				case RECORD:
				case FIXED:
				case ENUM:
					// should be forbidden
					throw new IllegalStateException("Cannot add record");
				case PRIMITIVE_TYPE:					
					// replace the primitive type node
					unlinkNodes(unionNode, notNullChild, ModelConstants.NONE);
					linkNodes(unionNode, nsNode, policy);
					break;
				case MAP:
				case ARRAY:
					addNameSpacedNode(notNullChild, nsNode, position, policy);
					break;
				default:
					throw new IllegalArgumentException("Invalid node type");
				}								
			}
			break;
		case PRIMITIVE_TYPE:
			// this record/enum/fixed was a child of an union node. This situation happens with the undo cmd.
			// so we have to add again this node			
			// the parent is necessarily an union node
			unionNode = (UnionNode) targetNode.getParent();
			addNameSpacedNode(unionNode, nsNode, position, policy);
			break;
		default:
			throw new IllegalArgumentException("Invalid target type");
		}		
		validateAttributes(nsNode, policy);
		return nsNode;
	}
	
	protected RecordNode addRecordNode(AvroNode targetNode, RecordNode recordNode, TargetPosition position, int policy) {
		addNameSpacedNode(targetNode, recordNode, position, policy);
		return recordNode;
	}
	
	//************************
	// CAN REMOVE ELEMENT
	//************************
	
	@Override
	public boolean canRemoveElement(AvroNode elementNode) {
		checkNodeClass(elementNode);
		NodeType type = elementNode.getType();
		switch (type) {
		case FIELD:
			return canRemoveField((FieldNode) elementNode);
		case RECORD:
			return canRemoveRecord((RecordNode) elementNode);
		case ENUM:
			return canRemoveEnum((EnumNode) elementNode);
		case FIXED:
			return canRemoveFixed((FixedNode) elementNode);
		case REF:
			return canRemoveRef((RefNode) elementNode);
		case ARRAY:
			return canRemoveArray((ArrayNode) elementNode);
		case MAP:
			return canRemoveMap((MapNode) elementNode);
		case PRIMITIVE_TYPE:
			return canRemovePrimitiveType((PrimitiveTypeNode) elementNode);
		case UNION:
			return canRemoveUnion((UnionNode) elementNode);
		default:
			return false;
		}
	}

	protected boolean canRemoveUnion(UnionNode unionNode) {
		return AttributeUtil.isChoiceType(unionNode);
	}
	
	protected boolean canRemovePrimitiveType(PrimitiveTypeNode primitiveTypeNode) {		
		return true;
	}
	
	protected boolean canRemoveField(FieldNode fieldNode) {
		return true;
	}
	
	protected boolean canRemoveRecord(RecordNode recordNode) {
		return true;
	}
	
	protected boolean canRemoveEnum(EnumNode enumNode) {
		return true;
	}
	
	protected boolean canRemoveFixed(FixedNode fixedNode) {
		return true;
	}
	
	protected boolean canRemoveRef(RefNode refNode) {
		return true;
	}
	
	protected boolean canRemoveArray(ArrayNode arrayNode) {
		return true;
	}
	
	protected boolean canRemoveMap(MapNode mapNode) {
		return true;
	}
	
	//************************
	// REMOVE ELEMENT
	//************************
	
	@Override
	public AvroNode removeElement(AvroNode elementNode) {
		return removeElement(elementNode, ModelConstants.UNREGISTER_AND_VALIDATE);
	}
	
	protected AvroNode removeElement(AvroNode elementNode, int policy) {
		checkNodeClass(elementNode);
		NodeType type = elementNode.getType();
		switch (type) {
		case FIELD:
			return removeField((FieldNode) elementNode, policy);
		case RECORD:
			return removeRecord((RecordNode) elementNode, policy);
		case ARRAY:
		case MAP:
			return removeArrayOrMap(elementNode, policy);
		case ENUM:
			return removeEnum((EnumNode) elementNode, policy);
		case FIXED:
			return removeFixed((FixedNode) elementNode, policy);
		case REF:
			return removeRef((RefNode) elementNode, policy);
		case PRIMITIVE_TYPE:
			return removePrimitiveType((PrimitiveTypeNode) elementNode, policy);
		case UNION:
			return removeChoice((UnionNode) elementNode, policy);
		default:
			throw new UnsupportedOperationException("Cannot remove element of type " + elementNode.getType());
		}
	}
	
	protected AvroNode removeNode(AvroNode node, int policy) {
		AvroNode parentNode = node.getParent();
		unlinkNodes(parentNode, node, policy);
		validateAttributes(parentNode, policy);
		return parentNode;
	}
	
	protected AvroNode removeChoice(UnionNode unionNode, int policy) {
		boolean isOptional = ModelUtil.hasNullChild(unionNode);
		// remove choice node
		AvroNode parentNode = unionNode.getParent();
		unlinkNodes(parentNode, unionNode, policy);
		if (isOptional) {
			// it was also an optional union node, so we have to add a new union node
			UnionNode optionalUnionNode = addUnionNode(parentNode, ModelConstants.NONE);
			// add null node
			createAndLinkPrimitiveTypeNode(optionalUnionNode, ModelConstants.FIRST_POSITION, PrimitiveType.NULL);			
			// add primitive type node
			PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(parentNode);
			createAndLinkPrimitiveTypeNode(optionalUnionNode, ModelConstants.LAST_POSITION, primitiveType);
		}
		validateAttributes(parentNode, policy);
		return parentNode;
	}
	
	protected AvroNode removePrimitiveType(PrimitiveTypeNode primitiveTypeNode, int policy) {
		AvroNode parentNode = primitiveTypeNode.getParent();
		ModelUtil.checkNodeTypes(parentNode, NodeType.UNION);		
		unlinkNodes(parentNode, primitiveTypeNode, policy);		
		validateAttributes(parentNode, policy);
		return parentNode;
	}
	
	protected AvroNode removeField(FieldNode fieldNode, int policy) {
		return removeNode(fieldNode, policy);
	}

	protected AvroNode removeEnum(EnumNode enumNode, int policy) {
		return removeNameSpacedNode(enumNode, policy);
	}
	
	protected AvroNode removeFixed(FixedNode fixedNode, int policy) {
		return removeNameSpacedNode(fixedNode, policy);
	}
	
	protected AvroNode removeNameSpacedNode(AvroNode node, int policy) {
		AvroNode parentNode = node.getParent();
		NodeType parentType = parentNode.getType();
		unlinkNodes(parentNode, node, policy);		
		if (parentType == NodeType.UNION) {			
			UnionNode unionNode = (UnionNode) parentNode;
			if (ModelUtil.isRootUnion(unionNode) && unionNode.getChildrenCount() == 1) {
				AvroNode child = unionNode.getChild(0);
				unlinkNodes(unionNode, child, ModelConstants.NONE);
				parentNode = unionNode.getParent();
				unlinkNodes(parentNode, unionNode, ModelConstants.NONE);
				linkNodes(parentNode, child, ModelConstants.NONE);
			}
		}
		validateAttributes(parentNode, policy);
		return parentNode;
	}
	
	protected AvroNode removeRef(RefNode refNode, int policy) {
		return removeNode(refNode, policy);
	}
	
	protected AvroNode removeRecord(RecordNode recordNode, int policy) {
		AvroNode parentNode = recordNode.getParent();
		unlinkNodes(parentNode, recordNode, policy);
		NodeType parentType = parentNode.getType();
		switch (parentType) {
		case FIELD:
		case ARRAY:
		case MAP:
		case ROOT:
			// nothing special to do
			break;
		case UNION:
			UnionNode unionNode = (UnionNode) parentNode;
			if (!ModelUtil.isRootUnion(unionNode) && !AttributeUtil.isChoiceType(unionNode)) {
				// simple optional case
				AvroNode unionParent = parentNode.getParent();
				NodeType unionParentType = unionParent.getType();
				switch (unionParentType) {
				case FIELD:
				case MAP:
				case ARRAY:	
					// "optional" use case
					// as we remove a record, we have to add a primitive type node
					PrimitiveTypeNode primTypeNode = createAndLinkPrimitiveTypeNode(unionNode, ModelConstants.LAST_POSITION);
					// set the type from the union parent node (which should be a field/map/array node, so a node with a primitive type attribute
					PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(unionParent);
					AttributeUtil.setPrimitiveType(primTypeNode, primitiveType);					
					break;
				default:
					throw new IllegalArgumentException("Invalid node type");
				}
			} else if (ModelUtil.isRootUnion(unionNode) && unionNode.getChildrenCount() == 1) {
				// only one child under this root union node
				AvroNode child = unionNode.getChild(0);
				unlinkNodes(unionNode, child, ModelConstants.NONE);
				parentNode = unionNode.getParent();
				unlinkNodes(parentNode, unionNode, ModelConstants.NONE);
				linkNodes(parentNode, child, ModelConstants.NONE);
			}
			break;
		default:
			throw new IllegalArgumentException("Invalid parent type");
		}
		validateAttributes(parentNode, policy);
		return parentNode;
	}
	
	protected AvroNode removeArrayOrMap(AvroNode arrayOrMapNode, int policy) {
		
		PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(arrayOrMapNode);
		
		// first get the child
		AvroNode childNode = null;
		if (arrayOrMapNode.hasChildren()) {
			childNode = arrayOrMapNode.getChild(0);
		}
		
		// then get the parent
		AvroNode parentNode = arrayOrMapNode.getParent();
		
		if (childNode != null) {
			unlinkNodes(arrayOrMapNode, childNode, ModelConstants.NONE);
		}
		
		unlinkNodes(parentNode, arrayOrMapNode, policy);
		
		if (childNode != null) {
			NodeType childType = childNode.getType();
			switch (childType) {
			case UNION:
				// in this case, we have to check of we have to add this child node into the parent
				UnionNode unionNode = (UnionNode) childNode;
				if (AttributeUtil.isChoiceType(unionNode)) {
					// we keep it!
				} else {
					// simple optional case
					// replace union node by its first not null child node
					childNode = ModelUtil.getFirstNotNullChild(unionNode);					
				}
				break;
			default:
				// we have to add this child node into the parent
				break;
			}
		}
		
		NodeType parentType = parentNode.getType();
		switch (parentType) {
		case FIELD:
		case ARRAY:
		case MAP:
			if (childNode != null && childNode.getType() != NodeType.PRIMITIVE_TYPE) {
				linkNodes(parentNode, childNode, ModelConstants.NONE);
			}
			break;
		case UNION:
			UnionNode parentUnionNode = (UnionNode) parentNode;
			if (AttributeUtil.isChoiceType(parentUnionNode)) {
				// TODO
			} else {
				// simple optional case
				if (childNode == null) {
					// it was an array/map of primitive type
					// add a primitive type node under this optional union node
					PrimitiveTypeNode primitiveTypeNode = createAndLinkPrimitiveTypeNode(parentNode, ModelConstants.LAST_POSITION);
					AttributeUtil.setPrimitiveType(primitiveTypeNode, primitiveType);
				} else {
					linkNodes(parentNode, childNode, ModelConstants.NONE);
				}
			}
			break;
		default:
			// should not happen
			throw new IllegalArgumentException("Invalid parent type");
		}
		
		validateAttributes(parentNode, policy);
		return parentNode;
	}	
	
	// ******************
	// Attribute part
	// ******************
	
	@Override
	public void setPrimitiveType(AvroNode node, PrimitiveType type) {
		AttributeUtil.checkAttributeIsDefined(node, AvroAttributes.PRIMITIVE_TYPE);
		AttributeUtil.setPrimitiveType(node, type);
		validate(node, AvroAttributes.PRIMITIVE_TYPE);
	}
	
	protected void validate(AvroNode node, String validatorKey) {
		validators.validate(node, validatorKey);
	}
	
	protected void validateAll(AvroNode node) {
		validators.validateAll(node);		
	}	
	
	protected AvroNode switchArrayOrMap(AvroNode arrayOrMapNode) {
		
		AvroNode newArrayOrMapNode = null;
		if (arrayOrMapNode.getType() == NodeType.ARRAY) {
			newArrayOrMapNode = createMapNode();
		} else {
			newArrayOrMapNode = createArrayNode();
		}
		
		replace(arrayOrMapNode, newArrayOrMapNode, ModelConstants.NONE);
		
		// restore attributes
		AttributeUtil.restoreAttribute(AvroAttributes.PRIMITIVE_TYPE, arrayOrMapNode, newArrayOrMapNode);
		AttributeUtil.restoreAttribute(AvroAttributes.OPTIONAL, arrayOrMapNode, newArrayOrMapNode);
		
		validateAll(newArrayOrMapNode);
		
		return newArrayOrMapNode;
	}
	
	@Override
	public AvroNode switchArrayMap(AvroNode node) {		
		ModelUtil.checkNodeTypes(node, NodeType.ARRAY, NodeType.MAP);
		return switchArrayOrMap(node);
	}
	
	@Override
	public void setOptional(AvroNode node, boolean optional) {
		ModelUtil.checkNodeTypes(node, NodeType.TYPED_NODE_TYPES);
		AttributeUtil.checkAttributeIsDefined(node, AvroAttributes.OPTIONAL);
		AttributeUtil.setAttributeValue(node, AvroAttributes.OPTIONAL, optional);
		if (optional) {
			addOptional(node);
		} else {
			removeOptional(node);
		}
		validateAll(node);
	}
	
	protected void addOptional(AvroNode node) {
		
		UnionNode unionNode = null;
		
		if (node.hasChildren(NodeType.UNION)) {
			unionNode = (UnionNode) node.getChild(0);
		} else {
			AvroNode childNode = null;
			if (node.hasChildren()) {
				// be careful, this node has children
				// we have to insert an union node
				childNode = node.getChild(0);
				unlinkNodes(node, childNode, ModelConstants.NONE);
			}
			// create union node
			unionNode = addUnionNode(node, ModelConstants.NONE);
			if (childNode != null) {
				// add it to the union node
				linkNodes(unionNode, childNode, ModelConstants.NONE);
			} else {
				// add a new PrimitiveType node
				AvroNode primTypeNode = createAndLinkPrimitiveTypeNode(unionNode, ModelConstants.LAST_POSITION);
				AttributeUtil.setPrimitiveType(primTypeNode, AttributeUtil.getPrimitiveType(node));
			}
		}
		// Add a Null child at the first position
		PrimitiveTypeNode nullNode = createAndLinkPrimitiveTypeNode(unionNode, ModelConstants.FIRST_POSITION);
		// set Null
		AttributeUtil.setPrimitiveType(nullNode, PrimitiveType.NULL);	

	}
	
	protected void removeOptional(AvroNode node) {
		// we have to remove the null node
		// and probably the union node
		// first remove the null node
		UnionNode unionNode = (UnionNode) node.getChild(0);
		// null node is necessarily the first one
		PrimitiveTypeNode nullNode = (PrimitiveTypeNode) unionNode.getChild(0);
		// remove it
		unlinkNodes(unionNode, nullNode, ModelConstants.NONE);
		// check the number of children
		// if it remains only one child, then remove union node too
		if (unionNode.getChildrenCount() < 2 && !AttributeUtil.isChoiceType(unionNode)) {
			// remove it
			AvroNode childNode = unionNode.getChild(0);
			unlinkNodes(unionNode, childNode, ModelConstants.NONE);
			unlinkNodes(node, unionNode, ModelConstants.NONE);
			if (childNode.getType() != NodeType.PRIMITIVE_TYPE) {
				linkNodes(node, childNode, ModelConstants.NONE);
			}
		}
	}	
	
	// ******************
	// Drag And Drop 
	// ******************

	@Override
	public boolean canMoveInDirection(AvroNode node, Direction direction) {
		if (ModelUtil.isOneOfType(node, NodeType.FIELD, NodeType.RECORD, NodeType.ENUM, NodeType.FIXED, NodeType.PRIMITIVE_TYPE, NodeType.REF, NodeType.ARRAY, NodeType.MAP)) {
			AvroNode parent = node.getParent();
			if (parent != null) {
				int childIndex = parent.getChildIndex(node);
				if (direction == Direction.UP) {
					return childIndex > 0;
				} else {
					return childIndex < parent.getChildrenCount() - 1;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean canDnDElement(Action action, AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
		return dndPolicy.acceptDnD(action, sourceNode, targetNode, position);
	}
	
	@Override
	public DnDParams executeDnDElement(Action action, AvroNode sourceNode, AvroNode targetNode,
			TargetPosition position) {
		if (dndPolicy.isHandlerDefined(action, sourceNode, targetNode, position)) {
			return dndPolicy.executeDnD(action, sourceNode, targetNode, position);
		} else {
			// default behavior (only for COPY & REFERENCE)
			// well in fact we could implement a default move behavior (first removeElement() then addElement()).
			BaseDnDParams params = BaseDnDParams.getParams(sourceNode, targetNode, position);
			switch (action) {
			case COPY:				
				AvroNode copiedNode = copyElement(sourceNode, targetNode, position);
				params.storeAvroNode(DnDParams.COPY_NODE, copiedNode);
				break;
			case REFERENCE:
				RefNode refNode = referenceElement(sourceNode, targetNode, position);
				params.storeAvroNode(DnDParams.REF_NODE, refNode);
				break;
			case MOVE:
				throw new UnsupportedOperationException("No default move action");
			}
			return params;
		}
	}
	
	@Override
	public void undoDnDElement(Action action, DnDParams dndParams) {
		if (dndPolicy.isHandlerDefined(action, dndParams)) {
			dndPolicy.undoDnD(action, dndParams);
		} else {
			// default behavior (only for COPY & REFERENCE)
			switch (action) {
			case COPY:
				AvroNode copyNode = dndParams.getAvroNode(DnDParams.COPY_NODE);
				removeElement(copyNode);
				break;
			case REFERENCE:
				AvroNode refNode = dndParams.getAvroNode(DnDParams.REF_NODE);
				removeElement(refNode);
				break;
			case MOVE:
				throw new UnsupportedOperationException("No default undo move action");
			}
		}
	}
	
	public AvroNode copyElement(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
		DnDContext dndContext = new DefaultDnDContext(sourceNode, targetNode, position);
		CopyStrategy strategy = copyStrategyProvider.getCopyStrategy(getContext(), dndContext);		
		AvroNode nodeCopy = copyEngine.copy(dndContext, strategy);
		addElement(targetNode, nodeCopy, position);
		return nodeCopy;
	}
	
	public RefNode referenceElement(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {
		AvroNode referencedNode = sourceNode;
		if (sourceNode.getType().isRef()) {
			referencedNode = ((RefNode) sourceNode).getReferencedNode();
		}
		RefNode refNode = new RefNodeImpl(referencedNode, getContext());
		refNode.init(attributeInitializer);
		addElement(targetNode, refNode, position);
		return refNode;
	}
		
	//************************
	// OTHER METHODS
	//************************

	protected void validateAttributes(AvroNode node, int policy) {
		if (validateAttributes(policy)) {
			validateAll(node);
		}
	}		
		
	protected void checkTargetPosition(TargetPosition position, TargetPosition... authorisedPositions) {
		for (TargetPosition pos : authorisedPositions) {
			if (pos == position) {
				return;
			}
		}
		throw new IllegalArgumentException("Invalid target position");
	}
	
	protected void checkAddNode(NodeType type, boolean checked, String reason) {
		if (!checked) {
			throw new IllegalArgumentException("Cannot add " + type.toString() + " node: " + reason);
		}
	}
	
	protected void checkNodeClass(AvroNode elementNode) {
		if (!ModelUtil.isNodeClassValid(elementNode)) {
			throw new IllegalStateException("Node class is not compatible with its type");
		}
	}
	
	protected void replace(AvroNode oldNode, AvroNode newNode, int policy) {		
		if (oldNode.getParent() == null) {
			throw new UnsupportedOperationException("Replace is available on node with a defined parent");
		}
		if (newNode.getParent() != null) {
			throw new IllegalArgumentException("New node should not have a parent");
		}
		if (newNode.hasChildren()) {
			throw new IllegalArgumentException("New node should not have children");
		}
		AvroNode parentNode = oldNode.getParent();
		List<AvroNode> children = new ArrayList<>(oldNode.getChildren());
		for (AvroNode child : children) {
			unlinkNodes(oldNode, child, ModelConstants.NONE);
		}			
		unlinkNodes(parentNode, oldNode, policy);
		linkNodes(parentNode, newNode, policy);
		for (AvroNode child : children) {
			linkNodes(newNode, child, ModelConstants.NONE);
		}
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
	
	protected void insertNode(AvroNode parentNode, AvroNode childNode, AvroNode insertedNode, int policy) {
		ModelUtil.insertNode(getContext(), parentNode, childNode, insertedNode, policy);
	}
	
	protected void insertNode(AvroNode parentNode, AvroNode insertedNode, int policy) {
		ModelUtil.insertNode(getContext(), parentNode, insertedNode, policy);
	}
	
	@Override
	public void dispose() {
		// nothing to dispose
	}	
	
}
