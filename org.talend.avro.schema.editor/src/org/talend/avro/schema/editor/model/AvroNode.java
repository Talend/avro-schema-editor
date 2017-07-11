package org.talend.avro.schema.editor.model;

import java.util.List;

import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;
import org.talend.avro.schema.editor.model.attributes.AvroAttributeSet;

/**
 * Base interface of the avro schema model. AvroNode represents an item of an avro schema (record, field, primitive type, and so on).
 * It has basically a type (NodeType), parent, children and attributes.
 * 
 * @author timbault
 *
 */
public interface AvroNode {
	
	/**
	 * Initialize the attributes of the node (initializer provides an initial value for each attribute)
	 *  
	 * @param initializer
	 */
	void init(AttributeInitializer initializer);
	
	/**
	 * Return the type of this node.
	 * 
	 * @return
	 */
	NodeType getType();
	
	boolean hasParent();
	
	AvroNode getParent();		
	
	void setParent(AvroNode parent);
	
	boolean hasChildren();
	
	boolean hasChildren(NodeType type);
	
	List<AvroNode> getChildren();	
	
	int getChildrenCount();
	
	List<AvroNode> getChildren(NodeType type);
	
	boolean hasChild(AvroNode child);
	
	AvroNode getChild(int index);
	
	int getChildIndex(AvroNode child);
	
	void addChild(AvroNode child);
	
	void addChild(AvroNode child, int index);
	
	/**
	 * Add a child at a given position
	 * 
	 * @param child
	 * @param target
	 * @param position
	 */
	void addChild(AvroNode child, AvroNode target, TargetPosition position);
	
	/**
	 * Moves a child of this node to the given target position.
	 * 
	 * @param child
	 * @param target
	 * @param position
	 */
	void moveChild(AvroNode child, AvroNode target, TargetPosition position);
	
	void removeChild(AvroNode child);
	
	/**
	 * Return all the attributes of this node.
	 * @return
	 */
	AvroAttributeSet getAttributes();
	
	/**
	 * Method called by an avro node visitor.
	 * 
	 * @param visitor
	 * @return
	 */
	boolean visitNode(IAvroNodeVisitor visitor);
			
}
