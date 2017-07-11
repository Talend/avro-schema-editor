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
import org.talend.avro.schema.editor.io.context.SchemaContext;
import org.talend.avro.schema.editor.io.context.UnionContext;
import org.talend.avro.schema.editor.io.context.UnionFinishContext;
import org.talend.avro.schema.editor.io.context.wrappers.TypeBuilderWrapper;
import org.talend.avro.schema.editor.io.context.wrappers.UnionWrapper;
import org.talend.avro.schema.editor.model.PrimitiveType;

public class IntermediateUnionContextImpl implements UnionContext, UnionFinishContext {

	private TypeBuilderWrapper typeBuilder;
	
	private UnionWrapper union;
	
	public IntermediateUnionContextImpl(Object object) {
		super();
		typeBuilder = new TypeBuilderWrapper(object);		
		union = new UnionWrapper(object);
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
	public PrimitiveTypeFinishContext primitiveType(PrimitiveType primitiveType) {
		Object type = typeBuilder.primitiveType(primitiveType);
		return new PrimitiveTypeContextImpl(type);
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
