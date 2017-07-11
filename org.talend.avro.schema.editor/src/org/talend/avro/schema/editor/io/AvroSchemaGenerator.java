package org.talend.avro.schema.editor.io;

import org.apache.avro.Schema;
import org.talend.avro.schema.editor.model.AvroNode;

/**
 * This generates an Avro Schema from a model node. 
 * 
 * @author timbault
 *
 */
public interface AvroSchemaGenerator {

	/**
	 * Generate an Avro Schema.
	 * 
	 * @param inputNode
	 * @return
	 */
	Schema generate(AvroNode inputNode);
	
}
