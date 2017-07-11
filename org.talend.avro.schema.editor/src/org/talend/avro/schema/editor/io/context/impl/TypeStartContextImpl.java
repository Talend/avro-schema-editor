package org.talend.avro.schema.editor.io.context.impl;

import org.apache.avro.SchemaBuilder.ArrayBuilder;
import org.apache.avro.SchemaBuilder.EnumBuilder;
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
import org.talend.avro.schema.editor.io.context.TypeStartContext;
import org.talend.avro.schema.editor.io.context.UnionContext;
import org.talend.avro.schema.editor.io.context.wrappers.TypeBuilderWrapper;
import org.talend.avro.schema.editor.model.PrimitiveType;

public class TypeStartContextImpl implements TypeStartContext {

	private TypeBuilderWrapper typeBuilder;

	public TypeStartContextImpl(Object object) {
		super();
		this.typeBuilder = new TypeBuilderWrapper(object);
	}

	@Override
	public FixedContext fixed(String name) {
		FixedBuilder<?> fixedBuilder = typeBuilder.fixed(name);
		return new FixedContextImpl(fixedBuilder);
	}

	@Override
	public EnumContext enumeration(String name) {
		EnumBuilder<?> enumBuilder = typeBuilder.enumeration(name);
		return new EnumContextImpl(enumBuilder);
	}

	@Override
	public PrimitiveTypeFinishContext primitiveType(PrimitiveType primitiveType) {
		Object type = typeBuilder.primitiveType(primitiveType);
		return new PrimitiveTypeContextImpl(type);
	}

	@Override
	public UnionContext union() {
		Object union = typeBuilder.union();
		return new IntermediateUnionContextImpl(union);
	}

	@Override
	public RecordContext record(String name) {
		RecordBuilder<?> recordBuilder = typeBuilder.record(name);
		return new RecordContextImpl(recordBuilder);
	}

	@Override
	public ArrayContext array() {
		ArrayBuilder<?> arrayBuilder = typeBuilder.array();
		return new ArrayOrMapContextImpl(arrayBuilder);
	}

	@Override
	public MapContext map() {
		MapBuilder<?> mapBuilder = typeBuilder.map();
		return new ArrayOrMapContextImpl(mapBuilder);
	}

	@Override
	public RefFinishContext ref(String name) {
		Object ref = typeBuilder.ref(name);
		return new RefContextImpl(ref);
	}	
	
}
