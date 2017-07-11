package org.talend.avro.schema.editor.io.context.impl;

import org.apache.avro.SchemaBuilder.FieldBuilder;
import org.talend.avro.schema.editor.io.context.FieldContext;
import org.talend.avro.schema.editor.io.context.TypeStartContext;

public class FieldContextImpl implements FieldContext {

	private FieldBuilder<?> fieldBuilder;
	
	public FieldContextImpl(FieldBuilder<?> fieldBuilder) {
		super();
		this.fieldBuilder = fieldBuilder;
	}

	@Override
	public FieldContext doc(String doc) {
		fieldBuilder = fieldBuilder.doc(doc);
		return this;
	}

	@Override
	public FieldContext aliases(String... aliases) {
		fieldBuilder = fieldBuilder.aliases(aliases);
		return this;
	}

	@Override
	public FieldContext custom(String key, String value) {
		fieldBuilder = fieldBuilder.prop(key, value);
		return this;
	}

	@Override
	public TypeStartContext type() {			
		//FieldTypeBuilder<?> fieldTypeBuilder = fieldBuilder.type();		
		return new TypeStartContextImpl(fieldBuilder);
	}

}
