package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents a schema context which allows to create a new record schema.
 * 
 * @author timbault
 *
 */
public interface RecordStartContext extends SchemaContext {

	/**
	 * Create a new record with the specified name. It returns a record definition context.
	 * 
	 * @param name
	 * @return
	 */
	RecordContext record(String name);
	
}
