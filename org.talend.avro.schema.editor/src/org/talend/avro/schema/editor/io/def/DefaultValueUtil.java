package org.talend.avro.schema.editor.io.def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericRecord;
import org.talend.avro.schema.editor.io.SchemaUtil;
import org.talend.avro.schema.editor.model.ArrayNode;
import org.talend.avro.schema.editor.model.FieldNode;
import org.talend.avro.schema.editor.model.MapNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.RecordNode;
import org.talend.avro.schema.editor.model.attributes.DefaultValue;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * This class provides some convenient methods around the default values. 
 * 
 * @author timbault
 *
 */
public class DefaultValueUtil {

	private static final String NULL = "null"; //$NON-NLS-1$
	
	private static final String LEFT_CURLY_BRACKET = "{"; //$NON-NLS-1$
	
	private static final String RIGHT_CURLY_BRACKET = "}"; //$NON-NLS-1$
	
	private static final String LEFT_SQUARE_BRACKET = "["; //$NON-NLS-1$
	
	private static final String RIGHT_SQUARE_BRACKET = "]"; //$NON-NLS-1$
	
	private static final String COMMA = ","; //$NON-NLS-1$
	
	private static final String COLON = ":"; //$NON-NLS-1$	

	private static final String SPACE = " "; //$NON-NLS-1$
	
	private static final String EMPTY = ""; //$NON-NLS-1$
	
	private static final String SINGLE_QUOTE = "'"; //$NON-NLS-1$
	
	private static final String DOUBLE_QUOTE = "\""; //$NON-NLS-1$
	
	/**
	 * Build a GenericRecord based on the given defaultValue and the corresponding record.
	 * 
	 * @param defaultValue
	 * @param recordNode
	 * @return
	 */
	public static GenericRecord recordDefaultValue(String defaultValue, RecordNode recordNode) {
		Schema schema = SchemaUtil.getSchema(recordNode);
		AvroGenericRecord genRecord = new AvroGenericRecord(schema);
		populateRecordDefaultValues(defaultValue, recordNode, genRecord);		
		return genRecord;
	}
	
	/**
	 * Build a Map<key, value> based on the given defaultValue and the corresponding map.
	 * @param defaultValue
	 * @param mapNode
	 * @return
	 */
	public static Map<String, Object> mapDefaultValue(String defaultValue, MapNode mapNode) {
		Map<String, Object> defValueMap = new HashMap<>();
		populateMapDefaultValues(defaultValue, mapNode, defValueMap);		
		return defValueMap;
	}
	
	/**
	 * Build a list of default values based on the given defaultValue string and the corresponding array node.
	 * @param defaultValue
	 * @param arrayNode
	 * @return
	 */
	public static List<Object> arrayDefaultValue(String defaultValue, ArrayNode arrayNode) {
		List<Object> defValueList = new ArrayList<>();
		populateArrayDefaultValues(defaultValue, arrayNode, defValueList);
		return defValueList;
	}
	
	/**
	 * Create a DefaultValue object from the default value of the given avro field.
	 * 
	 * @param field
	 * @return
	 */
	public static DefaultValue createDefaultValue(Field field) {
		DefaultValue defaultValue = new DefaultValue();
		Object defaultVal = field.defaultVal();
		if (defaultVal != null) {
			defaultValue.setDefined(true);
			Type fieldType = SchemaUtil.getFieldType(field);
			switch (fieldType) {
			case ARRAY:
				if (defaultVal instanceof List) {
					List<Object> defValueList = (List<Object>) defaultVal;
					String defValuesAsString = stringifyArray(defValueList, field);
					defaultValue.setValue(defValuesAsString);
				} else {
					defaultValue.setValue(NULL);
				}
				break;
			case BYTES:
				// TODO
				break;
			case ENUM:
				// default value is a string
				defaultValue.setValue(defaultVal.toString());
				break;
			case FIXED:
				// TODO
				break;
			case MAP:
			case RECORD:
				if (defaultVal instanceof Map) {
					Map<String, Object> defValueMap = (Map<String, Object>) defaultVal;
					String defValuesAsString = fieldType == Type.RECORD ? stringifyRecord(defValueMap, field) : stringifyMap(defValueMap, field);
					defaultValue.setValue(defValuesAsString);
				} else {
					// null (optional case)
					defaultValue.setValue(NULL);
				}
				break;
			case UNION:
				// TODO
				break;
			default:
				// primitive types
				if (SchemaUtil.isFieldOptional(field)) {
					defaultValue.setValue(NULL);
				} else {
					defaultValue.setValue(defaultVal.toString());
				}
				break;
			}			
		}
		return defaultValue;
	}
	
	private static String stringifyArray(List<Object> defValueList, Field field) {
		PrimitiveType typeOfArrayValues = SchemaUtil.getPrimitiveTypeOfMapOrArrayValues(field.schema(), Schema.Type.ARRAY);
		StringBuffer buf = new StringBuffer();
		buf.append(LEFT_SQUARE_BRACKET);
		int size = defValueList.size();
		for (int i = 0; i < size; i++) {
			Object value = defValueList.get(i);
			String valueAsString = value.toString();
			if (typeOfArrayValues == PrimitiveType.STRING) {
				valueAsString = addQuotes(valueAsString);
			}
			buf.append(valueAsString);
			if (i < size - 1) {
				buf.append(COMMA);
				buf.append(SPACE);
			}
		}
		buf.append(RIGHT_SQUARE_BRACKET);
		return buf.toString();
	}
	
	private static String stringifyMap(Map<String, Object> defValueMap, Field field) {
		PrimitiveType typeOfMapValues = SchemaUtil.getPrimitiveTypeOfMapOrArrayValues(field.schema(), Schema.Type.MAP);
		StringBuffer buf = new StringBuffer();
		buf.append(LEFT_CURLY_BRACKET);
		buf.append(SPACE);
		int size = defValueMap.size();
		int index = 0;
		for (Map.Entry<String, Object> entry : defValueMap.entrySet()) {
			String name = entry.getKey();			
			Object value = entry.getValue();
			String valueAsString = value.toString();
			if (typeOfMapValues == PrimitiveType.STRING) {
				valueAsString = addQuotes(valueAsString);
			}
			buf.append(addQuotes(name));
			buf.append(COLON);
			buf.append(valueAsString);
			if (index < size - 1) {
				buf.append(COMMA);
				buf.append(SPACE);
			}
			index++;
		}
		buf.append(SPACE);
		buf.append(RIGHT_CURLY_BRACKET);
		return buf.toString();
	}
	
	private static String stringifyRecord(Map<String, Object> defValueMap, Field field) {
		Map<String, PrimitiveType> primitiveTypes = SchemaUtil.getPrimitiveTypesOfRecordFields(field.schema());
		StringBuffer buf = new StringBuffer();
		buf.append(LEFT_CURLY_BRACKET);
		buf.append(SPACE);
		int size = defValueMap.size();
		int index = 0;
		for (Map.Entry<String, Object> entry : defValueMap.entrySet()) {
			String name = entry.getKey();
			PrimitiveType primitiveType = primitiveTypes.get(name);
			Object value = entry.getValue();
			String valueAsString = value.toString();
			if (primitiveType == PrimitiveType.STRING) {
				valueAsString = addQuotes(valueAsString);
			}
			buf.append(addQuotes(name));
			buf.append(COLON);
			buf.append(valueAsString);
			if (index < size - 1) {
				buf.append(COMMA);
				buf.append(SPACE);
			}
			index++;
		}
		buf.append(SPACE);
		buf.append(RIGHT_CURLY_BRACKET);
		return buf.toString();
	}
		
	private static void populateRecordDefaultValues(String defaultValue, RecordNode recordNode, GenericRecord genericRecord) {
				
		String defValue = defaultValue.trim();
		if (defValue.startsWith(LEFT_CURLY_BRACKET) && defValue.endsWith(RIGHT_CURLY_BRACKET)) {
			
			String temp = defValue.substring(1, defValue.length());
			temp = temp.substring(0, temp.length() - 1);
			
			List<String> fieldNames = ModelUtil.getFieldNames(recordNode);
			
			String[] keyValues = temp.split(COMMA);
			for (int i = 0; i < keyValues.length; i++) {
				String keyVal = keyValues[i];
				String[] keyValue = keyVal.trim().split(COLON);
				String name = removeQuotes(keyValue[0].trim());
				String value = removeQuotes(keyValue[1].trim());
				// check that key is a field name of the record
				if (fieldNames.contains(name)) {
					// OK
					FieldNode fieldNode = ModelUtil.getField(recordNode, name);
					int index = recordNode.getChildIndex(fieldNode);
					// check the type of the node
					if (ModelUtil.isTypedNodeOfPrimitiveType(fieldNode)) {
						// field of primitive type
						PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(fieldNode);
						Object defVal = ModelUtil.parsePrimitiveType(value, primitiveType);
						genericRecord.put(name, defVal);
						genericRecord.put(index, defVal);
					} else {
						// TODO what about complex types?!?
					}
				}
			}
			
		}
				
	}
	
	private static void populateMapDefaultValues(String defaultValue, MapNode mapNode, Map<String, Object> key2values) {
		
		String defValue = defaultValue.trim();
		if (defValue.startsWith(LEFT_CURLY_BRACKET) && defValue.endsWith(RIGHT_CURLY_BRACKET)) {
			
			// get the type of the array values
			NodeType valuesType = ModelUtil.getTypeOfNode(mapNode);
			
			String temp = defValue.substring(1, defValue.length());
			temp = temp.substring(0, temp.length() - 1);			
			
			String[] keyValues = temp.split(COMMA);
			for (int i = 0; i < keyValues.length; i++) {
				String keyVal = keyValues[i];
				String[] keyValue = keyVal.trim().split(COLON);
				String key = removeQuotes(keyValue[0].trim());
				String value = removeQuotes(keyValue[1].trim());
				// check that key is a field name of the record
				// check the type of the node
				if (valuesType == NodeType.PRIMITIVE_TYPE) {
					PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(mapNode);
					Object defVal = ModelUtil.parsePrimitiveType(value, primitiveType);
					key2values.put(key, defVal);
				} else {
					// TODO what about complex types?!?
				}
			}
			
		}
				
	}
	
	private static void populateArrayDefaultValues(String defaultValue, ArrayNode arrayNode, List<Object> defValueList) {
		String defValue = defaultValue.trim();
		if (defValue.startsWith(LEFT_SQUARE_BRACKET) && defValue.endsWith(RIGHT_SQUARE_BRACKET)) {
			
			// get the type of the array values
			NodeType valuesType = ModelUtil.getTypeOfNode(arrayNode);
			
			String temp = defValue.substring(1, defValue.length());
			temp = temp.substring(0, temp.length() - 1);
			
			String[] values = temp.split(COMMA);
			
			for (int i = 0; i < values.length; i++) {
				String value = removeQuotes(values[i].trim());
				if (valuesType == NodeType.PRIMITIVE_TYPE) {
					PrimitiveType primitiveType = AttributeUtil.getPrimitiveType(arrayNode);
					Object defVal = ModelUtil.parsePrimitiveType(value, primitiveType);
					if (defVal != null) {
						defValueList.add(defVal);
					}
				} else {
					// TODO what about the complex types?
				}
			}
			
		}
	}
	
	private static String removeQuotes(String value) {
		return value.replace(DOUBLE_QUOTE, EMPTY).replace(SINGLE_QUOTE, EMPTY);
	}
	
	private static String addQuotes(String value) {
		return DOUBLE_QUOTE + value + DOUBLE_QUOTE;
	}
	
}
