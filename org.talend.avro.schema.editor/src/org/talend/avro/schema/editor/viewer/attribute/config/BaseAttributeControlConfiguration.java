package org.talend.avro.schema.editor.viewer.attribute.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of an {@link AttributeControlConfiguration}. It stores the configurations into a Map.
 * 
 * @author timbault
 *
 */
public class BaseAttributeControlConfiguration implements AttributeControlConfiguration {

	private Map<String, Object> configMap = new HashMap<>();
	
	@Override
	public boolean hasConfiguration(String configId) {
		return configMap.get(configId) != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getConfiguration(String configId, Class<T> configClass) {
		Object config = configMap.get(configId);
		if (!configClass.isAssignableFrom(config.getClass())) {
			throw new IllegalArgumentException("Invalid class for config " + configId + ": " + configClass.getSimpleName() + " expected" );
		}
		return (T) config;
	}

	public void registerConfiguration(String configId, Object config) {
		configMap.put(configId, config);
	}
	
}
