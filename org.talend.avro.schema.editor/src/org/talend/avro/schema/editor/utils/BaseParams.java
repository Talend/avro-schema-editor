package org.talend.avro.schema.editor.utils;

import java.util.HashMap;
import java.util.Map;

import org.talend.avro.schema.editor.model.AvroNode;

/**
 * Base implementation of parameters.
 * 
 * @author timbault
 *
 */
public class BaseParams implements Params {

	private Map<String, String> stringParams = new HashMap<>();
	
	private Map<String, Integer> integerParams = new HashMap<>();
	
	private Map<String, Boolean> booleanParams = new HashMap<>();
	
	private Map<String, Object> objectParams = new HashMap<>();
	
	private Map<String, AvroNode> nodeParams = new HashMap<>();
	
	public static BaseParams getParams() {
		return new BaseParams();
	}
	
	public void storeString(String key, String value) {
		stringParams.put(key, value);
	}
	
	public void storeInteger(String key, Integer value) {
		integerParams.put(key, value);
	}
	
	public void storeBoolean(String key, Boolean value) {
		booleanParams.put(key, value);
	}
	
	public void storeObject(String key, Object value) {
		objectParams.put(key, value);
	}
	
	public void storeAvroNode(String key, AvroNode node) {
		nodeParams.put(key, node);
	}
	
	@Override
	public boolean isStringDefined(String key) {
		return stringParams.get(key) != null;
	}

	public String getString(String key) {
		return stringParams.get(key);
	}
	
	@Override
	public boolean isIntegerDefined(String key) {
		return integerParams.get(key) != null;
	}

	public Integer getInteger(String key) {
		return integerParams.get(key);
	}
	
	@Override
	public boolean isBooleanDefined(String key) {
		return booleanParams.get(key) != null;
	}

	public Boolean getBoolean(String key) {
		return booleanParams.get(key);
	}
	
	@Override
	public boolean isObjectDefined(String key) {
		return objectParams.get(key) != null;
	}

	public Object getObject(String key) {
		return objectParams.get(key);
	}
	
	@Override
	public boolean isAvroNodeDefined(String key) {
		return nodeParams.get(key) != null;
	}

	@Override
	public AvroNode getAvroNode(String key) {
		return nodeParams.get(key);
	}

	@Override
	public void dispose() {
		stringParams.clear();
		integerParams.clear();
		booleanParams.clear();
		objectParams.clear();
	}
	
}
