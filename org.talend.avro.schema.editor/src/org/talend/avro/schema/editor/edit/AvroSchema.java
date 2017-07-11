package org.talend.avro.schema.editor.edit;

/**
 * This interface represents an input for the avro schema editor part.
 * 
 * @author timbault
 * @see AvroSchemaEditorPart
 */
public interface AvroSchema {

	/**
	 * Returns the name of the schema.
	 * 
	 * @return
	 */
	String getName();
		
	/**
	 * Returns the schema as a string.
	 * 
	 * @return
	 */
	String getContent();
	
	/**
	 * Set the content of the schema as a string.
	 * 
	 * @param content
	 */
	void setContent(String content);
	
}
