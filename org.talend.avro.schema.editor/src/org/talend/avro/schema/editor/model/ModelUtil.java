package org.talend.avro.schema.editor.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * Some convenient methods about the model nodes and their hierarchy.
 * 
 * @author timbault
 * @see AvroNode
 *
 */
public class ModelUtil {

	/**
	 * Indicates if the given node has a null child (i.e. a primitive type node of type NULL).
	 * 
	 * @param node
	 * @return
	 */
	public static boolean hasNullChild(AvroNode node) {
		for (int i = 0; i < node.getChildrenCount(); i++) {
			AvroNode child = node.getChild(i);
			if (isNullNode(child)) {
				return true;
			}
		}
		return false;
	}	
	
	public static AvroNode getNullChild(AvroNode node) {
		for (int i = 0; i < node.getChildrenCount(); i++) {
			AvroNode child = node.getChild(i);
			if (isNullNode(child)) {
				return child;
			}
		}
		return null;
	}
	
	/**
	 * Returns true if the given node is a primitive Null node.
	 * 
	 * @param node
	 * @return
	 */
	public static boolean isNullNode(AvroNode node) {
		return node.getType() == NodeType.PRIMITIVE_TYPE && AttributeUtil.getPrimitiveType(node) == PrimitiveType.NULL;
	}
	
	/**
	 * Returns the first not null child of the given union node.
	 * 
	 * @param unionNode
	 * @return
	 */
	public static AvroNode getFirstNotNullChild(UnionNode unionNode) {
		for (int i = 0; i < unionNode.getChildrenCount(); i++) {
			AvroNode child = unionNode.getChild(i);
			if (!ModelUtil.isNullNode(child)) {
				return child;
			}
		}
		return null;
	}
	
	public static AvroNode getFirstNotNullPrimitiveTypeChild(UnionNode unionNode) {
		for (int i = 0; i < unionNode.getChildrenCount(); i++) {
			AvroNode child = unionNode.getChild(i);
			if (!ModelUtil.isNullNode(child) && child.getType() == NodeType.PRIMITIVE_TYPE) {
				return child;
			}
		}
		return null;
	}
	
	/**
	 * Returns the first child of the given union node which is of specified type.
	 * 
	 * @param unionNode
	 * @param type
	 * @return
	 */
	public static AvroNode getFirstTypedChild(UnionNode unionNode, NodeType type) {
		for (int i = 0; i < unionNode.getChildrenCount(); i++) {
			AvroNode child = unionNode.getChild(i);
			if (child.getType() == type) {
				return child;
			}
		}
		return null;
	}	
	
	/**
	 * Indicates if the given node is optional. It means that its child is an union node which contains a Null primitive type node.
	 * 
	 * @param node
	 * @return
	 */
	public static boolean isOptional(AvroNode node) {
		if (node.hasChildren(NodeType.UNION)) {
			UnionNode unionNode = (UnionNode) node.getChild(0);
			return hasNullChild(unionNode);
		}
		return false;
	}
	
	/**
	 * Indicates if the given node has a choice type. It means that its child is an union node with multiple types.
	 * @param node
	 * @return
	 */
	public static boolean isChoiceType(AvroNode node) {
		checkNodeTypes(node, NodeType.TYPED_NODE_TYPES);
		if (node.hasChildren(NodeType.UNION)) {
			UnionNode unionNode = (UnionNode) node.getChild(0);
			return AttributeUtil.isChoiceType(unionNode);
		}
		return false;
	}
	
	/**
	 * Returns the first parent of the given node which contains the specified attribute. It is recursive: it looks on all the node ancestors.
	 * 
	 * @param node
	 * @param attrName
	 * @return
	 */
	public static AvroNode getClosestParentWithAttribute(AvroNode node, String attrName) {
		AvroNode parent = node.getParent();
		while (parent != null && !AttributeUtil.hasAttribute(parent, attrName)) {
			parent = parent.getParent();
		}
		return parent;
	}
	
	/**
	 * Check if the given node's type is one of specified types.
	 * 
	 * @param node
	 * @param types
	 * @return
	 */
	public static boolean isOneOfType(AvroNode node, NodeType... types) {
		for (NodeType type : types) {
			if (type == node.getType()) {
				return true;
			}
		}
		return false;
	}
		
	/**
	 * Returns the first parent of the given node which is of the specified class.
	 * 
	 * @param node
	 * @param include 
	 * @param nodeClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AvroNode> T getFirstParentOfType(AvroNode node, boolean include, Class<T> nodeClass) {
		if (include && nodeClass.isAssignableFrom(node.getClass())) {
			return (T) node;
		}
		if (node.getParent() != null) {
			return getFirstParentOfType(node.getParent(), true, nodeClass);
		}
		return null;
	}
	
	/**
	 * Returns the first parent of the given node whose type is one of the specified types.
	 * 
	 * @param node
	 * @param include
	 * @param types
	 * @return
	 */
	public static AvroNode getFirstParentOfType(AvroNode node, boolean include, NodeType... types) {
		if (include && isOneOfType(node, types)) {
			return node;
		}
		if (node.getParent() != null) {
			return getFirstParentOfType(node.getParent(), true, types);
		}
		return null;
	}
	
	/**
	 * Returns the first child of the given node whose type is one of the specified types.
	 * 
	 * @param node
	 * @param include
	 * @param types
	 * @return
	 */
	public static AvroNode getFirstChildOfType(AvroNode node, boolean include, NodeType... types) {
		if (include && isOneOfType(node, types)) {
			return node;
		}
		for (int i = 0; i < node.getChildrenCount(); i++) {
			AvroNode child = node.getChild(i);
			AvroNode childOfType = getFirstChildOfType(child, true, types);
			if (childOfType != null) {
				return childOfType;
			}
		}
		return null;
	}
	
	public static boolean isRootMapOrArray(AvroNode mapOrArrayNode) {
		AvroNode typedParent = getFirstParentOfType(mapOrArrayNode, false, NodeType.FIELD, NodeType.MAP, NodeType.ARRAY);
		return typedParent.getType() == NodeType.FIELD;
	}
	
	/**
	 * Returns true if the given union node is child of the root node. It means that this union node is at the root of the model.
	 * 
	 * @param unionNode
	 * @return
	 */
	public static boolean isRootUnion(UnionNode unionNode) {
		return unionNode.getParent().getType() == NodeType.ROOT;
	}
	
	/**
	 * Returns the number of child. It takes into account the ref node case.
	 * 
	 * @param node
	 * @return
	 */
	public static int getChildrenCount(AvroNode node) {
		if (node.getType().isRef()) {
			node = ((RefNode) node).getReferencedNode();
		}
		return node.getChildrenCount();
	}
	
	/**
	 * Returns true if the given node is the root node of the model.
	 * 
	 * @param node
	 * @return
	 */
	public static boolean isRoot(AvroNode node) {
		return node.getType() == NodeType.ROOT;
	}
	
	/**
	 * Check if node1 is an ancestor of node2
	 * 
	 * @param node1
	 * @param node2
	 * @return true is node1 is one of the parent of the node2
	 */
	public static boolean isAncestor(AvroNode node1, AvroNode node2) {
		if (node1 == node2) {
			throw new IllegalArgumentException("Node1 and node2 must be different");
		}
		AvroNode parent = node2.getParent();
		while (parent != null) {
			if (parent == node1) {
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}
	
	/**
	 * Check that the given node has a valid type according to its class.
	 * 
	 * @param node
	 * @return
	 */
	public static boolean isNodeClassValid(AvroNode node) {
		NodeType type = node.getType();
		boolean ok = false;
		switch (type) {
		case ARRAY:
			ok = node instanceof ArrayNode;
			break;
		case MAP:
			ok = node instanceof MapNode;
			break;
		case ENUM:
			ok = node instanceof EnumNode;
			break;
		case FIELD:
			ok = node instanceof FieldNode;
			break;
		case FIXED:
			ok = node instanceof FixedNode;
			break;
		case PRIMITIVE_TYPE:
			ok = node instanceof PrimitiveTypeNode;
			break;
		case RECORD:
			ok = node instanceof RecordNode;
			break;
		case ROOT:
			ok = node instanceof RootNode;
			break;
		case UNION:
			ok = node instanceof UnionNode;
			break;
		case REF:
			ok = node instanceof RefNode;
			break;
		default:
			ok = false;
			break;
		}
		return ok;
	}
	
	public static List<PrimitiveType> getFreePrimitiveTypes(UnionNode unionNode, boolean includeNull, PrimitiveType... excludedTypes) {
//		if (!AttributeUtil.isChoiceType(unionNode)) {
//			throw new IllegalArgumentException("Union node must be a choice-type one");
//		}
		Set<PrimitiveType> excluded = new HashSet<>();
		for (PrimitiveType excludedType : excludedTypes) {
			excluded.add(excludedType);
		}
		List<PrimitiveType> freeTypes = PrimitiveType.getValuesAsList(includeNull);
		for (int i = 0; i < unionNode.getChildrenCount(); i++) {
			AvroNode child = unionNode.getChild(i);
			if (child.getType() == NodeType.PRIMITIVE_TYPE) {
				PrimitiveType type = AttributeUtil.getPrimitiveType(child);
				if ((type == PrimitiveType.NULL && includeNull) ||
					(type != PrimitiveType.NULL && !excluded.contains(type))) {
					freeTypes.remove(type);
				}
			}
		}
		return freeTypes;
	}
	
	public static boolean isTypedNodeOfPrimitiveType(AvroNode typedNode) {
		checkNodeTypes(typedNode, NodeType.TYPED_NODE_TYPES);
		if (typedNode.hasChildren()) {
			AvroNode child = typedNode.getChild(0);
			NodeType childType = child.getType();
			if (childType == NodeType.UNION && !AttributeUtil.isChoiceType(child)) {
				// simple optional case
				AvroNode firstNotNullChild = ModelUtil.getFirstNotNullChild((UnionNode) child);
				return firstNotNullChild.getType() == NodeType.PRIMITIVE_TYPE;
			} else {
				return false;
			}
		}
		return true;
	}
	
	public static PrimitiveType getPrimitiveTypeOfTypedNode(AvroNode typedNode) {
		checkNodeTypes(typedNode, NodeType.TYPED_NODE_TYPES);
		if (typedNode.hasChildren()) {
			AvroNode child = typedNode.getChild(0);
			NodeType childType = child.getType();
			if (childType == NodeType.UNION && !AttributeUtil.isChoiceType(child)) {
				// simple optional case
				AvroNode firstNotNullChild = ModelUtil.getFirstNotNullChild((UnionNode) child);
				return AttributeUtil.getPrimitiveType(firstNotNullChild);
			} else {
				throw new IllegalArgumentException("Given typed node has not a primitive type");
			}
		} else {
			return AttributeUtil.getPrimitiveType(typedNode);
		}		
	}
	
	public static boolean isTyped(AvroNode node) {
		NodeType type = node.getType();
		return type == NodeType.ARRAY || type == NodeType.MAP || type == NodeType.FIELD;
	}
	
	public static PrimitiveType getPrimitiveTypeFromChildren(AvroNode node) {
		PrimitiveType type = null;
		for (int i = 0; i < node.getChildrenCount(); i++) {
			AvroNode child = node.getChild(i);
			if (isTyped(child) && isTypedNodeOfPrimitiveType(child)) {
				type = getPrimitiveTypeOfTypedNode(child);
			} else {
				type = getPrimitiveTypeFromChildren(child);
			}
			if (type != null) {
				break;
			}
		}
		return type;
	}
	
	public static void checkType(NodeType type, NodeType... authorizedTypes) {
		for (NodeType at : authorizedTypes) {
			if  (type == at) {
				// OK
				return;
			}
		}
		throw new IllegalArgumentException("Invalid type");
	}
	
	public static void checkNodeTypes(AvroNode node, NodeType... types) {
		if (!isOneOfType(node, types)) {
			throw new IllegalArgumentException("Unsupported node type");
		}
	}
	
	public static void linkNodes(AvroContext context, AvroNode parentNode, AvroNode childNode, int index, int policy) {
		childNode.setParent(parentNode);
		if (index == ModelConstants.LAST_POSITION) {
			parentNode.addChild(childNode);
		} else {
			parentNode.addChild(childNode, index);
		}
		if (register(policy)) {
			registerNode(context, childNode);
		}
	}
	
	public static void linkNodes(AvroContext context, AvroNode parentNode, AvroNode childNode, int policy) {
		childNode.setParent(parentNode);
		// last position by default
		parentNode.addChild(childNode);
		if (register(policy)) {
			registerNode(context, childNode);
		}
	}
	
	public static void linkNodes(AvroContext context, AvroNode parentNode, AvroNode childNode, AvroNode targetNode, TargetPosition position, int policy) {
		childNode.setParent(parentNode);
		parentNode.addChild(childNode, targetNode, position);
		if (register(policy)) {
			registerNode(context, childNode);
		}
	}
	
	public static void unlinkNodes(AvroContext context, AvroNode parentNode, AvroNode childNode, int policy) {
		if (unregister(policy)) {
			unregisterNode(context, childNode);
		}
		parentNode.removeChild(childNode);
		childNode.setParent(null);		
	}
	
	public static void insertNode(AvroContext context, AvroNode parentNode, AvroNode childNode, AvroNode insertedNode, int policy) {
		if (childNode.getParent() != parentNode) {
			throw new IllegalArgumentException("Specified child node is not a child of the given parent node");
		}
		unlinkNodes(context, parentNode, childNode, ModelConstants.NONE);
		linkNodes(context, parentNode, insertedNode, policy);
		linkNodes(context, insertedNode, childNode, ModelConstants.NONE);
	}
	
	public static void insertNode(AvroContext context, AvroNode parentNode, AvroNode insertedNode, int policy) {
		AvroNode childNode = null;		
		if (parentNode.hasChildren()) {
			if (parentNode.getChildrenCount() > 1) {
				throw new IllegalArgumentException("Parent node should have only one child");
			}
			childNode = parentNode.getChild(0);
			unlinkNodes(context, parentNode, childNode, ModelConstants.NONE);
		}		
		linkNodes(context, parentNode, insertedNode, policy);
		if (childNode != null) {
			linkNodes(context, insertedNode, childNode, ModelConstants.NONE);
		}
	}
	
	private static boolean register(int policy) {
		return (policy & ModelConstants.REGISTER) != 0;
	}
	
	private static boolean unregister(int policy) {
		return (policy & ModelConstants.UNREGISTER) != 0;
	}
	
	private static void registerNode(AvroContext context, AvroNode node) {
		if (context.getSchemaRegistry().isRegistrable(node)) {
			context.getSchemaRegistry().register(node, true);
		}
	}
	
	private static void unregisterNode(AvroContext context, AvroNode node) {
		if (context.getSchemaRegistry().isRegistrable(node)) {
			context.getSchemaRegistry().unregister(node);
		}
	}
	
	public static void collect(AvroNode node, List<AvroNode> nodes) {
		nodes.add(node);
		for (AvroNode child : node.getChildren()) {
			collect(child, nodes);
		}
	}
	
	/**
	 * Return all the field names of the given record.
	 * 
	 * @param recordNode
	 * @return
	 */
	public static List<String> getFieldNames(RecordNode recordNode) {
		List<String> names = new ArrayList<>();
		for (int i = 0; i < recordNode.getChildrenCount(); i++) {
			AvroNode child = recordNode.getChild(i);
			String name = AttributeUtil.getNameFromAttribute(child);
			names.add(name);
		}
		return names;
	}
	
	/**
	 * Return the field of the given record which has the specified name.
	 *  
	 * @param recordNode
	 * @param fieldName
	 * @return
	 */
	public static FieldNode getField(RecordNode recordNode, String fieldName) {
		for (int i = 0; i < recordNode.getChildrenCount(); i++) {
			FieldNode fieldNode = (FieldNode) recordNode.getChild(i);
			if (fieldName.equals(AttributeUtil.getNameFromAttribute(fieldNode))) {
				return fieldNode;
			}
		}
		return null;
	}
	
	/**
	 * Return the type of the given node (not the direct type of the node, which is obtained with the getType() method).
	 * Do not use this method with an union node.
	 * 
	 * @param node
	 * @return
	 */
	public static NodeType getTypeOfNode(AvroNode node) {
		if (node.hasChildren()) {
			AvroNode child = node.getChild(0);
			NodeType type = child.getType();
			// check the special case of union
			if (type == NodeType.UNION) {
				if (hasNullChild(child)) {
					AvroNode firstNotNullChild = getFirstNotNullChild((UnionNode) child);
					return firstNotNullChild.getType();
				}
			}
			return type;
		} else {
			// no child, it is a primitive type
			return NodeType.PRIMITIVE_TYPE;
		}
	}
	
	/**
	 * Parse the given string into a value corresponding to the specified primitive type.
	 * 
	 * @param value
	 * @param type
	 * @return
	 */
	public static Object parsePrimitiveType(String value, PrimitiveType type) {
		switch (type) {
		case BOOLEAN:
			return Boolean.parseBoolean(value);
		case BYTES:
			// TODO handle bytes
			return null;
		case DOUBLE:
			return Double.parseDouble(value);
		case FLOAT:
			return Float.parseFloat(value);
		case INT:
			return Integer.parseInt(value);
		case LONG:
			return Long.parseLong(value);
		case STRING:
			return value;
		case NULL:
			return null;
		default:
			return null;
		}
	}
	
}
