package org.talend.avro.schema.editor.io.context.impl;

import org.talend.avro.schema.editor.io.context.RefFinishContext;
import org.talend.avro.schema.editor.io.context.SchemaContext;

public class RefContextImpl implements RefFinishContext {

	private Object object;
	
	public RefContextImpl(Object object) {
		super();
		this.object = object;
	}

	@Override
	public SchemaContext endRef() {
		return new EndNameSpacedElementContextImpl(object);
	}

}
