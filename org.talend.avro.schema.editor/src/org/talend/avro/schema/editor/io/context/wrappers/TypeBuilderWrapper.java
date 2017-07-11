package org.talend.avro.schema.editor.io.context.wrappers;

import org.apache.avro.SchemaBuilder.ArrayBuilder;
import org.apache.avro.SchemaBuilder.BaseFieldTypeBuilder;
import org.apache.avro.SchemaBuilder.BaseTypeBuilder;
import org.apache.avro.SchemaBuilder.EnumBuilder;
import org.apache.avro.SchemaBuilder.FieldBuilder;
import org.apache.avro.SchemaBuilder.FieldTypeBuilder;
import org.apache.avro.SchemaBuilder.FixedBuilder;
import org.apache.avro.SchemaBuilder.MapBuilder;
import org.apache.avro.SchemaBuilder.RecordBuilder;
import org.apache.avro.SchemaBuilder.TypeBuilder;
import org.apache.avro.SchemaBuilder.UnionAccumulator;
import org.apache.avro.SchemaBuilder.UnionFieldTypeBuilder;
import org.talend.avro.schema.editor.model.PrimitiveType;

public class TypeBuilderWrapper {

	private BaseTypeBuilder<?> baseTypeBuilder;
	
	private TypeBuilder<?> typeBuilder;
	
	private UnionFieldTypeBuilder<?> startUnion;

	private UnionAccumulator<?> continueUnion;	
	
	private FieldBuilder<?> fieldBuilder;
	
	private FieldTypeBuilder<?> fieldTypeBuilder;
	
	public TypeBuilderWrapper(Object object) {
		super();
		if (object instanceof UnionFieldTypeBuilder<?>) {
			startUnion = (UnionFieldTypeBuilder<?>) object;
		} 
		else if (object instanceof UnionAccumulator<?>) {
			continueUnion = (UnionAccumulator<?>) object;
		} 
		else if (object instanceof TypeBuilder<?>) {
			typeBuilder = (TypeBuilder<?>) object;
		}
		else if (object instanceof BaseTypeBuilder<?>) {
			baseTypeBuilder = (BaseTypeBuilder<?>) object;
		} 
		else if (object instanceof FieldBuilder<?>) {
			fieldBuilder = (FieldBuilder<?>) object;
		}
		else if (object instanceof FieldTypeBuilder<?>) {
			fieldTypeBuilder = (FieldTypeBuilder<?>) object;
		}
	}

	protected FieldTypeBuilder<?> getFieldTypeBuilder() {
		if (fieldTypeBuilder == null) {
			fieldTypeBuilder = fieldBuilder.type();
		}
		return fieldTypeBuilder;
	}
	
	protected boolean isFieldTypeBuilderAvailable() {
		return fieldBuilder != null || fieldTypeBuilder != null;
	}
	
	public Object union() {
		if (typeBuilder != null) {
			return typeBuilder.unionOf();
		}
		else if (isFieldTypeBuilderAvailable()) {
			return getFieldTypeBuilder().unionOf();
		}
		throw new UnsupportedOperationException();
	}
	
	public RecordBuilder<?> record(String name) {
		RecordBuilder<?> recordBuilder = null;
		if (startUnion != null) {
			recordBuilder = startUnion.record(name);			
		} else if (continueUnion != null) {
			BaseTypeBuilder<?> and = continueUnion.and();
			recordBuilder = and.record(name);			
		} else if (baseTypeBuilder != null) {
			recordBuilder = baseTypeBuilder.record(name);
		} else if (typeBuilder != null) {
			recordBuilder = typeBuilder.record(name);
		} else if (isFieldTypeBuilderAvailable()) {
			recordBuilder = getFieldTypeBuilder().record(name);
		} else {
			throw new UnsupportedOperationException();
		}
		return recordBuilder;
	}
	
	public EnumBuilder<?> enumeration(String name) {
		EnumBuilder<?> enumBuilder = null;
		if (startUnion != null) {
			enumBuilder = startUnion.enumeration(name);			
		} else if (continueUnion != null) {
			BaseTypeBuilder<?> and = continueUnion.and();
			enumBuilder = and.enumeration(name);
		} else if (baseTypeBuilder != null) {
			enumBuilder = baseTypeBuilder.enumeration(name);
		} else if (typeBuilder != null) {
			enumBuilder = typeBuilder.enumeration(name);
		} else if (isFieldTypeBuilderAvailable()) {
			enumBuilder = getFieldTypeBuilder().enumeration(name);
		} else {
			throw new UnsupportedOperationException();
		}
		return enumBuilder;
	}
	
	public FixedBuilder<?> fixed(String name) {
		FixedBuilder<?> fixedBuilder = null;
		if (startUnion != null) {
			fixedBuilder = startUnion.fixed(name);			
		} else if (continueUnion != null) {
			BaseTypeBuilder<?> and = continueUnion.and();
			fixedBuilder = and.fixed(name);
		} else if (baseTypeBuilder != null) {
			fixedBuilder = baseTypeBuilder.fixed(name);
		} else if (typeBuilder != null) {
			fixedBuilder = typeBuilder.fixed(name);
		} else if (isFieldTypeBuilderAvailable()) {
			fixedBuilder = getFieldTypeBuilder().fixed(name);
		} else {
			throw new UnsupportedOperationException();
		}
		return fixedBuilder;
	}
	
	public Object ref(String name) {
		if (fieldBuilder != null) {
			return fieldBuilder.type(name);
		} else if (continueUnion != null) {
			return continueUnion.and().type(name);
		} else if (typeBuilder != null) {
			return typeBuilder.type(name);
		} else if (baseTypeBuilder != null) {
			return baseTypeBuilder.type(name);
		} else if (startUnion != null) {
			// TODO !!!!!!????????
		}
		throw new UnsupportedOperationException();
	}
	
	public ArrayBuilder<?> array() {
		ArrayBuilder<?> arrayBuilder = null;
		if (startUnion != null) {
			arrayBuilder = startUnion.array();
		} else if (continueUnion != null) {
			BaseTypeBuilder<?> and = continueUnion.and();
			arrayBuilder = and.array();
		} else if (baseTypeBuilder != null) {
			arrayBuilder = baseTypeBuilder.array();
		} else if (typeBuilder != null) {
			arrayBuilder = typeBuilder.array();
		} else if (isFieldTypeBuilderAvailable()) {
			arrayBuilder = getFieldTypeBuilder().array();
		} else {
			throw new UnsupportedOperationException();
		}
		return arrayBuilder;
	}
	
	public MapBuilder<?> map() {
		MapBuilder<?> mapBuilder = null;
		if (startUnion != null) {
			mapBuilder = startUnion.map();
		} else if (continueUnion != null) {
			BaseTypeBuilder<?> and = continueUnion.and();
			mapBuilder = and.map();
		} else if (baseTypeBuilder != null) {
			mapBuilder = baseTypeBuilder.map();
		} else if (typeBuilder != null) {
			mapBuilder = typeBuilder.map();
		} else if (isFieldTypeBuilderAvailable()) {
			mapBuilder = getFieldTypeBuilder().map();
		} else {
			throw new UnsupportedOperationException();
		}
		return mapBuilder;
	}
	
	public Object primitiveType(PrimitiveType type) {
		if (startUnion != null) {
			return primitiveType(type, startUnion);
		} else if (continueUnion != null) {
			return primitiveType(type, continueUnion.and());
		} else if (typeBuilder != null) {
			return primitiveType(type, typeBuilder);
		} else if (baseTypeBuilder != null) {
			return primitiveType(type, baseTypeBuilder);
		} else if (isFieldTypeBuilderAvailable()) {
			return primitiveType(type, getFieldTypeBuilder());
		}
		throw new UnsupportedOperationException();
	}
	
	protected Object primitiveType(PrimitiveType primitiveType, UnionFieldTypeBuilder<?> startUnion) {
		UnionAccumulator<?> unionAccu = null;
		switch (primitiveType) {
		case NULL:
			unionAccu = startUnion.nullType();			
			break;
		case BOOLEAN:
			unionAccu = startUnion.booleanType();
			break;
		case BYTES:
			unionAccu = startUnion.bytesType();
			break;
		case DOUBLE:
			unionAccu = startUnion.doubleType();
			break;
		case FLOAT:
			unionAccu = startUnion.floatType();
			break;
		case INT:
			unionAccu = startUnion.intType();
			break;
		case LONG:
			unionAccu = startUnion.longType();
			break;
		case STRING:
			unionAccu = startUnion.stringType();
			break;
		}		
		return unionAccu;
	}
	
	protected Object primitiveType(PrimitiveType primitiveType, BaseTypeBuilder<?> typeBuilder) {
		Object type = null;
		switch (primitiveType) {
		case STRING:
			type = typeBuilder.stringType();
			break;
		case BOOLEAN:
			type = typeBuilder.booleanType();			
			break;
		case BYTES:
			type = typeBuilder.bytesType();
			break;
		case DOUBLE:
			type = typeBuilder.doubleType();
			break;
		case FLOAT:
			type = typeBuilder.floatType();
			break;
		case INT:
			type = typeBuilder.intType();
			break;
		case LONG:
			type = typeBuilder.longType();
			break;
		case NULL:
			type = typeBuilder.nullType();
			break;
		}
		return type;
	}
	
	protected Object primitiveType(PrimitiveType primitiveType, BaseFieldTypeBuilder<?> typeBuilder) {
		Object type = null;
		switch (primitiveType) {
		case STRING:
			type = typeBuilder.stringType();
			break;
		case BOOLEAN:
			type = typeBuilder.booleanType();			
			break;
		case BYTES:
			type = typeBuilder.bytesType();
			break;
		case DOUBLE:
			type = typeBuilder.doubleType();
			break;
		case FLOAT:
			type = typeBuilder.floatType();
			break;
		case INT:
			type = typeBuilder.intType();
			break;
		case LONG:
			type = typeBuilder.longType();
			break;
		case NULL:
			type = typeBuilder.nullType();
			break;
		}
		return type;
	}
	
}
