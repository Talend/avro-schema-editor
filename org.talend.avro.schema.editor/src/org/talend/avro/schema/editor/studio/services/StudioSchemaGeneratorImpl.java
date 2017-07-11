package org.talend.avro.schema.editor.studio.services;

import org.apache.avro.Schema;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.io.AvroSchemaGeneratorImpl;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.RecordNode;
import org.talend.avro.schema.editor.model.RootNode;

public class StudioSchemaGeneratorImpl extends AvroSchemaGeneratorImpl {
	
	public StudioSchemaGeneratorImpl(AvroContext context) {
		super(context);
	}

	@Override
	public Schema generate(AvroNode inputNode) {
		RootNode rootNode = null;
		RecordNode recordNode = null;
		if (inputNode.getType() == NodeType.ROOT) {
			// check if there is only one record without fields
			// in this case the schema is empty, remove the record node before generating the schema
			rootNode = (RootNode) inputNode;
			RecordNode child = (RecordNode) rootNode.getChild(0);
			if (!child.hasChildren()) {
				recordNode = child;
				rootNode.removeChild(recordNode);
			}
		}		
		Schema schema = super.generate(inputNode);
		if (rootNode != null && recordNode != null) {
			// add it
			rootNode.addChild(recordNode);
		}
		return schema;
	}	
	
}
