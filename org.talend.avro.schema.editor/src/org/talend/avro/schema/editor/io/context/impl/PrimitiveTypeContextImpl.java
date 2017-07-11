package org.talend.avro.schema.editor.io.context.impl;

import org.talend.avro.schema.editor.io.context.PrimitiveTypeFinishContext;
import org.talend.avro.schema.editor.io.context.TypeFinishContext;

public class PrimitiveTypeContextImpl implements PrimitiveTypeFinishContext {

	private Object object;
	
	public PrimitiveTypeContextImpl(Object object) {
		super();
		this.object = object;
	}

	@Override
	public TypeFinishContext endPrimitiveType() {
		return new EndTypeContextImpl(object);
	}

}
