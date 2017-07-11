package org.talend.avro.schema.editor.io.context.impl;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.talend.avro.schema.editor.io.context.ArrayFinishContext;
import org.talend.avro.schema.editor.io.context.FieldFinishContext;
import org.talend.avro.schema.editor.io.context.MapFinishContext;
import org.talend.avro.schema.editor.io.context.RootFinishContext;
import org.talend.avro.schema.editor.io.context.SchemaContext;
import org.talend.avro.schema.editor.io.context.TypeFinishContext;
import org.talend.avro.schema.editor.io.context.wrappers.FieldDefaultWrapper;
import org.talend.avro.schema.editor.io.context.wrappers.SchemaWrapper;

public class EndUnionContextImpl implements RootFinishContext, 
											FieldFinishContext, 
											MapFinishContext, 
											ArrayFinishContext {
	
	private Object endUnion;
	
	private SchemaWrapper schema;
	
	private FieldDefaultWrapper fieldDefault;
	
	public EndUnionContextImpl(Object endUnion) {
		super();
		this.endUnion = endUnion;
		schema = new SchemaWrapper(endUnion);
		fieldDefault = new FieldDefaultWrapper(endUnion);
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
	public Schema endRoot() {
		return schema.getSchema();
	}

	@Override
	public TypeFinishContext endArray() {
		return new EndTypeContextImpl(endUnion);
	}

	@Override
	public TypeFinishContext endMap() {
		return new EndTypeContextImpl(endUnion);
	}

}
