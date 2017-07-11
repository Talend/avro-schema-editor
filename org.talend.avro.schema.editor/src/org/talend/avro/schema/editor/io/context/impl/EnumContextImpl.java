package org.talend.avro.schema.editor.io.context.impl;

import org.apache.avro.SchemaBuilder.EnumBuilder;
import org.talend.avro.schema.editor.io.context.EnumContext;
import org.talend.avro.schema.editor.io.context.EnumFinishContext;

public class EnumContextImpl implements EnumContext {

	private EnumBuilder<?> enumBuilder;
	
	public EnumContextImpl(EnumBuilder<?> enumBuilder) {
		super();
		this.enumBuilder = enumBuilder;
	}

	@Override
	public EnumContext namespace(String namespace) {
		enumBuilder = enumBuilder.namespace(namespace);
		return this;
	}

	@Override
	public EnumContext doc(String doc) {
		enumBuilder = enumBuilder.doc(doc);
		return this;
	}

	@Override
	public EnumContext aliases(String... aliases) {
		enumBuilder = enumBuilder.aliases(aliases);
		return this;
	}

	@Override
	public EnumFinishContext symbols(String... symbols) {
		Object endSymbols = enumBuilder.symbols(symbols);
		return new EnumFinishContextImpl(endSymbols);
	}

}
