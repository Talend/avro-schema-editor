package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents a record field definition context.
 * 
 * @author timbault
 *
 */
public interface FieldContext extends SchemaContext {

	/**
	 * Set the documentation of the current field.
	 * 
	 * @param doc
	 * @return
	 */
	FieldContext doc(String doc);
	
	/**
	 * Set the aliases of the current field.
	 * 
	 * @param aliases
	 * @return
	 */
	FieldContext aliases(String... aliases);
	
	/**
	 * Add a custom properties on the current field.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	FieldContext custom(String key, String value);
	
	/**
	 * Enter in a type definition context. This context allows to specify the type of the current field.
	 * 
	 * @return
	 */
	TypeStartContext type();
	
}
