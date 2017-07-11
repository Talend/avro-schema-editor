package org.talend.avro.schema.editor.context;

import java.util.List;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.model.SchemaNode;

/**
 * Defines a listener of an {@link AvroContext}.
 * 
 * @author timbault
 * @see AvroContext
 *
 */
public interface AvroContextListener {

	/**
	 * Called when the root node of the listened context is changed.
	 * 
	 * @param context
	 * @param rootNode
	 */
	void onRootNodeChanged(AvroContext context, RootNode rootNode);
	
	/**
	 * Called when the input node of the listened context is changed.
	 * 
	 * @param context
	 * @param inputNode
	 */
	void onInputNodeChanged(AvroContext context, AvroNode inputNode);
	
	/**
	 * Called when the contextual nodes are changed.
	 * 
	 * @param context
	 * @param contextualNodes
	 */
	void onContextualNodesChanged(AvroContext context, List<AvroNode> contextualNodes);
	
	/**
	 * Called when the schema nodes are changed.
	 * 
	 * @param context
	 * @param schemaNodes
	 */
	void onSchemaNodesChanged(AvroContext context, List<SchemaNode> schemaNodes);
	
	/**
	 * Called when the listened context is disposed.
	 * 
	 * @param context
	 */
	void onContextDispose(AvroContext context);
	
}
