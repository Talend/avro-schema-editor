package org.talend.avro.schema.editor.model.attributes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomProperties {
	
	public static final String KEY = "key";	//$NON-NLS-1$
	
	public static final String VALUE = "value"; //$NON-NLS-1$
	
	private Map<String, String> properties = new HashMap<>();
	
	public void addProps(Map<String, Object> props, boolean reset) {
		if (reset) {
			this.properties.clear();
		}
		for (Map.Entry<String, Object> entry : props.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof String) {
				this.properties.put(key, (String) value);
			}
		}
	}
	
	public void addProperties(Map<String, String> properties, boolean reset) {
		if (reset) {
			this.properties.clear();
		}
		this.properties.putAll(properties);
	}
	
	public void addProperty(String key, String value) {
		properties.put(key, value);
	}
	
	public boolean hasProperty(String key) {
		return properties.get(key) != null;
	}
	
	public String getProperty(String key) {
		return properties.get(key);
	}
	
	public Set<KeyValue> getKeyValues() {
		Set<KeyValue> keyValues = new HashSet<>();
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			keyValues.add(new KeyValue(entry.getKey(), entry.getValue()));
		}
		return keyValues;
	}	

	public CustomProperties getACopy() {
		CustomProperties customProperties = new CustomProperties();
		customProperties.addProperties(properties, true);
		return customProperties;
	}
	
	public static class KeyValue {
		
		private String key;
		
		private String value;

		public KeyValue(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
		
	}
	
}
