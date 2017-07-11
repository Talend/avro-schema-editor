package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents a schema context which allows to create a new fixed element.
 * 
 * @author timbault
 *
 */
public interface FixedStartContext extends SchemaContext {

	/**
	 * Create a new fixed element with the specified name.
	 * 
	 * @param name
	 * @return
	 */
	FixedContext fixed(String name);
	
}
