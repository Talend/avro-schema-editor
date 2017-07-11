package org.talend.avro.schema.editor.model;

/**
 * This defines all the types of avro nodes found in a schema.
 * 
 * @author timbault
 *
 */
public enum NodeType implements ModelConstants {		
	ROOT(
			UNNAMED, WITHOUT_NAMESPACE, WITHOUT_PROPERTIES, IS_NODE, SINGLE_CHILD, IS_NOT_REF, "Root"),
	RECORD(
			NAMED, WITH_NAMESPACE, WITH_PROPERTIES, IS_NODE, MULTI_CHILDREN, IS_NOT_REF, "Record"),
	FIELD(
			NAMED, WITHOUT_NAMESPACE, WITH_PROPERTIES, IS_NODE, SINGLE_CHILD, IS_NOT_REF, "Field"),
	UNION(
			UNNAMED, WITHOUT_NAMESPACE, WITHOUT_PROPERTIES, IS_NODE, MULTI_CHILDREN, IS_NOT_REF, "Choice"),
	ENUM(
			NAMED, WITH_NAMESPACE, WITH_PROPERTIES, IS_LEAF, NO_CHILD, IS_NOT_REF, "Enumeration"),
	PRIMITIVE_TYPE(
			UNNAMED, WITHOUT_NAMESPACE, WITH_PROPERTIES, IS_LEAF, NO_CHILD, IS_NOT_REF, "Primitive Type"),
	ARRAY(
			UNNAMED, WITHOUT_NAMESPACE, WITH_PROPERTIES, IS_NODE, SINGLE_CHILD, IS_NOT_REF, "Array"),
	MAP(
			UNNAMED, WITHOUT_NAMESPACE, WITH_PROPERTIES, IS_NODE, SINGLE_CHILD, IS_NOT_REF, "Map"),
	FIXED(
			NAMED, WITH_NAMESPACE, WITH_PROPERTIES, IS_LEAF, NO_CHILD, IS_NOT_REF, "Fixed"),
	REF(
			NAMED, WITH_NAMESPACE, NA, IS_LEAF, NO_CHILD, IS_REF, "Reference");
	
	public static final NodeType[] TYPED_NODE_TYPES = new NodeType[] { FIELD, ARRAY, MAP };
	
	public static final NodeType[] ARRAY_OR_MAP = new NodeType[] { ARRAY, MAP };
	
	public static final NodeType[] REFERENCED_NODE_TYPES = new NodeType[] { RECORD, ENUM, FIXED };
	
	public static final NodeType[] REGISTERED_NODE_TYPES = new NodeType[] { RECORD, ENUM, FIXED };
	
	public static final NodeType[] NAMESPACED_NODE_TYPES = new NodeType[] { RECORD, ENUM, FIXED };
	
	public static int indexOf(NodeType[] types, NodeType type) {
		for (int i = 0; i < types.length; i++) {
			if (types[i] == type) {
				return i;
			}
		}
		return -1;
	}
	
	private String label;
	
	private boolean named;

	private boolean withNameSpace;
	
	private boolean propertiesHolder;
	
	private boolean leaf;
	
	private boolean multiChildren;
	
	private boolean ref;
	
	private NodeType(boolean named, boolean withNameSpace, boolean propertiesHolder, boolean leaf, boolean multiChildren, boolean ref, String label) {
		this.named = named;
		this.withNameSpace = withNameSpace;		
		this.propertiesHolder = propertiesHolder;
		this.leaf = leaf;
		this.multiChildren = multiChildren;
		this.ref = ref;		
		this.label = label;
	}
	
	public boolean isNamed() {
		return named;
	}
	
	public boolean hasNameSpace() {
		return withNameSpace;
	}
	
	public boolean isPropertiesHolder() {
		return propertiesHolder;
	}
	
	public boolean hasFullName() {
		return named && withNameSpace;
	}
	
	public boolean isLeaf() {
		return leaf;
	}

	public boolean isMultiChildren() {
		return multiChildren;
	}	
	
	public boolean isRef() {
		return ref;
	}	
	
	public static NodeType getType(String valueStr) {
		for (NodeType type : values()) {
			if (type.toString().toLowerCase().equals(valueStr.toLowerCase())) {
				return type;
			}
		}
		return null;
	}
	
	public String getDefaultLabel() {
		return this.toString().toLowerCase();
	}
	
	public String getDisplayLabel() {
		return label;
	}
	
}
