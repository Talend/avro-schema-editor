package org.talend.avro.schema.editor.model.attributes;

import java.util.Arrays;

/**
 * This class is used as the value of an attribute allowing to choose one of several options.
 * 
 * @author timbault
 *
 * @param <T>
 */
public class MultiChoiceValue<T> {

	/**
	 * The class of the options
	 */
	private Class<T> valueClass;
	
	/**
	 * The selected value
	 */
	private T value;
	
	/**
	 * Tha available options
	 */
	private T[] values;

	public MultiChoiceValue(Class<T> valueClass, T value, T[] values) {
		super();
		checkValues(value, values);
		this.valueClass = valueClass;
		this.value = value;
		this.values = values;
	}
	
	public Class<T> getValueClass() {
		return valueClass;
	}

	public T getValue() {
		return value;
	}
	
	public T[] getValues() {
		return values;
	}
	
	public void setValue(T value) {
		checkValues(value, values);
		this.value = value;
	}
	
	private void checkValues(T value, T[] availableValues) {
        if (Arrays.binarySearch(availableValues, value) < 0) {
        	throw new IllegalArgumentException("Specified value is not allowed");
        }
    }

	public int size() {
		return values.length;
	}
	
	public T getValueFor(int index) {
		return values[index];
	}
	
	public int getIndexOf(T value) {
		int index = 0;
		for (T val : values) {
			if (val.equals(value)) {
				return index;
			}
			index++;
		}
		return -1;
	}
	
	public void apply(MultiChoiceValue<T> multiChoiceValue) {
		this.values = multiChoiceValue.getValues();
		this.value = multiChoiceValue.getValue();
	}
	
	public MultiChoiceValue<T> getACopy() {
		return new MultiChoiceValue<T>(valueClass, value, values);
	}
	
}
