package org.talend.avro.schema.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;

/**
 * This enumeration defines the different primitive types which can be used in an avro schema.
 * 
 * @author timbault
 *
 */
public enum PrimitiveType {
	NULL(Schema.Type.NULL),
	BOOLEAN(Schema.Type.BOOLEAN),
	INT(Schema.Type.INT),
	LONG(Schema.Type.LONG),
	FLOAT(Schema.Type.FLOAT),
	DOUBLE(Schema.Type.DOUBLE),
	BYTES(Schema.Type.BYTES),
	STRING(Schema.Type.STRING);
	
	private Schema.Type type;
	
	private PrimitiveType(Type type) {
		this.type = type;
	}

	public static PrimitiveType[] valuesWithoutNull() {
		return new PrimitiveType[] { BOOLEAN, INT, LONG, FLOAT, DOUBLE, BYTES, STRING };
	}
	
	public static List<PrimitiveType> getValuesAsList(boolean includeNull) {
		List<PrimitiveType> values = new ArrayList<>();
		for (PrimitiveType type : values()) {
			if (type != NULL || includeNull) {
				values.add(type);
			}
		}
		return values;
	}
	
	public String getName() {
		return this.toString().toLowerCase();
	}
	
	public static PrimitiveType getType(Schema schema) {
		return getType(schema.getType());
	}
	
	public static PrimitiveType getType(Schema.Type schemaType) {
		for (PrimitiveType type : PrimitiveType.values()) {
			if (type.type == schemaType) {
				return type;
			}
		}
		return null;
	}
	
	public static boolean isPrimitive(Schema schema) {
		return getType(schema) != null;
	}
	
	public static boolean isPrimitive(Schema.Type schemaType) {
		return getType(schemaType) != null;
	}
	
	public static boolean isPrimitiveType(Schema schema) {
		return getPrimitiveType(schema) != null;
	}
	
	public static PrimitiveType getPrimitiveType(Schema schema) {
		Type type = schema.getType();
		if (type == Type.ARRAY) {
			type = schema.getElementType().getType();
		} else if (type == Type.MAP) {
			type = schema.getValueType().getType();
		}
		if (isPrimitive(type)) {
			return getType(type);
		}
		return null;
	}
	
}
