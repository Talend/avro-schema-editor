package org.talend.avro.schema.editor.model;

public interface SchemaContentProvider {

	/**
	 * Indicates whether the given node has children
	 * 
	 * @param node
	 * @return
	 */
	boolean hasChildren(AvroNode node);
	
	/**
	 * Return the children of the given node
	 * 
	 * @param node
	 * @return
	 */
	AvroNode[] getChildren(AvroNode node);
	
	/**
	 * Return the parent of the given node
	 * 
	 * @param node
	 * @return
	 */
	AvroNode getParent(AvroNode node);
	
}
