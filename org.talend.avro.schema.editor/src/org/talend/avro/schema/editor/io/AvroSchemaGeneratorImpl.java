package org.talend.avro.schema.editor.io;

import org.apache.avro.Schema;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;

/**
 * Implementation of an {@link AvroSchemaGenerator}.
 * 
 * @author timbault
 *
 */
public class AvroSchemaGeneratorImpl implements AvroSchemaGenerator {

	private AvroContext context;
	
	public AvroSchemaGeneratorImpl(AvroContext context) {
		super();
		this.context = context;
	}

	public Schema generate(AvroNode inputNode) {
		
		Schema schema = null;
		
		GenerateSchemaVisitor schemaGenerator = new GenerateSchemaVisitor(context);
		
		if (inputNode.visitNode(schemaGenerator)) {
			schema = schemaGenerator.getSchema();
		}
		
		return schema;
		
	}
	
}
