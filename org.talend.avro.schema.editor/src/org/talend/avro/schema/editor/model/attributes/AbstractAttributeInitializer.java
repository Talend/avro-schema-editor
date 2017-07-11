package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.attributes.custom.CustomAttributeConfiguration;
import org.talend.avro.schema.editor.model.attributes.custom.CustomAttributeIO;

/**
 * Abstract base implementation of an attribute initializer.
 * 
 * @author timbault
 *
 */
public abstract class AbstractAttributeInitializer implements AttributeInitializer {

	private AvroContext context;
	
	protected AbstractAttributeInitializer(AvroContext context) {
		super();
		this.context = context;
	}
	
	protected AvroContext getContext() {
		return context;
	}

	@Override
	public Object getInitialAttributeValue(NodeType type, String attributeName) {
		// custom attributes?
		if (isCustomAttribute(type, attributeName)) {
			return getCustomAttributeValue(type, attributeName);
		}
		return null;
	}
	
	@Override
	public boolean isVisible(NodeType type, String attributeName) {
		switch (attributeName) {
		case AvroAttributes.NAME:
			return isNameAttributeVisible(type);
		case AvroAttributes.PRIMITIVE_TYPE:
			return isPrimitiveTypeAttributeVisible(type);
		case AvroAttributes.CHOICE_TYPE:
			// choice type attribute is always hidden
			return false;
		default:
			return true;
		}
	}

	protected boolean isNameAttributeVisible(NodeType type) {
		switch (type) {
		case ARRAY:
		case MAP:
		case UNION:
		case PRIMITIVE_TYPE:
			return false;
		default:
			return true;
		}
	}
	
	protected boolean isPrimitiveTypeAttributeVisible(NodeType type) {
		switch (type) {
		case PRIMITIVE_TYPE:
			return true;
		default:
			return true;
		}
	}
	
	@Override
	public boolean isEnabled(NodeType type, String attributeName) {	
		return true;
	}
	
	protected abstract String getProperty(String attributeName);
	
	protected boolean isCustomAttribute(NodeType type, String attributeName) {
		return context.getCustomAttributeConfiguration() != null && context.getCustomAttributeConfiguration().isCustomAttribute(context, type, attributeName);
	}
	
	protected Object getCustomAttributeValue(NodeType type, String attributeName) {
		CustomAttributeConfiguration customAttributeConfiguration = context.getCustomAttributeConfiguration();
		String property = getProperty(attributeName);
		CustomAttributeIO<?> customAttributeIO = customAttributeConfiguration.getCustomAttributeIO(context, type, attributeName);		
		return customAttributeIO.decodeAttributeValue(attributeName, property);					
	}

	@Override
	public boolean provideInitialAttributeValue(NodeType type, String attributeName) {
		switch (attributeName) {
		case AvroAttributes.NAME:
			return type.isNamed();
		case AvroAttributes.PATH:
			return false;
		default:
			return true;
		}
	}	
	
}
