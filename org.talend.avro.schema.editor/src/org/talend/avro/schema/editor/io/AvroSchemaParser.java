package org.talend.avro.schema.editor.io;

import org.talend.avro.schema.editor.model.RootNode;

/**
 * This parses a file or a string representing an avro schema into a root node.
 * 
 * @author timbault
 *
 */
public interface AvroSchemaParser {	
	
	/**
	 * Parse a string  into a root node.
	 * 
	 * @param content
	 * @param description
	 * @return
	 */
	RootNode parse(String content, String description);
	
}
