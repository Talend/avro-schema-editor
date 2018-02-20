package org.talend.avro.schema.editor.io.def;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

/**
 * Custom implementation of a GenericRecord. It is used to build default value of a field of type record.
 * 
 * @author timbault
 *
 */
public class AvroGenericRecord implements GenericRecord {

	/**
	 * The schema corresponding to the record.
	 */
	private Schema schema;
	
	private Map<String, Object> name2values = new HashMap<>();
	
	private Map<Integer, Object> index2values = new HashMap<>();
	
	public AvroGenericRecord(Schema schema) {
		super();
		this.schema = schema;
	}

	@Override
	public Object get(int index) {
		return index2values.get(index);
	}

	@Override
	public void put(int index, Object value) {
		index2values.put(index, value);
	}

	@Override
	public Schema getSchema() {
		return schema;
	}

	@Override
	public Object get(String fieldName) {
		return name2values.get(fieldName);
	}

	@Override
	public void put(String fieldName, Object value) {
		name2values.put(fieldName, value);
	}

}
