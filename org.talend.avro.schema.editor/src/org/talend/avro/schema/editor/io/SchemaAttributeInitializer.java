package org.talend.avro.schema.editor.io;

import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.attributes.AbstractAttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.ArrayOrMapValue;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.model.attributes.CustomProperties;
import org.talend.avro.schema.editor.model.attributes.PrimitiveTypes;
import org.talend.avro.schema.editor.model.attributes.StringList;

/**
 * Attribute initializer used when parsing an avro schema file. It provides values for all the types of schemas except fields.
 * 
 * @author timbault
 *
 */
public class SchemaAttributeInitializer extends AbstractAttributeInitializer {

	private Schema schema;
	
	public SchemaAttributeInitializer(AvroContext context) {
		super(context);
	}
	
	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	@Override
	public Object getInitialAttributeValue(NodeType type, String attributeName) {
		
		Type schemaType = schema == null ? null : schema.getType();
		
		switch (attributeName) {
		case AvroAttributes.NAME:			
			return getSchemaName(schema, type);
		case AvroAttributes.NAME_SPACE:
			return schema.getNamespace();
		case AvroAttributes.DOC:
			return schema.getDoc();
		case AvroAttributes.ALIASES:
			StringList aliases = new StringList();
			aliases.setValues(schema.getAliases());
			return aliases;
		case AvroAttributes.OPTIONAL:
			Schema arrayOrMapType = null;
			if (type == NodeType.ARRAY) {
				arrayOrMapType = schema.getElementType();
			} else if (type == NodeType.MAP) {
				arrayOrMapType = schema.getValueType();
			}
			schemaType = arrayOrMapType.getType();
			if (schemaType == Type.UNION) {
				return SchemaUtil.unionHasNullChild(arrayOrMapType);				
			}
			return false;
		case AvroAttributes.SYMBOLS:
			StringList symbols = new StringList();
			symbols.setValues(schema.getEnumSymbols());
			return symbols;
		case AvroAttributes.SIZE:
			return schema.getFixedSize();
		case AvroAttributes.PRIMITIVE_TYPE:
			PrimitiveType primitiveType = PrimitiveType.STRING;			
			if (type == NodeType.ARRAY) {
				schemaType = schema.getElementType().getType();
			} else if (type == NodeType.MAP) {
				schemaType = schema.getValueType().getType();
			} else if (type == NodeType.UNION) {
				PrimitiveType unionPrimitiveType = SchemaUtil.getSinglePrimitiveTypeOfUnion(schema);
				if (unionPrimitiveType != null) {
					primitiveType = unionPrimitiveType;
				}
			}
			if (PrimitiveType.isPrimitive(schemaType)) {
				primitiveType = PrimitiveType.getType(schemaType);
			}
			PrimitiveTypes primitiveTypes = new PrimitiveTypes(primitiveType);			
			return primitiveTypes;
		case AvroAttributes.CHOICE_TYPE:
			if (schema.getType() == Type.UNION) {
				return SchemaUtil.isMultiChoiceUnion(schema);
			}
			return false;
		case AvroAttributes.CUSTOM_PROPERTIES:
			Map<String, Object> objectProps = schema.getObjectProps();
			CustomProperties props = new CustomProperties();
			props.addProps(objectProps, true);
			return props;
		case AvroAttributes.TYPE_PROPERTIES:
			CustomProperties typeProps = new CustomProperties();
			objectProps = null;
			if (type == NodeType.ARRAY) {
				objectProps = schema.getElementType().getObjectProps();
			} else if (type == NodeType.MAP) {
				objectProps = schema.getValueType().getObjectProps();
			} else if (PrimitiveType.isPrimitive(schemaType)) {
				objectProps = schema.getObjectProps();
			}
			if (objectProps != null) {
				typeProps.addProps(objectProps, true);
			}
			return typeProps;
		case AvroAttributes.ARRAY_OR_MAP:
			if (type == NodeType.ARRAY || type == NodeType.MAP) {
				return new ArrayOrMapValue(type);
			}
			return null;
		}
		return super.getInitialAttributeValue(type, attributeName);
	}

	protected String getSchemaName(Schema schema, NodeType type) {
		switch (type) {
		case UNION:
		case ARRAY:
		case MAP:
			return type.toString().toLowerCase();
		case PRIMITIVE_TYPE:
			PrimitiveType primitiveType = PrimitiveType.getPrimitiveType(schema);
			return primitiveType.getName();
		case ROOT:
			return "root";
		default:
			return schema.getName();
		}
	}	
	
	@Override
	public boolean isEnabled(NodeType type, String attributeName) {
		switch (attributeName) {
		case AvroAttributes.PRIMITIVE_TYPE:
			return PrimitiveType.getPrimitiveType(schema) != null;
		case AvroAttributes.CHOICE_TYPE:
			// multi choice is always enabled
			return true;
		}
		return super.isEnabled(type, attributeName);
	}

	@Override
	protected String getProperty(String attributeName) {
		return schema.getProp(attributeName);
	}
	
}
