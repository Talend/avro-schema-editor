package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents a fixed element definition context.
 * 
 * @author timbault
 *
 */
public interface FixedContext extends SchemaContext {

	/**
	 * Set the name space of the current fixed element.
	 * 
	 * @param namespace
	 * @return
	 */
	FixedContext namespace(String namespace);
		
	/**
	 * Set the aliases of the current fixed element.
	 * 
	 * @param aliases
	 * @return
	 */
	FixedContext aliases(String... aliases);
	
	/**
	 * Set the size of the current fixed element.
	 * 
	 * @param size
	 * @return
	 */
	FixedFinishContext size(int size);
	
}
