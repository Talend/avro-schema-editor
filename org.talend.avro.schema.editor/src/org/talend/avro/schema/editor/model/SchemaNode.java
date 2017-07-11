package org.talend.avro.schema.editor.model;

import java.util.List;

/**
 * Schema nodes are the nodes used to display the model (composed of avro nodes) in a tree viewer for example.
 * A schema node always represents an existing model node.
 * 
 * @author timbault
 *
 */
public interface SchemaNode {

	boolean hasParent();
	
	SchemaNode getParent();		

	boolean hasChildren();
	
	List<SchemaNode> getChildren();	
	
	int getChildrenCount();
	
	/**
	 * Returns the referenced model node.
	 * 
	 * @return
	 */
	AvroNode getAvroNode();
	
}
