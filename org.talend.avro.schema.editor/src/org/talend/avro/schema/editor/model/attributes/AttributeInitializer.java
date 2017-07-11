package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.NodeType;

/**
 * This provides the initial value of attributes.
 * 
 * @author timbault
 *
 */
public interface AttributeInitializer {

	/**
	 * Indicates whether an initial value is provided for the given node type and attribute key 
	 * 
	 * @param type
	 * @param attributeName
	 * @return
	 */
	boolean provideInitialAttributeValue(NodeType type, String attributeName);
	
	/**
	 * Return the initial value
	 * 
	 * @param type
	 * @param attributeName
	 * @return
	 */
	Object getInitialAttributeValue(NodeType type, String attributeName);
	
	/**
	 * Indicates whether the attribute is initially visible or not.
	 * 
	 * @param type
	 * @param attributeName
	 * @return
	 */
	boolean isVisible(NodeType type, String attributeName);
	
	/**
	 * Indicates whether the attribute is initially enabled or not.
	 * 
	 * @param type
	 * @param attributeName
	 * @return
	 */
	boolean isEnabled(NodeType type, String attributeName);
	
}
