package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents a record definition context.
 * 
 * @author timbault
 *
 */
public interface RecordContext extends SchemaContext {

	/**
	 * Set the name space of the current record.
	 * 
	 * @param namespace
	 * @return
	 */
	RecordContext namespace(String namespace);
	
	/**
	 * Set the documentation of the current record.
	 * 
	 * @param doc
	 * @return
	 */
	RecordContext doc(String doc);
	
	/**
	 * Set the aliases of the current record.
	 * 
	 * @param aliases
	 * @return
	 */
	RecordContext aliases(String... aliases);
	
	/**
	 * Enter in a field creation context. This context allows to create the fields of the current record.
	 * 
	 * @return
	 */
	FieldStartContext fields();
	
}
