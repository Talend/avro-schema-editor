package org.talend.avro.schema.editor.io;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.apache.avro.SchemaBuilder.FieldTypeBuilder;
import org.apache.avro.SchemaBuilder.RecordBuilder;
import org.talend.avro.schema.editor.model.FieldNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.RecordNode;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * This class provides some convenient methods around the {@link Schema} avro API.
 * 
 * @author timbault
 *
 */
public class SchemaUtil {

	/**
	 * Return the first child of the given union which is not null.
	 * 
	 * @param unionSchema
	 * @return
	 */
	public static Schema getFirstNotNullSchema(Schema unionSchema) {
		List<Schema> unionTypes = unionSchema.getTypes();
		for (Schema unionType : unionTypes) {
			if (unionType.getType() != Type.NULL) {
				return unionType; 
			}
		}
		return null;
	}
	
	public static PrimitiveType getSinglePrimitiveTypeOfUnion(Schema fieldSchema) {
		PrimitiveType primitiveType = null;
		List<Schema> unionTypes = fieldSchema.getTypes();
		for (Schema unionType : unionTypes) {
			if (unionType.getType() != Type.NULL) {
				if (PrimitiveType.isPrimitive(unionType)) {
					if (primitiveType == null) {
						primitiveType = PrimitiveType.getPrimitiveType(unionType);
					} else {
						primitiveType = null;
						break;
					}
				} 
			}
		}
		return primitiveType;
	}
		
	public static boolean unionHasNullChild(Schema unionSchema) {
		return unionHasTypedChild(unionSchema, Type.NULL);
	}
	
	public static boolean unionHasTypedChild(Schema unionSchema, Type type) {
		List<Schema> unionTypes = unionSchema.getTypes();
		for (Schema unionType : unionTypes) {
			if (unionType.getType() == type) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isOptionalUnion(Schema unionSchema) {
		return unionHasNullChild(unionSchema);
	}
	
	public static boolean isMultiChoiceUnion(Schema unionSchema) {
		List<Schema> unionTypes = unionSchema.getTypes();
		int nbrOfNoNullChild = 0;
		for (Schema unionType : unionTypes) {
			if (unionType.getType() != Type.NULL) {
				nbrOfNoNullChild++;
			}
		}
		return nbrOfNoNullChild > 1;
	}
	
	/**
	 * Build a schema corresponding to the given record. It handles only fields of primitive type.
	 * 
	 * @param recordNode
	 * @return
	 */
	public static Schema getSchema(RecordNode recordNode) {
		String recordName = AttributeUtil.getNameFromAttribute(recordNode);
		RecordBuilder<?> recordBuilder = SchemaBuilder.record(recordName);
		FieldAssembler<?> fields = recordBuilder.fields();
		for (int i = 0; i < recordNode.getChildrenCount(); i++) {
			FieldNode fieldNode = (FieldNode) recordNode.getChild(i);
			String fieldName = AttributeUtil.getNameFromAttribute(fieldNode);
			FieldTypeBuilder<?> fieldTypeBuilder = fields.name(fieldName).type();
			if (ModelUtil.isTypedNodeOfPrimitiveType(fieldNode)) {				
				PrimitiveType primitiveType = ModelUtil.getPrimitiveTypeOfTypedNode(fieldNode);
				switch (primitiveType) {
				case BOOLEAN:
					fieldTypeBuilder.booleanType().noDefault();
					break;
				case BYTES:
					fieldTypeBuilder.bytesType().noDefault();
					break;
				case DOUBLE:
					fieldTypeBuilder.doubleType().noDefault();
					break;
				case FLOAT:
					fieldTypeBuilder.floatType().noDefault();
					break;
				case INT:
					fieldTypeBuilder.intType().noDefault();
					break;
				case LONG:
					fieldTypeBuilder.longType().noDefault();
					break;
				case STRING:
					fieldTypeBuilder.stringType().noDefault();
					break;
				case NULL:
					fieldTypeBuilder.nullType().noDefault();
					break;
				default:
					break;
				}
			}
		}
		return (Schema) fields.endRecord();
	}
	
	/**
	 * Return the type of the field.
	 * 
	 * @param field
	 * @return
	 */
	public static Type getFieldType(Field field) {
		Schema fieldSchema = field.schema();
		Type fieldSchemaType = fieldSchema.getType();
		if (fieldSchemaType == Type.UNION) {
			// specific case of union
			// check if it is an optional case or not
			if (isOptionalUnion(fieldSchema)) {
				return getFirstNotNullSchema(fieldSchema).getType();
			}
		}
		return fieldSchemaType;
	}
	
	/**
	 * Indicates if the field is optional.
	 * 
	 * @param field
	 * @return
	 */
	public static boolean isFieldOptional(Field field) {
		Schema fieldSchema = field.schema();
		Type fieldSchemaType = fieldSchema.getType();
		if (fieldSchemaType == Type.UNION) {
			return isOptionalUnion(fieldSchema);
		}
		return false;
	}	
	
	/**
	 * This method returns in a Map the primitive type of all the fields of the given schema.
	 * The schema must be a record (or an union with null and a record). 
	 * 
	 * @param schema record or union schema
	 * @return
	 */
	public static Map<String, PrimitiveType> getPrimitiveTypesOfRecordFields(Schema schema) {
		if (schema.getType() != Type.RECORD && schema.getType() != Type.UNION) {
			throw new IllegalArgumentException("Schema must be a record or an union");
		}
		Schema recordSchema = schema;
		if (schema.getType() == Type.UNION) {
			// get the record
			recordSchema = getFirstNotNullSchema(schema);
			if (recordSchema.getType() != Type.RECORD) {
				throw new IllegalArgumentException("Schema must be a record");
			}
		}
		Map<String, PrimitiveType> types = new HashMap<>();
		List<Field> fields = schema.getFields();
		for (Field field : fields) {
			Type fieldType = getFieldType(field);
			PrimitiveType primitiveType = PrimitiveType.getType(fieldType);
			if (primitiveType != null) {
				types.put(field.name(), primitiveType);
			}
		}
		return types;
	}
	
	/**
	 * Return the type of the values of the 'map' or 'array' schema. The schema must be a map or an array
	 * (or an union containing null and a map or an array).
	 * 
	 * @param schema
	 * @param type
	 * 
	 * @return
	 */
	public static PrimitiveType getPrimitiveTypeOfMapOrArrayValues(Schema schema, Schema.Type type) {
		if (type != Schema.Type.MAP && type != Schema.Type.ARRAY) {
			throw new IllegalArgumentException("Invalid type");
		}
		if (schema.getType() != type && schema.getType() != Type.UNION) {
			throw new IllegalArgumentException("Schema must be a map, an array or an union");
		}
		Schema theSchema = schema;
		if (schema.getType() == Type.UNION) {
			// get the map/array
			theSchema = getFirstNotNullSchema(schema);
			if (theSchema.getType() != type) {
				throw new IllegalArgumentException("Schema must be a map or an array");
			}
		}
		Schema valueType = null;
		if (type == Type.MAP) {
			valueType = theSchema.getValueType();
		} else {
			valueType = theSchema.getElementType();
		}
		Type theType = valueType.getType();
		return PrimitiveType.getType(theType);
	}
	
}
