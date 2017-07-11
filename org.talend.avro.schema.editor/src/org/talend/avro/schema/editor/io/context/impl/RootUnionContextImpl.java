package org.talend.avro.schema.editor.io.context.impl;

import org.apache.avro.SchemaBuilder.BaseTypeBuilder;
import org.apache.avro.SchemaBuilder.EnumBuilder;
import org.apache.avro.SchemaBuilder.FixedBuilder;
import org.apache.avro.SchemaBuilder.RecordBuilder;
import org.talend.avro.schema.editor.io.context.ArrayContext;
import org.talend.avro.schema.editor.io.context.EnumContext;
import org.talend.avro.schema.editor.io.context.FixedContext;
import org.talend.avro.schema.editor.io.context.MapContext;
import org.talend.avro.schema.editor.io.context.PrimitiveTypeFinishContext;
import org.talend.avro.schema.editor.io.context.RecordContext;
import org.talend.avro.schema.editor.io.context.RefFinishContext;
import org.talend.avro.schema.editor.io.context.UnionContext;
import org.talend.avro.schema.editor.model.PrimitiveType;

public class RootUnionContextImpl implements UnionContext {
	
	private BaseTypeBuilder<?> rootUnion;
	
	public RootUnionContextImpl(BaseTypeBuilder<?> rootUnion) {
		super();
		this.rootUnion = rootUnion;
	}	

	@Override
	public RecordContext record(String name) {		
		RecordBuilder<?> recordBuilder = rootUnion.record(name);
		return new RecordContextImpl(recordBuilder);
	}

	@Override
	public EnumContext enumeration(String name) {
		EnumBuilder<?> enumBuilder = rootUnion.enumeration(name);
		return new EnumContextImpl(enumBuilder);
	}

	@Override
	public FixedContext fixed(String name) {
		FixedBuilder<?> fixedBuilder = rootUnion.fixed(name);
		return new FixedContextImpl(fixedBuilder);
	}

	@Override
	public PrimitiveTypeFinishContext primitiveType(PrimitiveType primitiveType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ArrayContext array() {
		throw new UnsupportedOperationException();
	}

	@Override
	public MapContext map() {
		throw new UnsupportedOperationException();
	}

	@Override
	public RefFinishContext ref(String name) {
		throw new UnsupportedOperationException();
	}

}
