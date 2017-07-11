package org.talend.avro.schema.editor.context;

import java.util.ArrayList;
import java.util.List;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.registry.SchemaRegistry;

/**
 * 
 * @author timbault
 *
 */
public class SearchNodeContext {
	
	private List<AvroNode> foundNodes = new ArrayList<>();
	
	private int index;

	private SchemaRegistry schemaRegistry;
	
	public SearchNodeContext(SchemaRegistry schemaRegistry) {
		super();
		this.schemaRegistry = schemaRegistry;
	}

	protected List<AvroNode> findNodes(NodeType type, String pattern, boolean withRef) {
		return schemaRegistry.getNodeAndRefNodesFromFullName(type, pattern, withRef);
	}
	
	public boolean searchNodes(NodeType type, String pattern, boolean withRef) {
		reset();
		List<AvroNode> foundNodes = findNodes(type, pattern, withRef);
		if (foundNodes != null && !foundNodes.isEmpty()) {
			registerFoundNodes(foundNodes);
			return true;
		}
		return false;
	}
	
	protected void registerFoundNodes(List<AvroNode> nodes) {
		index = -1;
		foundNodes.clear();
		foundNodes.addAll(nodes);
	}
	
	public boolean hasNext() {
		return !foundNodes.isEmpty() && index < foundNodes.size() - 1;
	}
	
	public AvroNode next() {		
		if (!foundNodes.isEmpty() && index < foundNodes.size() - 1) {
			index++;
			return foundNodes.get(index);
		}
		return null;
	}
	
	public boolean hasPrevious() {
		return !foundNodes.isEmpty() && index > 0;
	}
	
	public AvroNode previous() {
		if (!foundNodes.isEmpty() && index > 0) {
			index--;
			return foundNodes.get(index);
		}
		return null;
	}
	
	public void reset() {
		foundNodes.clear();
		index = -1;
	}
	
	public void dispose() {
		foundNodes.clear();
	}
	
}
