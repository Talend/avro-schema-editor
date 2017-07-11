package org.talend.avro.schema.editor.io;

import org.apache.avro.Schema;

/**
 * 
 * @author timbault
 *
 */
public class AvroSchemaFormatter {

	public String format(String content) {		
		Schema schema = new Schema.Parser().parse(content);
		return schema.toString(true);		
	}
	
}
