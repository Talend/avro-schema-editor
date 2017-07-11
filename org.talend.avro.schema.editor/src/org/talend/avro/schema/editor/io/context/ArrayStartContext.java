package org.talend.avro.schema.editor.io.context;

public interface ArrayStartContext extends SchemaContext {

	/**
	 * Create a new array element.
	 * 
	 * @return
	 */
	ArrayContext array();
	
}
