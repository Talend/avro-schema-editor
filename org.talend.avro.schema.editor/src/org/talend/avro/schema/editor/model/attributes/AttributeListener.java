package org.talend.avro.schema.editor.model.attributes;

/**
 * This listener is notified each time the attribute's value is changed.
 * 
 * @author timbault
 *
 * @param <T>
 */
public interface AttributeListener<T> {

	/**
	 * Called each time the attribute's value is changed.
	 * 
	 * @param attribute
	 * @param oldValue
	 * @param newValue
	 */
	void onAttributeValueChanged(AvroAttribute<T> attribute, T oldValue, T newValue);
	
}
