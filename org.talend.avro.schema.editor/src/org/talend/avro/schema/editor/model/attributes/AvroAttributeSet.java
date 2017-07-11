package org.talend.avro.schema.editor.model.attributes;

import java.util.List;

/**
 * This interface represents a set of {@link AvroAttribute}.
 * 
 * @author timbault
 *
 */
public interface AvroAttributeSet {

	/**
	 * Indicates if an attribute with the specified name is defined in the set.
	 * 
	 * @param name
	 * @return
	 */
	boolean isDefined(String name);
	
	/**
	 * Get if it exists the attribute with the given name. Returns null is it does not exist.
	 * 
	 * @param name
	 * @return
	 */
	AvroAttribute<?> getAttribute(String name);
	
	/**
	 * Returns if it exists the attribute with the specified name and class.
	 * If an attribute with the specified name exists but with a different class, the method raises an exception.
	 * 
	 * @param name
	 * @param attributeClass
	 * @return
	 */
	<T extends AvroAttribute<?>> T getAttributeFromClass(String name, Class<T> attributeClass);
	
	/**
	 * Returns if it exists the attribute with the specified name and value class.
	 * If an attribute with the specified name exists but with a different value class, the method raises an exception.
	 * 
	 * @param name
	 * @param attributeValueClass
	 * @return
	 */
	<T> AvroAttribute<T> getAttributeFromValueClass(String name, Class<T> attributeValueClass);
	
	/**
	 * Returns the value of the attribute corresponding to the specified name and value class.
	 * If an attribute with the specified name exists but with a different value class, the method raises an exception.
	 * @param name
	 * @param attributeValueClass
	 * @return
	 */
	<T> T getAttributeValue(String name, Class<T> attributeValueClass);
	
	/**
	 * Returns a sorted list of defined attributes.
	 * 
	 * @return
	 */
	List<AvroAttribute<?>> getSortedAttributes();
	
}
