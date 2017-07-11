package org.talend.avro.schema.editor.model.attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base implementation of an {@link AvroAttributeSet}.
 * 
 * @author timbault
 *
 */
public class AvroAttributesImpl implements AvroAttributeSet {

	private Map<String, AvroAttribute<?>> attributes = new HashMap<>();
	
	private List<String> sortedAttributeNames = new ArrayList<>();
	
	@Override
	public AvroAttribute<?> getAttribute(String name) {
		return attributes.get(name);
	}
	
	@Override
	public boolean isDefined(String name) {
		return getAttribute(name) != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AvroAttribute<?>> T getAttributeFromClass(String name, Class<T> attributeClass) {
		AvroAttribute<?> attribute = getAttribute(name);
		if (!attributeClass.isAssignableFrom(attribute.getClass())) {
			throw new IllegalArgumentException("Invalid attribute class");
		}
		return (T) attribute;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> AvroAttribute<T> getAttributeFromValueClass(String name, Class<T> attributeValueClass) {
		AvroAttribute<?> attribute = getAttribute(name);
		if (!attributeValueClass.isAssignableFrom(attribute.getValueClass())) {
			throw new IllegalArgumentException("Invalid attribute value class");
		}
		return (AvroAttribute<T>) attribute;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttributeValue(String name, Class<T> attributeValueClass) {
		AvroAttribute<?> attribute = getAttribute(name);
		if (!attributeValueClass.isAssignableFrom(attribute.getValueClass())) {
			throw new IllegalArgumentException("Invalid attribute value class");
		}
		return (T) attribute.getValue();
	}

	public void addAttribute(AvroAttribute<?> attribute) {
		if (attributes.get(attribute.getName()) != null) {
			throw new IllegalArgumentException("Attribute already registered");
		}
		String attrName = attribute.getName();
		attributes.put(attrName, attribute);
		sortedAttributeNames.add(attrName);
	}

	@Override
	public List<AvroAttribute<?>> getSortedAttributes() {
		List<AvroAttribute<?>> sortedAttributes = new ArrayList<>();
		for (String attrName : sortedAttributeNames) {
			sortedAttributes.add(getAttribute(attrName));
		}
		return sortedAttributes;
	}
	
}
