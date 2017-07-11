package org.talend.avro.schema.editor.viewer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.SchemaNode;
import org.talend.avro.schema.editor.model.SchemaNodeRegistry;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * This class is responsible for node/selection conversion between tree viewer inside a {@link SchemaViewer} and external world.
 * <p>
 * The tree viewer inside a schema viewer only handles {@link SchemaNode} and external world should only work with {@link AvroNode}.
 * 
 * @author timbault
 *
 */
public class SchemaViewerNodeConverter {

	private SchemaNodeRegistry schemaNodeRegistry;
	
	public SchemaViewerNodeConverter(SchemaNodeRegistry schemaNodeRegistry) {
		super();
		this.schemaNodeRegistry = schemaNodeRegistry;
	}

	/**
	 * Try to convert given object into an AvroNode. Throw exception if the given object is not an AvroNode or a SchemaNode.
	 *  
	 * @param object
	 * @return
	 */
	public AvroNode convertToAvroNode(Object object) {
		if (object instanceof AvroNode) {
			return (AvroNode) object;
		} else if (object instanceof SchemaNode) {
			return ((SchemaNode) object).getAvroNode();
		}
		throw new IllegalArgumentException("Cannot convert given object into an AvroNode");
	}
		
	public List<AvroNode> convertToAvroNodes(List<Object> objects) {
		List<AvroNode> avroNodes = new ArrayList<>();
		for (Object object : objects) {
			avroNodes.add(convertToAvroNode(object));
		}
		return avroNodes;
	}
	
	@SuppressWarnings("unchecked")
	public List<AvroNode> convertToAvroNodes(IStructuredSelection selection) {
		return convertToAvroNodes(selection.toList());
	}
	
	/**
	 * Try to convert given object into a SchemaNode. Throw exception if the given object is not an AvroNode or a SchemaNode.
	 * 
	 * @param object
	 * @return
	 */
	public SchemaNode convertToSchemaNode(Object object) {
		if (object instanceof SchemaNode) {
			return (SchemaNode) object;
		} else if (object instanceof AvroNode) {
			AvroNode node = (AvroNode) object;
			SchemaNode schemaNode = schemaNodeRegistry.getSchemaNode(node);
			if (schemaNode == null) {
				// should not happen
				throw new IllegalStateException("No SchemaNode associated to the AvroNode " + AttributeUtil.getNameFromAttribute(node));
			}
			return schemaNode;
		}
		throw new IllegalArgumentException("Cannot convert given object into a SchemaNode");
	}
	
	public List<SchemaNode> convertToSchemaNodes(List<Object> objects) {
		List<SchemaNode> schemaNodes = new ArrayList<>();
		for (Object object : objects) {
			schemaNodes.add(convertToSchemaNode(object));
		}
		return schemaNodes;
	}
	
	public List<SchemaNode> convertToSchemaNodes(IStructuredSelection selection) {
		return convertToSchemaNodes(selection.toList());
	}
	
	/**
	 * Convert an incoming selection (i.e. a selection coming from external world) into a valid selection for the tree viewer inside schema viewer.
	 * 
	 * @param incomingSelection
	 * @return
	 */
	public IStructuredSelection convertIncomingSelection(IStructuredSelection incomingSelection) {
		Set<SchemaNode> selectedSchemaNodes = new HashSet<>();
		if (incomingSelection != null) {
			Iterator<?> iterator = incomingSelection.iterator();
			while (iterator.hasNext()) {
				Object object = iterator.next();				
				selectedSchemaNodes.add(convertToSchemaNode(object));				
			}
		}
		return new StructuredSelection(new ArrayList<>(selectedSchemaNodes));
	}
	
	public IStructuredSelection convertOutgoingSelection(IStructuredSelection outgoingSelection) {
		Set<AvroNode> selectedAvroNodes = new HashSet<>();
		if (outgoingSelection != null) {
			// outgoing selection contains only schema nodes
			// so we have to convert them to avro nodes
			Iterator<?> iterator = outgoingSelection.iterator();
			while (iterator.hasNext()) {
				Object object = iterator.next();
				selectedAvroNodes.add(convertToAvroNode(object));				
			}
		}
		return new StructuredSelection(new ArrayList<>(selectedAvroNodes));
	}	
	
}
