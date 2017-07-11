package org.talend.avro.schema.editor.io.context.impl;

import org.talend.avro.schema.editor.io.context.FixedFinishContext;
import org.talend.avro.schema.editor.io.context.SchemaContext;

public class FixedFinishContextImpl implements FixedFinishContext {

	private Object object;
	
	public FixedFinishContextImpl(Object object) {
		super();
		this.object = object;
	}

	@Override
	public SchemaContext endFixed() {
		return new EndNameSpacedElementContextImpl(object);
	}

}
