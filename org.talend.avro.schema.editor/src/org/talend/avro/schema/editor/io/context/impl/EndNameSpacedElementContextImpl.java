package org.talend.avro.schema.editor.io.context.impl;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder.ArrayBuilder;
import org.apache.avro.SchemaBuilder.BaseTypeBuilder;
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
import org.talend.avro.schema.editor.io.context.RootFinishContext;
import org.talend.avro.schema.editor.io.context.SchemaContext;
import org.talend.avro.schema.editor.io.context.TypeFinishContext;
import org.talend.avro.schema.editor.io.context.wrappers.FieldDefaultWrapper;
import org.talend.avro.schema.editor.io.context.wrappers.SchemaWrapper;
import org.talend.avro.schema.editor.io.context.wrappers.TypeBuilderWrapper;
import org.talend.avro.schema.editor.io.context.wrappers.UnionWrapper;
import org.talend.avro.schema.editor.model.PrimitiveType;

public class EndNameSpacedElementContextImpl implements TypeFinishContext, RootFinishContext {
	
	private Object object;
	
	private FieldDefaultWrapper fieldDefault;
	
	private UnionWrapper union;
	
	private SchemaWrapper schema;
	
	public EndNameSpacedElementContextImpl(Object object) {
		this.object = object;
		union = new UnionWrapper(object);
		schema = new SchemaWrapper(object);
		fieldDefault = new FieldDefaultWrapper(object);
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
	public SchemaContext endUnion() {
		Object endUnion = union.endUnion();
		return new EndUnionContextImpl(endUnion);
	}

	@Override
	public RecordContext record(String name) {		
		BaseTypeBuilder<?> and = union.and();
		RecordBuilder<?> recordBuilder = and.record(name);
		return new RecordContextImpl(recordBuilder);
	}

	@Override
	public FixedContext fixed(String name) {		
		BaseTypeBuilder<?> and = union.and();
		FixedBuilder<?> fixedBuilder = and.fixed(name);
		return new FixedContextImpl(fixedBuilder);
	}

	@Override
	public EnumContext enumeration(String name) {
		BaseTypeBuilder<?> and = union.and();
		EnumBuilder<?> enumBuilder = and.enumeration(name);
		return new EnumContextImpl(enumBuilder);
	}

	@Override
	public Schema endRoot() {		
		return schema.getSchema();
	}

	@Override
	public TypeFinishContext endMap() {
		return new EndTypeContextImpl(object);
	}

	@Override
	public TypeFinishContext endArray() {
		return new EndTypeContextImpl(object);
	}

	@Override
	public PrimitiveTypeFinishContext primitiveType(PrimitiveType primitiveType) {
		BaseTypeBuilder<?> and = union.and();
		TypeBuilderWrapper typeBuilder = new TypeBuilderWrapper(and);
		Object type = typeBuilder.primitiveType(primitiveType);
		return new PrimitiveTypeContextImpl(type);
	}

	@Override
	public MapContext map() {
		BaseTypeBuilder<?> and = union.and();
		MapBuilder<?> mapBuilder = and.map();
		return new ArrayOrMapContextImpl(mapBuilder);
	}

	@Override
	public ArrayContext array() {
		BaseTypeBuilder<?> and = union.and();
		ArrayBuilder<?> arrayBuilder = and.array();
		return new ArrayOrMapContextImpl(arrayBuilder);
	}

	@Override
	public RefFinishContext ref(String name) {
		BaseTypeBuilder<?> and = union.and();
		TypeBuilderWrapper typeBuilder = new TypeBuilderWrapper(and);
		Object ref = typeBuilder.ref(name);
		return new RefContextImpl(ref);
	}
	
}
