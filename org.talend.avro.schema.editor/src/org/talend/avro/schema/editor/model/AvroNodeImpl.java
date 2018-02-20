package org.talend.avro.schema.editor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.talend.avro.schema.editor.Defines;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AliasesAttribute;
import org.talend.avro.schema.editor.model.attributes.ArrayOrMapAttribute;
import org.talend.avro.schema.editor.model.attributes.ArrayOrMapValue;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.AvroAttributeImpl;
import org.talend.avro.schema.editor.model.attributes.AvroAttributeSet;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.model.attributes.AvroAttributesImpl;
import org.talend.avro.schema.editor.model.attributes.BooleanAttribute;
import org.talend.avro.schema.editor.model.attributes.CustomProperties;
import org.talend.avro.schema.editor.model.attributes.CustomPropertiesAttribute;
import org.talend.avro.schema.editor.model.attributes.DefaultValue;
import org.talend.avro.schema.editor.model.attributes.DefaultValueAttribute;
import org.talend.avro.schema.editor.model.attributes.DocAttribute;
import org.talend.avro.schema.editor.model.attributes.IntegerAttribute;
import org.talend.avro.schema.editor.model.attributes.NameAttribute;
import org.talend.avro.schema.editor.model.attributes.NameProvider;
import org.talend.avro.schema.editor.model.attributes.NameSpaceAttribute;
import org.talend.avro.schema.editor.model.attributes.NotEditableNameAttribute;
import org.talend.avro.schema.editor.model.attributes.PathAttribute;
import org.talend.avro.schema.editor.model.attributes.PrimitiveTypeAttribute;
import org.talend.avro.schema.editor.model.attributes.PrimitiveTypes;
import org.talend.avro.schema.editor.model.attributes.StringList;
import org.talend.avro.schema.editor.model.attributes.SymbolsAttribute;
import org.talend.avro.schema.editor.model.attributes.custom.CustomAttributeConfiguration;
import org.talend.avro.schema.editor.model.path.PathService;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * Base abstract implementation of an avro node.
 * 
 * @author timbault
 *
 */
public abstract class AvroNodeImpl implements AvroNode {

	private NodeType type;
	
	private AvroNode parent;
	
	private List<AvroNode> children = new ArrayList<>();
	
	private AvroAttributesImpl attributes = new AvroAttributesImpl();
	
	private AvroContext context;
	
	public AvroNodeImpl(NodeType type, AvroContext context) {
		super();
		this.type = type;
		this.context = context;
	}
	
	public void init(AttributeInitializer initializer) {
		addPathAttribute(initializer);
		addCustomAttributes(initializer);
	}
	
	public NodeType getType() {
		return type;
	}

	protected AvroContext getContext() {
		return context;
	}
	
	@Override
	public boolean hasParent() {		
		return parent != null;
	}

	@Override
	public AvroNode getParent() {
		return parent;
	}

	@Override
	public void setParent(AvroNode parent) {
		this.parent = parent;
	}

	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}
	
	public boolean hasChildren(NodeType type) {
		return !getChildren(type).isEmpty();
	}

	@Override
	public boolean hasChild(AvroNode child) {
		return children.contains(child);
	}
	
	@Override
	public AvroNode getChild(int index) {
		return children.get(index);
	}

	@Override
	public int getChildIndex(AvroNode child) {
		if (!children.contains(child)) {
			throw new IllegalArgumentException("Unknown child");
		}
		return children.indexOf(child);
	}
	
	@Override
	public int getChildrenCount() {
		return children.size();
	}

	@Override
	public List<AvroNode> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	@Override
	public List<AvroNode> getChildren(NodeType type) {
		List<AvroNode> typedChildren = new ArrayList<>();
		for (AvroNode child : children) {
			if (child.getType() == type) {
				typedChildren.add(child);
			}
		}
		return typedChildren;
	}

	@Override
	public void addChild(AvroNode child) {
		if (children.contains(child)) {
			throw new IllegalArgumentException("Child already present");
		}
		children.add(child);		
	}
	
	@Override
	public void addChild(AvroNode child, int index) {
		if (children.contains(child)) {
			throw new IllegalArgumentException("Child already present");
		}
		children.add(index, child);
	}

	@Override
	public void addChild(AvroNode child, AvroNode target, TargetPosition position) {
		if (children.contains(child)) {
			throw new IllegalArgumentException("Child already present");
		}
		if (target != null &&!children.contains(target)) {
			throw new IllegalArgumentException("Unknown target node");
		}		
		if (target == null) {
			// last position by default
			children.add(child);
		} else {
			int targetIndex = children.indexOf(target);
			if (position == TargetPosition.AFTER || (Defines.CONVERT_UPON_TO_AFTER && position == TargetPosition.UPON)) {
				targetIndex++;
			}
			children.add(targetIndex, child);
		}
	}
	
	@Override
	public void moveChild(AvroNode child, AvroNode target, TargetPosition position) {
		if (!children.contains(child)) {
			throw new IllegalArgumentException("Unknown child");
		}
		if (!children.contains(target)) {
			throw new IllegalArgumentException("Unknown target node");
		}
		children.remove(child);
		addChild(child, target, position);
	}

	@Override
	public void removeChild(AvroNode child) {
		children.remove(child);
	}	
	
	@Override
	public AvroAttributeSet getAttributes() {
		return attributes;
	}

	protected boolean visitChildNodes(AvroNode node, IAvroNodeVisitor visitor) {
		boolean continueVisit = true;
		for (AvroNode childNode : node.getChildren()) {
			if (!childNode.visitNode(visitor)) {
				continueVisit = false;
				break;
			}
		}
		return continueVisit;
	}
	
	@SuppressWarnings("unchecked")
	protected final <T> void initializeAll(AvroAttribute<T> attribute, AttributeInitializer initializer) {
		NodeType type = attribute.getHolder().getType();
		String attributeName = attribute.getName();
		if (initializer.provideInitialAttributeValue(type, attributeName)) {
			attribute.setValue((T) initializer.getInitialAttributeValue(type, attributeName));
		}
		attribute.setVisible(initializer.isVisible(type, attributeName));
		attribute.setEnabled(initializer.isEnabled(type, attributeName));
		attributes.addAttribute(attribute);
	}
	
	protected final <T> void initializeStates(AvroAttribute<T> attribute, AttributeInitializer initializer) {
		NodeType type = attribute.getHolder().getType();
		String attributeName = attribute.getName();
		attribute.setVisible(initializer.isVisible(type, attributeName));
		attribute.setEnabled(initializer.isEnabled(type, attributeName));
		attributes.addAttribute(attribute);
	}
	
	@SuppressWarnings("unchecked")
	protected final <T> void initializeValue(AvroAttribute<T> attribute, AttributeInitializer initializer) {
		NodeType type = attribute.getHolder().getType();
		String attributeName = attribute.getName();
		if (initializer.provideInitialAttributeValue(type, attributeName)) {
			attribute.setValue((T) initializer.getInitialAttributeValue(type, attributeName));
		}
		attributes.addAttribute(attribute);
	}
	
	protected final AvroAttribute<Boolean> addBooleanAttribute(String attributeName, boolean defaultValue, AttributeInitializer initializer) {
		AvroAttribute<Boolean> booleanAttribute = new BooleanAttribute(this, attributeName, defaultValue);
		initializeAll(booleanAttribute, initializer);
		return booleanAttribute;
	}
	
	protected final AvroAttribute<Integer> addIntegerAttribute(String attributeName, int defaultValue, AttributeInitializer initializer) {
		AvroAttribute<Integer> intAttribute = new IntegerAttribute(this, attributeName, defaultValue);
		initializeAll(intAttribute, initializer);
		return intAttribute;
	}
	
	protected final AvroAttribute<String> addPathAttribute(AttributeInitializer initializer) {
		PathAttribute pathAttribute = new PathAttribute(this, context.getService(PathService.class));
		initializeAll(pathAttribute, initializer);
		return pathAttribute;
	}
	
	protected final AvroAttribute<String> addNameAttribute(AttributeInitializer initializer) {
		NameAttribute nameAttribute = new NameAttribute(this);
		initializeAll(nameAttribute, initializer);
		return nameAttribute;
	}
	
	protected final AvroAttribute<String> addNotEditableNameAttribute(AttributeInitializer initializer, NameProvider nameProvider) {
		NotEditableNameAttribute nameAttribute = new NotEditableNameAttribute(this, nameProvider);
		initializeStates(nameAttribute, initializer);
		return nameAttribute;
	}
	
	protected final AvroAttribute<String> addNameSpaceAttribute(AttributeInitializer initializer) {
		NameSpaceAttribute nameSpaceAttribute = new NameSpaceAttribute(this);
		initializeAll(nameSpaceAttribute, initializer);
		return nameSpaceAttribute;
	}
	
	protected final AvroAttribute<String> addDocAttribute(AttributeInitializer initializer) {
		DocAttribute docAttribute = new DocAttribute(this);
		initializeAll(docAttribute, initializer);
		return docAttribute;
	}
	
	protected final AvroAttribute<DefaultValue> addDefaultValueAttribute(AttributeInitializer initializer) {
		DefaultValueAttribute defaultValueAttribute = new DefaultValueAttribute(this);
		initializeAll(defaultValueAttribute, initializer);
		return defaultValueAttribute;
	}
	
	protected final AvroAttributeImpl<StringList> addAliasesAttribute(AttributeInitializer initializer) {
		AliasesAttribute aliasesAttribute = new AliasesAttribute(this, new StringList());
		initializeAll(aliasesAttribute, initializer);
		return aliasesAttribute;
	}
	
	protected final AvroAttribute<PrimitiveTypes> addPrimitiveTypeAttribute(AttributeInitializer initializer) {
		PrimitiveTypeAttribute primitiveTypeAttribute = new PrimitiveTypeAttribute(this, new PrimitiveTypes(PrimitiveType.STRING));
		initializeAll(primitiveTypeAttribute, initializer);
		return primitiveTypeAttribute;
	}
	
	protected final AvroAttribute<ArrayOrMapValue> addArrayOrMapAttribute(AttributeInitializer initializer) {
		ArrayOrMapAttribute arrayOrMapAttr = new ArrayOrMapAttribute(this);
		initializeAll(arrayOrMapAttr, initializer);
		return arrayOrMapAttr;
	}
	
	protected PrimitiveTypeAttribute getPrimitiveTypeAttribute() {
		if (!attributes.isDefined(AvroAttributes.PRIMITIVE_TYPE)) {
			throw new IllegalStateException("No primitive type attribute");
		}
		return attributes.getAttributeFromClass(AvroAttributes.PRIMITIVE_TYPE, PrimitiveTypeAttribute.class);
	}
	
	protected final AvroAttribute<StringList> addSymbolsAttribute(AttributeInitializer initializer) {
		SymbolsAttribute symbolsAttribute = new SymbolsAttribute(this, new StringList());
		initializeAll(symbolsAttribute, initializer);
		return symbolsAttribute;
	}
	
	protected final AvroAttribute<CustomProperties> addCustomPropertiesAttribute(String attributeName, AttributeInitializer initializer) {
		CustomPropertiesAttribute propsAttr = new CustomPropertiesAttribute(this, attributeName);
		initializeAll(propsAttr, initializer);
		return propsAttr;
	}
	
	protected final void addCustomAttributes(AttributeInitializer initializer) {
		CustomAttributeConfiguration customAttributeConfiguration = context.getCustomAttributeConfiguration();
		if (customAttributeConfiguration != null) {
			AvroAttribute<Object>[] customAttributes = customAttributeConfiguration.configureAttributes(context, this, attributes);
			if (customAttributes != null) {
				for (AvroAttribute<Object> customAttr : customAttributes) {
					initializeValue(customAttr, initializer);
					// we don't change visible and enable state since it is already set in configureAttributes method
				}
			}
		}
	}

	@Override
	public String toString() {
		if (AttributeUtil.hasNameAttribute(this)) {
			return AttributeUtil.getNameFromAttribute(this);
		}
		return super.toString();
	}
	
}
