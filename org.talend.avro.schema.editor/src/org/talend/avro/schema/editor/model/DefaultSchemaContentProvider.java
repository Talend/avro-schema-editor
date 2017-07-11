package org.talend.avro.schema.editor.model;

import org.talend.avro.schema.editor.viewer.SchemaViewerContentProvider;

/**
 * Default implementation of a {@link SchemaViewerContentProvider}
 * 
 * @author timbault
 *
 */
public class DefaultSchemaContentProvider implements SchemaContentProvider {

	@Override
	public boolean hasChildren(AvroNode node) {
		return node.hasChildren();
	}

	@Override
	public AvroNode[] getChildren(AvroNode node) {
		return node.getChildren().toArray(new AvroNode[node.getChildrenCount()]);
	}

	@Override
	public AvroNode getParent(AvroNode node) {
		return node.getParent();
	}
	
}
