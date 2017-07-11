package org.talend.avro.schema.editor.io;

import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.attributes.AbstractAttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.model.attributes.CustomProperties;
import org.talend.avro.schema.editor.model.attributes.PrimitiveTypes;
import org.talend.avro.schema.editor.model.attributes.StringList;

/**
 * Attribute initializer used when parsing an avro schema file. It provides values for field attributes.
 * 
 * @author timbault
 *
 */
public class FieldAttributeInitializer extends AbstractAttributeInitializer {

	private Field field;
	
	public FieldAttributeInitializer(AvroContext context) {
		super(context);
	}
	
	public void setField(Field field) {
		this.field = field;
	}

	@Override
	public Object getInitialAttributeValue(NodeType type, String attributeName) {
		Schema fieldSchema = field.schema();
		switch (attributeName) {
		case AvroAttributes.NAME:
			return field.name();
		case AvroAttributes.DOC:
			return field.doc();
		case AvroAttributes.ALIASES:
			StringList aliases = new StringList();
			aliases.setValues(field.aliases());
			return aliases;
		case AvroAttributes.OPTIONAL:
			if (fieldSchema.getType() == Type.UNION) {
				return SchemaUtil.unionHasNullChild(fieldSchema);				
			}
			return false;
		case AvroAttributes.PRIMITIVE_TYPE:
			PrimitiveTypes primitiveTypes = new PrimitiveTypes(PrimitiveType.STRING);
			if (PrimitiveType.isPrimitive(fieldSchema)) {
				PrimitiveType primitiveType = PrimitiveType.getType(fieldSchema);
				primitiveTypes.setValue(primitiveType);
			} else {
				Type schemaType = fieldSchema.getType();
				switch (schemaType) {
				case UNION:
					// in case of union we have to check if this union has only one primitive type
					PrimitiveType primitiveType = SchemaUtil.getSinglePrimitiveTypeOfUnion(fieldSchema);
					if (primitiveType != null) {
						// ok there is only one child and it is a primitive type
						primitiveTypes.setValue(primitiveType);
					}
				default:
					break;
				}
			}
			return primitiveTypes;
		case AvroAttributes.CUSTOM_PROPERTIES:
			Map<String, Object> objectProps = field.getObjectProps();
			CustomProperties props = new CustomProperties();
			props.addProps(objectProps, true);
			return props;	
		case AvroAttributes.TYPE_PROPERTIES:
			CustomProperties typeProps = new CustomProperties();
			if (PrimitiveType.isPrimitive(fieldSchema)) {
				objectProps = fieldSchema.getObjectProps();
				typeProps.addProps(objectProps, true);
			}
			return typeProps;
		}
		return super.getInitialAttributeValue(type, attributeName);
	}
	
	@Override
	public boolean isEnabled(NodeType type, String attributeName) {
		Schema fieldSchema = field.schema();
		Type schemaType = fieldSchema.getType();
		switch (attributeName) {
		case AvroAttributes.PRIMITIVE_TYPE:			
			if (PrimitiveType.isPrimitive(fieldSchema)) {
				return true;
			} else {				
				switch (schemaType) {
				case UNION:
					// it is enabled if there is only one child with primitive type
					return SchemaUtil.getSinglePrimitiveTypeOfUnion(fieldSchema) != null;
				case ARRAY:					
				case MAP:
					return PrimitiveType.getPrimitiveType(fieldSchema) != null;
				default:
					return false;
				}
			}
		case AvroAttributes.OPTIONAL:
			// optional is always enabled
			return true;
		}
		return true;
	}

	@Override
	protected String getProperty(String attributeName) {		
		return field.getProp(attributeName);
	}
	
}
