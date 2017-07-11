package org.talend.avro.schema.editor.utils;

import org.talend.avro.schema.editor.model.AvroNode;

/**
 * This stores parameters as (key, value). The types of value handled are: String, Integer, Boolean and Object.
 * 
 * @author timbault
 *
 */
public interface Params {
	
	boolean isStringDefined(String key);
	
	String getString(String key);

	boolean isIntegerDefined(String key);
	
	Integer getInteger(String key);

	boolean isBooleanDefined(String key);
	
	Boolean getBoolean(String key);

	boolean isAvroNodeDefined(String key);
	
	AvroNode getAvroNode(String key);
	
	boolean isObjectDefined(String key);
	
	Object getObject(String key);

	void dispose();
	
}
