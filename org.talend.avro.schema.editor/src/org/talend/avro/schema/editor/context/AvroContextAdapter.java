package org.talend.avro.schema.editor.context;

import java.util.List;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.model.SchemaNode;

public class AvroContextAdapter implements AvroContextListener {

	@Override
	public void onRootNodeChanged(AvroContext context, RootNode rootNode) {
		// nothing
	}

	@Override
	public void onInputNodeChanged(AvroContext context, AvroNode inputNode) {
		// nothing		
	}

	@Override
	public void onContextualNodesChanged(AvroContext context, List<AvroNode> contextualNodes) {
		// nothing
	}
	
	@Override
	public void onSchemaNodesChanged(AvroContext context, List<SchemaNode> schemaNodes) {
		// nothing		
	}

	@Override
	public void onContextDispose(AvroContext context) {
		// nothing
	}

}
