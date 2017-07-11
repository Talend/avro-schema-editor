package org.talend.avro.schema.editor.io.context.impl;

import org.talend.avro.schema.editor.io.context.EnumFinishContext;
import org.talend.avro.schema.editor.io.context.SchemaContext;

public class EnumFinishContextImpl implements EnumFinishContext {

	private Object object;

	public EnumFinishContextImpl(Object object) {
		super();
		this.object = object;
	}

	@Override
	public SchemaContext endEnum() {
		return new EndNameSpacedElementContextImpl(object);
	}
	
}
