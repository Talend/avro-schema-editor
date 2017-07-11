package org.talend.avro.schema.editor.io.context;

/**
 * This interface represents a schema context which allows to create a new enumeration element.
 * 
 * @author timbault
 *
 */
public interface EnumStartContext extends SchemaContext {

	/**
	 * Create a new enumeration element with the specified name.
	 * 
	 * @param name
	 * @return
	 */
	EnumContext enumeration(String name);
	
}
