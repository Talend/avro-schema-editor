package org.talend.avro.schema.editor.io.context;

public interface ArrayContext extends SchemaContext {

	/**
	 * This methods allows to define the type of the current array element.
	 * 
	 * @return
	 */
	TypeStartContext type();
	
}
