package org.talend.avro.schema.editor.model;

import java.util.List;

public interface SchemaNodeRegistry {

	void setSchemaContentProvider(SchemaContentProvider schemaContentProvider);
	
	SchemaNode getSchemaNode(AvroNode node);
	
	List<SchemaNode> getSchemaNodes(AvroNode node);
	
	SchemaNode getParent(AvroNode node);
	
	boolean hasChildren(AvroNode node);
	
	List<SchemaNode> getChildren(AvroNode node);
	
	SchemaNode getParent(AvroNode node, RefNode refNode);
	
	List<SchemaNode> getChildren(AvroNode node, RefNode refNode);
	
	void dispose();
	
}
