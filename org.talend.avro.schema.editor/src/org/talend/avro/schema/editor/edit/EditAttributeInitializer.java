package org.talend.avro.schema.editor.edit;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.attributes.AbstractAttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.ArrayOrMapValue;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.model.attributes.CustomProperties;
import org.talend.avro.schema.editor.model.attributes.PrimitiveTypes;
import org.talend.avro.schema.editor.model.attributes.StringList;

/**
 * Attribute initializer used when editing an avro schema (e.g. when adding new record or field in the editor).
 * <p>
 * 
 * @author timbault
 *
 */
public class EditAttributeInitializer extends AbstractAttributeInitializer {
	
	public EditAttributeInitializer(AvroContext context) {
		super(context);
	}

	@Override
	public Object getInitialAttributeValue(NodeType type, String attributeName) {
		switch (attributeName) {
		case AvroAttributes.NAME:			
			return getNodeName(type);
		case AvroAttributes.NAME_SPACE:
			return getContext().getEnclosingNameSpace();
		case AvroAttributes.DOC:
			return "";
		case AvroAttributes.ALIASES:
			StringList aliases = new StringList();			
			return aliases;
		case AvroAttributes.SYMBOLS:
			StringList symbols = new StringList();			
			return symbols;
		case AvroAttributes.SIZE:
			return 1;
		case AvroAttributes.ARRAY_OR_MAP:
			if (type == NodeType.ARRAY || type == NodeType.MAP) {
				return new ArrayOrMapValue(type);
			}
			return null;
		case AvroAttributes.OPTIONAL:
			return false;
		case AvroAttributes.CHOICE_TYPE:
			return false;
		case AvroAttributes.PRIMITIVE_TYPE:
			return new PrimitiveTypes(getContext().getDefaultPrimitiveType());
		case AvroAttributes.CUSTOM_PROPERTIES:
			return new CustomProperties();
		case AvroAttributes.TYPE_PROPERTIES:
			return new CustomProperties();
		}
		return super.getInitialAttributeValue(type, attributeName);
	}

	protected String getNodeName(NodeType type) {
		switch (type) {
		case FIELD:
		case RECORD:
		case ENUM:
		case FIXED:
			return getContext().getAvailableName(type);
		default:
			return type.toString().toLowerCase();
		}
	}

	@Override
	protected String getProperty(String attributeName) {
		return null; // not implemented
	}

	@Override
	public boolean provideInitialAttributeValue(NodeType type, String attributeName) {
		if (isCustomAttribute(type, attributeName)) {
			return false;
		}
		return super.provideInitialAttributeValue(type, attributeName);
	}
	
}
