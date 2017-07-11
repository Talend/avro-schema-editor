package org.talend.avro.schema.editor.io.context.wrappers;

import java.util.List;
import java.util.Map;

import org.apache.avro.SchemaBuilder.ArrayDefault;
import org.apache.avro.SchemaBuilder.BooleanDefault;
import org.apache.avro.SchemaBuilder.BytesDefault;
import org.apache.avro.SchemaBuilder.DoubleDefault;
import org.apache.avro.SchemaBuilder.EnumDefault;
import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.apache.avro.SchemaBuilder.FixedDefault;
import org.apache.avro.SchemaBuilder.FloatDefault;
import org.apache.avro.SchemaBuilder.GenericDefault;
import org.apache.avro.SchemaBuilder.IntDefault;
import org.apache.avro.SchemaBuilder.LongDefault;
import org.apache.avro.SchemaBuilder.MapDefault;
import org.apache.avro.SchemaBuilder.NullDefault;
import org.apache.avro.SchemaBuilder.RecordDefault;
import org.apache.avro.SchemaBuilder.StringDefault;
import org.apache.avro.generic.GenericRecord;

public class FieldDefaultWrapper {
		
	private NullDefault<?> nullDefault;

	private BooleanDefault<?> booleanDefault;
	
	private IntDefault<?> intDefault;
	
	private LongDefault<?> longDefault;
	
	private FloatDefault<?> floatDefault;
	
	private DoubleDefault<?> doubleDefault;
	
	private BytesDefault<?> bytesDefault;
	
	private StringDefault<?> stringDefault;
	
	private RecordDefault<?> recordDefault;
	
	private EnumDefault<?> enumDefault;
	
	private FixedDefault<?> fixedDefault;
	
	private ArrayDefault<?> arrayDefault;
	
	private MapDefault<?> mapDefault;
	
	private GenericDefault<?> genDefault;
	
	public FieldDefaultWrapper(Object object) {
		nullDefault = initDefault(object, NullDefault.class);
		booleanDefault = initDefault(object, BooleanDefault.class);
		intDefault = initDefault(object, IntDefault.class);
		longDefault = initDefault(object, LongDefault.class);
		floatDefault = initDefault(object, FloatDefault.class);
		doubleDefault = initDefault(object, DoubleDefault.class);
		bytesDefault = initDefault(object, BytesDefault.class);
		stringDefault = initDefault(object, StringDefault.class);
		recordDefault = initDefault(object, RecordDefault.class);
		enumDefault = initDefault(object, EnumDefault.class);
		fixedDefault = initDefault(object, FixedDefault.class);
		arrayDefault = initDefault(object, ArrayDefault.class);
		mapDefault = initDefault(object, MapDefault.class);
		genDefault = initDefault(object, GenericDefault.class);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T initDefault(Object object, Class<T> defaultClass) {
		if (defaultClass.isAssignableFrom(object.getClass())) {
			return (T) object;
		}
		return null;
	}
	
	public FieldAssembler<?> noDefault() {
		if (nullDefault != null) {
			return nullDefault.noDefault();
		} 
		if (booleanDefault != null) {
			return booleanDefault.noDefault();
		}
		if (intDefault != null) {
			return intDefault.noDefault();
		} 
		if (longDefault != null) {
			return longDefault.noDefault();
		} 
		if (floatDefault != null) {
			return floatDefault.noDefault();
		} 
		if (doubleDefault != null) {
			return doubleDefault.noDefault();
		}
		if (bytesDefault != null) {
			return bytesDefault.noDefault();
		}
		if (stringDefault != null) {
			return stringDefault.noDefault();
		} 
		if (recordDefault != null) {
			return recordDefault.noDefault();
		} 
		if (enumDefault != null) {
			return enumDefault.noDefault();
		} 
		if (fixedDefault != null) {
			return fixedDefault.noDefault();
		} 
		if (arrayDefault != null) {
			return arrayDefault.noDefault();
		} 
		if (mapDefault != null) {
			return mapDefault.noDefault();
		}
		if (genDefault != null) {
			return genDefault.noDefault();
		}
		throw new UnsupportedOperationException();
	}
	
	public FieldAssembler<?> setDefaultValue(Object defaultVal) {
		if (nullDefault != null) {
			return nullDefault.nullDefault();
		}
		if (booleanDefault != null) {
			return booleanDefault.booleanDefault((boolean) defaultVal);
		}
		if (intDefault != null) {
			return intDefault.intDefault((int) defaultVal);
		} 
		if (longDefault != null) {
			return longDefault.longDefault((long) defaultVal);
		} 
		if (floatDefault != null) {
			return floatDefault.floatDefault((float) defaultVal);
		} 
		if (doubleDefault != null) {
			return doubleDefault.doubleDefault((double) defaultVal);
		}
		if (bytesDefault != null) {
			return bytesDefault.bytesDefault((byte[]) defaultVal);
		}
		if (stringDefault != null) {
			return stringDefault.stringDefault((String) defaultVal);
		} 
		if (recordDefault != null) {
			return recordDefault.recordDefault((GenericRecord) defaultVal);
		} 
		if (enumDefault != null) {
			return enumDefault.enumDefault((String) defaultVal);
		} 
		if (fixedDefault != null) {
			return fixedDefault.fixedDefault((byte[]) defaultVal);
		} 
		if (arrayDefault != null) {
			return arrayDefault.arrayDefault((List<?>) defaultVal);
		} 
		if (mapDefault != null) {
			return mapDefault.mapDefault((Map<?, ?>) defaultVal);
		}
		if (genDefault != null) {
			return genDefault.withDefault(defaultVal);
		}
		throw new UnsupportedOperationException();
	}
	
}
