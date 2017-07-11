package org.talend.avro.schema.editor.model.attributes;

import org.talend.avro.schema.editor.model.AvroNode;

/**
 * This interface represents an avro attribute of type T.
 * 
 * @author timbault
 *
 * @param <T>
 */
public interface AvroAttribute<T> {

	/**
	 * Returns the avro node holding this attribute.
	 * 
	 * @return
	 */
	AvroNode getHolder();
	
	/**
	 * Returns the identifier of this attribute.
	 * 
	 * @return
	 */
	String getName();
	
	Class<T> getValueClass();
	
	T getValue();
	
	void setValue(T value);	
	
	// FIXME should not be here, is it UI information, the visibility of an attribute should be handled in the UI part of code.
	boolean isVisible();
	
	// FIXME should not be here, is it UI information, the visibility of an attribute should be handled in the UI part of code.
	void setVisible(boolean visible);
	
	boolean isEnabled();
	
	void setEnabled(boolean enabled);
	
	void addListener(AttributeListener<T> listener);
	
	void removeListener(AttributeListener<T> listener);
	
	/**
	 * Returns a copy of the attribute's value.
	 * 
	 * @return
	 */
	T getCopyOfValue();
	
}
