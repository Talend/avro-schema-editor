package org.talend.avro.schema.editor.io.context.impl;

import org.apache.avro.SchemaBuilder.FixedBuilder;
import org.talend.avro.schema.editor.io.context.FixedContext;
import org.talend.avro.schema.editor.io.context.FixedFinishContext;

public class FixedContextImpl implements FixedContext {

	private FixedBuilder<?> fixedBuilder;
	
	public FixedContextImpl(FixedBuilder<?> fixedBuilder) {
		super();
		this.fixedBuilder = fixedBuilder;
	}

	@Override
	public FixedContext namespace(String namespace) {
		fixedBuilder = fixedBuilder.namespace(namespace);
		return this;
	}

	@Override
	public FixedContext aliases(String... aliases) {
		fixedBuilder = fixedBuilder.aliases(aliases);
		return this;
	}

	@Override
	public FixedFinishContext size(int size) {
		Object endFixed = fixedBuilder.size(size);
		return new FixedFinishContextImpl(endFixed);
	}	
	
}
