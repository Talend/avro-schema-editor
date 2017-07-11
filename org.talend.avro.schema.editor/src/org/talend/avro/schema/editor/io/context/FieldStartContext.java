package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents a schema context which allows to create a new field element.
 * 
 * @author timbault
 *
 */
public interface FieldStartContext extends SchemaContext {

	/**
	 * Create a new field with the specified name.
	 * 
	 * @param name
	 * @return
	 */
	FieldContext name(String name);
	
}
