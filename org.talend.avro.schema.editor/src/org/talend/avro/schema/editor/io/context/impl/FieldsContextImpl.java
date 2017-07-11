package org.talend.avro.schema.editor.io.context.impl;

import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.apache.avro.SchemaBuilder.FieldBuilder;
import org.talend.avro.schema.editor.io.context.FieldContext;
import org.talend.avro.schema.editor.io.context.FieldStartContext;
import org.talend.avro.schema.editor.io.context.SchemaContext;
import org.talend.avro.schema.editor.io.context.RecordFinishContext;

public class FieldsContextImpl implements FieldStartContext, RecordFinishContext {

	private FieldAssembler<?> fieldAssembler;
	
	public FieldsContextImpl(FieldAssembler<?> fieldAssembler) {
		super();
		this.fieldAssembler = fieldAssembler;
	}

	@Override
	public FieldContext name(String name) {
		FieldBuilder<?> fieldBuilder = fieldAssembler.name(name);
		return new FieldContextImpl(fieldBuilder);
	}

	@Override
	public SchemaContext endRecord() {
		Object endRecord = fieldAssembler.endRecord();
		return new EndNameSpacedElementContextImpl(endRecord);
	}

}
