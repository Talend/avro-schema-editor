package org.talend.avro.schema.editor.io.context.impl;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.SchemaBuilder.BaseTypeBuilder;
import org.apache.avro.SchemaBuilder.EnumBuilder;
import org.apache.avro.SchemaBuilder.FixedBuilder;
import org.apache.avro.SchemaBuilder.RecordBuilder;
import org.talend.avro.schema.editor.io.context.EnumContext;
import org.talend.avro.schema.editor.io.context.FixedContext;
import org.talend.avro.schema.editor.io.context.RecordContext;
import org.talend.avro.schema.editor.io.context.RootContext;
import org.talend.avro.schema.editor.io.context.RootFinishContext;
import org.talend.avro.schema.editor.io.context.UnionContext;

/**
 * Default implementation of a {@link RootContext}.
 * 
 * @author timbault
 *
 */
public class RootContextImpl implements RootContext, RootFinishContext {

	@Override
	public RecordContext record(String name) {
		RecordBuilder<?> recordBuilder = SchemaBuilder.record(name);
		return new RecordContextImpl(recordBuilder);
	}

	@Override
	public UnionContext union() {
		BaseTypeBuilder<?> union = SchemaBuilder.unionOf();
		return new RootUnionContextImpl(union);
	}
	
	@Override
	public EnumContext enumeration(String name) {
		EnumBuilder<?> enumBuilder = SchemaBuilder.enumeration(name);
		return new EnumContextImpl(enumBuilder);
	}

	@Override
	public FixedContext fixed(String name) {
		FixedBuilder<?> fixedBuilder = SchemaBuilder.fixed(name);
		return new FixedContextImpl(fixedBuilder);
	}

	@Override
	public Schema endRoot() {
		// this is used only for empty schema.
		return null;
	}
	
}
