package org.talend.avro.schema.editor.io.context.wrappers;

import org.apache.avro.Schema;

public class SchemaWrapper {

	private Schema schema;

	public SchemaWrapper(Object object) {
		super();
		if (object instanceof Schema) {
			this.schema = (Schema) object;
		}
	}
	
	public Schema getSchema() {
		if (schema != null) {
			return schema;
		}
		throw new UnsupportedOperationException();
	}
	
}
