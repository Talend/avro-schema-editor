package org.talend.avro.schema.editor.model;

import org.talend.avro.schema.editor.model.attributes.AvroAttributeSet;

/**
 * This interface represents a reference to an avro node. It is used to make a reference to a namespaced avro node (like a record or an enum)
 * 
 * @author timbault
 *
 */
public interface RefNode extends AvroNode {

	/**
	 * Return the referenced node
	 * @return
	 */
	AvroNode getReferencedNode();
	
	/**
	 * Return the type of the referenced node
	 * @return
	 */
	NodeType getReferencedType();
	
	/**
	 * Return the attributes of the referenced node
	 * @return
	 */
	AvroAttributeSet getReferencedAttributes();
	
}
