package org.talend.avro.schema.editor.io.context.impl;

import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.apache.avro.SchemaBuilder.RecordBuilder;
import org.talend.avro.schema.editor.io.context.FieldStartContext;
import org.talend.avro.schema.editor.io.context.RecordContext;

public class RecordContextImpl implements RecordContext {

	private RecordBuilder<?> recordBuilder;
	
	public RecordContextImpl(RecordBuilder<?> recordBuilder) {
		super();
		this.recordBuilder = recordBuilder;
	}

	@Override
	public RecordContext namespace(String namespace) {
		recordBuilder = recordBuilder.namespace(namespace);
		return this;
	}

	@Override
	public RecordContext doc(String doc) {
		recordBuilder = recordBuilder.doc(doc);
		return this;
	}

	@Override
	public RecordContext aliases(String... aliases) {
		recordBuilder = recordBuilder.aliases(aliases);
		return this;
	}

	@Override
	public FieldStartContext fields() {
		FieldAssembler<?> fieldAssembler = recordBuilder.fields();
		return new FieldsContextImpl(fieldAssembler);
	}

}
