package org.talend.avro.schema.editor.viewer.attribute.config;

import org.talend.avro.schema.editor.viewer.attribute.AttributeControl;

/**
 * This provides some configuration data for an {@link AttributeControl}.
 * <p>
 * For example it provides widget styles or label providers.
 * <p>
 * A configuration is defined by a string identifier and a value.
 * <p> 
 * 
 * @author timbault
 * @see AttributeControlConfigurationConstants
 *
 */
public interface AttributeControlConfiguration {

	/**
	 * Indicates if a configuration exists.
	 * 
	 * @param configId
	 * @return
	 */
	boolean hasConfiguration(String configId);
	
	/**
	 * Returns a configuration specified by the given identifier and value class.
	 * 
	 * @param configId
	 * @param configClass
	 * @return
	 */
	<T> T getConfiguration(String configId, Class<T> configClass);
	
}
