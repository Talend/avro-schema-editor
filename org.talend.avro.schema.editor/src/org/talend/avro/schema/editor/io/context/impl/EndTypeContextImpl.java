package org.talend.avro.schema.editor.io.context.impl;

import org.apache.avro.SchemaBuilder.ArrayBuilder;
import org.apache.avro.SchemaBuilder.EnumBuilder;
import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.apache.avro.SchemaBuilder.FixedBuilder;
import org.apache.avro.SchemaBuilder.MapBuilder;
import org.apache.avro.SchemaBuilder.RecordBuilder;
import org.talend.avro.schema.editor.io.context.ArrayContext;
import org.talend.avro.schema.editor.io.context.EnumContext;
import org.talend.avro.schema.editor.io.context.FixedContext;
import org.talend.avro.schema.editor.io.context.MapContext;
import org.talend.avro.schema.editor.io.context.PrimitiveTypeFinishContext;
import org.talend.avro.schema.editor.io.context.RecordContext;
import org.talend.avro.schema.editor.io.context.RefFinishContext;
import org.talend.avro.schema.editor.io.context.SchemaContext;
import org.talend.avro.schema.editor.io.context.TypeFinishContext;
import org.talend.avro.schema.editor.io.context.wrappers.FieldDefaultWrapper;
import org.talend.avro.schema.editor.io.context.wrappers.TypeBuilderWrapper;
import org.talend.avro.schema.editor.io.context.wrappers.UnionWrapper;
import org.talend.avro.schema.editor.model.PrimitiveType;

public class EndTypeContextImpl implements TypeFinishContext {

	private Object endType;
	
	private TypeBuilderWrapper typeBuilder;
	
	private FieldDefaultWrapper fieldDefault;
	
	private UnionWrapper union;
	
	public EndTypeContextImpl(Object endType) {
		super();
		this.endType = endType;
		union = new UnionWrapper(endType);
		fieldDefault = new FieldDefaultWrapper(endType);
		typeBuilder = new TypeBuilderWrapper(endType);
	}

	@Override
	public SchemaContext noDefault() {
		FieldAssembler<?> fieldAssembler = fieldDefault.noDefault();
		return new FieldsContextImpl(fieldAssembler);
	}

	@Override
	public SchemaContext setDefaultValue(Object defaultValue) {
		FieldAssembler<?> fieldAssembler = fieldDefault.setDefaultValue(defaultValue);
		return new FieldsContextImpl(fieldAssembler);
	}

	@Override
	public TypeFinishContext endMap() {
		return new EndTypeContextImpl(endType);
	}

	@Override
	public TypeFinishContext endArray() {
		return new EndTypeContextImpl(endType);
	}

	@Override
	public RecordContext record(String name) {
		RecordBuilder<?> recordBuilder = typeBuilder.record(name);
		return new RecordContextImpl(recordBuilder);
	}

	@Override
	public EnumContext enumeration(String name) {
		EnumBuilder<?> enumBuilder = typeBuilder.enumeration(name);
		return new EnumContextImpl(enumBuilder);
	}

	@Override
	public FixedContext fixed(String name) {
		FixedBuilder<?> fixedBuilder = typeBuilder.fixed(name);
		return new FixedContextImpl(fixedBuilder);
	}

	@Override
	public PrimitiveTypeFinishContext primitiveType(PrimitiveType primitiveType) {
		Object type = typeBuilder.primitiveType(primitiveType);
		return new PrimitiveTypeContextImpl(type);
	}

	@Override
	public MapContext map() {
		MapBuilder<?> mapBuilder = typeBuilder.map();
		return new ArrayOrMapContextImpl(mapBuilder);
	}

	@Override
	public ArrayContext array() {
		ArrayBuilder<?> arrayBuilder = typeBuilder.array();
		return new ArrayOrMapContextImpl(arrayBuilder);
	}

	@Override
	public RefFinishContext ref(String name) {
		Object ref = typeBuilder.ref(name);
		return new RefContextImpl(ref);
	}

	@Override
	public SchemaContext endUnion() {
		Object endUnion = union.endUnion();
		return new EndUnionContextImpl(endUnion);
	}

}
