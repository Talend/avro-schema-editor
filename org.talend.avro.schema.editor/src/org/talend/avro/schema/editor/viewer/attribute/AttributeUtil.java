package org.talend.avro.schema.editor.viewer.attribute;

import java.util.ArrayList;
import java.util.List;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.RefNode;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.AvroAttributeSet;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.model.attributes.DefaultValue;
import org.talend.avro.schema.editor.model.attributes.NameSpaceDefinition;
import org.talend.avro.schema.editor.model.attributes.PrimitiveTypeAttribute;
import org.talend.avro.schema.editor.model.attributes.PrimitiveTypes;
import org.talend.avro.schema.editor.model.attributes.StringList;
import org.talend.avro.schema.editor.model.attributes.custom.CustomAttributeConfiguration;
import org.talend.avro.schema.editor.model.attributes.custom.CustomAttributeIO;

/**
 * This class provides several convenient methods to handle attributes.
 * 
 * @author timbault
 *
 */
public class AttributeUtil {

	protected static AvroNode getReferencedNode(AvroNode node) {
		if (node.getType().isRef()) {
			return ((RefNode) node).getReferencedNode();
		}
		return node;
	}
	
	protected static AvroAttributeSet getReferencedAttributes(AvroNode node) {
		if (node.getType().isRef()) {
			return ((RefNode) node).getReferencedAttributes();
		}
		return node.getAttributes();
	}
	
	public static boolean hasNameAttribute(AvroNode node) {
		return getReferencedAttributes(node).isDefined(AvroAttributes.NAME);
	}
	
	public static String getNameFromAttribute(AvroNode node) {
		return getNameFromAttribute(getReferencedAttributes(node));
	}
		
	protected static String getNameFromAttribute(AvroAttributeSet attributes) {
		return attributes.getAttributeFromValueClass(AvroAttributes.NAME, String.class).getValue();
	}

	public static String getTrueName(AvroNode node) {		
		if (isNameWithNameSpace(node)) {
			return getTrueNameFromFullName(node);
		}
		return getNameFromAttribute(node);
	}
	
	protected static String getTrueNameFromFullName(AvroNode node) {
		String fullname = getNameFromAttribute(node);
		return getTrueNameFromFullName(fullname);
	}
	
	public static String getTrueNameFromFullName(String fullname) {
		int lastDotIndex = fullname.lastIndexOf(NameSpaceDefinition.DOT);
		return fullname.substring(lastDotIndex + 1, fullname.length());
	}	
	
	public static String getFullName(AvroNode node) {
		String name = getNameFromAttribute(node);
		if (isNameWithNameSpace(node)) {
			return name;
		} else {
			String nameSpace = getTrueNameSpace(node);
			return isStringDefined(nameSpace) ? nameSpace + NameSpaceDefinition.DOT + name : name;
		}
	}
	
	public static String getFullName(String namespace, String name) {
		return isStringDefined(namespace) ? namespace + NameSpaceDefinition.DOT + name : name;
	}
	
	public static boolean isStringDefined(String value) {
		return value != null && !value.trim().isEmpty();
	}
	
	public static NameSpaceDefinition getNameSpaceDefinition(AvroNode node) {
		if (!hasNameSpaceAttribute(node)) {
			throw new IllegalArgumentException("Namespace attribute is not defined on the given node");
		}
		// first case: the namespace is specified directly in the name
		if (isNameWithNameSpace(node)) {
			return NameSpaceDefinition.IN_NAME;
		}
		// sceond case: the namespace is specified in the namespace attribute
		String nameSpace = getNameSpaceFromAttribute(node);
		if (nameSpace != null && !nameSpace.trim().isEmpty()) {
			return NameSpaceDefinition.EXPLICIT;
		}
		// last case: the namespace is inherited
		return NameSpaceDefinition.INHERITED;
	}
	
	public static String getNameSpaceFromName(AvroNode node) {
		return getNameSpaceFromName(node, true);
	}
	
	protected static String getNameSpaceFromName(AvroNode node, boolean check) {
		if (check && !isNameWithNameSpace(node)) {
			throw new IllegalArgumentException("The name of the specified node has no dot");
		}
		String name = getNameFromAttribute(node);
		return getNameSpaceFromFullName(name);
	}
		
	public static String getNameSpaceFromFullName(String fullname) {
		int lastDotIndex = fullname.lastIndexOf(NameSpaceDefinition.DOT);
		if (lastDotIndex == -1) {
			return null;
		}
		return fullname.substring(0, lastDotIndex);		
	}
	
	public static String getTrueNameSpace(AvroNode node) {
		NameSpaceDefinition nameSpaceDefinition = getNameSpaceDefinition(node);
		return getTrueNameSpace(node, nameSpaceDefinition, true);
	}
	
	public static String getTrueNameSpace(AvroNode node, NameSpaceDefinition nameSpaceDefinition, boolean check) {		
		switch (nameSpaceDefinition) {
		case EXPLICIT:
			return getNameSpaceFromAttribute(node);
		case IN_NAME:
			return getNameSpaceFromName(node, check);
		case INHERITED:
			return getInheritedNameSpace(node, check);
		}
		return null;
	}
	
	public static String tryToGetTrueNameSpace(AvroNode node, NameSpaceDefinition... nameSpaceDefinitions) {
		for (NameSpaceDefinition nsDef : nameSpaceDefinitions) {
			String nameSpace = getTrueNameSpace(node, nsDef, false);
			if (nameSpace != null) {
				return nameSpace;
			}
		}
		return null;
	}
	
	public static boolean isNameWithNameSpace(AvroNode node) {
		String name = getNameFromAttribute(node);
		return isNameWithNameSpace(name);
	}
	
	public static boolean isNameWithNameSpace(String name) {
		return name.contains(".");
	}
	
	protected static String getNameSpaceFromAttribute(AvroAttributeSet attributes) {
		return attributes.getAttributeFromValueClass(AvroAttributes.NAME_SPACE, String.class).getValue();
	}
	
	public static boolean hasNameSpaceAttribute(AvroNode node) {
		return getReferencedAttributes(node).isDefined(AvroAttributes.NAME_SPACE);
	}
	
	public static boolean hasNameSpaceValueInAttribute(AvroNode node) {
		String nameSpace = getNameSpaceFromAttribute(node);
		return nameSpace != null && !nameSpace.trim().isEmpty();
	}
	
	public static String getNameSpaceFromAttribute(AvroNode node) {		
		if (!hasNameSpaceAttribute(node)) {
			throw new IllegalArgumentException("Namespace attribute is not defined on the given node");
		}
		String nameSpace = getNameSpaceFromAttribute(getReferencedAttributes(node));
		return nameSpace;
	}	
	
	public static String getInheritedNameSpace(AvroNode node) {
		return getInheritedNameSpace(node, true);
	}
	
	protected static String getInheritedNameSpace(AvroNode node, boolean check) {
		if (check && !hasNameSpaceAttribute(node)) {
			throw new IllegalArgumentException("Namespace attribute is not defined on the given node");
		}
		if (check && isNameWithNameSpace(node)) {
			throw new IllegalArgumentException("Namespace cannot be inherited: the name of the node contains a valid name space");
		}
		if (check && hasNameSpaceValueInAttribute(node)) {
			throw new IllegalArgumentException("Namespace cannot be inherited: the namespace is defined in the namespace attribute");
		}
		// ok the namespace is inherited
		AvroNode parentNSNode = ModelUtil.getFirstParentOfType(node, false, NodeType.NAMESPACED_NODE_TYPES);
		if (parentNSNode != null) {
			return getTrueNameSpace(parentNSNode);
		}
		return null;
	}
	
	public static String getEnclosingNameSpace(AvroNode node, boolean include) {
		AvroNode parentNSNode = ModelUtil.getFirstParentOfType(node, include, NodeType.NAMESPACED_NODE_TYPES);
		if (parentNSNode != null) {
			return getTrueNameSpace(parentNSNode);
		}
		return null;
	}
	
	public static boolean hasDefaultValueAttribute(AvroNode node) {
		return node.getAttributes().isDefined(AvroAttributes.DEFAULT);
	}
	
	public static boolean hasDefaultValue(AvroNode node) {
		if (hasDefaultValueAttribute(node)) {
			return isDefaultValueDefined(node);
		}
		return false;
	}
	
	public static boolean isDefaultValueDefined(AvroNode node) {
		return node.getAttributes().getAttributeFromValueClass(AvroAttributes.DEFAULT, DefaultValue.class).getValue().isDefined();
	}
	
	public static String getDefaultValueAsString(AvroNode node) {
		return node.getAttributes().getAttributeFromValueClass(AvroAttributes.DEFAULT, DefaultValue.class).getValue().getValue();
	}
	
	public static boolean hasDoc(AvroNode node) {
		if (hasDocAttribute(node)) {
			String doc = getDoc(node);
			return doc != null && !doc.trim().isEmpty();
		}
		return false;
	}
	
	public static boolean hasDocAttribute(AvroNode node) {
		return node.getAttributes().isDefined(AvroAttributes.DOC);
	}
	
	public static String getDoc(AvroNode node) {
		return getDoc(node.getAttributes());
	}
	
	protected static String getDoc(AvroAttributeSet attributes) {
		return attributes.getAttributeFromValueClass(AvroAttributes.DOC, String.class).getValue();
	}
	
	public static boolean hasAliasesAttribute(AvroNode node) {
		return node.getAttributes().isDefined(AvroAttributes.ALIASES);
	}
	
	public static boolean hasAliases(AvroNode node) {
		if (hasAliasesAttribute(node)) {
			String[] aliases = getAliases(node);
			return aliases.length > 0;
		}
		return false;
	}
	
	public static String[] getAliases(AvroNode node) {
		return getAliases(node.getAttributes());
	}
	
	protected static String[] getAliases(AvroAttributeSet attributes) {
		StringList aliasesSet = attributes.getAttributeFromValueClass(AvroAttributes.ALIASES, StringList.class).getValue();
		return aliasesSet.getValuesAsArray();
	}

	public static String[] getSymbols(AvroNode node) {
		return getSymbols(node.getAttributes());
	}
	
	protected static String[] getSymbols(AvroAttributeSet attributes) {
		StringList symbolsSet = attributes.getAttributeFromValueClass(AvroAttributes.SYMBOLS, StringList.class).getValue();
		return symbolsSet.getValuesAsArray();
	}
	
	public static int getSize(AvroNode node) {
		return getSize(node.getAttributes());
	}
	
	protected static int getSize(AvroAttributeSet attributes) {
		AvroAttribute<Integer> attribute = attributes.getAttributeFromValueClass(AvroAttributes.SIZE, Integer.class);
		return attribute.getValue();
	}
	
	public static String[] getCustomAttributeNames(AvroNode node, AvroContext context) {
		CustomAttributeConfiguration customAttributeConfiguration = context.getCustomAttributeConfiguration();
		if (customAttributeConfiguration == null) {
			return new String[0];
		}
		AvroAttributeSet attributes = node.getAttributes();
		List<AvroAttribute<?>> sortedAttributes = attributes.getSortedAttributes();
		List<AvroAttribute<?>> customAttributes = new ArrayList<>();
		for (AvroAttribute<?> attribute : sortedAttributes) {
			if (customAttributeConfiguration.isCustomAttribute(context, node.getType(), attribute.getName())) {
				customAttributes.add(attribute);
			}
		}
		String[] customAttributeNames = new String[customAttributes.size()];
		for (int i = 0; i < customAttributeNames.length; i++) {
			customAttributeNames[i] = customAttributes.get(i).getName();
		}
		return customAttributeNames;
	}
	
	@SuppressWarnings("unchecked")
	public static String getCustomAttributeValueAsString(AvroNode node, String customAttributeName, AvroContext context) {
		CustomAttributeConfiguration customAttributeConfiguration = context.getCustomAttributeConfiguration();
		if (customAttributeConfiguration != null) {
			AvroAttribute<Object> customAttribute = (AvroAttribute<Object>) node.getAttributes().getAttribute(customAttributeName);		
			CustomAttributeIO<Object> customAttributeIO = (CustomAttributeIO<Object>) customAttributeConfiguration.getCustomAttributeIO(context, node.getType(), customAttributeName);
			return customAttributeIO.encodeAttributeValue(customAttribute);
		}
		return "";
	}
	
	public static boolean hasComplexType(AvroNode node) {
		return hasPrimitiveTypeAttribute(node) 
				&& (node.hasChildren(NodeType.ARRAY) 
						|| node.hasChildren(NodeType.MAP)
						|| node.hasChildren(NodeType.RECORD)
						|| node.hasChildren(NodeType.UNION)
						|| node.hasChildren(NodeType.FIXED)
						|| node.hasChildren(NodeType.ENUM)
						|| node.hasChildren(NodeType.REF)
						); 
	}
	
	public static boolean hasPrimitiveTypeAttribute(AvroNode node) {
		return hasPrimitiveTypeAttribute(node.getAttributes());
	}
	
	protected static boolean hasPrimitiveTypeAttribute(AvroAttributeSet attributes) {
		return attributes.isDefined(AvroAttributes.PRIMITIVE_TYPE);
	}
	
	public static PrimitiveType getPrimitiveType(AvroNode node) {
		return getPrimitiveType(node.getAttributes());
	}
	
	public static PrimitiveTypes getPrimitiveTypes(AvroNode node) {
		return node.getAttributes().getAttributeFromValueClass(AvroAttributes.PRIMITIVE_TYPE, PrimitiveTypes.class).getValue();
	}
	
	protected static PrimitiveType getPrimitiveType(AvroAttributeSet attributes) {
		PrimitiveTypeAttribute primitiveTypeAttribute = attributes.getAttributeFromClass(AvroAttributes.PRIMITIVE_TYPE, PrimitiveTypeAttribute.class);
		return primitiveTypeAttribute.getValue().getValue();
	}
	
	public static boolean isAttributeEnabled(AvroNode node, String attributeName) {
		return node.getAttributes().getAttribute(attributeName).isEnabled();
	}
	
	public static void setAttributeEnabled(AvroNode node, String attributeName, boolean enabled) {
		setAttributeEnabled(node.getAttributes(), attributeName, enabled);
	}
	
	protected static void setAttributeEnabled(AvroAttributeSet attributes, String attributeName, boolean enabled) {
		AvroAttribute<?> attribute = attributes.getAttribute(attributeName);
		if (attribute == null) {
			throw new IllegalArgumentException("Attribute " + attributeName + " does not exist!");
		}
		attribute.setEnabled(enabled);
	}
	
	public static boolean isAttributeVisible(AvroNode node, String attributeName) {
		return node.getAttributes().getAttribute(attributeName).isVisible();
	}
	
	public static void setAttributeVisible(AvroNode node, String attributeName, boolean visible) {
		node.getAttributes().getAttribute(attributeName).setVisible(visible);
	}
	
	@SuppressWarnings("unchecked")
	public static void setAttributeValue(AvroNode node, String attributeName, Object value) {
		AvroAttribute<Object> attribute = (AvroAttribute<Object>) node.getAttributes().getAttribute(attributeName);
		attribute.setValue(value);
	}	
	
	public static void setPrimitiveType(AvroNode node, PrimitiveType type) {
		AvroAttributeSet attributes = node.getAttributes();
		if (!attributes.isDefined(AvroAttributes.PRIMITIVE_TYPE)) {
			throw new IllegalArgumentException("Cannot set primitive type for this node");
		}
		PrimitiveTypeAttribute primTypeAttr = 
				(PrimitiveTypeAttribute) attributes.getAttribute(AvroAttributes.PRIMITIVE_TYPE);
		primTypeAttr.setType(type);
	}
	
	public static final boolean contains(String[] attributes, String attribute) {
		for (String attr : attributes) {
			if (attr.equals(attribute)) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isOptional(AvroNode node) {
		ModelUtil.checkNodeTypes(node, NodeType.TYPED_NODE_TYPES);
		AvroAttribute<Boolean> optionalAttr = (AvroAttribute<Boolean>) node.getAttributes().getAttribute(AvroAttributes.OPTIONAL);
		return optionalAttr.getValue();
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isChoiceType(AvroNode node) {
		AvroAttribute<Boolean> choiceTypeAttr = (AvroAttribute<Boolean>) node.getAttributes().getAttribute(AvroAttributes.CHOICE_TYPE);
		return choiceTypeAttr.getValue();
	}
	
	public static boolean hasAttribute(AvroNode node, String attrName) {
		return node.getAttributes().isDefined(attrName);
	}	

	public static void restoreAttribute(String attributeName, AvroNode sourceNode, AvroNode targetNode) {
		applyAttributeValue(attributeName, sourceNode, targetNode);
		setAttributeEnabled(targetNode, attributeName, isAttributeEnabled(sourceNode, attributeName));
		setAttributeVisible(targetNode, attributeName, isAttributeVisible(sourceNode, attributeName));
	}
	
	public static void applyAttributeValue(String attributeName, AvroNode sourceNode, AvroNode targetNode) {
		if (!AttributeUtil.hasAttribute(sourceNode, attributeName)) {
			throw new IllegalArgumentException("Source node has not attribute " + attributeName);
		}
		if (!AttributeUtil.hasAttribute(targetNode, attributeName)) {
			throw new IllegalArgumentException("Target node has not attribute " + attributeName);
		}
		switch (attributeName) {
		case AvroAttributes.PRIMITIVE_TYPE:
			PrimitiveTypes sourcePrimitiveTypes = getPrimitiveTypes(sourceNode);
			PrimitiveTypes targetPrimitiveTypes = getPrimitiveTypes(targetNode);
			targetPrimitiveTypes.setValue(sourcePrimitiveTypes.getValue());
			AvroAttribute<PrimitiveTypes> targetAttribute = targetNode.getAttributes().getAttributeFromValueClass(attributeName, PrimitiveTypes.class);
			targetAttribute.setValue(targetPrimitiveTypes);
			break;			
		default:
			applyAttributeSimpleValue(attributeName, sourceNode, targetNode);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected static void applyAttributeSimpleValue(String attributeName, AvroNode sourceNode, AvroNode targetNode) {		
		AvroAttribute<Object> sourceAttribute = (AvroAttribute<Object>) sourceNode.getAttributes().getAttribute(attributeName);
		AvroAttribute<Object> targetAttribute = (AvroAttribute<Object>) targetNode.getAttributes().getAttribute(attributeName);
		targetAttribute.setValue(sourceAttribute.getValue());
	}
	
	public static final boolean replaceAttribute(List<AvroAttribute<?>> attributes, AvroAttribute<?> attribute1, AvroAttribute<?> attribute2) {
		int index = attributes.indexOf(attribute1);
		if (index != -1) {
			attributes.add(index, attribute2);
			attributes.remove(attribute1);
			return true;
		}
		return false;
	}
	
	public static void checkAttributeValue(AvroNode node, String attrName, Object value) {
		checkAttributeIsDefined(node, attrName);
		@SuppressWarnings("unchecked")
		AvroAttribute<Object> attribute = (AvroAttribute<Object>) node.getAttributes().getAttribute(attrName);
		if (!attribute.getValue().equals(value)) {
			throw new IllegalStateException("Attribute " + attrName + " has invalid value");
		}
	}
	
	public static void checkAttributeIsDefined(AvroNode node, String attrName) {
		if (!node.getAttributes().isDefined(attrName)) {
			throw new IllegalArgumentException("Node " + node + " has no attribute " + attrName);
		}
	}	
	
}
